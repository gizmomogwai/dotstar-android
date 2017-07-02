package com.flopcode.dotstar.android;

public class Application extends android.app.Application {

  public static StickyParameterEventBus PARAMETERS;

  @Override
  public void onCreate() {
    super.onCreate();
    PARAMETERS = new StickyParameterEventBus();
  }

  @Override
  public void onTerminate() {
    PARAMETERS = null;
    super.onTerminate();
  }
}
