package com.flopcode.dotstar.android;

/**
 * color from/to the server #123456
 * color in android int 0xff123456
 */
public class ColorConvert {
  /**
   * @param value color from the server is in the form #123456
   * @return int of the form 0xff123456
   */
  public static int convertColorFromServerToAndroid(String value) {
    return (int)Long.parseLong(String.format("FF%s", value.substring(1).toUpperCase()), 16);
  }

  public static String convertColorFromAndroid2String(int color) {
    return "#" + Integer.toHexString(color).substring(2);
  }

}
