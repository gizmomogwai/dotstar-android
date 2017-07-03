package com.flopcode.dotstar.android.parameters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import com.flopcode.dotstar.android.Index;
import com.flopcode.dotstar.android.R;
import com.google.common.collect.ImmutableMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

;

@SuppressWarnings("unused")
public class RangeParameter extends Parameter {
  public RangeParameter(String presetName, String parameterName) {
    super(presetName, parameterName);
  }

  private String calculateTitle() {
    return calculateTitle(getParameterName(), Float.parseFloat(getValue()), getMin(), getMax());
  }

  private String calculateTitle(String parameterName, float value, float min, float max) {
    return "Range " + parameterName + "(" + String.format("%.1f", value) + "/" + min + " - " + max + ")";
  }

  private float getMin() {
    return Float.parseFloat(getProperty("min"));
  }

  private float getMax() {
    return Float.parseFloat(getProperty("max"));
  }

  @Override
  public View createView(LayoutInflater inflater, ViewGroup rootView, final Context context) {
    final Button res = (Button) inflater.inflate(R.layout.preset_color_button, rootView, false);
    onCreate(new Listener() {
      @Override
      public void onChange(String name, String value) {
        res.setText(calculateTitle());
      }
    });
    res.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        float min = getMin();
        float max = getMax();
        float value = Float.parseFloat(getValue());
        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax((int) ((max - min) * 10));
        seekBar.setProgress((int) ((value - min) * 10));
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
          .setTitle(calculateTitle())
          .setView(seekBar)
          .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
          })
          .create();
        alertDialog.show();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            final float value = (i / 10.0f) + getMin();
            alertDialog.setTitle(calculateTitle());
            Call<Void> call = getDotStar(getConnectionPrefs(context)).set(ImmutableMap.of(getParameterName(), String.format("%.1f", value)));
            call.enqueue(new Callback<Void>() {
              @Override
              public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(Index.LOG_TAG, "could set range for '" + getParameterName() + "'");
                post("" + value);
              }

              @Override
              public void onFailure(Call<Void> call, Throwable t) {
                Log.e(Index.LOG_TAG, "could not set range for '" + getParameterName() + "'", t);
              }
            });
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
          }
        });
      }

    });
    return res;
  }
}
