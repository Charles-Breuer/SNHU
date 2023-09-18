package com.zybooks.projectcs360;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.telephony.TelephonyManager;
import android.view.View;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
{
    //------------- Class Fields ----------------//
    EventAlarmManager m_EventAlarmManager;
    private DatabaseHelper m_Database;
    private EventModel m_SelectedEvent = null;
    private long m_CurrentUserID;

    //------------- Override Functions ----------------//

    // This onCreate() function establishes member fields. It also inflates the AllEvents fragment
    // by default. This function also turns on SMS notifications if the proper permissions have been
    // granted.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        m_CurrentUserID = intent.getLongExtra("userID", -1);
        if (m_CurrentUserID == -1) {throw new RuntimeException("No valid userID defined for onCreate() function in MainActivity");}

        m_Database = new DatabaseHelper(this);
        BottomNavigationView navView = findViewById(R.id.main_navigation_bar);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment_viewer);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        }

        m_EventAlarmManager = new EventAlarmManager(this, m_Database);  // 'this' refers to the Activity
        if ((!m_EventAlarmManager.getRunningStatus()) &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String phoneNumber = telephonyManager.getLine1Number();
            m_EventAlarmManager.startChecking(phoneNumber);
        }

    }

    // This onDestroy() function stops SMS notifications if they are running.
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (m_EventAlarmManager.getRunningStatus()) { m_EventAlarmManager.stopChecking(); }

    }
    // Permission checking callback function for SMS functionality. This function notifies the user
    // if the necessary permissions have been granted.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == m_EventAlarmManager.getSmsPermissionRequestCode())
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.all_events_successful_SMS), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.all_events_denied_SMS), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //------------- Getters and Setters ----------------//
    public void setSelectedEvent(EventModel event)
    {
        m_SelectedEvent = event;
    }
    public EventModel getSelectedEvent()
    {
        return m_SelectedEvent;
    }
    public long getCurrentUserID() {
        return m_CurrentUserID;
    }
    public DatabaseHelper getCurrentDatabase() {
        return m_Database;
    }

    //------------- External Functionality ----------------//

    // Function to check and request permissions. Used for SMS permissions.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED)
        {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else
        {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.all_events_already_granted_SMS), Toast.LENGTH_SHORT).show();
        }
    }

    //------------- Internal Functionality ----------------//
    // Function used to get a reference to the current fragment associated with main_fragment_viewer
    // in activity_main.xml
    private Fragment getCurrentFragment()
    {
        NavHostFragment currentNavFragment = (NavHostFragment)(getSupportFragmentManager().findFragmentById(R.id.main_fragment_viewer));
        if (currentNavFragment == null) {return null;}

        Fragment displayFragment = null;
        FragmentManager childFragmentManager = currentNavFragment.getChildFragmentManager();
        displayFragment = childFragmentManager.getPrimaryNavigationFragment();

        return displayFragment;
    }

    //------------- Event Handling Functions ----------------//
    // These functions provide basic navigation associated with the button_bar's at the bottom
    // of each fragment.

    public void onFragViewEventClick(View view)
    {
        Navigation.findNavController(view).navigate(R.id.action_navigation_view_to_navigation_all_events);
    }

    public void onFragViewEventClick2(View view)
    {
        Navigation.findNavController(view).navigate(R.id.action_navigation_view_to_navigation_edit);
    }


    public void onFragEditEventClickCancel(View view)
    {
        Navigation.findNavController(view).navigate(R.id.action_navigation_edit_to_navigation_view);
    }

    // This function handles updating the event held in m_SelectedEvent. It also updates the event
    // in m_Database.
    public void onFragEditEventClick(View view)
    {
        EditEvent currentFragment = (EditEvent)getCurrentFragment();
        if (currentFragment == null) {System.exit(0);}

        assert currentFragment != null;
        String eventTitle = currentFragment.getEventName();
        String eventDescription = currentFragment.getEventDescription();
        String eventDate = currentFragment.getEventDate();
        String eventTime = currentFragment.getEventTime();
        if (eventTitle.isEmpty() ||
                eventDescription.isEmpty() ||
                eventDate.isEmpty() ||
                eventTime.isEmpty())
        {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
            return;
        }

        eventTime = Utility.convert12To24HourFormat(eventTime);

        m_SelectedEvent.setTitle(eventTitle);
        m_SelectedEvent.setDescription(eventDescription);
        m_SelectedEvent.setDate(eventDate);
        m_SelectedEvent.setTime(eventTime);

        if(!m_Database.updateEvent(m_SelectedEvent))
            throw new RuntimeException("Event Update was unsuccessful!");

        Navigation.findNavController(view).navigate(R.id.action_navigation_edit_to_navigation_view);
    }

    // This function handles deleting event held in m_SelectedEvent. It also deletes the event
    // in m_Database.
    public void onFragEditEventClick2(View view)
    {
        if(!m_Database.deleteEvent(m_SelectedEvent))
            throw new RuntimeException("Deletion of event was unsuccessful!");

        m_SelectedEvent = null;
        Navigation.findNavController(view).navigate(R.id.action_navigation_edit_to_navigation_all_events);
    }

    public void onFragCreateEventCancel(View view)
    {
        Navigation.findNavController(view).navigate(R.id.action_navigation_create_to_navigation_all_events);
    }

    // This function handles creating a new event and associating it with m_SelectedEvent. It also
    // creates the event in the database.
    public void onFragCreateEventConfirm(View view)
    {
        CreateEvent currentFragment = (CreateEvent)getCurrentFragment();
        if (currentFragment == null) {System.exit(0);}

        assert currentFragment != null;
        String eventTitle = currentFragment.getEventName();
        String eventDescription = currentFragment.getEventDescription();
        String eventDate = currentFragment.getEventDate();
        String eventTime = currentFragment.getEventTime();
        if (eventTitle.isEmpty() || eventDescription.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty())
        {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
            return;
        }
        eventTime = Utility.convert12To24HourFormat(eventTime);
        EventModel newEvent = new EventModel();
        newEvent.setTitle(eventTitle);
        newEvent.setDescription(eventDescription);
        newEvent.setDate(eventDate);
        newEvent.setTime(eventTime);
        m_Database.addEvent(newEvent, m_CurrentUserID);
        m_SelectedEvent = newEvent;
        Navigation.findNavController(view).navigate(R.id.action_navigation_create_to_navigation_view);
    }

    public void onFragSettingsClick(View v){
        startActivity(new Intent(MainActivity.this, ActivityLogin.class));
    }

    // This function is the callback function for the SMS notification button in fragment_settings.xml
    public void onFragSettingsClick2(View v)
    {
        checkPermission(Manifest.permission.SEND_SMS, m_EventAlarmManager.getSmsPermissionRequestCode());
        checkPermission(Manifest.permission.READ_PHONE_STATE, m_EventAlarmManager.getPhoneStatePermissionRequestCode());
        checkPermission(Manifest.permission.READ_PHONE_NUMBERS, m_EventAlarmManager.getReadPhoneNumbersPermissionRequestCode());

        if ((!m_EventAlarmManager.getRunningStatus()) &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String phoneNumber = telephonyManager.getLine1Number();
            m_EventAlarmManager.startChecking(phoneNumber);
        }
    }

}