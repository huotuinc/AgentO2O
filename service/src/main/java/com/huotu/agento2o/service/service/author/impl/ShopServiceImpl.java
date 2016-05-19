/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.author.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.ShopService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "shopService")
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据登录名查找未删除的门店
     * @param userName
     * @return
     */
    @Override
    public Shop findByUserName(String userName) {
//        return shopRepository.findByUsernameAndStatus(userName, AgentStatusEnum.CHECKED);
        return shopRepository.findByUsername(userName);
    }

    @Override
    public Shop findById(Integer id) {
        return shopRepository.findOne(id);
    }

    @Override
    @Transactional
    public Shop addShop(Shop shop) {
        if(shop.getId()==null){ //新增保存
            //判断门店登录名是否唯一
            Shop checkShop = findByUserName(shop.getUsername());
            if(checkShop != null){
                return null;
            }
            shop.setStatus(AgentStatusEnum.NOT_CHECK);
            shop.setPassword(passwordEncoder.encode(shop.getPassword()));
        }else{  //编辑保存 不能修改密码
            Shop oldShop = shopRepository.findOne(shop.getId());
            oldShop.setUsername(shop.getUsername());
            oldShop.setProvince(shop.getProvince());
            oldShop.setCity(shop.getCity());
            oldShop.setDistrict(shop.getDistrict());
            oldShop.setName(shop.getName());
            oldShop.setContact(shop.getContact());
            oldShop.setMobile(shop.getMobile());
            oldShop.setTelephone(shop.getTelephone());
            oldShop.setAddress(shop.getAddress());
            oldShop.setLan(shop.getLan());
            oldShop.setLat(shop.getLat());
            oldShop.setComment(shop.getComment());
            oldShop.setServeiceTel(shop.getServeiceTel());
            oldShop.setAfterSalTel(shop.getAfterSalTel());
            oldShop.setAfterSalQQ(shop.getAfterSalQQ());
            oldShop.setAfterSalQQ(shop.getAfterSalQQ());
            shop = oldShop;
        }
        return shopRepository.save(shop);
    }

    @Override
    public void flush() {
        shopRepository.flush();
    }

    @Override
    public List<Shop> findByParentAuthor(Author author) {
        List<Shop> shops = shopRepository.findByParentAuthorAndIsDeleted(author,false);
        return shops;
    }

    @Override
    @Transactional
    public void updateStatus(AgentStatusEnum Status, int id) {
        shopRepository.updateStatus(Status,id);
    }

    @Override
    @Transactional
    public void updateStatusAndComment(AgentStatusEnum Status, String comment, int id) {
        shopRepository.updateStatusAndComment(Status, comment, id);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        shopRepository.updateIsDeleted(id);
    }

    @Override
    @Transactional
    public void updateIsDisabledById(boolean isDisabled ,int id) {
        shopRepository.updateIsDisabled(isDisabled,id);
    }

    @Override
    @Transactional
    public void updatePasswordById(String password, int id) {
        password = passwordEncoder.encode(password);
        shopRepository.updatePassword(password,id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Shop shop = findByUserName(username);
        if(shop == null){
            throw new UsernameNotFoundException("没有该门店");
        }
        return shop;
    }

    public Page<Shop> findAll(int pageIndex, int pageSize, ShopSearchCondition searchCondition){
        Specification<Shop> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //门店过滤条件
            if (!StringUtils.isEmpty(searchCondition.getName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + searchCondition.getName() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getProvince())) {
                predicates.add(cb.like(root.get("province").as(String.class), "%" + searchCondition.getProvince() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getCity())) {
                predicates.add(cb.like(root.get("city").as(String.class), "%" + searchCondition.getCity() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getDistrict())) {
                predicates.add(cb.like(root.get("district").as(String.class), "%" + searchCondition.getDistrict() + "%"));
            }
            if (searchCondition.getStatus() != -1) {
                predicates.add(cb.equal(root.get("status").as(AgentStatusEnum.class),EnumHelper.getEnumType(AgentStatusEnum.class,searchCondition.getStatus())));
            }

            //上级代理商过滤条件
            if(searchCondition.getParentAuthor()!=null){
                predicates.add(cb.equal(root.get("parentAuthor").as(Author.class), searchCondition.getParentAuthor()));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_name())) {
                predicates.add(cb.like(root.get("parentAuthor").get("name").as(String.class), "%" + searchCondition.getParent_name() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_province())) {
                predicates.add(cb.like(root.get("parentAuthor").get("province").as(String.class), "%" + searchCondition.getParent_province() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_city())) {
                predicates.add(cb.like(root.get("parentAuthor").get("city").as(String.class), "%" + searchCondition.getParent_city() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_district())) {
                predicates.add(cb.like(root.get("parentAuthor").get("district").as(String.class), "%" + searchCondition.getParent_district() + "%"));
            }

            //等级过滤
            // TODO: 2016/5/18
            if (searchCondition.getParent_agentLevel()!=-1) {
//                predicates.add(cb.equal(root.get("parentAuthor").get("agentLevel").get("levelId").as(Integer.class),  searchCondition.getParent_agentLevel() ));
            }

            // TODO: 2016/5/18 未提交状态显示条件
//            predicates.add(cb.notEqual(root.get("status").as(AgentStatusEnum.class),EnumHelper.getEnumType(AgentStatusEnum.class,0)));
            predicates.add(cb.equal(root.get("isDeleted").as(Boolean.class),false));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return shopRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));
    }

    @Override
    public HSSFWorkbook createWorkBook(List<Shop> shops) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        shops.forEach(shop -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();

            cellDescList.add(ExcelHelper.asCell(shop.getName()));
            cellDescList.add(ExcelHelper.asCell(shop.getProvince()+'—'+shop.getCity()+'—'+shop.getDistrict()));
            cellDescList.add(ExcelHelper.asCell(shop.getUsername()));
            cellDescList.add(ExcelHelper.asCell(shop.getContact()));
            cellDescList.add(ExcelHelper.asCell(shop.getMobile()));
            cellDescList.add(ExcelHelper.asCell(shop.getStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(shop.isDisabled() ? "冻结": "激活" ));
            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("门店列表", SysConstant.SHOP_EXPORT_HEADER, rowAndCells);
    }
}
