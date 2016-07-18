/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.order.ajaxhandler;

import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * todo
 * Created by allan on 1/2/16.
 */
@Controller
@RequestMapping(value = "/agent/afterSale/ajax", method = RequestMethod.POST)
@PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('ORDER')")
public class AfterSaleHandler {
    @Autowired
    private MallAfterSalesService afterSalesService;
    @Autowired
    private MallDeliveryService deliveryService;
//    @Autowired
//    private ErpOrderService erpOrderService;

    /**
     * 同意退款
     *
     * @param afterId
     * @return
     */
    @RequestMapping("/refundAgree")
    @ResponseBody
    public ApiResult refundAgree(String afterId) throws UnsupportedEncodingException {
        MallAfterSales afterSales = afterSalesService.findByAfterId(afterId);
        if (afterSales.refundable()) {
            //先到伙伴商城那里执行退货
            ApiResult apiResult = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            if (afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM &&
                    afterSales.getAfterSaleType() == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND) {
                //生成退货单
                List<MallAfterSalesItem> forReturn = afterSales.getAfterSalesItems().stream()
                        .filter(item -> item.getIsLogic() == 1)
                        .collect(Collectors.toList());
                if (forReturn.size() > 0) {
                    LogiModel logiModel = JSON.parseObject(forReturn.get(0).getAfterContext(), LogiModel.class);
                    String dicReturnItemsStr = afterSales.getOrderItem().getItemId() + "," + afterSales.getOrderItem().getNums();
                    apiResult = deliveryService.pushRefund(afterSales.getOrderId(), logiModel, afterSales.getShop().getId(), dicReturnItemsStr);
                }
            }
            if (apiResult.getCode() == ResultCodeEnum.SUCCESS.getResultCode()) {
                //更改售后表状态,更改协商表
                afterSalesService.afterSaleAgree(afterSales, "同意退款，等待退款！",
                        AfterSaleEnum.AfterSaleStatus.REFUNDING, AfterSaleEnum.AfterItemsStatus.REFUNDING);
            }
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "操作失败,请检查售后状态", null);
    }

    /**
     * 同意退货
     *
     * @param afterId
     * @param returnAddr
     * @return
     */
    @RequestMapping("/returnAgree")
    @ResponseBody
    public ApiResult returnAgree(String afterId, String returnAddr) {
        MallAfterSales afterSales = afterSalesService.findByAfterId(afterId);
        if (afterSales.returnable()) {
            //更改售后表状态,更改协商表
            afterSalesService.afterSaleAgree(afterSales, returnAddr,
                    AfterSaleEnum.AfterSaleStatus.WAITING_BUYER_RETURN, AfterSaleEnum.AfterItemsStatus.WAITING_BUYER_RETURN);

            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "操作失败,请检查售后状态", null);
    }

    /**
     * 拒绝申请
     *
     * @param afterId
     * @param reason
     * @return
     */
    @RequestMapping("/afterSaleRefuse")
    @ResponseBody
    public ApiResult afterSalesRefuse(String afterId, String reason) {
        MallAfterSales afterSales = afterSalesService.findByAfterId(afterId);
        if (afterSales.refusable()) {
            //更改售后表状态,更改协商表
            afterSalesService.afterSaleRefuse(afterSales, reason);

//            //推送erp
//            erpOrderService.pushAfterSale(afterSales.getOrderId());

            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "操作失败,请检查售后状态", null);
    }
}
