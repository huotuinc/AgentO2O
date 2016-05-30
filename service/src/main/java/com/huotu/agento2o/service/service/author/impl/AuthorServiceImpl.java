package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.repository.author.AuthorRepository;
import com.huotu.agento2o.service.service.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service("authorService")
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Author findById(Integer id) {
        return id == null ? null : authorRepository.findOne(id);
    }

    public List<Author> findByCustomerId(Integer customerId) {
        return authorRepository.findByCustomer_CustomerId(customerId);
    }

    @Override
    public List<Author> findByParentAgentId(Agent agent) {
        return authorRepository.findByParentAuthor(agent);
    }

    public Author addAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public boolean updatePwd(Integer id, String password) {
        Author author = authorRepository.findOne(id);
        if(author == null){
            return false;
        }
        author.setPassword(passwordEncoder.encode(password));
        authorRepository.save(author);
        return true;
    }

    @Override
    public boolean checkPwd(Integer id, String password) {
        Author author = authorRepository.findOne(id);
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
