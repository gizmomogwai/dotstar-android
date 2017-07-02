package com.flopcode.dotstar.android.parameters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.flopcode.dotstar.android.Application;


public abstract class Parameter {
  final String presetName;
  final String parameterName;
  private Listener mListener;

  public Parameter(String presetName, String parameterName) {
    this.presetName = presetName;
    this.parameterName = parameterName;
  }

  protected String getPresetName() {
    return presetName;
  }

  protected String getParameterName() {
    return parameterName;
  }

  protected String getValue() {
    return getProperty("value");
  }

  protected String getDefaultValue() {
    return getProperty("defaultValue");
  }

  protected String getProperty(String propertyName) {
    return Application.PARAMETERS.get(presetName, parameterName, propertyName);
  }

  public abstract View createView(LayoutInflater inflater, ViewGroup rootView, Context context);

  protected void onCreate(Listener listener) {
    this.mListener = listener;
    String value = getValue();
    listener.onChange(getParameterName(), value);
    Application.PARAMETERS.register(presetName, parameterName, mListener);
  }

  public void onDestroy() {
    Application.PARAMETERS.unregister(presetName, parameterName, mListener);
  }

  public void post(String value) {
    Application.PARAMETERS.post(presetName, parameterName, value);
  }

  public static void staticPost(String presetName, String parameterName, String value) {
    Application.PARAMETERS.post(presetName, parameterName, value);
  }
}
