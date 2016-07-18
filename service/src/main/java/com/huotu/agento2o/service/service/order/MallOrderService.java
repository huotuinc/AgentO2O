package com.huotu.agento2o.service.service.order;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/11.
 */
public interface MallOrderService {
    MallOrder findByOrderId(String orderId);

    MallOrder findByShopAndOrderId(ShopAuthor shop, String orderId);

    void updatePayStatus(String orderId, OrderEnum.PayStatus payStatus);

    ApiResult updateRemark(ShopAuthor shop, String orderId, String agentMarkType, String agentMarkText);

    Page<MallOrder> findAll(int pageIndex, Author author, int pageSize, OrderSearchCondition searchCondition);

    OrderDetailModel findOrderDetail(String orderId);

    HSSFWorkbook createWorkBook(List<MallOrder> orders);
}
