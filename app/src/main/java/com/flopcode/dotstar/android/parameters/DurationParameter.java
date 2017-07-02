package com.flopcode.dotstar.android.parameters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.flopcode.dotstar.android.Index;
import com.flopcode.dotstar.android.R;
import com.google.common.collect.ImmutableMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

@SuppressWarnings("unused")
public class DurationParameter extends Parameter {
  public DurationParameter(String presetName, String parameterName) {
    super(presetName, parameterName);
  }

  @Override
  public View createView(LayoutInflater inflater, final ViewGroup rootView, final Context context) {
    final Button button = (Button) inflater.inflate(R.layout.preset_color_button, rootView, false);
    onCreate(new Listener() {

      @Override
      public void onChange(String name, String value) {
        button.setText(toLabel(name, value));
      }
    });
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showDurationDialog();
      }

      private void showDurationDialog() {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View duration = inflater.inflate(R.layout.preset_duration, null);
        ((EditText) duration.findViewById(R.id.duration)).setText(getValue());
        new AlertDialog.Builder(context)
          .setMessage(String.format("enter duration for '%s', defaults to '%s'",
            getParameterName(),
            getDefaultValue()))
          .setTitle("Duration")
          .setView(duration)
          .setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              Dialog d = (Dialog) dialogInterface;
              EditText duration = (EditText) d.findViewById(R.id.duration);
              final String newValue = duration.getText().toString();
              getDotStar(getConnectionPrefs(context))
                .set(ImmutableMap.of(getParameterName(), newValue))
                .enqueue(new Callback<Void>() {
                  @Override
                  public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i(Index.LOG_TAG, "could set duration for '" + getParameterName() + "'");
                    post(newValue);
                  }

                  @Override
                  public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(Index.LOG_TAG, "could not set duration for '" + getParameterName() + "'", t);
                  }
                });
            }
          })
          .create().show();
      }
    });
    return button;
  }

  private String toLabel(String name, String value) {
    return String.format("%s: %ss", name, value);
  }
}
