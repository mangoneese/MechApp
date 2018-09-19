package com.mercury.mechanize;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BottomSheetDriverFragment extends BottomSheetDialogFragment {
    String mLocation,mProblem;

    public static BottomSheetDriverFragment newInstance(String location, String problem)
    {
        BottomSheetDriverFragment d = new BottomSheetDriverFragment();
        Bundle args = new Bundle();
        args.putString("location",location);
        args.putString("problem",problem);
        d.setArguments(args);
        return d;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = getArguments().getString("location");
        mProblem = getArguments().getString("problem");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_driver,container,false);
        //TextView =...
        return view;
    }

}
