/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.settlement;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;

import java.time.LocalDateTime;

/**
 * Created by helloztt on 2016/6/14.
 */
public interface AccountService {

    Account findByAuthor(Author author);

    Account findById(Integer id);

    Account findByAuthorAndCustomer(Integer agentId,Integer shopId, Integer customerId);

    Account save(Account account);

    /**
     * 申请提现
     * 修改供应商账户余额，同时生成一条提现记录
     *
     * @param accountId
     * @param withdrawApplyInfo
     * @return
     * @throws Exception
     */
    ApiResult applyWithdraw(Integer accountId, WithdrawApplyInfo withdrawApplyInfo) throws Exception;

    /**
     * 审核提现，并打款
     * 修改提现记录状态，同时修改供应商账户申请中货款，已提货款
     *
     * @param withdrawId
     * @return
     * @throws Exception
     */
    ApiResult changeWithdraw(Integer withdrawId, int status) throws Exception;

    long getRecords(Account account, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth) throws Exception;
}
