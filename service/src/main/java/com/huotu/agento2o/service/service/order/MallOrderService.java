package com.huotu.agento2o.service.service.order;

import com.hot.datacenter.entity.order.MallOrder;
import com.hot.datacenter.ienum.OrderEnum;
import com.hot.datacenter.service.CrudService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.CusOrderSearch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/11.
 */
public interface MallOrderService extends CrudService<MallOrder, String, CusOrderSearch> {
    MallOrder findByShopAndOrderId(ShopAuthor shop, String orderId);

    void updatePayStatus(String orderId, OrderEnum.PayStatus payStatus);

    Page<MallOrder> findAll(int pageIndex, Author author, int pageSize, CusOrderSearch orderSearch);

    ApiResult updateRemark(ShopAuthor shop, String orderId, String agentMarkType, String agentMarkText);

    OrderDetailModel findOrderDetail(String orderId);

    HSSFWorkbook createWorkBook(List<MallOrder> orders);
}
