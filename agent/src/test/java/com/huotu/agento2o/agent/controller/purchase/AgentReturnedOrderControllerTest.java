package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
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

    private static String BASE_URL = "/purchase";

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

    // 一级代理商下级门店 退货单
    private List<AgentReturnedOrder> mockFirstLevelShopReturnOrderList = new ArrayList<>();

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
    public void testAddReturnOrder(){

    }

    @Test
    public void testShowReturnedOrderList() throws Exception {
        String controllerUrl = BASE_URL + "/showReturnedOrderList";
        System.out.println(mockFirstLevelAgent.getId());
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult resultWithNoSearch = mockMvc.perform(
                post(controllerUrl)
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



    }

    @Test
    public void testCancelReturnOrder(){

    }

    @Test
    public void testShowReturnedOrderDetail(){

    }

    @Test
    public void testShowDelivery(){

    }


    @Test
    public void testDeliveryReturnOrder(){

    }

    @Test
    public void testEditReturnNum(){

    }

    @Test
    public void testShowAgentReturnedOrderList(){

    }

    @Test
    public void testCheckAgentReturnOrder(){

    }

    @Test
    public void testReceiveReturnOrder(){

    }

    @Test
    public void testPayReturnOrder(){

    }


}
