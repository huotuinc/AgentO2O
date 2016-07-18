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

import com.hot.datacenter.entity.client.User;
import com.hot.datacenter.entity.customer.AgentShop;
import com.hot.datacenter.entity.customer.MallAgent;
import com.hot.datacenter.ienum.AgentStatusEnum;
import com.hot.datacenter.repository.client.UserRepository;
import com.hot.datacenter.repository.customer.AgentShopRepository;
import com.hot.datacenter.repository.customer.MallCustomerRepository;
import com.hot.datacenter.search.AgentShopSearch;
import com.hot.datacenter.service.AbstractCrudService;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.service.author.AgentShopService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "shopService")
public class AgentShopServiceImpl extends AbstractCrudService<AgentShop, Integer, AgentShopSearch> implements AgentShopService {
    @Autowired
    private AgentShopRepository shopRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AgentShop findByUserName(String userName) {
        return shopRepository.findByUsername(userName);
    }

    @Override
    public List<AgentShop> findByAgentId(Integer agentId) {
        return shopRepository.findByAgent_Id(agentId);
    }

    @Override
    @Transactional
    @SuppressWarnings("Duplicates")
    public ApiResult saveOrUpdateShop(AgentShop shop, String hotUserName, MallAgent agent) {
        if (agent == null || agent.getCustomerId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //小伙伴账号绑定限制
        User user = null;
        if (StringUtil.isNotEmpty(hotUserName)) {
            user = userRepository.findByLoginNameAndCustomerId(hotUserName,
                    shop.getCustomerId());
            if (user == null) {
                return new ApiResult("小伙伴账号不存在", 400);
            }

            AgentShop shopUser = shopRepository.findByUser_userId(user.getUserId());
            if (shopUser != null && !shopUser.getId().equals(shop.getId())) {
                return new ApiResult("小伙伴账号已被绑定", 400);
            }
        }
        shop.setUser(user);

        if (shop.getId() == null) { //新增
            //判断门店登录名是否唯一
            AgentShop checkShop = findByUserName(shop.getUsername());
            if (checkShop != null) {
                return ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
            }
            shop.setCreateTime(new Date());
            shop.setPassword(passwordEncoder.encode(shop.getPassword()));
            shop.setAgent(agent);
            shop.setCustomerId(agent.getCustomerId());
        } else {  //编辑
            AgentShop oldShop = shopRepository.findOne(shop.getId());
            if (oldShop.isDisabled()) {
                return new ApiResult("该门店已被冻结");
            }
            if (oldShop.isDeleted()) {
                return new ApiResult("该门店已被刪除");
            }
            //当门店状态为待审核和审核通过时代理商不能修改
            if (oldShop.getStatus() == null || oldShop.getStatus().getCode() == AgentStatusEnum.CHECKING.getCode() || oldShop.getStatus().getCode() == AgentStatusEnum.CHECKED.getCode()) {
                return new ApiResult("不能修改");
            }
            if (shop.getStatus() != null) {
                oldShop.setStatus(shop.getStatus());
            }
            oldShop.setUsername(shop.getUsername());
            oldShop.setAddress_Area(shop.getAddress_Area());
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
            oldShop.setUser(shop.getUser());
            oldShop.setEmail(shop.getEmail());
            shop = oldShop;
        }
        shop = shopRepository.saveAndFlush(shop);
        //如果结算账户不存在，则新建结算账户
        Account account = accountRepository.findByShop_Id(shop.getId());
        if (account == null) {
            account = new Account();
            account.setShop(shop);
            accountRepository.save(account);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    @SuppressWarnings("Duplicates")
    public ApiResult saveShopConfig(ShopAuthor shop, String hotUserName) {
        ShopAuthor oldShop = shopRepository.findOne(shop.getId());
        if (oldShop.isDisabled()) {
            return new ApiResult("该门店已被冻结");
        }
        if (oldShop.isDeleted()) {
            return new ApiResult("该门店已被刪除");
        }
        //只有当门店状态为审核通过时，门店才能修改
        if (!(oldShop.getStatus() != null && oldShop.getStatus().getCode() == AgentStatusEnum.CHECKED.getCode())) {
            return new ApiResult("不能修改");
        }
        //小伙伴账号绑定限制
        UserBaseInfo userBaseInfo = null;
        if (StringUtil.isNotEmpty(hotUserName)) {
            userBaseInfo = userRepository.findByLoginNameAndMallCustomer_customerId(hotUserName,
                    oldShop.getCustomer().getCustomerId());
            if (userBaseInfo == null) {
                return new ApiResult("小伙伴账号不存在", 400);
            }
            ShopAuthor shopUser = shopRepository.findByUserBaseInfo_userId(userBaseInfo.getUserId());
            if (shopUser != null && !shopUser.getId().equals(shop.getId())) {
                return new ApiResult("小伙伴账号已被绑定", 400);
            }
        }
        oldShop.setUsername(shop.getUsername());
        oldShop.setAddress_Area(shop.getAddress_Area());
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
        oldShop.setServeiceTel(shop.getServeiceTel());
        oldShop.setAfterSalTel(shop.getAfterSalTel());
        oldShop.setAfterSalQQ(shop.getAfterSalQQ());
        oldShop.setUserBaseInfo(userBaseInfo);
        oldShop.setBankName(shop.getBankName());
        oldShop.setAccountName(shop.getAccountName());
        oldShop.setAccountNo(shop.getAccountNo());
        oldShop.setEmail(shop.getEmail());
        shopRepository.save(oldShop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public void flush() {
        shopRepository.flush();
    }

    @Override
    public ApiResult updateStatus(AgentStatusEnum status, int shopId, Agent agent) {
        ShopAuthor shop = findByIdAndParentAuthor(shopId, agent);
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
        ShopAuthor shop = shopRepository.findOne(id);
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
        ShopAuthor shop = findByIdAndParentAuthor(shopId, agent);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //当门店状态为待审核和审核通过时代理商不能删除
        if (shop.getStatus() == null || shop.getStatus().getCode() == AgentStatusEnum.CHECKING.getCode() || shop.getStatus().getCode() == AgentStatusEnum.CHECKED.getCode()) {
            return new ApiResult("不可删除");
        }
        shop.setDeleted(true);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult updateIsDisabledById(int id) {
        ShopAuthor shop = shopRepository.findOne(id);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        } else {
            shop.setDisabled(!shop.isDisabled());
            shopRepository.save(shop);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult updatePasswordById(String password, int shopId) {
        AgentShop shop = shopRepository.findOne(shopId);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (shop.isDisabled()) {
            return new ApiResult("该门店已被冻结");
        }
        password = passwordEncoder.encode(password);
        shop.setPassword(password);
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        ShopAuthor shop = findByUserName(userName);
        if (shop == null) {
            throw new UsernameNotFoundException("没有该门店");
        }
        return shop;
    }

    @Override
    public Page<AgentShop> findAll(int pageIndex, int pageSize, AgentShopSearch agentShopSearch) {
        return findAll(agentShopSearch, new PageRequest(pageIndex - 1, pageSize));
    }

    @Override
    public Specification<AgentShop> specification(AgentShopSearch agentShopSearch) {
        Specification<AgentShop> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //平台过滤
            if (agentShopSearch.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId").as(Integer.class), agentShopSearch.getCustomerId()));
            }

            //门店过滤条件
            if (!StringUtils.isEmpty(agentShopSearch.getName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + agentShopSearch.getName() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getProvinceCode())) {
                predicates.add(cb.like(root.get("provinceCode").as(String.class), "%" + agentShopSearch.getProvinceCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getCityCode())) {
                predicates.add(cb.like(root.get("cityCode").as(String.class), "%" + agentShopSearch.getCityCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getDistrictCode())) {
                predicates.add(cb.like(root.get("districtCode").as(String.class), "%" + agentShopSearch.getDistrictCode() + "%"));
            }
            if (agentShopSearch.getStatus() != -1) {
                predicates.add(cb.equal(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, agentShopSearch.getStatus())));
            }
            predicates.add(cb.equal(root.get("isDeleted").as(Boolean.class), false));

            //上级代理商过滤条件
            if (agentShopSearch.getParentAuthor() != null) {
                predicates.add(cb.equal(root.get("agent").as(Agent.class), agentShopSearch.getParentAuthor()));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getParent_name())) {
                predicates.add(cb.like(root.get("agent").get("name").as(String.class), "%" + agentShopSearch.getParent_name() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getParent_username())) {
                predicates.add(cb.like(root.get("agent").get("username").as(String.class), "%" + agentShopSearch.getParent_username() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getParent_provinceCode())) {
                predicates.add(cb.like(root.get("agent").get("provinceCode").as(String.class), "%" + agentShopSearch.getParent_provinceCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getParent_cityCode())) {
                predicates.add(cb.like(root.get("agent").get("cityCode").as(String.class), "%" + agentShopSearch.getParent_cityCode() + "%"));
            }
            if (!StringUtil.isEmptyStr(agentShopSearch.getParent_districtCode())) {
                predicates.add(cb.like(root.get("agent").get("districtCode").as(String.class), "%" + agentShopSearch.getParent_districtCode() + "%"));
            }

            //等级过滤
            if (agentShopSearch.getParent_agentLevel() != -1) {
                predicates.add(cb.equal(root.get("agent").get("agentLevel").get("levelId").as(Integer.class), agentShopSearch.getParent_agentLevel()));
            }

            //平台显示列表
            if ("list".equals(agentShopSearch.getType())) {//门店列表
                predicates.add(cb.notEqual(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, 0)));
            } else if ("audit".equals(agentShopSearch.getType())) {//门店审核
                predicates.add(cb.equal(root.get("status").as(AgentStatusEnum.class), EnumHelper.getEnumType(AgentStatusEnum.class, 1)));
                predicates.add(cb.equal(root.get("isDisabled").as(Boolean.class), false));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return specification;
    }

    @Override
    public List<AgentShop> findByCustomerId(Integer customerId) {
        return shopRepository.findByIsDeletedFalseAndIsDisabledFalseAndStatusAndCustomerId(AgentStatusEnum.CHECKED, customerId);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<ShopAuthor> shops) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        shops.forEach(shop -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(shop.getUsername()));
            cellDescList.add(ExcelHelper.asCell(shop.getName()));
            cellDescList.add(ExcelHelper.asCell(shop.getAddress_Area()));
            cellDescList.add(ExcelHelper.asCell(shop.getAddress()));
            cellDescList.add(ExcelHelper.asCell(shop.getLan()));
            cellDescList.add(ExcelHelper.asCell(shop.getLat()));
            cellDescList.add(ExcelHelper.asCell(shop.getContact()));
            cellDescList.add(ExcelHelper.asCell(shop.getMobile()));
            cellDescList.add(ExcelHelper.asCell(shop.getTelephone()));
            cellDescList.add(ExcelHelper.asCell(shop.getEmail()));
            cellDescList.add(ExcelHelper.asCell(shop.getParentAgent().getUsername()));
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
        names = userRepository.findByLoginNameLikeAndMallCustomer_customerId("%" + name + "%", customerId);
        return names;
    }

    @Override
    public ApiResult updateAccountInfo(Integer shopId, String bankName, String accountName, String accountNo) {
        AgentShop shop = findOne(shopId);
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
