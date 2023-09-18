package com.zybooks.projectcs360;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;

public class EditEvent extends Fragment
{

    //------------- Class Fields ----------------//
    private EditText m_EventTitle;
    private EditText m_EventDescription;
    private EditText m_EventDate;
    private EditText m_EventTime;

    //------------- Constructor(s) ----------------//
    public EditEvent()
    {
        // Required empty public constructor
    }

    //------------- Override Functions ----------------//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // This onCreateView function initializes member fields and associates input views in
    // fragment_edit_event.xml with onClick functionality
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);

        MainActivity activity = (MainActivity)requireActivity();
        EventModel currentEvent = activity.getSelectedEvent();
        if (currentEvent == null) { Navigation.findNavController(rootView).navigate(R.id.action_navigation_edit_to_navigation_all_events);}

        m_EventTitle = rootView.findViewById(R.id.edit_event_title_input);
        assert currentEvent != null;
        m_EventTitle.setText(currentEvent.getTitle());
        m_EventTitle.setOnClickListener(v -> openInputDialogTitle());
        m_EventDescription = rootView.findViewById(R.id.edit_event_description_input);
        m_EventDescription.setText(currentEvent.getDescription());
        m_EventDescription.setOnClickListener(v -> openInputDialogDescription());
        m_EventDate = rootView.findViewById(R.id.edit_event_date_input);
        m_EventDate.setText(currentEvent.getDate());
        m_EventDate.setOnClickListener(v -> showDatePickerDialog());
        m_EventDate.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus)
            {
                showDatePickerDialog();
            }
        });

        m_EventTime = rootView.findViewById(R.id.edit_event_time_input);
        m_EventTime.setText(Utility.convert24To12HourFormat(currentEvent.getTime()));
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

    // Function associated with date input in fragment_edit_event.xml. Creates a dialog
    // that receives date input from user.
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
                    m_EventDate.setText(String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear));
                },
                year, month, day);

        datePickerDialog.show();
    }

    // Function associated with time input in fragment_edit_event.xml. Creates a dialog
    // that receives time input from user.
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
                    if (selectedHour < 12)
                    {
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

    // Function associated with title input in fragment_edit_event.xml. Creates a dialog
    // that receives title (text) input from user.
    private void openInputDialogTitle()
    {
        final EditText input = new EditText(requireContext());
        input.setText(m_EventTitle.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.title_dialog_label))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.positive_button_dialog),
                        (dialog, which) ->
                        {
                            String userInput = input.getText().toString();
                            m_EventTitle.setText(userInput);
                        })
                .setNegativeButton(getResources().getString(R.string.negative_button_dialog),
                        null)
                .show();
    }
    // Function associated with description input in fragment_edit_event.xml. Creates a dialog
    // that receives description (text) input from user.
    private void openInputDialogDescription()
    {
        final EditText input = new EditText(requireContext());
        input.setText(m_EventDescription.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.description_dialog_label))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.positive_button_dialog),
                        (dialog, which) ->
                        {
                            String userInput = input.getText().toString();
                            m_EventDescription.setText(userInput);
                        })
                .setNegativeButton(getResources().getString(R.string.negative_button_dialog),
                        null)
                .show();
    }
}