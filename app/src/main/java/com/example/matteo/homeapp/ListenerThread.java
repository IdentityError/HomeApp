package com.example.matteo.homeapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import xdroid.toaster.Toaster;

public class ListenerThread implements Runnable
{
    BufferedReader inFromCabinet;
    String serverResponse;

    ListenerThread()
    {
        if(MainActivity.rackSocket != null)
            try{ inFromCabinet = new BufferedReader(new InputStreamReader(MainActivity.rackSocket.getInputStream())); } catch (Exception e) {}
    }

    @Override
    public void run()
    {
        while(MainActivity.connectedToRack)
        {
            try
            {
                serverResponse = inFromCabinet.readLine();
                if(serverResponse != null)
                {
                    serverResponse = serverResponse.toLowerCase();
                    CheckCommandToExecute();
                }
            } catch (IOException ex) {}
        }
    }

    private void CheckCommandToExecute()
    {
        switch (serverResponse)
        {
            case "serverdown":
            {
                MainActivity.toolbarConnectionText.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.toolbarConnectionText.setText("Connection interrupted from server");
                    }
                });
                try { MainActivity.rackSocket.close(); } catch (final Exception e) { }
                MainActivity.connectedToRack = false;
                break;
            }
            case "p1unable":
            {
                Toaster.toast("Unable to connect to P1");
                break;
            }
            case "p2unable":
            {
                Toaster.toast("Unable to connect to P2");
                break;
            }
        }
    }
}