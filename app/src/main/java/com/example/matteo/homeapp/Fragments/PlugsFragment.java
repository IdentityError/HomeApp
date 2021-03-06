package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.matteo.homeapp.HomeApp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SendDataRunnable;
import com.example.matteo.homeapp.HomeApp.UtilitiesClass;

public class PlugsFragment extends Fragment
{
    MainActivity mainActivity;
    Button windowPlugOnButton, windowPlugOffButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_plugs, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        windowPlugOnButton = getView().findViewById(R.id.windowPlugOnButton);
        windowPlugOffButton = getView().findViewById(R.id.windowPlugOffButton);
        windowPlugOnButton.setOnClickListener(clickListener);
        windowPlugOffButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.windowPlugOnButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("p1-windowplug_on", mainActivity));
                    break;
                case R.id.windowPlugOffButton:
                    if(mainActivity.isConnectedToRack())
                        UtilitiesClass.getInstance().ExecuteRunnable(new SendDataRunnable("p1-windowplug_off", mainActivity));
                    break;
            }
        }
    };
}
