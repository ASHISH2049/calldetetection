package com.ashishshs.callnumberdetection;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {
    public static String incomingNumber, friendName;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        try {

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {


                //contact of a Save  phone nmber


                ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                Cursor cursor = contentResolver.query(uri, null, null, null, null);

//                Log.i("CONTACT_PROVIDER_DEMO", "TOTAL # OF CONTACT :::" + Integer.toString(cursor.getCount()));
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {

                        @SuppressLint("Range") String contactNAme = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        contactNumber = contactNumber.replace(" ", "");
                        contactNumber = contactNumber.replace("-", "");


                        if (contactNumber.equals(incomingNumber)) {

                            Log.i("CONTACT_PROVIDER_DEMO", "contact name :::" + incomingNumber + "ph# :::" + contactNAme);
                            Toast.makeText(context, "mi phone" + contactNAme, Toast.LENGTH_SHORT).show();
                            friendName = contactNAme;


                        }
                        else {
                            friendName = callServerAPI(contactNumber);
                        }


                    }
                }


                //contact of a Save  phone nmber
                context.startService(new Intent(context, FloatingViewService.class));

                Toast.makeText(context, "Ringing State Number is -" + incomingNumber, Toast.LENGTH_SHORT).show();
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String callServerAPI(String mobile) {

        return "Your Name ..........";

    }
}
