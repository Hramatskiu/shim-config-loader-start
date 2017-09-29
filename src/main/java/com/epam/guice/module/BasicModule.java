package com.epam.guice.module;

import com.epam.guice.annotation.TestAnnotation;
import com.epam.guice.aspect.TestAspect;
import com.epam.guice.component.TestComponent;
import com.epam.guice.component.TestComponentInt;
import com.epam.guice.inteceptor.TestMethodInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.aspectj.lang.Aspects;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.matcher.Matchers.subclassesOf;

public class BasicModule extends AbstractModule {
    protected void configure() {
        /*bind(TestComponent.class)
                .toInstance(new TestComponent());*/
        bindInterceptor(annotatedWith(TestAnnotation.class),
                annotatedWith(TestAnnotation.class),
                new TestMethodInterceptor());
        //Aspects.aspectOf(TestAspect.class);
    }
}
