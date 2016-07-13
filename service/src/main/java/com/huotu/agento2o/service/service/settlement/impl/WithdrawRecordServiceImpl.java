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
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.WithdrawEnum;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.repository.settlement.WithdrawRecordRepository;
import com.huotu.agento2o.service.searchable.WithdrawRecordSearcher;
import com.huotu.agento2o.service.service.settlement.WithdrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/14.
 */
@Service
public class WithdrawRecordServiceImpl implements WithdrawRecordService {

    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;
    @Autowired
    private AccountRepository accountRepository;


    @Override
    public Page<WithdrawRecord> getPage(Account account, WithdrawRecordSearcher withdrawRecordSearcher) {
        Specification<WithdrawRecord> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("account").as(Account.class), account));
            if(withdrawRecordSearcher.getStatus()!= -1) {
                predicates.add(cb.equal(root.get("status").as(WithdrawEnum.WithdrawEnumStatus.class), EnumHelper.getEnumType(WithdrawEnum.WithdrawEnumStatus.class,withdrawRecordSearcher.getStatus())));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getApplyStartTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getApplyStartTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getApplyEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getApplyEndTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getRemitStartTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("remitTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getRemitStartTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getRemitEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("remitTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getRemitEndTime(),StringUtil.TIME_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return withdrawRecordRepository.findAll(specification,
                new PageRequest(withdrawRecordSearcher.getPageNo()-1, withdrawRecordSearcher.getPageSize(),new Sort(Sort.Direction.DESC,"applyTime")));
    }

    @Override
    @Transactional
    public void save(Integer authorAccountId, WithdrawApplyInfo withdrawApplyInfo) {
        Account account = accountRepository.findOne(authorAccountId);
        if(account == null){
            return;
        }
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setAmount(withdrawApplyInfo.getBalance());
        withdrawRecord.setWithdrawNo(SerialNo.create());
        withdrawRecord.setApplyTime(new Date());
        withdrawRecord.setStatus(WithdrawEnum.WithdrawEnumStatus.APPLYING);
        withdrawRecord.setAccount(account);
        withdrawRecordRepository.save(withdrawRecord);
    }

    @Override
    public Page<WithdrawRecord> getPageByCustomerId(Integer customerId, WithdrawRecordSearcher withdrawRecordSearcher) {
        Specification<WithdrawRecord> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.or(
                    cb.equal(root.get("account").get("agent").get("customer").get("customerId").as(Integer.class),customerId),
                    cb.equal(root.get("account").get("shop").get("customer").get("customerId").as(Integer.class),customerId)
            ));
            if(withdrawRecordSearcher.getStatus()!= -1) {
                predicates.add(cb.equal(root.get("status").as(WithdrawEnum.WithdrawEnumStatus.class),
                        EnumHelper.getEnumType(WithdrawEnum.WithdrawEnumStatus.class,withdrawRecordSearcher.getStatus())));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getApplyStartTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getApplyStartTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getApplyEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getApplyEndTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getRemitStartTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("remitTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getRemitStartTime(),StringUtil.TIME_PATTERN)));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getRemitEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("remitTime").as(Date.class),
                        StringUtil.DateFormat(withdrawRecordSearcher.getRemitEndTime(),StringUtil.TIME_PATTERN)));
            }
            if( withdrawRecordSearcher.getShopId() != 0 ) {
                predicates.add(cb.equal(root.get("account").get("shop").get("id").as(Integer.class), withdrawRecordSearcher.getShopId()));
            }else if(withdrawRecordSearcher.getAgentId() != 0){
                predicates.add(cb.equal(root.get("account").get("agent").get("id").as(Integer.class), withdrawRecordSearcher.getShopId()));
            }
            if(!StringUtils.isEmpty(withdrawRecordSearcher.getWithdrawNo())) {
                predicates.add(cb.like(root.get("withdrawNo").as(String.class),"%" + withdrawRecordSearcher.getWithdrawNo() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return withdrawRecordRepository.findAll(specification,
                new PageRequest(withdrawRecordSearcher.getPageNo()-1, withdrawRecordSearcher.getPageSize(),new Sort(Sort.Direction.DESC,"applyTime")));
    }

    @Override
    public WithdrawRecord findById(Integer id) {
        return withdrawRecordRepository.findOne(id);
    }

    @Override
    public WithdrawApplyInfo findInfoById(Integer id) {
        WithdrawRecord withdrawRecord = withdrawRecordRepository.findOne(id);
        WithdrawApplyInfo withdrawApplyInfo = null;
        if(withdrawRecord != null){
            withdrawApplyInfo = new WithdrawApplyInfo();
            withdrawApplyInfo.setWithdrawNo(withdrawRecord.getWithdrawNo());
            withdrawApplyInfo.setBalance(withdrawRecord.getAmount());
            withdrawApplyInfo.setBankName(withdrawRecord.getBankName());
            withdrawApplyInfo.setAccountName(withdrawRecord.getAccountName());
            withdrawApplyInfo.setAccountNo(withdrawRecord.getAccountNo());
            withdrawApplyInfo.setShopName(withdrawRecord.getAccount().getAuthorName());
            withdrawApplyInfo.setStatus(withdrawRecord.getStatus());
        }
        return withdrawApplyInfo;
    }

    @Override
    public WithdrawRecord save(WithdrawRecord withdrawRecord) {
        return withdrawRecordRepository.saveAndFlush(withdrawRecord);
    }
}
