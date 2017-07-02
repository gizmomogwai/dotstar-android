package com.flopcode.dotstar.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.flopcode.dotstar.android.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.flopcode.dotstar.android.Index.LOG_TAG;

/**
 * A fragment representing a single com.flopcode.dotstar.android.Preset detail screen.
 * This fragment is either contained in a {@link Index}
 * in two-pane mode (on tablets) or a {@link PresetDetailActivity}
 * on handsets.
 */
public class PresetDetailFragment extends Fragment {
  /**
   * The fragment argument representing the item ID that this fragment
   * represents.
   */
  public static final String PRESET_NAME = "presetName";

  private String mPresetName;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public PresetDetailFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.w(LOG_TAG, "Fragment.onCreate");
    if (getArguments().containsKey(PRESET_NAME)) {
      mPresetName = (String) getArguments().getSerializable(PRESET_NAME);

      Activity activity = this.getActivity();
      CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
      if (appBarLayout != null) {
        appBarLayout.setTitle(mPresetName);
      }
    }
  }

  List<Parameter> mParameters = new ArrayList<>();

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
    Log.w(LOG_TAG, "Fragment.onCreateView");
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.preset_detail, container, false);
    for (Map<String, String> parameters : Application.PARAMETERS.get(mPresetName)) {
      Parameter p = Parameters.get(mPresetName, parameters.get("name"), parameters.get("type"));
      mParameters.add(p);
      View view = p.createView(inflater, rootView, getContext());
      if (view != null) {
        rootView.addView(view);
      }
    }
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    for (Parameter p : mParameters) {
      p.onDestroy();
    }
    mParameters.clear();
  }

  static class Parameters {
    public static Parameter get(String presetName, String parameterName, String type) {
      String className = "com.flopcode.dotstar.android.parameters." +
        type.substring(0, 1).toUpperCase() + type.substring(1) +
        "Parameter";
      try {
        return (Parameter) Class
          .forName(className).getConstructor(String.class, String.class)
          .newInstance(presetName, parameterName);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    Log.w(LOG_TAG, "Fragment.onAttach");
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.w(LOG_TAG, "Fragment.onActivityCreated");
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.w(LOG_TAG, "Fragment.onStart");
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.w(LOG_TAG, "Fragment.onResume");
  }
}
