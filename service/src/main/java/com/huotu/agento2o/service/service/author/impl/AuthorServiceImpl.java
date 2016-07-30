package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.service.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service("authorService")
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;

    public Author findById(Author requestAuthor) {
        return mallCustomerRepository.findOne(requestAuthor.getId());
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
