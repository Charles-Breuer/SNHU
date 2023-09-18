package com.zybooks.projectcs360;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewEvent extends Fragment
{

    //------------- Constructor(s) ----------------//
    public ViewEvent()
    {
        // Required empty public constructor
    }

    //------------- Override Functions ----------------//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // This onCreateView() function associates the current event in MainActivity m_SelectedEvent
    // to the TextView views in fragment_view_event.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_event, container, false);
        MainActivity activity = (MainActivity)requireActivity();
        EventModel currentEvent = activity.getSelectedEvent();
        if (currentEvent == null) { Navigation.findNavController(rootView).navigate(R.id.action_navigation_view_to_navigation_all_events); }

        //------------- Class Fields ----------------//
        TextView m_EventTitle = rootView.findViewById(R.id.view_event_title);
        TextView m_EventDescription = rootView.findViewById(R.id.view_event_description);
        TextView m_EventDate = rootView.findViewById(R.id.view_event_date);
        TextView m_EventTime = rootView.findViewById(R.id.view_event_time);

        assert currentEvent != null;
        m_EventTitle.setText(currentEvent.getTitle());
        m_EventDescription.setText(currentEvent.getDescription());
        m_EventDate.setText(Utility.convertDateToFullFormat(currentEvent.getDate()));
        m_EventTime.setText( Utility.convert24To12HourFormat(currentEvent.getTime()));
        return rootView;
    }
}