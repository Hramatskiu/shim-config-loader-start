package com.epam.guice.component;

import com.epam.guice.annotation.TestAnnotation;
import com.google.inject.Inject;

import java.util.logging.Logger;

public class TestComponent4 {
    @Inject
    private Logger logger;

    @Inject
    private TestService testService;

    @TestAnnotation
    public boolean sendMessage(String message, boolean bool) {
        return testService.sendMessage(message);
    }

    public void testing(String string){
        System.out.println(string);
    }
}
