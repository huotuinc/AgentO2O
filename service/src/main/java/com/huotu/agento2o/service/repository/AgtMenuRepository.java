/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository;

import com.huotu.agento2o.service.entity.AgtMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/10.
 */
@Repository
public interface AgtMenuRepository extends JpaRepository<AgtMenu,String>{
    @Query("select menu from AgtMenu menu where menu.parent is null and menu.isDisabled=0 order by menu.sortNum")
    List<AgtMenu> findPrimary();

    @Query("select menu from AgtMenu menu where menu.parent is null order by menu.sortNum")
    List<AgtMenu> findAllPrimary();

    @Query("delete from AgtMenu where parent.menuId=?1")
    @Modifying
    void deleteByParentId(String parentId);

    List<AgtMenu> findByParent_MenuIdAndIsDisabled(String menuId, int isDisabled);
}
