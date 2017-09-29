package com.epam.guice.inteceptor;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.logging.Logger;

public class TestMethodInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object[] objectArray = methodInvocation.getArguments();
        System.out.println("ssd");
        for (Object object : objectArray) {
            if (object instanceof String){
                System.out.println(object.toString());
            }
            else {
                System.out.println("something else");
            }

            //logger.info("Sending message: " + object.toString());
        }
        return methodInvocation.proceed();
    }
}
