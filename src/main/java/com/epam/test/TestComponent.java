package com.epam.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestComponent {
  private ITestComponent testComponent1;

  @PostConstruct
  public void doNothing() {
    testComponent1.doNothing();
  }

  @Autowired
  public void setTestComponent1( ITestComponent testComponent1 ) {
    this.testComponent1 = testComponent1;
  }
}
