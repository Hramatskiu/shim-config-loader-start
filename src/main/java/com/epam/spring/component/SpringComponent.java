package com.epam.spring.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
//@Scope("singleton")
public class SpringComponent {
    @Autowired
    private SpringService springService;

    public boolean sendMessage(String message, boolean bool) {
        return springService.sendMessage(message);
    }

    public void testing(String string){
        System.out.println(string);
    }
}
