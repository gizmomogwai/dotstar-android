package com.flopcode.dotstar.android.parameters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flopcode.dotstar.android.Application;
import com.flopcode.dotstar.android.ColorConvert;
import com.flopcode.dotstar.android.Index;
import com.flopcode.dotstar.android.R;
import com.google.common.collect.ImmutableMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

@SuppressWarnings("unused")
public class ColorParameter extends Parameter {
  public ColorParameter(String presetName, String parameterName) {
    super(presetName, parameterName);
  }

  @Override
  public View createView(LayoutInflater inflater, ViewGroup rootView, final Context context) {
    final Button button = (Button) inflater.inflate(R.layout.preset_color_button, rootView, false);
    onCreate(new Listener() {
      @Override
      public void onChange(String name, String value) {
        button.setBackgroundColor(ColorConvert.convertColorFromServerToAndroid(value));
      }
    });

    button.setText(getParameterName());
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showColorPicker();
      }

      private void showColorPicker() {
        ColorPickerDialogBuilder
          .with(context)
          .setTitle("Choose color for " + getParameterName())
          .initialColor(ColorConvert
            .convertColorFromServerToAndroid(getValue()))
          .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
          .showAlphaSlider(false)
          .density(30)
          .setOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(final int selectedColor) {
              final String serverColor = ColorConvert.convertColorFromAndroid2String(selectedColor);
              Call<Void> call = getDotStar(getConnectionPrefs(context)).set(ImmutableMap.of(getParameterName(), serverColor));
              call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                  Log.i(Index.LOG_TAG, "could set color for '" + getParameterName() + "'");
                  post(serverColor);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                  Log.e(Index.LOG_TAG, "could not set color for '" + getParameterName() + "'", t);
                }
              });
            }

          })
          .setPositiveButton("ok", new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
              System.out.println("ok clicked");
            }
          })
          .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              System.out.println("cancel clicked");
            }
          })
          .build()
          .show();
      }
    });
    return button;
  }
}
