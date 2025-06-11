package org.example.learn.spring.boot.arthas.hello.controller;

import org.example.learn.spring.boot.arthas.hello.model.User;
import org.example.learn.spring.boot.arthas.hello.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/findAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> findAllUsers() {
        logger.info("handling findAllUsers...");
        return userService.findAllUsers();
    }
}
