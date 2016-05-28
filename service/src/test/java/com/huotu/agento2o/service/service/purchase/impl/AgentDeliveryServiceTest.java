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
        Page page = agentDeliveryService.showPurchaseDeliveryList(deliverySearcher);
    }

    @Test
    public void testShowReturnDeliveryList(){
        DeliverySearcher deliverySearcher = new DeliverySearcher();
        Page page = agentDeliveryService.showReturnDeliveryList(deliverySearcher);
    }
}
