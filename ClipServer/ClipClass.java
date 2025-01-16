package com.example.clipserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.core.app.NotificationCompat;

//Written by Aleksandr Elifirenko
//This is a clip server app that runs foreground as well as keeps the clips
//so that when the client connects, they are able to play the clips.
//It also makes a notification as well as contain the
//methods for the AIDL file.

public class ClipClass extends Service {

    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mPlayer;
    private int mStartID;
    private static final String CHANNEL_ID = "Music player style";

    @Override
    public IBinder onBind(Intent intent) {
        return new mBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Create the notification
        this.createNotificationChannel();

        final Intent notificationIntent =
                new Intent(getApplicationContext(), ClipClass.class);

        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification;

        //
        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setContentTitle("Music Playing").setContentText("Click to Access Music Player")
                .setTicker("Music is playing!")
                .setContentIntent(pendingIntent)
                .build();

        //Start the foreground with the created notification
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
    }

    //Start of service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null != mPlayer) {
            // ID for this start command
            mStartID = startId;
        }

        // Don't automatically restart this Service if it is killed
        return START_NOT_STICKY;
    }

    //On destroy function stops the media player
    @Override
    public void onDestroy() {
        if (null != mPlayer) {
            mPlayer.stop();
            mPlayer.release();
        }
    }

    //Create the notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music player notification";
            String description = "The channel for music player notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Implements the AIDL service to play clips
    public class mBinder extends ClipAIDL.Stub {
        //Plays the clip
        @Override
        public void playClip(String name) throws RemoteException {
            //Stop the previous clip
            if(mPlayer != null){
                mPlayer.release();
            }
            //Create a new one
            mPlayer = MediaPlayer.create(getApplicationContext(), getClip(name));

            if (null != mPlayer) {
                mPlayer.setLooping(false);
                // Stop Service when music has finished playing
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    //Once the clips ends kill the media player
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        stopSelf(mStartID);
                    }
                });
                mPlayer.start() ;
            }
        }

        //Pauses the clip
        @Override
        public void pauseClip() throws RemoteException {
            if(mPlayer != null)
                mPlayer.pause();
        }

        //Resumes the clip
        @Override
        public void resumeClip() throws RemoteException {
            if(mPlayer != null)
                mPlayer.start();
        }

        //Stops the clip
        @Override
        public void stopClip() throws RemoteException {
            if(mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        }

        //Stops service
        @Override
        public void stopService() throws RemoteException {
            stopSelf();
        }
    }

    //Returns clip based on its number
    private int getClip(String name) {
        switch (name) {
            case "1":
                return R.raw.clip1;
            case "2":
                return R.raw.clip2;
            case "3":
                return R.raw.clip3;
            case "4":
                return R.raw.clip4;
            case "5":
                return R.raw.clip5;
            default:
                return 0;
        }
    }
}