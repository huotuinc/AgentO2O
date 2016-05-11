package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.service.author.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "agentService")
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Agent findById(Integer id) {
        return agentRepository.findOne(id);
    }

    /**
     * 根据登录名查找未删除且审核通过的代理商
     * @param userName
     * @return
     */
    @Override
    public Agent findByUserName(String userName) {
        return agentRepository.findByUsernameAndStatus(userName,AgentStatusEnum.CHECKED);
    }

    /**
     * 确保userName和password不能为空
     * @param agent
     * @return
     */
    @Override
    @Transactional
    public Agent addAgent(Agent agent) {
        //判断代理商登录名是否唯一
        Agent checkAgent = findByUserName(agent.getUsername());
        if(checkAgent != null){
            return null;
        }
        agent.setPassword(passwordEncoder.encode(agent.getPassword()));
        return agentRepository.save(agent);
    }

    @Override
    public void flush() {
        agentRepository.flush();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Agent agent = findByUserName(username);
        if(agent == null){
            throw new UsernameNotFoundException("没有该代理商");
        }
        return agent;
    }
}
