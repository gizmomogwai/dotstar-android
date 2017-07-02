package com.flopcode.dotstar.android;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorConvertTest {
  @Test
  public void convertColorFromServerToAndroid() throws Exception {
    assertThat(ColorConvert.convertColorFromServerToAndroid("#123456")).isEqualTo(0xff123456);
    assertThat(ColorConvert.convertColorFromServerToAndroid("#ffffff")).isEqualTo(0xffffffff);
  }
}
