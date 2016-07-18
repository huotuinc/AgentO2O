/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.settlement.impl;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.WithdrawEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.repository.settlement.WithdrawRecordRepository;
import com.huotu.agento2o.service.service.settlement.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/14.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;

    @Override
    public Account findByAuthor(Author author) {
        Account account = null;
        if (author != null && author.getType() == Agent.class) {
            account = accountRepository.findByAgent(author.getAuthorAgent());
        } else if (author != null && author.getType() == ShopAuthor.class) {
            account = accountRepository.findByShop(author.getAuthorShop());
        }
        if (account == null) {
            account = new Account();
            account.setAuthor(author);
            account = accountRepository.save(account);
        }
        return account;
    }

    @Override
    public Account findById(Integer id) {
        return accountRepository.findOne(id);
    }

    @Override
    public Account findByAuthorAndCustomer(Integer agentId,Integer shopId, Integer customerId) {
        Author author = null;
        if(agentId != null && agentId != 0){
            author = customerRepository.findOne(agentId);
        }else if(shopId != null && shopId != 0){
            author = shopRepository.findOne(shopId);
        }
        if (author == null || !author.getCustomer().getCustomerId().equals(customerId)) {
            return null;
        }
        Account account = findByAuthor(author);
        return account;
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public ApiResult applyWithdraw(Integer accountId, WithdrawApplyInfo withdrawApplyInfo) throws Exception {
        Account account = accountRepository.findOne(accountId);
        //异常判断
        if (account.getBalance() < withdrawApplyInfo.getBalance())
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, null);
        //待提货款减少，申请中货款增加
        account.setBalance((double) Math.round((account.getBalance() - withdrawApplyInfo.getBalance()) * 100) / 100);
        account.setApplyingMoney((double) Math.round((account.getApplyingMoney() + withdrawApplyInfo.getBalance()) * 100) / 100);
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setAccount(account);
        withdrawRecord.setStatus(WithdrawEnum.WithdrawEnumStatus.APPLYING);
        withdrawRecord.setAmount(withdrawApplyInfo.getBalance());
        withdrawRecord.setWithdrawNo(SerialNo.create());
        withdrawRecord.setApplyTime(new Date());
        withdrawRecord.setBankName(account.getAuthorBankName());
        withdrawRecord.setAccountName(account.getAuthorAccountName());
        withdrawRecord.setAccountNo(account.getAuthorAccountNo());
        accountRepository.save(account);
        withdrawRecordRepository.save(withdrawRecord);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult changeWithdraw(Integer withdrawId, int status) throws Exception {
        ApiResult apiResult = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        if (withdrawId != 0) {
            WithdrawRecord withdrawRecord = withdrawRecordRepository.findOne(withdrawId);
            if (withdrawRecord == null) {
                apiResult.setCode(500);
                apiResult.setMsg("提现编号不存在！");
            } else if (status == WithdrawEnum.WithdrawEnumStatus.PAYED.getCode()) {
                //打款时不用判断提现次数限制，直接打款
                Account account = withdrawRecord.getAccount();
                account.setWithdrew((double) Math.round((account.getWithdrew() + withdrawRecord.getAmount()) * 100) / 100);
                account.setApplyingMoney((double) Math.round((account.getApplyingMoney() - withdrawRecord.getAmount()) * 100) / 100);
                accountRepository.save(account);
                apiResult.setCode(200);
                apiResult.setMsg("操作成功！");
                withdrawRecord.setStatus(EnumHelper.getEnumType(WithdrawEnum.WithdrawEnumStatus.class, status));
                withdrawRecord.setRemitTime(new Date());
                withdrawRecordRepository.save(withdrawRecord);
            }
        }
        return apiResult;
    }

    @Override
    public long getRecords(Account account, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth) throws Exception {
        Specification<WithdrawRecord> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("shopAccount").as(Account.class), account));
            //找出 申请中和已支付的流水记录
            predicates.add(cb.or(cb.equal(root.get("status").as(WithdrawEnum.WithdrawEnumStatus.class), WithdrawEnum.WithdrawEnumStatus.APPLYING),
                    cb.equal(root.get("status").as(WithdrawEnum.WithdrawEnumStatus.class), WithdrawEnum.WithdrawEnumStatus.PAYED)));
            if (!StringUtils.isEmpty(firstDayOfMonth)) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime").as(Date.class),
                        Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(firstDayOfMonth)));
            }
            if (!StringUtils.isEmpty(lastDayOfMonth)) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime").as(Date.class),
                        Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(lastDayOfMonth)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return withdrawRecordRepository.count(specification);
    }
}
