package com.flopcode.dotstar.android.parameters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.flopcode.dotstar.android.Index;
import com.flopcode.dotstar.android.R;
import com.google.common.collect.ImmutableMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

@SuppressWarnings("unused")
public class BoolParameter extends Parameter {

  public BoolParameter(String presetName, String parameterName) {
    super(presetName, parameterName);
  }

  @Override
  public View createView(LayoutInflater inflater, ViewGroup rootView, final Context context) {
    final CheckBox res = (CheckBox) inflater.inflate(R.layout.preset_checkbox, rootView, false);
    onCreate(new Listener() {
      @Override
      public void onChange(String name, String value) {
        res.setChecked(Boolean.parseBoolean(value));
      }
    });
    res.setText(getParameterName());
    res.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
        final String s = "" + b;
        Call<Void> call = getDotStar(getConnectionPrefs(context)).set(ImmutableMap.of(getParameterName(), s));
        call.enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            Log.i(Index.LOG_TAG, "could set bool for '" + getParameterName() + "'");
            post(s);
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            Log.e(Index.LOG_TAG, "could not set bool for '" + getParameterName() + "'", t);
          }
        });
      }
    });
    return res;
  }


}
