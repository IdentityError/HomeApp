package com.example.matteo.homeapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.matteo.homeapp.Fragments.Pi2Fragment;
import com.example.matteo.homeapp.MainActivity;
import com.example.matteo.homeapp.R;
import com.example.matteo.homeapp.Runnables.SSHCommandThread;
import com.example.matteo.homeapp.Runnables.SendDataThread;
import com.example.matteo.homeapp.UtilitiesClass;

import xdroid.toaster.Toaster;

public class SettingsFragment extends Fragment
{
    private MainActivity mainActivity;
    EditText rackIPText, rackPortText, sshCommandLine, defaultRainbowRate;
    FloatingActionButton saveSettingsButton;
    Button reconnectRackButton, reconnectP1Button, reconnectP2Button, sendSSHButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_settings, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        UtilitiesClass.getInstance().HideSoftInputKeyboard(getView());

        sendSSHButton = getView().findViewById(R.id.sendSshButton);
        reconnectRackButton = getView().findViewById(R.id.reconnectRackButton);
        reconnectP1Button = getView().findViewById(R.id.reconnectP1Button);
        reconnectP2Button = getView().findViewById(R.id.reconnectP2Button);

        defaultRainbowRate = getView().findViewById(R.id.defaultRainbowRateText);
        sshCommandLine = getView().findViewById(R.id.sshCommandLine);
        saveSettingsButton = getView().findViewById(R.id.saveSettingsButton);

        rackIPText = getView().findViewById(R.id.rack_ip);
        rackPortText = getView().findViewById(R.id.rack_port);

        sendSSHButton.setOnClickListener(clickListener);
        saveSettingsButton.setOnClickListener(clickListener);
        reconnectRackButton.setOnClickListener(clickListener);
        reconnectP1Button.setOnClickListener(clickListener);
        reconnectP2Button.setOnClickListener(clickListener);

        UtilitiesClass.getInstance().LoadAppPreferences();
        rackIPText.setHint(mainActivity.rackIP);
        rackPortText.setHint(mainActivity.rackPort);
        defaultRainbowRate.setHint(Pi2Fragment.defaultRainbowRate);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.saveSettingsButton:
                    String newRackPort = rackPortText.getText().toString().trim();
                    String newRackIP = rackIPText.getText().toString().trim();
                    String newDefaultRainbowRate = defaultRainbowRate.getText().toString().trim();
                    if (!newRackIP.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "RACK_IP", newRackIP);
                        rackIPText.setHint(newRackIP);
                        mainActivity.rackIP = newRackIP;
                        mainActivity.connectedToRack = false;
                        mainActivity.StartConnectionThread();
                    }
                    if(!newRackPort.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "RACK_PORT", newRackPort);
                        rackPortText.setHint(newRackPort);
                        mainActivity.rackPort = newRackPort;
                        mainActivity.connectedToRack = false;
                        mainActivity.StartConnectionThread();
                    }
                    if(!newDefaultRainbowRate.equals(""))
                    {
                        UtilitiesClass.getInstance().SaveSharedPreferencesKey("settings", "DEFAULT_RAINBOW_RATE", newDefaultRainbowRate);
                        defaultRainbowRate.setHint(newDefaultRainbowRate);
                    }
                    Toaster.toast("Settings saved");
                    break;

                case R.id.reconnectRackButton:
                    if(!mainActivity.connectedToRack && mainActivity.IsConnectedToWiFi())
                        mainActivity.StartConnectionThread();
                    break;

                case R.id.reconnectP1Button:
                    if(mainActivity.connectedToRack && mainActivity.IsConnectedToWiFi())
                        new Thread(new SendDataThread("p1-connect", mainActivity)).start();
                    break;

                case R.id.reconnectP2Button:
                    if(mainActivity.connectedToRack && mainActivity.IsConnectedToWiFi())
                        new Thread(new SendDataThread("p2-connect", mainActivity)).start();
                    break;
                case R.id.sendSshButton:
                    if(!sshCommandLine.getText().toString().equals("") && !mainActivity.connectedToRack)
                        new Thread(new SSHCommandThread("192.168.1.40", "rack", "rackpcpassword", sshCommandLine.getText().toString())).start();
                    break;
            }
        }
    };
}