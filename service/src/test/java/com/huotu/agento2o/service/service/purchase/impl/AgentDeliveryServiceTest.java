package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * Created by wuxiongliu on 2016/5/27.
 */
public class AgentDeliveryServiceTest extends CommonTestBase {

    @Autowired
    private AgentDeliveryService agentDeliveryService;

    @Test
    public void testShowPurchaseDeliveryList(){
        DeliverySearcher deliverySearcher = new DeliverySearcher();
        deliverySearcher.setParentAgentId(1);
        Page page = agentDeliveryService.showPurchaseDeliveryList(deliverySearcher);
        System.out.println(page.getContent().size());
    }

    @Test
    public void testShowReturnDeliveryList(){
        DeliverySearcher deliverySearcher = new DeliverySearcher();
        deliverySearcher.setParentAgentId(1);
        Page page = agentDeliveryService.showReturnDeliveryList(deliverySearcher);
    }
}
