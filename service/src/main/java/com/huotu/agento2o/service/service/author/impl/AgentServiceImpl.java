package com.huotu.agento2o.service.service.author.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.repository.level.AgentLevelRepository;
import com.huotu.agento2o.service.repository.user.UserBaseInfoRepository;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "agentService")
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private MallCustomerService mallCustomerService;

    @Autowired
    private UserBaseInfoRepository userBaseInfoRepository;


    @Override
    public Agent findById(Integer agentId, Integer customerId) {
        return agentId == null || customerId == null ? null : agentRepository.findByIdAndCustomer_customerId(agentId, customerId);
    }

    @Override
    public Agent findByAgentId(Integer agentId) {
        return agentId == null ? null : agentRepository.findOne(agentId);
    }

    @Override
    public Agent findByUserName(String userName) {
        return agentRepository.findByUsernameAndStatus(userName, AgentStatusEnum.CHECKED);
    }

    /**
     * 确保userName和password不能为空
     *
     * @param agent
     * @return
     */
    @Override
    @Transactional
    public Agent addAgent(Agent agent) {
        //判断代理商登录名是否唯一
        if (agent != null && isEnableAgent(agent.getUsername())) {
            agent.setPassword(passwordEncoder.encode(agent.getPassword()));
            agent.setCreateTime(new Date());
            return agentRepository.save(agent);
        }
        return null;
    }

    @Override
    public void flush() {
        agentRepository.flush();
    }

    @Override
    public List<Agent> findByAgentLevelId(Integer levelId) {
        return agentRepository.findByAgentLevel_levelIdAndIsDeletedFalse(levelId);
    }

    @Override
    public boolean isEnableAgent(String userName) {
        return agentRepository.findByUsernameAndIsDeletedFalse(userName) == null;
    }

    @Override
    public Page<Agent> getAgentList(Integer customerId, AgentSearcher agentSearcher) {
        Specification<Agent> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("customer").get("customerId").as(Integer.class), customerId));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), false));
            if (StringUtil.isNotEmpty(agentSearcher.getAgentLoginName())) {
                predicates.add(criteriaBuilder.like(root.get("username").as(String.class), "%" + agentSearcher.getAgentLoginName() + "%"));
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
            if (StringUtil.isNotEmpty(agentSearcher.getProvince())) {
                predicates.add(criteriaBuilder.equal(root.get("province").as(String.class), agentSearcher.getProvince()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getCity())) {
                predicates.add(criteriaBuilder.equal(root.get("city").as(String.class), agentSearcher.getCity()));
            }
            if (StringUtil.isNotEmpty(agentSearcher.getDistrict())) {
                predicates.add(criteriaBuilder.equal(root.get("district").as(String.class), agentSearcher.getDistrict()));
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
        return agentRepository.findByParentAuthor_id(agentId);
    }

    @Override
    @Transactional
    public ApiResult addOrUpdate(Integer customerId, Integer agentLevelId, Integer parentAgentId, String hotUserName, Agent requestAgent) {
        MallCustomer customer = mallCustomerService.findByCustomerId(customerId);
        AgentLevel agentLevel = agentLevelService.findById(agentLevelId, customerId);
        Agent parentAgent = null;
        Agent agent = null;
        UserBaseInfo userBaseInfo = null;
        //必须保证平台方和等级存在才能保存代理商
        if (customer == null || agentLevel == null || requestAgent == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (parentAgentId != -1) {
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
            agent = findById(requestAgent.getId(), customerId);
            //当代理商不存在、已删除情况下无法修改
            if (agent == null || agent.isDeleted()) {
                return new ApiResult("该代理商已失效");
            }
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
        }
        agent.setAgentLevel(agentLevel);
        agent.setName(requestAgent.getName());
        agent.setComment(requestAgent.getComment());
        agent.setAddress(requestAgent.getAddress());
        agent.setCity(requestAgent.getCity());
        agent.setContact(requestAgent.getContact());
        agent.setDistrict(requestAgent.getDistrict());
        agent.setMobile(requestAgent.getMobile());
        agent.setParentAuthor(parentAgent);
        agent.setUserBaseInfo(userBaseInfo);
        agent.setProvince(requestAgent.getProvince());
        agent.setTelephone(requestAgent.getTelephone());
        agent.setEmail(requestAgent.getEmail());
        agentRepository.save(agent);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
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
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getProvince()) + ' ' + StringUtil.getNullStr(agent.getCity()) + ' ' + StringUtil.getNullStr(agent.getDistrict())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(agent.getAddress())));
            cellDescList.add(ExcelHelper.asCell(agent.getAgentLevel() == null ? "" : agent.getAgentLevel().getLevelName()));
            cellDescList.add(ExcelHelper.asCell(agent.isDisabled() ? "冻结" : "激活"));
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
    public int resetPassword(Integer agentId, String password) {
        return agentRepository.resetPassword(agentId, passwordEncoder.encode((password)));
    }

    @Override
    @Transactional
    public ApiResult saveAgentConfig(Integer agentId, Agent requestAgent) {
        if (agentId == null || requestAgent == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        Agent agent = findByAgentId(agentId);
        //当代理商不存在、已删除、已冻结情况下无法修改
        if (agent == null || agent.isDeleted() || agent.isDisabled()) {
            return new ApiResult("该账号已失效");
        }
        agent.setName(requestAgent.getName());
        agent.setComment(requestAgent.getComment());
        agent.setAddress(requestAgent.getAddress());
        agent.setCity(requestAgent.getCity());
        agent.setContact(requestAgent.getContact());
        agent.setDistrict(requestAgent.getDistrict());
        agent.setMobile(requestAgent.getMobile());
        agent.setProvince(requestAgent.getProvince());
        agent.setTelephone(requestAgent.getTelephone());
        agent.setAccountName(requestAgent.getAccountName());
        agent.setAccountNo(requestAgent.getAccountNo());
        agent.setBankName(requestAgent.getBankName());
        agent.setEmail(requestAgent.getEmail());
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);

    }

    @Override
    public List<String> getHotUserNames(Integer customerId, String name) {
        List<String> names = new ArrayList<>();
        names = userBaseInfoRepository.findByLoginNameLikeAndMallCustomer_customerId("%" + name + "%", customerId);
        return names;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Agent agent = findByUserName(username);
        if (agent == null) {
            throw new UsernameNotFoundException("没有该代理商");
        }
        return agent;
    }
}
