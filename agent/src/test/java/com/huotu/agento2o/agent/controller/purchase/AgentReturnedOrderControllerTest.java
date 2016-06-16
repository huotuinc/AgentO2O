package com.huotu.agento2o.agent.controller.purchase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by wuxiongliu on 2016/6/3.
 */
public class AgentReturnedOrderControllerTest extends CommonTestBase {

    private static String BASE_URL = "/returnedOrder";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private Agent mockFirstLevelAgent;
    //二级代理商
    private Agent mockSecondLevelAgent;
    //一级代理商下级门店
    private Shop mockFirstLevelShop;
    //二级代理商下级门店
    private Shop mockSecondLevelShop;

    //无规格商品(只有一个货品)
    private List<MallGoods> mockGoodsWith1ProductList = new ArrayList<>();
    //多规格商品(有多个货品)
    private List<MallGoods> mockGoodsWithNProductList = new ArrayList<>();
    //一级代理商货品
    private List<AgentProduct> mockFirstLevelAgentProductList = new ArrayList<>();
    private List<MallGoods> mockFirstLevelAgentGoodsWith1ProductList = new ArrayList<>();
    private List<MallGoods> mockFirstLevelAgentGoodsWithNProductList = new ArrayList<>();

    // 一级代理商退货单
    private List<AgentReturnedOrder> mockFirstLevelAgentReturnOrderList = new ArrayList<>();

    // 一级代理商退货单明细
    private List<AgentReturnedOrderItem> mockFirstLevelAgentReturnOrderItemList = new ArrayList<>();

    // 一级代理商下级门店 退货单
    private List<AgentReturnedOrder> mockFirstLevelShopReturnOrderList = new ArrayList<>();

    // 一级代理商下级门店退货单明细
    private List<AgentReturnedOrderItem> mockFirstLevelShopReturnOrderItemList = new ArrayList<>();

    // 二级代理商退货单
    private List<AgentReturnedOrder> mockSecondLevelAgentReturnOrderList = new ArrayList<>();



    /**
     * addReturnOrder
     * showReturnedOrderList
     * cancelReturnOrder
     * showReturnedOrderDetail
     * showDelivery
     * deliveryReturnOrder
     * editReturnNum
     * showAgentReturnedOrderList
     * checkAgentReturnOrder
     * receiveReturnOrder
     * payReturnOrder
     */

    @Before
    public void init(){
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent);

        //平台商品相关
        for (int i = 0; i < random.nextInt(10) + 10; i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), null);
            List<MallProduct> mockGoodsWith1ProductsList = new ArrayList<>();
            mockGoodsWith1ProductsList.add(mockMallProduct(mockGoodsWith1Products));
            mockGoodsWith1Products.setProducts(mockGoodsWith1ProductsList);
            mockGoodsWith1Products.setStore(mockGoodsWith1ProductsList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWith1Products = mockMallGoods(mockGoodsWith1Products);
            mockGoodsWith1ProductList.add(mockGoodsWith1Products);
        }

        for (int i = 0; i < random.nextInt(10) + 10; i++) {
            MallGoods mockGoodsWithNProducts = mockMallGoods(mockCustomer.getCustomerId(), null);
            List<MallProduct> productList = new ArrayList<>();
            for (int j = 0; j < random.nextInt(10) + 2; j++) {
                productList.add(mockMallProduct(mockGoodsWithNProducts));
            }
            mockGoodsWithNProducts.setProducts(productList);
            mockGoodsWithNProducts.setStore(mockGoodsWithNProductList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWithNProducts = mockMallGoods(mockGoodsWithNProducts);
            mockGoodsWithNProductList.add(mockGoodsWithNProducts);
        }

        //一级代理商货品
        for (int i = 0; i <= random.nextInt(mockGoodsWith1ProductList.size()); i++) {
            mockFirstLevelAgentGoodsWith1ProductList.add(mockGoodsWith1ProductList.get(i));
            MallGoods mockGoodsWith1Products = mockGoodsWith1ProductList.get(i);
            mockGoodsWith1Products.getProducts().forEach(product -> {
                mockFirstLevelAgentProductList.add(mockAgentProduct(product, mockFirstLevelAgent));
            });
        }
        for (int i = 0; i <= random.nextInt(mockGoodsWithNProductList.size()); i++) {
            mockFirstLevelAgentGoodsWithNProductList.add(mockGoodsWithNProductList.get(i));
            for (int j = 0; j < random.nextInt(mockGoodsWithNProductList.get(i).getProducts().size()) + 1; j++) {
                mockFirstLevelAgentProductList.add(mockAgentProduct(mockGoodsWithNProductList.get(i).getProducts().get(j), mockFirstLevelAgent));
            }
        }

        // 一级代理商退货单
        for(int i = 0; i< Constant.PAGESIZE*2; i++){
            mockFirstLevelAgentReturnOrderList.add(mockAgentReturnOrder(mockFirstLevelAgent));
        }

        // 一级代理商退货单明细,每条退货单模拟了一个退货货品
        for(int i=0;i<mockFirstLevelAgentReturnOrderList.size();i++){
            mockFirstLevelAgentReturnOrderItemList.add(mockAgentReturnOrderItem(mockFirstLevelAgentReturnOrderList.get(i),mockGoodsWith1ProductList.get(0).getProducts().get(0)));
        }

        //一级代理商下级门店退货单
        for(int i = 0; i< Constant.PAGESIZE*2; i++){
            mockFirstLevelShopReturnOrderList.add(mockAgentReturnOrder(mockFirstLevelShop));
        }

        // 一级代理商下级门店退货单明细，每条退货单模拟了一个退货货品
        for(int i=0;i<mockFirstLevelShopReturnOrderList.size();i++){
            mockFirstLevelShopReturnOrderItemList.add(mockAgentReturnOrderItem(mockFirstLevelShopReturnOrderList.get(i),mockGoodsWith1ProductList.get(0).getProducts().get(0)));
        }

        // 二级代理商退货单
        for(int i = 0; i< Constant.PAGESIZE*2; i++){
            mockSecondLevelAgentReturnOrderList.add(mockAgentReturnOrder(mockSecondLevelAgent));
        }

    }

    @Test
    public void testShowPurchasedProductList() throws Exception {
        String controllerUrl = BASE_URL + "/showPurchasedProductList";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView = result.getModelAndView();
        List<AgentProduct> agentProducts = (List<AgentProduct>) modelAndView.getModel().get("agentProductList");

        Assert.assertEquals(mockFirstLevelAgentProductList.size(),agentProducts.size());
        for(int i=0;i<agentProducts.size();i++){
            Assert.assertEquals(agentProducts.get(i).getGoodsId(),mockFirstLevelAgentProductList.get(i).getGoodsId());
            Assert.assertEquals(agentProducts.get(i).getProduct(),mockFirstLevelAgentProductList.get(i).getProduct());
        }

    }

    @Test
    public void testAddReturnOrder() throws Exception {
        AgentProduct firstLevelAgentProduct = mockFirstLevelAgentProductList.get(0);
        String productId = String.valueOf(firstLevelAgentProduct.getProduct().getProductId());


        String controllerUrl = BASE_URL + "/addReturnOrder";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("productIds",productId+",")
                .param("productNums","1,")
                .param("mobile","18705153967")
                .param("sendmentStatus","HOME_DELIVERY")
                .param("authorComment","50"))
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    public void testShowReturnedOrderList() throws Exception {
        String controllerUrl = BASE_URL + "/showReturnedOrderList";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1. 没有搜索条件
        MvcResult resultWithNoSearch = mockMvc.perform(post(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andExpect(model().attributeExists("agentReturnedOrderList"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("agentId"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("searchCondition"))
                .andExpect(model().attributeExists("pageIndex"))
                .andExpect(model().attributeExists("authorType"))
                .andReturn();
        List<AgentReturnedOrder> agentReturnedOrderListWithNoSearch = (List<AgentReturnedOrder>) resultWithNoSearch.getModelAndView().getModel().get("agentReturnedOrderList");
        Integer pageSizeWithNoSearch = (Integer) resultWithNoSearch.getModelAndView().getModel().get("pageSize");
        System.out.println(pageSizeWithNoSearch+"   "+mockFirstLevelAgentReturnOrderList.size());
        Assert.assertNotNull(agentReturnedOrderListWithNoSearch);
        Assert.assertEquals(Math.min(pageSizeWithNoSearch, mockFirstLevelAgentReturnOrderList.size()), agentReturnedOrderListWithNoSearch.size());

        //2. 按退货单号
        MvcResult  resultWithRorderId = mockMvc.perform(post(controllerUrl)
                 .session(session)
                 .param("rOrderId",mockFirstLevelAgentReturnOrderList.get(0).getROrderId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andExpect(model().attributeExists("agentReturnedOrderList"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("agentId"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("searchCondition"))
                .andExpect(model().attributeExists("pageIndex"))
                .andExpect(model().attributeExists("authorType"))
                .andReturn();


        List<AgentReturnedOrder> agentReturnedOrderListWithROrderId = (List<AgentReturnedOrder>) resultWithRorderId.getModelAndView().getModel().get("agentReturnedOrderList");
        Assert.assertNotNull(agentReturnedOrderListWithROrderId);
        Assert.assertEquals(1,agentReturnedOrderListWithROrderId.size());
        Assert.assertEquals(mockFirstLevelAgentReturnOrderList.get(0).getROrderId(), agentReturnedOrderListWithROrderId.get(0).getROrderId());

    }

    @Test
    public void testCancelReturnOrder() throws Exception {
        String controllerUrl = BASE_URL + "/cancelReturnOrder";
        String rOrderId = mockFirstLevelAgentReturnOrderList.get(0).getROrderId();
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(post(controllerUrl)
                                    .session(session)
                                    .param("rOrderId",rOrderId))
                                    .andExpect(status().isOk())
                                    .andReturn();
        String responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject jsonObject = JSON.parseObject(responseData);
        Assert.assertEquals("200",jsonObject.getString("code"));
        Assert.assertEquals("请求成功",jsonObject.getString("msg"));

        rOrderId = null;
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",rOrderId))
                .andExpect(status().isOk())
                .andReturn();
        responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        jsonObject = JSON.parseObject(responseData);
        Assert.assertEquals("0",jsonObject.getString("code"));
        Assert.assertEquals("退单号不能为空",jsonObject.getString("msg"));
    }

    @Test
    public void testShowReturnedOrderDetail() throws Exception {
        String controllerUrl = BASE_URL + "/showReturnedOrderDetail";
        String rOrderId = mockFirstLevelAgentReturnOrderList.get(0).getROrderId();
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //获取一级代理商采购退货发货详情
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",rOrderId))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        List<AgentReturnedOrderItem> agentReturnedOrderItems = (List<AgentReturnedOrderItem>) modelAndView.getModel().get("agentReturnedOrderItems");
        Assert.assertTrue(agentReturnedOrderItems.size() == 1);
        Assert.assertEquals(agentReturnedOrderItems.get(0).getId(),mockFirstLevelAgentReturnOrderItemList.get(0).getId());

        // 获取一级代理商下级门店信息退货发货详情
        Integer subAuthorId = mockFirstLevelShop.getId();
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",rOrderId)
                .param("subAuthorId",String.valueOf(subAuthorId)))
                .andExpect(status().isOk())
                .andReturn();
        modelAndView = result.getModelAndView();
        agentReturnedOrderItems = (List<AgentReturnedOrderItem>) modelAndView.getModel().get("agentReturnedOrderItems");
        Assert.assertTrue(agentReturnedOrderItems.size() == 1);
        Assert.assertEquals(agentReturnedOrderItems.get(0).getId(),mockFirstLevelShopReturnOrderItemList.get(0).getId());

    }

    @Test
    public void testEditReturnNum() throws Exception {
        String controllerUrl = BASE_URL + "/editReturnNum";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("productId","")
                .param("num",""))
                .andExpect(status().isOk())
                .andReturn();

        String responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject jsonObject = JSON.parseObject(responseData);
        String msg = jsonObject.getString("msg");
        Integer code = jsonObject.getInteger("code");
        Assert.assertTrue(code==500);
        Assert.assertEquals("没有传输数据",msg);

        // 可用库存充足
        Integer num = mockFirstLevelAgentProductList.get(0).getStore()-10;
        Integer productId = mockFirstLevelAgentProductList.get(0).getProduct().getProductId();
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("productId",String.valueOf(productId))
                .param("num",String.valueOf(num)))
                .andExpect(status().isOk())
                .andReturn();
        responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        jsonObject = JSON.parseObject(responseData);
        msg = jsonObject.getString("msg");
        code = jsonObject.getInteger("code");
        Assert.assertTrue(code==200);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultMsg(),msg);

        // 可用库存不足
        num = mockFirstLevelAgentProductList.get(0).getStore()+10;
        productId = mockFirstLevelAgentProductList.get(0).getProduct().getProductId();
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("productId",String.valueOf(productId))
                .param("num",String.valueOf(num)))
                .andExpect(status().isOk())
                .andReturn();
        responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        jsonObject = JSON.parseObject(responseData);
        msg = jsonObject.getString("msg");
        code = jsonObject.getInteger("code");
        Assert.assertTrue(code==0);
        Assert.assertEquals("库存不足",msg);
    }

    @Test
    public void testShowAgentReturnedOrderList(){
        // 显示一级代理商下级门店 及其下级代理商退货单列表


    }




    // 测试退货单从退货申请，到退货流程结束整个过程
    @Test
    public void testReturnProcess() throws Exception {
        // 二级代理商增加一个退货单
        AgentReturnedOrder agentReturnedOrder = mockSecondLevelAgentReturnOrderList.get(0);
        System.out.println("status:"+agentReturnedOrder.getStatus());

        // 一级代理商审核
        String controllerUrl = BASE_URL + "/checkAgentReturnOrder";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",String.valueOf(agentReturnedOrder.getROrderId()))
                .param("checkStatus","2")
                .param("statusComment",""))
                .andExpect(status().isOk())
                .andReturn();

        String responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject jsonObject = JSON.parseObject(responseData);
        String msg = jsonObject.getString("msg");
        Integer code = jsonObject.getInteger("code");
        Assert.assertTrue(code==ResultCodeEnum.DATA_NULL.getResultCode());
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultMsg(),msg);



        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",String.valueOf(agentReturnedOrder.getROrderId()))
                .param("checkStatus","1")
                .param("statusComment",""))
                .andExpect(status().isOk())
                .andReturn();

        responseData = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        jsonObject = JSON.parseObject(responseData);
        msg = jsonObject.getString("msg");
        code = jsonObject.getInteger("code");
        Assert.assertTrue(code==ResultCodeEnum.SUCCESS.getResultCode());
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultMsg(),msg);
        Assert.assertEquals(PurchaseEnum.OrderStatus.CHECKED,agentReturnedOrder.getStatus());


        // 二级代理商发货
        controllerUrl = BASE_URL + "/delivery";
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("orderId",agentReturnedOrder.getROrderId()))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals(PurchaseEnum.ShipStatus.DELIVERED,agentReturnedOrder.getShipStatus());

        // 一级代理商收货
        controllerUrl = BASE_URL + "/receiveAgentReturnOrder";
        Assert.assertTrue(agentReturnedOrder.receivable());
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",agentReturnedOrder.getROrderId()))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertFalse(agentReturnedOrder.receivable());

        // 一级代理商支付
        controllerUrl = BASE_URL + "/payAgentReturnOrder";
        Assert.assertTrue(agentReturnedOrder.payabled());
        result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("rOrderId",agentReturnedOrder.getROrderId()))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertFalse(agentReturnedOrder.payabled());
    }

}
