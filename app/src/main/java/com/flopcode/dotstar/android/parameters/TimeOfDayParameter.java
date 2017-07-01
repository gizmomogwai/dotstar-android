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

import java.util.Calendar;
import java.util.Map;

import static com.flopcode.dotstar.android.Index.getConnectionPrefs;
import static com.flopcode.dotstar.android.Index.getDotStar;

@SuppressWarnings("unused")
public class TimeOfDayParameter extends Parameter {
  public TimeOfDayParameter(Map<String, String> params) {
    super(params);
  }

  public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current time as the default values for the picker
      final Calendar c = Calendar.getInstance();
      int hour = c.get(Calendar.HOUR_OF_DAY);
      int minute = c.get(Calendar.MINUTE);

      // Create a new instance of TimePickerDialog and return it
      return new TimePickerDialog(getActivity(), this, hour, minute,
        DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      // Do something with the time chosen by the user
      final String name = getArguments().getString("name");
      Call<Void> call = getDotStar(getConnectionPrefs(getActivity()))
        .set(ImmutableMap.of(name, formatTimeOfDay(hourOfDay, minute)));
      call.enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
          Log.i(Index.LOG_TAG, "could set timeOfDay for '" + name + "'");
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
          Log.e(Index.LOG_TAG, "could not set timeOfDay for '" + name + "'", t);
        }
      });

    }

    private String formatTimeOfDay(int hourOfDay, int minute) {
      return String.format("%02d:%02d:00", hourOfDay, minute);
    }
  }

  @Override
  public View createButton(LayoutInflater inflater, ViewGroup rootView, final Context context) {
    final Button res = (Button) inflater.inflate(R.layout.preset_color_button, rootView, false);
    res.setText(name);
    res.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showColorPicker();
      }

      private void showColorPicker() {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        newFragment.setArguments(args);
        FragmentManager fm = ((FragmentActivity) context).getFragmentManager();
        newFragment.show(fm, "timePicker");
      }
    });
    return res;
  }
}
