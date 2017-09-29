package com.epam.guice;

import com.epam.guice.aspect.TestAspect;
import com.epam.guice.component.TestComponent;
import com.epam.guice.component.TestComponent2;
import com.epam.guice.component.TestComponent3;
import com.epam.guice.component.TestComponent4;
import com.epam.guice.module.BasicModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoogleGuiceTaskApp {
    public static void main(String args[]) {
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        long start = date.getTime();
        System.out.println(date.getTime());

        Injector injector = Guice.createInjector(new BasicModule());
        date = new Date();
        System.out.println(start - date.getTime());
        TestAspect testAspect = injector.getInstance(TestAspect.class);
        TestComponent testComponent = injector.getInstance(TestComponent.class);
        TestComponent2 testComponent2 = injector.getInstance(TestComponent2.class);
        date = new Date();
        System.out.println(start - date.getTime());
        TestComponent3 testComponent3 = injector.getInstance(TestComponent3.class);
        TestComponent4 testComponent4 = injector.getInstance(TestComponent4.class);
        date = new Date();
        System.out.println(start - date.getTime());
        testComponent = injector.getInstance(TestComponent.class);
        testComponent = injector.getInstance(TestComponent.class);
        testComponent = injector.getInstance(TestComponent.class);testComponent = injector.getInstance(TestComponent.class);
        testComponent = injector.getInstance(TestComponent.class);
        testComponent = injector.getInstance(TestComponent.class);


        testComponent.sendMessage("haha", true);
        testComponent.testing("gg");
        testComponent.sendMessage("haha", true);
        testComponent.testing("gg");

        testComponent2.sendMessage("haha", true);
        testComponent2.testing("gg");

        testComponent3.sendMessage("haha", true);
        testComponent3.testing("gg");

        testComponent4.sendMessage("haha", true);
        testComponent4.testing("gg");

        //dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
        System.out.println( start - date.getTime());
    }
}
