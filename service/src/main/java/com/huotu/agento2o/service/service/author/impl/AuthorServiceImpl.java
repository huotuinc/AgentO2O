package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
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
        return mallCustomerRepository.findOne(requestAuthor.getId());
    }

    @Override
    @Transactional
    public boolean updatePwd(Author requestAuthor, String password) {
        if (requestAuthor != null && Agent.class == requestAuthor.getType()) {
            MallCustomer author = mallCustomerRepository.findOne(requestAuthor.getId());
            if(author == null){
                return false;
            }
            author.setPassword(passwordEncoder.encode(password));
            mallCustomerRepository.save(author);
        } else if (requestAuthor != null && Shop.class == requestAuthor.getType()) {
            Shop author = shopRepository.findOne(requestAuthor.getId());
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
        Author author= mallCustomerRepository.findOne(requestAuthor.getId());
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
