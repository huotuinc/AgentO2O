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

import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.searchable.WithdrawRecordSearcher;
import org.springframework.data.domain.Page;

/**
 * Created by helloztt on 2016/6/14.
 */
public interface WithdrawRecordService {
    Page<WithdrawRecord> getPage(Account account, WithdrawRecordSearcher withdrawRecordSearcher);

    void save(Integer authorAccountId, WithdrawApplyInfo withdrawApplyInfo);

    Page<WithdrawRecord> getPageByCustomerId(Integer customerId, WithdrawRecordSearcher withdrawRecordSearcher);

    WithdrawRecord findById(Integer id);

    WithdrawApplyInfo findInfoById(Integer id);

    WithdrawRecord save(WithdrawRecord withdrawRecord);
}
