package com.example.matteo.homeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentPi2 extends Fragment
{
    String[] commands = new String[]
    {
        "Pi2 Commands",
        "Morning Routine"
    };
    Spinner pi2Spinner;
    FloatingActionButton deleteCommandLineButton;
    CheckBox toggleTimerCheckBox;
    TimePicker timerSetter;
    EditText commandLine;
    PrintWriter outToCabinet;
    Button sendButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragmentpi2_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(MainActivity.rackSocket != null)
            try{ outToCabinet = new PrintWriter(MainActivity.rackSocket.getOutputStream());} catch (Exception e) {}

        pi2Spinner = getView().findViewById(R.id.pi2Commands);
        deleteCommandLineButton = getView().findViewById(R.id.deleteCommandLineButton);
        toggleTimerCheckBox = getView().findViewById(R.id.toggleTImerCheckBox);
        timerSetter = getView().findViewById(R.id.timePicker);
        commandLine = getView().findViewById(R.id.commandLine);
        sendButton = getView().findViewById(R.id.sendButton);

        SetArrayAdapterForSpinner();

        pi2Spinner.setOnItemSelectedListener(itemChangeListener);
        sendButton.setOnClickListener(sendDataButtonListener);
       deleteCommandLineButton.setOnClickListener(deleteCommandLineButtonListener);
    }

    private View.OnClickListener sendDataButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(MainActivity.connectedToRack && !commandLine.getText().equals(""))
                if(commandLine.getText().equals("Morning Routine"))
                {
                    String command1 = "t" + Integer.toString(UtilitiesClass.GetTimerSeconds(6, 25)) + "-rainbowstart500";
                    String command2 = "t" + Integer.toString(UtilitiesClass.GetTimerSeconds(7, 10)) + "-rainbowstop";
                    new MainActivity.SendDataToServerAsync().execute(command1);
                    new MainActivity.SendDataToServerAsync().execute(command2);
                }
                else
                {
                    if (toggleTimerCheckBox.isChecked())
                        new MainActivity.SendDataToServerAsync().execute("p2-t" + Integer.toString(UtilitiesClass.GetTimerSeconds(timerSetter.getHour(), timerSetter.getMinute())) + "-" + commandLine.getText());
                    else
                        new MainActivity.SendDataToServerAsync().execute("p2-" + commandLine.getText());
                }
        }
    };

    private View.OnClickListener deleteCommandLineButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            commandLine.setText("");
        }
    };

    private void SetArrayAdapterForSpinner()
    {
        final List<String> rackCommands = new ArrayList<>(Arrays.asList(commands));
        final ArrayAdapter<String> rackCommandsSpinnerAdapter = new ArrayAdapter<String>(MainActivity.context, R.layout.rack_commands_spinner, rackCommands)
        {
            @Override
            public boolean isEnabled(int position)
            {
                if(position == 0)
                    return false;
                else
                    return true;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);

                return view;
            }
        };
        rackCommandsSpinnerAdapter.setDropDownViewResource(R.layout.rack_commands_spinner);
        pi2Spinner.setAdapter(rackCommandsSpinnerAdapter);
    }

    private AdapterView.OnItemSelectedListener itemChangeListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            switch (position)
            {
                case 1:
                    commandLine.setText("Morning Routine");
                    break;
            }
            pi2Spinner.setSelection(0);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };
}
