package com.epam.spring.util;

import java.util.Arrays;
import java.util.Objects;

public class CheckingParamsUtil {
  public static boolean checkParamsWithNullAndEmpty( String... params ) {
    return params != null && Arrays.stream( params ).allMatch( param -> Objects.nonNull( param ) && !param.isEmpty() );
  }

  public static boolean checkParamsWithNull( String... params ) {
    return params != null && Arrays.stream( params ).allMatch( Objects::nonNull );
  }
}
