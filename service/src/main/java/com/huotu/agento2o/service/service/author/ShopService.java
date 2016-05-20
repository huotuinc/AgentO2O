/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface ShopService extends UserDetailsService {

    Shop findByUserName(String userName);

    Shop findById(Integer id);

    Shop addShop(Shop shop);

    void flush();

    /**
     * 更新审核状态
     * @param Status
     * @param id
     */
    void updateStatus(AgentStatusEnum Status, int id);

    /**
     * 更新审核状态和备注
     * @param Status
     * @param id
     */
    void updateStatusAndComment(AgentStatusEnum Status, String comment, int id);

    void deleteById(int id);

    /**
     * 冻结解冻
     * @param isDisabled
     * @param id
     */
    void updateIsDisabledById(boolean isDisabled , int id);

    void updatePasswordById(String password , int id);

    Page<Shop> findAll(int pageIndex, int pageSize, ShopSearchCondition searchCondition);

    HSSFWorkbook createWorkBook(List<Shop> shops);

}
