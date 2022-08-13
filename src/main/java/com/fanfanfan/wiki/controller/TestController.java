package com.fanfanfan.wiki.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello111";
    }
    @PostMapping("/hello/post")
    public String hepost(String name){
        return "helloworld"+name;
    }

}
