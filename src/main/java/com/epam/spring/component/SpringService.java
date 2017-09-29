package com.epam.spring.component;

import org.springframework.stereotype.Component;

@Component
public class SpringService {
    public boolean sendMessage(String messsage){
        System.out.println(messsage);

        return true;
    }
}
