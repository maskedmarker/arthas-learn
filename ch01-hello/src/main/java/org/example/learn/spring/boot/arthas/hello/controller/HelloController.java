package org.example.learn.spring.boot.arthas.hello.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(path = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String hello() {
        logger.info("handling hello...");
        return "hello world";
    }
}
