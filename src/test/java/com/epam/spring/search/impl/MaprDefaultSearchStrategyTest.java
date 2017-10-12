package com.epam.spring.search.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Collections;

public class MaprDefaultSearchStrategyTest {
  @Test
  public void resolveCommandResultWhenCommandResultIsEmptyStringShould() throws Exception {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    maprDefaultSearchStrategy.resolveCommandResult( StringUtils.EMPTY, Collections.emptyList() );
  }

}