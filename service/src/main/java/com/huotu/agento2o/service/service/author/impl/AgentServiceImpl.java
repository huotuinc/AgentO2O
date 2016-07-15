package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.repository.user.UserBaseInfoRepository;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "agentService")
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MallPasswordEncoder passwordEncoder;

    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private MallCustomerService mallCustomerService;

    @Autowired
    private UserBaseInfoRepository userBaseInfoRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private Environment env;

    private Random random = new Random();


    @Override
    public Agent findById(Integer agentId, Integer customerId) {
        return agentId == null || customerId == null ? null : agentRepository.findByIdAndCustomer_customerId(agentId, customerId);
    }

    @Override
    public Agent findByAgentId(Integer agentId) {
        return agentId == null ? null : agentRepository.findOne(agentId);
    }

    @Override
    public void flush() {
        agentRepository.flush();
    }

    @Override
    public List<Agent> findByAgentLevelId(Integer levelId) {
        return levelId == null ? null : agentRepository.findByAgentLevel_levelIdAndIsDeletedFalse(levelId);
    }

    @Override
    public boolean isEnableAgent(String userName) {
        return mallCustomerService.findByUserName(userName) == null;
    }

    @Override
    public Page<Agent> getAgentList(Integer customerId, AgentSearcher agentSearcher) {
        Specification<Agent> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("customer").get("customerId").as(Integer.class), customerId));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), false));
            if (StringUtil.isNotEmpty(agentSearcher.getAgentLoginName())) {
                predicates.add(criteriaBuilder.like(root.get("mallCustomer").get("username").as(String.class), "%" + agentSearcher.getAgentLoginName() + "%"));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getAgentName())) {
                predicates.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + agentSearcher.getAgentName() + "%"));
            }
            if (agentSearcher.getAgentStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("isDisabled").as(Boolean.class), agentSearcher.getAgentStatus() == 1));
            }
            if (agentSearcher.getLevelId() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("agentLevel").get("levelId").as(Integer.class), agentSearcher.getLevelId()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getProvinceCode())) {
                predicates.add(criteriaBuilder.equal(root.get("provinceCode").as(String.class), agentSearcher.getProvinceCode()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getCityCode())) {
                predicates.add(criteriaBuilder.equal(root.get("cityCode").as(String.class), agentSearcher.getCityCode()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getDistrictCode())) {
                predicates.add(criteriaBuilder.equal(root.get("districtCode").as(String.class), agentSearcher.getDistrictCode()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getBeginTime())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), StringUtil.DateFormat(agentSearcher.getBeginTime(), StringUtil.TIME_PATTERN)));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getEndTime())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), StringUtil.DateFormat(agentSearcher.getEndTime(), StringUtil.TIME_PATTERN)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return agentRepository.findAll(specification, new PageRequest(agentSearcher.getPageNo() - 1, agentSearcher.getPageSize()));
    }

    @Override
    @Transactional
    public int deleteAgent(Integer agentId) {
        return agentRepository.deleteAgent(agentId);
    }

    @Override
    @Transactional
    public int freezeAgent(Integer agentId) {
        return agentRepository.updateDisabledStatus(agentId, true);
    }

    @Override
    @Transactional
    public int unfreezeAgent(Integer agentId) {
        return agentRepository.updateDisabledStatus(agentId, false);
    }

    @Override
    public List<Agent> findByParentAgentId(Integer agentId) {
        return agentRepository.findByParentAgent_Id(agentId);
    }

    @Override
    @Transactional
    public ApiResult addOrUpdate(Integer customerId, Integer agentLevelId, Integer parentAgentId, String hotUserName, Agent requestAgent) {
        MallCustomer customer = mallCustomerService.findByCustomerId(customerId);
        AgentLevel agentLevel = agentLevelService.findById(agentLevelId, customerId);
        Agent parentAgent = null;
        Agent agent = null;
        MallCustomer mallAgent = null;
        UserBaseInfo userBaseInfo = null;
        //必须保证平台方和等级存在才能保存代理商
        if (customerId == null || customer == null || agentLevel == null || requestAgent == null || requestAgent.getId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (parentAgentId != null && parentAgentId != -1) {
            parentAgent = findById(parentAgentId, customerId);
        }
        //小伙伴账号绑定限制
        if (StringUtil.isNotEmpty(hotUserName)) {
            userBaseInfo = userBaseInfoRepository.findByLoginNameAndMallCustomer_customerId(hotUserName, customerId);
            if (userBaseInfo == null) {
                return new ApiResult("小伙伴账号不存在");
            }
            Agent userAgent = agentRepository.findByUserBaseInfo_userId(userBaseInfo.getUserId());
            if (userAgent != null && userAgent.getId() != requestAgent.getId()) {
                return new ApiResult("小伙伴账号已被绑定");
            }
        }
        //根据代理商的id判断是增加还是修改，当大于0时是修改
        if (requestAgent.getId() > 0) {
            mallAgent = mallCustomerService.findByCustomerId(requestAgent.getId());
            //当代理商不存在、已删除情况下无法修改
            if (mallAgent == null || mallAgent.getAgent() == null || mallAgent.getAgent().getCustomer() == null || !customerId.equals(mallAgent.getAgent().getCustomer().getCustomerId()) || mallAgent.getAgent().isDeleted()) {
                return new ApiResult("该代理商已失效");
            }
            agent = mallAgent.getAgent();
        } else {
            //判断用户名是否可用
            if (!isEnableAgent(requestAgent.getUsername())) {
                return ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
            }
            agent = new Agent();
            agent.setCustomer(customer);
            agent.setCreateTime(new Date());
            agent.setUsername(requestAgent.getUsername());
            agent.setPassword(passwordEncoder.encode(requestAgent.getPassword()));
            agent.setStatus(AgentStatusEnum.CHECKED);
            agent.setDisabled(false);
            agent.setDeleted(false);
            mallAgent = newMallCustomer(agent);
            agent.setId(mallAgent.getCustomerId());
            mallAgent.setAgent(agent);
        }
        mallAgent.setNickName(agent.getName());
        agent.setAgentLevel(agentLevel);
        agent.setName(requestAgent.getName());
        agent.setComment(requestAgent.getComment());
        agent.setAddress(requestAgent.getAddress());
        agent.setContact(requestAgent.getContact());
        agent.setMobile(requestAgent.getMobile());
        agent.setParentAgent(parentAgent);
        agent.setUserBaseInfo(userBaseInfo);
        agent.setProvinceCode(requestAgent.getProvinceCode());
        agent.setCityCode(requestAgent.getCityCode());
        agent.setDistrictCode(requestAgent.getDistrictCode());
        agent.setAddress_Area(requestAgent.getAddress_Area());
        agent.setTelephone(requestAgent.getTelephone());
        agent.setEmail(requestAgent.getEmail());
        mallAgent = mallCustomerService.save(mallAgent);
        agent = mallAgent.getAgent();
        Account account = accountRepository.findByAgent_Id(agent.getId());
        if(account == null){
            account = new Account();
            account.setAgent(agent);
            accountRepository.save(account);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    private MallCustomer newMallCustomer(Agent agent) {
        String key = StringUtil.createRandomStr(6);
        Integer token = random.nextInt(900000) + 100000;
        String mainDomian = SysConstant.COOKIE_DOMAIN;
        String url = String.format("http://distribute.%s/index.aspx?key=%s&t=huotu", mainDomian, key);
        MallCustomer customer = new MallCustomer();
        customer.setUsername(agent.getUsername());
        customer.setPassword(passwordEncoder.encode(agent.getPassword()));
        customer.setNickName(agent.getName());
        customer.setIndustryType(0);
        customer.setUserActivate(1);
        customer.setRoleID(-2);
        customer.setBelongManagerID(3);
        customer.setEmail("");
        customer.setIsOld(1);
        customer.setDeveloperUrl(url);
        customer.setDeveloperToken(String.valueOf(token));
        customer.setType(1);
        customer.setScore(0.0);
        customer.setCityID(0);
        return mallCustomerService.save(customer);

    }

    @Override
    public HSSFWorkbook createWorkBook(List<Agent> agents) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        agents.forEach(agent -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getName())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getUsername())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getContact())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getMobile())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getTelephone())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getEmail())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getAddress_Area())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getAddress())));
            cellDescList.add(ExcelHelper.asCell(agent.getAgentLevel() == null ? "" : agent.getAgentLevel().getLevelName()));
            cellDescList.add(ExcelHelper.asCell(agent.isDisabled() ? "冻结" : "激活"));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getComment())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(StringUtil.DateFormat(agent.getCreateTime(), StringUtil.TIME_PATTERN))));

            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("代理商列表", SysConstant.AGENT_EXPORT_HEADER, rowAndCells);
    }

    @Override
    public Agent findByUserBaseInfoId(Integer userId) {
        return agentRepository.findByUserBaseInfo_userId(userId);
    }

    @Override
    @Transactional
    public ApiResult saveAgentConfig(Integer agentId, Agent requestAgent,String hotUserName) {
        if (agentId == null || requestAgent == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        MallCustomer mallAgent = mallCustomerService.findByCustomerId(agentId);
        //当代理商不存在、已删除、已冻结情况下无法修改
        if (mallAgent == null || mallAgent.getAgent() == null || mallAgent.getAgent().isDeleted() || mallAgent.getAgent().isDisabled()) {
            return new ApiResult("该账号已失效");
        }
        Agent agent = mallAgent.getAgent();

        UserBaseInfo userBaseInfo = null;
        //小伙伴账号绑定限制
        if (StringUtil.isNotEmpty(hotUserName)) {
            userBaseInfo = userBaseInfoRepository.findByLoginNameAndMallCustomer_customerId(hotUserName, agent.getCustomer().getCustomerId());
            if (userBaseInfo == null) {
                return new ApiResult("小伙伴账号不存在");
            }
            Agent userAgent = agentRepository.findByUserBaseInfo_userId(userBaseInfo.getUserId());
            if (userAgent != null && userAgent.getId() != requestAgent.getId()) {
                return new ApiResult("小伙伴账号已被绑定");
            }
        }
        agent.setUserBaseInfo(userBaseInfo);
        agent.setName(requestAgent.getName());
        agent.setComment(requestAgent.getComment());
        agent.setAddress(requestAgent.getAddress());
        agent.setContact(requestAgent.getContact());
        agent.setMobile(requestAgent.getMobile());
        agent.setProvinceCode(requestAgent.getProvinceCode());
        agent.setCityCode(requestAgent.getCityCode());
        agent.setDistrictCode(requestAgent.getDistrictCode());
        agent.setAddress_Area(requestAgent.getAddress_Area());
        agent.setTelephone(requestAgent.getTelephone());
        agent.setAccountName(requestAgent.getAccountName());
        agent.setAccountNo(requestAgent.getAccountNo());
        agent.setBankName(requestAgent.getBankName());
        agent.setEmail(requestAgent.getEmail());
        mallCustomerService.save(mallAgent);
        Account account = accountRepository.findByAgent_Id(agent.getId());
        if(account == null){
            account = new Account();
            account.setAgent(agent);
            accountRepository.save(account);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);

    }

    @Override
    public List<String> getHotUserNames(Integer customerId, String name) {
        List<String> names = new ArrayList<>();
        names = userBaseInfoRepository.findByLoginNameLikeAndMallCustomer_customerId("%" + name + "%", customerId);
        return names;
    }
}
