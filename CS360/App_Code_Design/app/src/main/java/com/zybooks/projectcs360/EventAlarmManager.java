package com.zybooks.projectcs360;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.SmsManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAlarmManager
{
    //------------- Class Fields ----------------//
    private final Activity m_Context;
    private boolean m_IsRunning = false;
    private final Handler m_Handler = new Handler();
    private final DatabaseHelper m_Database;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 2;
    private static final int READ_PHONE_NUMBERS_REQUEST_CODE = 3;

    //------------- Constructor(s) ----------------//
    public EventAlarmManager(Activity context, DatabaseHelper databaseHelper)
    {
        this.m_Context = context;
        this.m_Database = databaseHelper;
    }

    //------------- Getters and Setters ----------------//
    public final boolean getRunningStatus() {return m_IsRunning;}
    public int getSmsPermissionRequestCode() {return SMS_PERMISSION_REQUEST_CODE;}

    public int getPhoneStatePermissionRequestCode() {return READ_PHONE_STATE_PERMISSION_REQUEST_CODE;}

    public int getReadPhoneNumbersPermissionRequestCode() {return READ_PHONE_NUMBERS_REQUEST_CODE;}


    //------------- External Functionality ----------------//

    // Function is associated with startChecking() function. startChecking() calls this function
    // at predetermined intervals. This function iterates through the current events associated
    // with the current userID in the database. During each iteration, it checks if the time of
    // each event matches (precision is within the same minute) the current time.
    // If the time does match, the app sends an SMS notification to the user.
    public void checkEventsAndSendSMS(String phoneNumber)
    {
        MainActivity mainActivity = (MainActivity) m_Context;
        long currentUserID = mainActivity.getCurrentUserID();

        List<EventModel> allEvents = m_Database.getAllEventsForUser(currentUserID); // Assuming you have a method that returns all events
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        for (EventModel event : allEvents)
        {
            if (event.getTime().equals(currentTime)) {
                sendSMS(phoneNumber, "Your event " + event.getTitle() + " is happening now!");
            }
        }
    }

    // Function starts a separate thread that calls checkEventAndSendSMS at predetermined intervals
    public void startChecking(String phoneNumber)
    {
        m_IsRunning = true;
        m_Handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                checkEventsAndSendSMS(phoneNumber);
                m_Handler.postDelayed(this, 10000);  // Check every ten seconds
            }
        });
    }

    // Function stops execution of thread started in startChecking() function.
    public void stopChecking()
    {
        m_IsRunning = false;
        m_Handler.removeCallbacksAndMessages(null);
    }

    //------------- Internal Functionality ----------------//

    // Function sends SMS to user. Provides error checking.
    private void sendSMS(String phoneNumber, String message)
    {
        if (ContextCompat.checkSelfPermission(m_Context, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } else
        {
            ActivityCompat.requestPermissions(m_Context, new String[]{android.Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }


}


