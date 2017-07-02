package com.flopcode.dotstar.android;

import android.util.Log;
import com.flopcode.dotstar.android.parameters.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flopcode.dotstar.android.Index.LOG_TAG;

public class StickyParameterEventBus {

  Map<String, List<Listener>> mParameterListeners = new HashMap<>();
  Map<String, Map<String, String>[]> mPresets = new HashMap<>();

  public void register(String presetName, String parameterName, Listener l) {
    String key = makeKey(presetName, parameterName);
    if (!mParameterListeners.containsKey(key)) {
      mParameterListeners.put(key, new ArrayList<Listener>());
    }
    mParameterListeners.get(key).add(l);
  }

  private String makeKey(String presetName, String parameterName) {
    return presetName + "/" + parameterName;
  }

  public Map<String, String>[] get(String presetName) {
    return mPresets.get(presetName);
  }

  public String getValue(String presetName, String parameterName) {
    return get(presetName, parameterName, "value");
  }
  public String getName(String presetName, String parameterName) {
    return get(presetName, parameterName, "name");
  }
  public String getType(String presetName, String parameterName) {
    return get(presetName, parameterName, "type");
  }

  public String get(String presetName, String parameterName, String propertyName) {
    Map<String, String>[] parameters = mPresets.get(presetName);
    for (Map<String, String> p : parameters) {
      if (parameterName.equals(p.get("name"))) {
        return p.get(propertyName);
      }
    }
    throw new IllegalArgumentException("could not find parameterName " + parameterName);
  }
  public void post(String presetName, String parameterName, String value) {
    Map<String, String>[] parameters = mPresets.get(presetName);
    for (Map<String, String> p : parameters) {
      if (parameterName.equals(p.get("name"))) {
        p.put("value", value);
      }
    }

    String key = makeKey(presetName, parameterName);
    List<Listener> h = mParameterListeners.get(key);
    if (h == null) {
      return;
    }
    for (Listener l : h) {
      l.onChange(parameterName, value);
    }
  }


  public void unregister(String presetName, String parameterName, Listener listener) {
    String key = makeKey(presetName, parameterName);
    if (mParameterListeners.containsKey(key)) {
      boolean h = mParameterListeners.get(key).remove(listener);
      Log.d(LOG_TAG, "removed a listener from '" + key + "' nr of listeners: " + mParameterListeners.size());
    } else {
      Log.e(LOG_TAG, "could not find listeners for " + key);
    }
  }

  public void set(List<Preset> presets) {
    mPresets.clear();
    for (Preset p : presets) {
      mPresets.put(p.name, p.parameters);
    }
  }
}
