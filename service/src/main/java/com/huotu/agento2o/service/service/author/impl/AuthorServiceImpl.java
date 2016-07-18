package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.service.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service("authorService")
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;

    public Author findById(Author requestAuthor) {
        if (requestAuthor != null && Agent.class == requestAuthor.getType()) {
            return mallCustomerRepository.findOne(requestAuthor.getId());
        } else if (requestAuthor != null && ShopAuthor.class == requestAuthor.getType()) {
            return shopRepository.findOne(requestAuthor.getId());
        }else{
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updatePwd(Author requestAuthor, String password) {
        if (requestAuthor != null && Agent.class == requestAuthor.getType()) {
            CustomerAuthor author = mallCustomerRepository.findOne(requestAuthor.getId());
            if(author == null){
                return false;
            }
            author.setPassword(passwordEncoder.encode(password));
            mallCustomerRepository.save(author);
        } else if (requestAuthor != null && ShopAuthor.class == requestAuthor.getType()) {
            ShopAuthor author = shopRepository.findOne(requestAuthor.getId());
            if(author == null){
                return false;
            }
            author.setPassword(passwordEncoder.encode(password));
            shopRepository.save(author);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPwd(Author requestAuthor, String password) {
        Author author = null;
        if (requestAuthor != null && Agent.class == requestAuthor.getType()) {
            author = mallCustomerRepository.findOne(requestAuthor.getId());
        } else if (requestAuthor != null && ShopAuthor.class == requestAuthor.getType()) {
            author = shopRepository.findOne(requestAuthor.getId());
        }
        if(author == null){
            return false;
        }
        if(passwordEncoder.matches(password,author.getPassword())){
            return true;
        }else{
            return false;
        }
    }
}
