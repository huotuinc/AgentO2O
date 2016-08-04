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

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.repository.user.UserBaseInfoRepository;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.sql.Time;
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
    private UserBaseInfoRepository userBaseInfoRepository;
    //    @Autowired
//    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private MallCustomerService mallCustomerSerivce;
    @Autowired
    private AccountRepository accountRepository;


    // TODO: 2016/7/28 delete
    /*@Override
    public Shop findByUserName(String userName) {
        return shopRepository.findByUsername(userName);
    }*/

    @Override
    public Shop findById(Integer id) {
        return shopRepository.findOne(id);
    }

    @Override
    public Shop findByIdAndParentAuthor(Integer shopId, Agent agent) {
        return shopId == null || agent == null ? null : shopRepository.findByIdAndAgent(shopId, agent);
    }

    @Override
    public List<Shop> findByAgentId(Integer agentId) {
        return shopRepository.findByAgent_IdAndIsDeletedFalse(agentId);
    }

    @Override
    public Shop findByIdAndCustomer_Id(Integer shopId, Integer customer_Id) {
        MallCustomer customer = mallCustomerSerivce.findByCustomerId(customer_Id);
        return shopId == null || customer == null ? null : shopRepository.findByIdAndCustomer(shopId, customer);
    }

    // TODO: 2016/7/28
    @Override
    @Transactional
    @SuppressWarnings("Duplicates")
    public ApiResult saveOrUpdateShop(Shop shop, String hotUserName, Agent agent) {
        MallCustomer mallShop = null;
        Shop oldShop = null;
        if (agent == null || agent.getCustomer() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //小伙伴账号绑定限制
        UserBaseInfo userBaseInfo = null;
        if (StringUtil.isNotEmpty(hotUserName)) {
            userBaseInfo = userBaseInfoRepository.findByLoginNameAndMallCustomer_customerId(hotUserName,
                    agent.getCustomer().getCustomerId());
            if (userBaseInfo == null) {
                return new ApiResult("小伙伴账号不存在", 400);
            }

            Shop shopUser = shopRepository.findByUserBaseInfo_userIdAndIsDeletedFalse(userBaseInfo.getUserId());
            if (shopUser != null && !shopUser.getId().equals(shop.getId())) {
                return new ApiResult("小伙伴账号已被绑定", 400);
            }
        }
        shop.setUserBaseInfo(userBaseInfo);

        if (shop.getId() == null || shop.getId() == 0) { //新增
            //判断门店登录名是否唯一
            MallCustomer checkShop = mallCustomerSerivce.findByUserName(shop.getUsername());
            if (checkShop != null) {
                return ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
            }
            oldShop = new Shop();
            oldShop.setCreateTime(new Date());
            oldShop.setUsername(shop.getUsername());
            oldShop.setAgent(agent);
            oldShop.setCustomer(agent.getCustomer());
            //设置默认工作日和上下班时间
            oldShop.setWorkday(Constant.WORKDAY);
            oldShop.setOpenTime(Time.valueOf(Constant.OPEN_TIME));
            oldShop.setCloseTime(Time.valueOf(Constant.CLOSE_TIME));
            mallShop = mallCustomerSerivce.newCustomer(shop.getUsername(), shop.getPassword(), CustomerTypeEnum.AGENT_SHOP);
            oldShop.setId(mallShop.getCustomerId());
            mallShop.setShop(oldShop);
        } else {  //编辑
            mallShop = mallCustomerSerivce.findByCustomerId(shop.getId());
            if (mallShop == null || mallShop.getShop() == null || mallShop.getShop().isDisabled() || mallShop.getShop().isDeleted()) {
                return new ApiResult("该门店已失效");
            }
            oldShop = mallShop.getShop();
            //当门店状态为待审核和审核通过时代理商不能修改
            if (oldShop.getStatus() == null || oldShop.getStatus() == AgentStatusEnum.CHECKING || oldShop.getStatus() == AgentStatusEnum.CHECKED) {
                return new ApiResult("不能修改");
            }
        }
        if (shop.getStatus() != null) {
            oldShop.setStatus(shop.getStatus());
        }
        mallShop.setNickName(shop.getName());
        oldShop.setAddressArea(shop.getAddressArea());
        oldShop.setProvinceCode(shop.getProvinceCode());
        oldShop.setCityCode(shop.getCityCode());
        oldShop.setDistrictCode(shop.getDistrictCode());
        oldShop.setName(shop.getName());
        oldShop.setContact(shop.getContact());
        oldShop.setMobile(shop.getMobile());
        oldShop.setTelephone(shop.getTelephone());
        oldShop.setAddress(shop.getAddress());
        oldShop.setLan(shop.getLan());
        oldShop.setLat(shop.getLat());
        oldShop.setComment(shop.getComment());
        oldShop.setUserBaseInfo(shop.getUserBaseInfo());
        oldShop.setEmail(shop.getEmail());
        mallCustomerSerivce.save(mallShop);
        //如果结算账户不存在，则新建结算账户
        Account account = accountRepository.findByShop_Id(oldShop.getId());
        if (account == null) {
            account = new Account();
            account.setAuthor(mallShop);
            accountRepository.save(account);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    @SuppressWarnings("Duplicates")
    public ApiResult saveShopConfig(Shop shop, String hotUserName) {
        MallCustomer mallShop = mallCustomerSerivce.findByCustomerId(shop.getId());
        if (mallShop == null || mallShop.getShop() == null || mallShop.getShop().isDisabled() || mallShop.getShop().isDeleted()) {
            return new ApiResult("该门店已失效");
        }
        Shop oldShop = mallShop.getShop();
        //只有当门店状态为审核通过时，门店才能修改
        if (!(oldShop.getStatus() != null && oldShop.getStatus() == AgentStatusEnum.CHECKED)) {
            return new ApiResult("不能修改");
        }
        //小伙伴账号绑定限制
        UserBaseInfo userBaseInfo = null;
        if (StringUtil.isNotEmpty(hotUserName)) {
            userBaseInfo = userBaseInfoRepository.findByLoginNameAndMallCustomer_customerId(hotUserName,
                    oldShop.getCustomer().getCustomerId());
            if (userBaseInfo == null) {
                return new ApiResult("小伙伴账号不存在", 400);
            }
            Shop shopUser = shopRepository.findByUserBaseInfo_userIdAndIsDeletedFalse(userBaseInfo.getUserId());
            if (shopUser != null && !shopUser.getId().equals(shop.getId())) {
                return new ApiResult("小伙伴账号已被绑定", 400);
            }
        }
        mallShop.setNickName(shop.getName());
        oldShop.setAddressArea(shop.getAddressArea());
        oldShop.setProvinceCode(shop.getProvinceCode());
        oldShop.setCityCode(shop.getCityCode());
        oldShop.setDistrictCode(shop.getDistrictCode());
        oldShop.setName(shop.getName());
        oldShop.setContact(shop.getContact());
        oldShop.setMobile(shop.getMobile());
        oldShop.setTelephone(shop.getTelephone());
        oldShop.setAddress(shop.getAddress());
        oldShop.setLan(shop.getLan());
        oldShop.setLat(shop.getLat());
        oldShop.setComment(shop.getComment());
        oldShop.setServiceTel(shop.getServiceTel());
        oldShop.setAfterSalTel(shop.getAfterSalTel());
        oldShop.setAfterSalQQ(shop.getAfterSalQQ());
        oldShop.setUserBaseInfo(userBaseInfo);
        oldShop.setBankName(shop.getBankName());
        oldShop.setAccountName(shop.getAccountName());
        oldShop.setAccountNo(shop.getAccountNo());
        oldShop.setEmail(shop.getEmail());
        oldShop.setLogo(shop.getLogo());
        oldShop.setWorkday(shop.getWorkday());
        oldShop.setOpenTime(shop.getOpenTime());
        oldShop.setCloseTime(shop.getCloseTime());
        mallCustomerSerivce.save(mallShop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public void flush() {
        shopRepository.flush();
    }

    @Override
    public ApiResult updateStatus(AgentStatusEnum status, int shopId, Agent agent) {
        Shop shop = findByIdAndParentAuthor(shopId, agent);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (shop.isDeleted()) {
            return new ApiResult("该门店已被删除");
        }
        if (shop.isDisabled()) {
            return new ApiResult("该门店已被冻结");
        }
        shop.setStatus(status);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult updateStatusAndAuditComment(AgentStatusEnum status, String auditComment, int id) {
        Shop shop = shopRepository.findOne(id);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (shop.isDisabled()) {
            return new ApiResult("该门店已被冻结");
        }
        shop.setStatus(status);
        shop.setAuditComment(auditComment);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult deleteById(int shopId, Agent agent) {
        Shop shop = findByIdAndParentAuthor(shopId, agent);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //当门店状态为待审核和审核通过时代理商不能删除
        if (shop.getStatus() == null || shop.getStatus() == AgentStatusEnum.CHECKING || shop.getStatus() == AgentStatusEnum.CHECKED) {
            return new ApiResult("不可删除");
        }
        shop.setDeleted(true);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult updateIsDisabledById(int id) {
        Shop shop = shopRepository.findOne(id);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        } else {
            shop.setDisabled(!shop.isDisabled());
            shopRepository.save(shop);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }


    @Override
    public Page<Shop> findAll(int pageIndex, int pageSize, ShopSearchCondition searchCondition) {
        Specification<Shop> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //平台过滤
            if (searchCondition.getMallCustomer() != null) {
                predicates.add(cb.equal(root.get("customer").as(MallCustomer.class), searchCondition.getMallCustomer()));
            }

            //门店过滤条件
            if (!StringUtils.isEmpty(searchCondition.getName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + searchCondition.getName() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getProvinceCode())) {
                predicates.add(cb.like(root.get("provinceCode").as(String.class), "%" + searchCondition.getProvinceCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getCityCode())) {
                predicates.add(cb.like(root.get("cityCode").as(String.class), "%" + searchCondition.getCityCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getDistrictCode())) {
                predicates.add(cb.like(root.get("districtCode").as(String.class), "%" + searchCondition.getDistrictCode() + "%"));
            }
            if (searchCondition.getStatus() != -1) {
                predicates.add(cb.equal(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, searchCondition.getStatus())));
            }
            predicates.add(cb.equal(root.get("isDeleted").as(Boolean.class), false));

            //上级代理商过滤条件
            if (searchCondition.getParentAuthor() != null) {
                predicates.add(cb.equal(root.get("agent").as(Agent.class), searchCondition.getParentAuthor()));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_name())) {
                predicates.add(cb.like(root.get("agent").get("name").as(String.class), "%" + searchCondition.getParent_name() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_username())) {
                predicates.add(cb.like(root.get("agent").get("username").as(String.class), "%" + searchCondition.getParent_username() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_provinceCode())) {
                predicates.add(cb.like(root.get("agent").get("provinceCode").as(String.class), "%" + searchCondition.getParent_provinceCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_cityCode())) {
                predicates.add(cb.like(root.get("agent").get("cityCode").as(String.class), "%" + searchCondition.getParent_cityCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(searchCondition.getParent_districtCode())) {
                predicates.add(cb.like(root.get("agent").get("districtCode").as(String.class), "%" + searchCondition.getParent_districtCode() + "%"));
            }

            //等级过滤
            if (searchCondition.getParent_agentLevel() != -1) {
                predicates.add(cb.equal(root.get("agent").get("agentLevel").get("levelId").as(Integer.class), searchCondition.getParent_agentLevel()));
            }

            //平台显示列表
            if ("list".equals(searchCondition.getType())) {//门店列表
                predicates.add(cb.notEqual(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, 0)));
            } else if ("audit".equals(searchCondition.getType())) {//门店审核
                predicates.add(cb.equal(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, 1)));
                predicates.add(cb.equal(root.get("isDisabled").as(Boolean.class), false));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return shopRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));
    }

    @Override
    public List<Shop> findAll() {
        return shopRepository.findByIsDeletedFalseAndIsDisabledFalseAndStatus(AgentStatusEnum.CHECKED);
    }

    @Override
    public List<Shop> findByCustomerId(Integer customerId) {
        return shopRepository.findByIsDeletedFalseAndIsDisabledFalseAndStatusAndCustomer_CustomerId(AgentStatusEnum.CHECKED, customerId);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<Shop> shops) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        shops.forEach(shop -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(shop.getUsername()));
            cellDescList.add(ExcelHelper.asCell(shop.getName()));
            cellDescList.add(ExcelHelper.asCell(shop.getAddressArea()));
            cellDescList.add(ExcelHelper.asCell(shop.getAddress()));
            cellDescList.add(ExcelHelper.asCell(shop.getLan()));
            cellDescList.add(ExcelHelper.asCell(shop.getLat()));
            cellDescList.add(ExcelHelper.asCell(shop.getContact()));
            cellDescList.add(ExcelHelper.asCell(shop.getMobile()));
            cellDescList.add(ExcelHelper.asCell(shop.getTelephone()));
            cellDescList.add(ExcelHelper.asCell(shop.getEmail()));
            cellDescList.add(ExcelHelper.asCell(shop.getAgent().getUsername()));
//            cellDescList.add(ExcelHelper.asCell(shop.getServeiceTel()));
//            cellDescList.add(ExcelHelper.asCell(shop.getAfterSalTel()));
//            cellDescList.add(ExcelHelper.asCell(shop.getAfterSalQQ()));
            cellDescList.add(ExcelHelper.asCell(shop.getComment()));
            cellDescList.add(ExcelHelper.asCell(shop.getAuditComment() == null ? "" : shop.getAuditComment()));
            cellDescList.add(ExcelHelper.asCell(shop.getStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(shop.isDisabled() ? "冻结" : "激活"));
            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("门店列表", SysConstant.SHOP_EXPORT_HEADER, rowAndCells);
    }

    @Override
    public List<String> getHotUserNames(Integer customerId, String name) {
        List<String> names = new ArrayList<>();
        names = userBaseInfoRepository.findByLoginNameLikeAndMallCustomer_customerId("%" + name + "%", customerId);
        return names;
    }

    @Override
    public ApiResult updateAccountInfo(Integer shopId, String bankName, String accountName, String accountNo) {
        Shop shop = findById(shopId);
        if (shop == null || shop.isDeleted() || shop.isDisabled()) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        shop.setBankName(bankName);
        shop.setAccountName(accountName);
        shop.setAccountNo(accountNo);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
