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
import com.huotu.agento2o.service.common.WithdrawEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.settlement.AuthorAccount;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.repository.author.AuthorRepository;
import com.huotu.agento2o.service.repository.settlement.AuthorAccountRepository;
import com.huotu.agento2o.service.repository.settlement.WithdrawRecordRepository;
import com.huotu.agento2o.service.service.settlement.AuthorAccountService;
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
public class AuthorAccountServiceImpl implements AuthorAccountService {
    @Autowired
    private AuthorAccountRepository authorAccountRepository;
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public AuthorAccount findByAuthor(Author author) {
        AuthorAccount authorAccount = authorAccountRepository.findByAuthor(author);
        if (authorAccount == null) {
            authorAccount = new AuthorAccount();
            authorAccount.setAuthor(author);
            authorAccount = authorAccountRepository.save(authorAccount);
        }
        return authorAccount;
    }

    @Override
    public AuthorAccount findById(Integer id) {
        return authorAccountRepository.findOne(id);
    }

    @Override
    public AuthorAccount findByAuthorAndCustomer(Integer authorId, Integer customerId) {
        Author author = authorRepository.findOne(authorId);
        if (author == null || !author.getCustomer().getCustomerId().equals(customerId)) {
            return null;
        }
        AuthorAccount authorAccount = findByAuthor(author);
        return authorAccount;
    }

    @Override
    @Transactional
    public AuthorAccount save(AuthorAccount authorAccount) {
        return authorAccountRepository.save(authorAccount);
    }

    @Override
    @Transactional
    public ApiResult applyWithdraw(Integer authorAccountId, WithdrawApplyInfo withdrawApplyInfo) throws Exception {
        AuthorAccount authorAccount = authorAccountRepository.findOne(authorAccountId);
        //异常判断
        if (authorAccount.getBalance() < withdrawApplyInfo.getBalance())
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, null);
        //待提货款减少，申请中货款增加
        authorAccount.setBalance((double) Math.round((authorAccount.getBalance() - withdrawApplyInfo.getBalance()) * 100) / 100);
        authorAccount.setApplyingMoney((double) Math.round((authorAccount.getApplyingMoney() + withdrawApplyInfo.getBalance()) * 100) / 100);
        AuthorAccount shopAccount = findByAuthor(authorAccount.getAuthor());
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setShopAccount(shopAccount);
        withdrawRecord.setStatus(WithdrawEnum.WithdrawEnumStatus.APPLYING);
        withdrawRecord.setAmount(withdrawApplyInfo.getBalance());
        withdrawRecord.setWithdrawNo(SerialNo.create());
        withdrawRecord.setApplyTime(new Date());
        withdrawRecord.setBankName(authorAccount.getAuthor().getBankName());
        withdrawRecord.setAccountName(authorAccount.getAuthor().getAccountName());
        withdrawRecord.setAccountNo(authorAccount.getAuthor().getAccountNo());
        authorAccountRepository.save(authorAccount);
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
                AuthorAccount authorAccount = withdrawRecord.getShopAccount();
                authorAccount.setWithdrew((double) Math.round((authorAccount.getWithdrew() + withdrawRecord.getAmount()) * 100) / 100);
                authorAccount.setApplyingMoney((double) Math.round((authorAccount.getApplyingMoney() - withdrawRecord.getAmount()) * 100) / 100);
                authorAccountRepository.save(authorAccount);
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
    public long getRecords(AuthorAccount authorAccount, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth) throws Exception {
        Specification<WithdrawRecord> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("shopAccount").as(AuthorAccount.class), authorAccount));
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
