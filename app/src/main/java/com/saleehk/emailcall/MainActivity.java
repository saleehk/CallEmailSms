package com.saleehk.emailcall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "getCallDetails";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    EditText phoneEditText;
    EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        messageEditText = (EditText) findViewById(R.id.content);
        phoneEditText = (EditText) findViewById(R.id.phone);
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneEditText.getText().toString();
                Toast.makeText(MainActivity.this, getLastCallPhoneNumber(), Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();

                if (android.os.Build.VERSION.SDK_INT >= 22) {
                    Log.e("Alert", "Checking SubscriptionId");
                    try {
                        Log.e("Alert", "SubscriptionId is " + smsManager.getSubscriptionId());
                    } catch (Exception e) {
                        Log.e("Alert", e.getMessage());
                        Log.e("Alert", "Fixed SubscriptionId to 1");
                        smsManager = SmsManager.getSmsManagerForSubscriptionId(1);
                    }
                }
                smsManager.sendTextMessage(phoneNumber, null, messageEditText.getText().toString(), null, null);

            }
        });
        String phoneNumber = getLastCallPhoneNumber();

        phoneEditText.setText(phoneNumber);
        messageEditText.setText("mail@mail.com");
        checkPermissions();
        SmsManager smsManager = SmsManager.getDefault();


    }

    @Override
    protected void onResume() {
        super.onResume();

        String phoneNumber = getLastCallPhoneNumber();

        phoneEditText.setText(phoneNumber);
        Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
    }

    public String getLastCallPhoneNumber() {

        Uri contacts = CallLog.Calls.CONTENT_URI;
        String phNumber = "";

        try {

            Cursor managedCursor = this.getContentResolver().query(contacts, null, null, null,
                    android.provider.CallLog.Calls.DATE + " DESC limit 2;");

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            if (managedCursor.moveToFirst()) { // added line
                phNumber = managedCursor.getString(number);

            }
            managedCursor.close();

        } catch (SecurityException e) {
            Log.e("Security Exception", "User denied call log permission");

        }

        return phNumber;

    }

    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_PHONE_STATE,
                        },
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }


}
