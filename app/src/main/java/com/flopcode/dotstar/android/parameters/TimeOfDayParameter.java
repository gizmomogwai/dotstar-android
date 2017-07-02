package com.flopcode.dotstar.android.parameters;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import com.flopcode.dotstar.android.Index;
import com.flopcode.dotstar.android.R;
import com.google.common.collect.ImmutableMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

@SuppressWarnings("unused")
public class TimeOfDayParameter extends Parameter {
  public static final String PARAMETER_NAME = "parameterName";
  public static final String PRESET_NAME = "presetName";
  private Button mButton;
  private Alarm mAlarm;

  public TimeOfDayParameter(String presetName, String parameterName) {
    super(presetName, parameterName);
  }

  public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Create a new instance of TimePickerDialog and return it
      return new TimePickerDialog(getActivity(),
        this,
        getArguments().getInt("hour"),
        getArguments().getInt("minute"),
        DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      final String presetName = getArguments().getString(PRESET_NAME);
      final String parameterName = getArguments().getString(PARAMETER_NAME);
      final String tod = formatTimeOfDay(hourOfDay, minute);
      Call<Void> call = getDotStar(getConnectionPrefs(getActivity()))
        .set(ImmutableMap.of(parameterName, tod));
      call.enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
          Log.i(Index.LOG_TAG, "could set timeOfDay for '" + parameterName + "'");
          staticPost(presetName, parameterName, tod);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
          Log.e(Index.LOG_TAG, "could not set timeOfDay for '" + parameterName + "'", t);
        }
      });

    }

    private String formatTimeOfDay(int hourOfDay, int minute) {
      return String.format("%02d:%02d:00", hourOfDay, minute);
    }

  }

  private static class Alarm {
    public final int hour;
    public final int minute;

    public Alarm(String s) {
      String[] parts = s.split(":");
      hour = Integer.parseInt(parts[0]);
      minute = Integer.parseInt(parts[1]);
    }

    @Override
    public String toString() {
      return String.format("%02d:%02d", hour, minute);
    }
  }

  @Override
  public View createView(LayoutInflater inflater, ViewGroup rootView, final Context context) {
    mButton = (Button) inflater.inflate(R.layout.preset_color_button, rootView, false);
    onCreate(new Listener() {
      @Override
      public void onChange(String name, String value) {
        mButton.setText(getLabel(name, new Alarm(value)));
      }
    });
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showColorPicker();
      }

      private void showColorPicker() {
        TimePickerFragment newFragment = new TimePickerFragment();
        Alarm alarm = new Alarm(getValue());
        Bundle args = new Bundle();
        args.putString(PRESET_NAME, getPresetName());
        args.putString(PARAMETER_NAME, getParameterName());
        args.putInt("hour", alarm.hour);
        args.putInt("minute", alarm.minute);
        newFragment.setArguments(args);
        FragmentManager fm = ((FragmentActivity) context).getFragmentManager();
        newFragment.show(fm, "timePicker");
      }
    });
    return mButton;
  }

  static String getLabel(String name, Alarm alarm) {
    return String.format("%s: %s", name, alarm.toString());
  }
}
