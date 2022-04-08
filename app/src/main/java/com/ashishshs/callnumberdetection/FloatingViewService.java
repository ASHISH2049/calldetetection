package com.ashishshs.callnumberdetection;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;

import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatingViewService extends Service {
    public static TextView callName,callNumber;
    public  Button saveContact;
    private WindowManager mWindowManager;
    private View mFloatingView;
//    String message;

    private WindowManager.LayoutParams params;

    public FloatingViewService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();



        mFloatingView = LayoutInflater.from(this).inflate(R.layout.call_window, null);


        callName=mFloatingView.findViewById(R.id.t1);
        callNumber=mFloatingView.findViewById(R.id.callNumber);
        saveContact=mFloatingView.findViewById(R.id.contact);




        callName.setText(PhoneStateReceiver.friendName);
        callNumber.setText(PhoneStateReceiver.incomingNumber);

//

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, PhoneStateReceiver.friendName)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, PhoneStateReceiver.incomingNumber);
                contactIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(contactIntent);
//
                Log.i("TAGASHSISHSSHSHSH", "onClick: ");
            }
        });


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = MainActivity.screenHeight/2;
//        params.y = 500;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
//                mFloatingView.setVisibility(view.GONE);
            }
        });


        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("fromwhere","ser");
                            startActivity(intent);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });




//        Intent intent = new Intent();
//        String message = intent.getStringExtra("nameFriend");


//        PhoneStateReceiver receiver=new PhoneStateReceiver();
//        String message=receiver.getValue();


//        Log.i("TAG", "kapil: "+message);
//        tv.setText(message);





    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

//    private void startMyOwnForeground() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
//            String channelName = "My Background Service";
//            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//            chan.setLightColor(Color.BLUE);
//            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            assert manager != null;
//            manager.createNotificationChannel(chan);
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//            Notification notification = notificationBuilder.setOngoing(true)
//                    .setContentTitle("App is running in background")
//                    .setPriority(NotificationManager.IMPORTANCE_MIN)
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .build();
//            startForeground(2, notification);
//        }
//    }

}
