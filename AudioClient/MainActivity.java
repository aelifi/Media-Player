package com.example.audioclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clipserver.ClipAIDL;

//Written by Aleksandr Elifirenko
//This is a client app that binds to a service app to play the clips.
//Contains all the button functionality for the clips like start service, stop service,
//play clips, pause clips, resume clips and stop clips. The buttons appear and disappear
//depending on the button pressed. Lastly, it has the main layout since the clip server does
//not provide any widgets except a welcome message.

public class MainActivity extends AppCompatActivity {
    //All the widgets
    private boolean mIsBound = false;
    private ClipAIDL mClipAIDLService;
    Button startService;
    Button stopService;
    Button clip1;
    Button clip2;
    Button clip3;
    Button clip4;
    Button clip5;
    Button stopClip;
    Button resumeClip;
    Button pauseClip;

    //State of the buttons
    private final int stoppedService = 0;
    private final int serviceStarted = 1;
    private final int playingClip = 2;
    private final int paused = 3;
    private final int stopped = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find the widgets
        startService = findViewById(R.id.start_service);
        stopService = findViewById(R.id.stop_service);
        clip1 = findViewById(R.id.clip1);
        clip2 = findViewById(R.id.clip2);
        clip3 = findViewById(R.id.clip3);
        clip4 = findViewById(R.id.clip4);
        clip5 = findViewById(R.id.clip5);
        stopClip = findViewById(R.id.stopClip);
        resumeClip = findViewById(R.id.resumeClip);
        pauseClip = findViewById(R.id.pauseClip);

        buttonState(stoppedService); //Initial button state

        //Start service
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent for package
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.clipserver", "com.example.clipserver.ClipClass"));
                startForegroundService(intent); //Start the foreground after setting the package
                if(!mIsBound) { //Bind if not bound then enable buttons
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    buttonState(serviceStarted);
                }
            }
        });

        //Plays clip 1
        clip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //if bound play the clip and set the button state
                    try {
                        mClipAIDLService.playClip("1");
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Plays clip 2
        clip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //if bound play the clip and set the button state
                    try {
                        mClipAIDLService.playClip("2");
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Plays clip 3
        clip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //if bound play the clip and set the button state
                    try {
                        mClipAIDLService.playClip("3");
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Plays clip 4
        clip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //if bound play the clip and set the button state
                    try {
                        mClipAIDLService.playClip("4");
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Plays clip 5
        clip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //if bound play the clip and set the button state
                    try {
                        mClipAIDLService.playClip("5");
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Pauses the clip
        pauseClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //If bound pause the clip and change button state
                    try {
                        mClipAIDLService.pauseClip();
                        buttonState(paused);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Resumes the clip
        resumeClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //If bound resume the clip and change button state
                    try {
                        mClipAIDLService.resumeClip();
                        buttonState(playingClip);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Stops the clip
        stopClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) { //If bound stop the clip and change button state
                    try {
                        mClipAIDLService.stopClip();
                        buttonState(stopped);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //Stops the service all together
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Stop service, unbind, set bound to false and disable all buttons
                    mClipAIDLService.stopService();
                    unbindService(mConnection);
                    mIsBound = false;
                    buttonState(stoppedService);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //Button state for different clip events
    private void buttonState(int state) {
        if(state == 0){ //Stopped service state only start service is enabled
            clip1.setEnabled(false);
            clip2.setEnabled(false);
            clip3.setEnabled(false);
            clip4.setEnabled(false);
            clip5.setEnabled(false);
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(false);
            stopClip.setEnabled(false);
            stopService.setEnabled(false);
            startService.setEnabled(true);
        }
        if(state == 1){ //Service started, can play clips and stop service
            clip1.setEnabled(true);
            clip2.setEnabled(true);
            clip3.setEnabled(true);
            clip4.setEnabled(true);
            clip5.setEnabled(true);
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(false);
            stopClip.setEnabled(false);
            stopService.setEnabled(true);
            startService.setEnabled(false);
        }if(state == 2){ //While clip playing, can pause or stop clip and stop service
            clip1.setEnabled(true);
            clip2.setEnabled(true);
            clip3.setEnabled(true);
            clip4.setEnabled(true);
            clip5.setEnabled(true);
            pauseClip.setEnabled(true);
            resumeClip.setEnabled(false);
            stopClip.setEnabled(true);
            stopService.setEnabled(true);
            startService.setEnabled(false);
        }if(state == 3){ //Paused state,can resume or stop or play different clip, also stop service
            clip1.setEnabled(true);
            clip2.setEnabled(true);
            clip3.setEnabled(true);
            clip4.setEnabled(true);
            clip5.setEnabled(true);
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(true);
            stopClip.setEnabled(true);
            stopService.setEnabled(true);
            startService.setEnabled(false);
        }if(state == 4){ //Stopped clip, can play new clip or stop service (just like service started)
            clip1.setEnabled(true);
            clip2.setEnabled(true);
            clip3.setEnabled(true);
            clip4.setEnabled(true);
            clip5.setEnabled(true);
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(false);
            stopClip.setEnabled(false);
            stopService.setEnabled(true);
            startService.setEnabled(false);
        }
    }

    //On destroy, unbind the connection
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    //Connection to the service method
    private final ServiceConnection mConnection = new ServiceConnection() {
        //If connection was successful then convert IBinder to the interface and set bound to true
        @Override
        public void onServiceConnected(ComponentName name, IBinder iservice) {
            mClipAIDLService = ClipAIDL.Stub.asInterface(iservice);
            mIsBound = true;
        }

        //If disconnected then set AIDL service to null and bound to false
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mClipAIDLService = null;
            mIsBound = false;
        }
    };
}



