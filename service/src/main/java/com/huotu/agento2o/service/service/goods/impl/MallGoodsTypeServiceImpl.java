/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods.impl;

import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.repository.goods.MallGoodsTypeRepository;
import com.huotu.agento2o.service.service.goods.MallGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/25.
 */
@Service
public class MallGoodsTypeServiceImpl implements MallGoodsTypeService {
    @Autowired
    private MallGoodsTypeRepository typeRepository;

    @Override
    public List<MallGoodsType> getGoodsTypeByParentId(String parentId) {
        return typeRepository.findByParentStandardTypeIdAndDisabledFalseAndCustomerIdOrderByTOrderAsc(parentId,-1);
    }

    @Override
    public MallGoodsType getGoodsTypeByStandardTypeId(String standardTypeId) {
        return typeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId(standardTypeId,-1);
    }

    @Override
    public List<MallGoodsType> getAllParentTypeList(String standardTypeId) {
        MallGoodsType goodsType = typeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId(standardTypeId,-1);
        List<MallGoodsType> typeList = new ArrayList<>();
        if(goodsType != null){
            String path = "|0";
            if(!StringUtil.isEmptyStr(goodsType.getPath())){
                path = path + goodsType.getPath();
            }else{
                path = path + "|";
            }
            //根据path，检索出当前及所有父路径下的类型值
            String[] parentStandardTypeIdList = path.split("\\|"); //“.”或“|”必须前面加\\才能分开成数组，即String.split("\\|")
            for(int i = 0;i < parentStandardTypeIdList.length ; i++){
                if(parentStandardTypeIdList[i] != null && parentStandardTypeIdList[i].length()> 0){
                    List<MallGoodsType> parentTypeList = typeRepository.findByParentStandardTypeIdAndDisabledFalseAndCustomerIdOrderByTOrderAsc(parentStandardTypeIdList[i], -1);
                    if(parentTypeList != null && parentTypeList.size() > 0){
                        typeList.addAll(parentTypeList);
                    }
                }
            }
            if(goodsType.isParent()){
                List<MallGoodsType> subTypeList = typeRepository.findByParentStandardTypeIdAndDisabledFalseAndCustomerIdOrderByTOrderAsc(goodsType.getStandardTypeId() , -1);
                if(subTypeList != null && subTypeList.size() > 0 ){
                    typeList.addAll(subTypeList);
                }

            }
        }else{
            List<MallGoodsType> tempTypeList = typeRepository.findByParentStandardTypeIdAndDisabledFalseAndCustomerIdOrderByTOrderAsc("0",-1);
            typeList.addAll(tempTypeList);
        }
        return typeList;
    }

    @Override
    public List<MallGoodsType> getCustomerTypeList(Integer customerId) {
        List<MallGoodsType> customerType = typeRepository.findByCustomerIdOrderByTOrderAsc(customerId);
        return customerType;
    }
}
