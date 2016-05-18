/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;
import com.huotu.agento2o.service.model.order.LogiModel;
import com.huotu.agento2o.service.repository.order.MallAfterSalesItemRepository;
import com.huotu.agento2o.service.service.order.MallAfterSalesItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/17.
 */
@Service
public class MallAfterSalesItemServiceImpl implements MallAfterSalesItemService{

    @Autowired
    private MallAfterSalesItemRepository afterSalesItemRepository;

    @Override
    public List<MallAfterSalesItem> findByAfterId(String afterId) {
        List<MallAfterSalesItem> afterSalesItems = afterSalesItemRepository.findByAfterSales_AfterIdOrderByItemIdDesc(afterId);
        afterSalesItems.forEach(item -> {
            if (!StringUtils.isEmpty(item.getImgFiles())) {
                String[] imgFileArrays = item.getImgFiles().split(",");
                List<String> imgFileList = new ArrayList<>();
                for (String file : imgFileArrays) {
                    imgFileList.add(SysConstant.HUOBANMALL_RESOURCE_HOST + file);
                }
                item.setImgFileList(imgFileList);
            }

            if (item.getIsLogic() == 1) {
                LogiModel logiModel = JSON.parseObject(item.getAfterContext(), LogiModel.class);
                if (!StringUtils.isEmpty(logiModel.getLogiImg())) {
                    String[] logiImgArray = logiModel.getLogiImg().split(",");
                    List<String> logiImgs = new ArrayList<>();
                    for (String img : logiImgArray) {
                        logiImgs.add(SysConstant.HUOBANMALL_RESOURCE_HOST + img);
                    }
                    logiModel.setLogiImgs(logiImgs);
                }
                item.setLogiModel(logiModel);
            }
            if (item.getAfterItemsStatus() == AfterSaleEnum.AfterItemsStatus.REFUNDING_FINISH) {
                List<String> refundInfo = new ArrayList<>();
                String[] refundInfoInfoArray = item.getReply().split("\\|");
                for (String info : refundInfoInfoArray) {
                    refundInfo.add(info);
                }
                item.setRefundInfos(refundInfo);
            }
        });
        return afterSalesItems;
    }

    @Override
    public void updateStatus(AfterSaleEnum.AfterItemsStatus afterItemsStatus, int itemId) {

    }

    @Override
    public MallAfterSalesItem findTopByAfterId(String afterId) {
        return null;
    }

    @Override
    public MallAfterSalesItem save(MallAfterSalesItem afterSalesItem) {
        return afterSalesItemRepository.save(afterSalesItem);
    }

    @Override
    public MallAfterSalesItem findTopByIsLogic(MallAfterSales afterSales, int isLogic) {
        return afterSalesItemRepository.findTopByAfterSalesAndIsLogicNotOrderByItemIdDesc(afterSales, isLogic);
    }
}
