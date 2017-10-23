package com.epam.test;

import org.springframework.stereotype.Component;

@Component
public class TestComponent1 implements ITestComponent {
  public void doNothing() {
    System.out.println( "Do nothing" );
  }
}
