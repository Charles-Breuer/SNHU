package com.zybooks.projectcs360;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;

public class CreateEvent extends Fragment
{
    //------------- Class Fields ----------------//
    private EditText m_EventTitle;
    private EditText m_EventDescription;
    private EditText m_EventDate;
    private EditText m_EventTime;

    //------------- Constructor(s) ----------------//
    public CreateEvent()
    {
        // Required empty public constructor
    }

    //------------- Override Functions ----------------//
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    // This onCreateView function establishes member fields and provides onClick functionality
    // to the different input views in fragment_create_event.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        m_EventTitle = rootView.findViewById(R.id.create_event_title_input);
        m_EventTitle.setOnClickListener(v -> openInputDialogTitle());
        m_EventDescription = rootView.findViewById(R.id.create_event_description_input);
        m_EventDescription.setOnClickListener(v -> openInputDialogDescription());
        m_EventDate = rootView.findViewById(R.id.create_event_date_input);
        m_EventDate.setOnClickListener(v -> showDatePickerDialog());
        m_EventDate.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                showDatePickerDialog();
            }
        });

        m_EventTime = rootView.findViewById(R.id.create_event_time_input);
        m_EventTime.setOnClickListener(v -> showTimePickerDialog());
        m_EventTime.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                showTimePickerDialog();
            }
        });

        return rootView;
    }
    //------------- Getters and Setters ----------------//
    public String getEventName()
    {
        return m_EventTitle.getText().toString();
    }

    public String getEventDescription()
    {
        return m_EventDescription.getText().toString();
    }

    public String getEventDate()
    {
        return m_EventDate.getText().toString();
    }

    public String getEventTime()
    {
        return m_EventTime.getText().toString();
    }


    //------------- Internal Functions ----------------//

    // This function displays a dialog that retrieves user input for the date
    private void showDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Create a new instance of DatePickerDialog
        @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) ->
                {
                    // Display the selected date in the EditText
                    m_EventDate.setText(String.format("%02d/%02d/%d",
                            selectedMonth + 1,
                            selectedDay,
                            selectedYear));
                },
                year, month, day);

        datePickerDialog.show();
    }

    // This function displays a dialog that retrieves user input for the time of the new event
    private void showTimePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) ->
                {
                    String AM_PM;
                    if (selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                        if (selectedHour != 12) {  // Convert 13-23 to 1-11 for display
                            selectedHour -= 12;
                        }
                    }
                    @SuppressLint("DefaultLocale") String selectedTime = String.format("%02d:%02d %s", selectedHour, selectedMinute, AM_PM);
                    m_EventTime.setText(selectedTime);
                },
                hour, minute, false); // Use 'false' to get AM/PM picker

        timePickerDialog.show();
    }

    // This function displays a dialog that retrieves user input for the title of their new event
    private void openInputDialogTitle()
    {
        final EditText input = new EditText(requireContext());
        input.setText(m_EventTitle.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.title_dialog_label))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.positive_button_dialog),
                        (dialog, which) -> {
                            String userInput = input.getText().toString();
                            m_EventTitle.setText(userInput);
                        })
                .setNegativeButton(getResources().getString(R.string.negative_button_dialog), null)
                .show();
    }

    // This function displays a dialog that retrieves user input
    // for the description of their new event
    private void openInputDialogDescription()
    {
        final EditText input = new EditText(requireContext());
        input.setText(m_EventDescription.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.description_dialog_label))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.positive_button_dialog), (dialog, which) -> {
                    String userInput = input.getText().toString();
                    m_EventDescription.setText(userInput);
                })
                .setNegativeButton(getResources().getString(R.string.negative_button_dialog),
                        null)
                .show();
    }
}