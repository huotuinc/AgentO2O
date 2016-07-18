/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.settlement;

import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.settlement.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/6/8.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

//    Account findByAgentAndShop(Author author);
    Account findByAgent(Agent agent);

    Account findByShop(ShopAuthor shop);

//    Account findByAuthor_Id(Integer authorId);
    Account findByAgent_Id(Integer agentId);
    Account findByShop_Id(Integer shopId);
}
