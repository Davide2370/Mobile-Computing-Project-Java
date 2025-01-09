// Generated by view binder compiler. Do not edit!
package com.example.progetto_java.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.progetto_java.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentVicinanzeBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout fragmentVicinanze;

  @NonNull
  public final ProgressBar spinnerID;

  @NonNull
  public final RecyclerView userRecyclerView;

  private FragmentVicinanzeBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout fragmentVicinanze, @NonNull ProgressBar spinnerID,
      @NonNull RecyclerView userRecyclerView) {
    this.rootView = rootView;
    this.fragmentVicinanze = fragmentVicinanze;
    this.spinnerID = spinnerID;
    this.userRecyclerView = userRecyclerView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentVicinanzeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentVicinanzeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_vicinanze, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentVicinanzeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout fragmentVicinanze = (ConstraintLayout) rootView;

      id = R.id.spinnerID;
      ProgressBar spinnerID = ViewBindings.findChildViewById(rootView, id);
      if (spinnerID == null) {
        break missingId;
      }

      id = R.id.userRecyclerView;
      RecyclerView userRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (userRecyclerView == null) {
        break missingId;
      }

      return new FragmentVicinanzeBinding((ConstraintLayout) rootView, fragmentVicinanze, spinnerID,
          userRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}