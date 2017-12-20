package com.epam.loader.common.util;

public class ByteCopierUtil {
  public static byte[] addBytesToArray( byte[] dest, byte[] addition, int additionCount ) {
    dest = normalizeArray( dest );
    addition = normalizeArray( addition );
    additionCount = normalizeAdditionCount( addition.length, additionCount );

    byte[] result = new byte[ dest.length + additionCount ];

    System.arraycopy( dest, 0, result, 0, dest.length );
    System.arraycopy( addition, 0, result, dest.length, additionCount );

    return result;
  }

  private static int normalizeAdditionCount( int additionArrayLength, int additionCount ) {
    return additionCount < 1 || additionCount > additionArrayLength ? additionArrayLength : additionCount;
  }

  private static byte[] normalizeArray( byte[] array ) {
    return array == null ? new byte[0] : array;
  }
}
