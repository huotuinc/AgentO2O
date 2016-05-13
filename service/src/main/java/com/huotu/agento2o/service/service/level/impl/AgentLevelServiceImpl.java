package com.huotu.agento2o.service.service.level.impl;

import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.repository.level.AgentLevelRepository;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by WangJie on 2016/5/11.
 */
@Service("agentLevelService")
public class AgentLevelServiceImpl implements AgentLevelService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

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
}
