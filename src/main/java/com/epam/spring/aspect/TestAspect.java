package com.epam.spring.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TestAspect {
    @Before("execution(* com.epam.spring.component.SpringComponent.sendMessage(..))")
    public void logBefore(JoinPoint joinPoint) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println("logBefore() is running!");
        System.out.println("hijacked : " + username);
        System.out.println("******");
    }

    @Pointcut
    public void test(){

    }
}
