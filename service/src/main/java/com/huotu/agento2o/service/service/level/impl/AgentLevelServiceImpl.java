package com.huotu.agento2o.service.service.level.impl;

import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.level.AgentLevelRepository;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by WangJie on 2016/5/11.
 */
@Service("agentLevelService")
public class AgentLevelServiceImpl implements AgentLevelService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @Autowired
    private MallCustomerRepository customerRepository;

    @Override
    public List<AgentLevel> findByCustomertId(Integer customerId) {
        return agentLevelRepository.findByCustomer_customerIdOrderByLevel(customerId);
    }

    @Override
    public AgentLevel addAgentLevel(AgentLevel agentLevel) {
        return agentLevelRepository.save(agentLevel);
    }

    @Override
    public void deleteAgentLevel(Integer id) {
        agentLevelRepository.delete(id);
    }

    @Override
    public AgentLevel findById(Integer id) {
        return agentLevelRepository.findOne(id);
    }

    @Override
    public void flush() {
        agentLevelRepository.flush();
    }

    @Override
    public Integer findLastLevel(Integer customerId) {
        return agentLevelRepository.findLastLevel(customerId);
    }

    @Override
    @Transactional
    public boolean addOrUpdate(Integer levelId, Integer customerId, AgentLevel agentLevel) {
        AgentLevel newAgentLevel;
        if (levelId > 0) {
            newAgentLevel = findById(levelId);
        } else {
            Integer level = findLastLevel(customerId);
            newAgentLevel = new AgentLevel();
            MallCustomer customer = customerRepository.findOne(customerId);
            if (customer == null) {
                return false;
            }
            newAgentLevel.setCustomer(customer);
            //等级依次递增，初始值为0
            newAgentLevel.setLevel(level == null ? 0 : level + 1);
        }
        newAgentLevel.setLevelName(agentLevel.getLevelName());
        newAgentLevel.setComment(agentLevel.getComment());
        addAgentLevel(newAgentLevel);
        return true;
    }
}
