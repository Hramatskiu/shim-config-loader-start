package com.epam.shim.configurator.util;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LocalProccessCommandExecutor {
  public static String executeCommand( String command ) {
    StringBuilder result = new StringBuilder( StringUtils.EMPTY );

    try {
      Process p = Runtime.getRuntime().exec( command );
      p.waitFor();
      BufferedReader reader = new BufferedReader(
        new InputStreamReader( p.getInputStream() )
      );
      String line;
      while ( ( line = reader.readLine() ) != null ) {
        result.append( line );
      }

    } catch ( IOException | InterruptedException e1 ) {
      e1.printStackTrace();
    }

    System.out.println( "Done" );

    return result.toString();
  }

}
