package com.zybooks.projectcs360;

import static com.zybooks.projectcs360.Utility.convert24To12HourFormat;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class AllEvents extends Fragment
{
    //------------- Class Fields ----------------//
    TableLayout m_MainTable;

    //------------- Constructor(s) ----------------//
    public AllEvents()
    {
        // Required empty public constructor
    }


    //------------- Override Functions ----------------//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // This onCreateView function establishes member fields and dynamically creates
    // TableRow views that display event information for all events associated
    // with the current user.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MainActivity activity = (MainActivity)requireActivity();
        DatabaseHelper database = activity.getCurrentDatabase();
        long userID = activity.getCurrentUserID();
        List<EventModel> allEvents = database.getAllEventsForUser(userID);
        Utility.sortEventsDescending(allEvents);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);
        m_MainTable = rootView.findViewById(R.id.all_events_main_table);

        if (allEvents.size() == 0)
        {
            TableRow currentRow = new TableRow(requireContext());
            TextView currentTextView = new TextView(requireContext());
            currentTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            currentTextView.setText(getResources().getString(R.string.all_events_empty_label));
            currentTextView.setGravity(Gravity.CENTER);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            currentRow.setLayoutParams(params);
            TableRow.LayoutParams textParams = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
            );
            currentTextView.setLayoutParams(textParams);
            currentRow.setPadding(0, dpToPx(16), 0, 0);
            currentRow.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            currentRow.addView(currentTextView);
            m_MainTable.addView(currentRow);

            return rootView;
        }

        int colorBlack = ContextCompat.getColor(requireContext(), R.color.black);
        int colorWhite = ContextCompat.getColor(requireContext(), R.color.white);
        int colorPrimary2 = ContextCompat.getColor(requireContext(), R.color.primary_2);
        int colorPrimary3 = ContextCompat.getColor(requireContext(), R.color.primary_3);

        String dateCacheLabelGeneration = "";
        for (int count = 0; count < allEvents.size(); count++)
        {
            if (dateCacheLabelGeneration.isEmpty() || !(allEvents.get(count).getDate()).equals(dateCacheLabelGeneration))
            {
                String labelDisplay;
                dateCacheLabelGeneration = allEvents.get(count).getDate();

                labelDisplay =  Utility.getRelativeDateDifference(
                        Utility.getCurrentDate(),
                        dateCacheLabelGeneration);

                TableRow dateLabel = new TableRow(requireContext());
                TextView dateLabelTextView = new TextView(requireContext());

                TableLayout.LayoutParams dateLabelParams = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
                dateLabel.setLayoutParams(dateLabelParams);
                dateLabel.setBackgroundColor(colorPrimary2);

                TableRow.LayoutParams dateLabelTextViewParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                dateLabelTextView.setLayoutParams(dateLabelTextViewParams);
                dateLabelTextView.setText(labelDisplay);
                dateLabelTextView.setTextColor(colorWhite);
                dateLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                dateLabelTextView.setPadding(dpToPx(16), 0, dpToPx(16), dpToPx(6));

                dateLabel.addView(dateLabelTextView);
                m_MainTable.addView(dateLabel);

                View horizontalRule = new View(requireContext());
                TableLayout.LayoutParams horizontalRuleParam = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(2));
                horizontalRule.setLayoutParams(horizontalRuleParam);
                horizontalRule.setBackgroundColor(colorBlack);
                m_MainTable.addView(horizontalRule);
            }
            
            
            
            TableRow newEventMainContainer = new TableRow(requireContext());
            TableLayout newEventMainLayout = new TableLayout(requireContext());
            TableRow newEventTitleRow = new TableRow(requireContext());
            TextView newEventTitleTextView = new TextView(requireContext());
            TableRow newEventDescriptionRow = new TableRow(requireContext());
            TextView newEventDescriptionTextView = new TextView(requireContext());
            TextView newEventTimeTextView = new TextView(requireContext());

            // Set up TextView formats
            TableRow.LayoutParams titleParams = new TableRow.LayoutParams(
                    (int)(getScreenWidth() / 2.8),
                    TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams descriptionParams = new TableRow.LayoutParams(
                    (int)(getScreenWidth() / 1.5),
                    TableLayout.LayoutParams.WRAP_CONTENT);
            newEventTitleTextView.setText(allEvents.get(count).getTitle());
            newEventTitleTextView.setLayoutParams(titleParams);
            newEventTitleTextView.setPadding(dpToPx(16), 0, dpToPx(16), 0);
            newEventTitleTextView.setTextColor(colorWhite);
            newEventTitleTextView.setMaxLines(1);
            newEventTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            newEventTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            newEventTimeTextView.setText(convert24To12HourFormat(allEvents.get(count).getTime()));
            newEventTimeTextView.setLayoutParams(titleParams);
            newEventTimeTextView.setPadding(dpToPx(16), 0, dpToPx(16), 0);
            newEventTimeTextView.setTextColor(colorWhite);
            newEventTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            newEventTimeTextView.setMaxLines(1);
            newEventTimeTextView.setEllipsize(TextUtils.TruncateAt.END);
            newEventTimeTextView.setGravity(Gravity.END);
            newEventDescriptionTextView.setText(allEvents.get(count).getDescription());
            newEventDescriptionTextView.setLayoutParams(descriptionParams);
            newEventDescriptionTextView.setPadding(dpToPx(16), 0, dpToPx(16), dpToPx(4));
            newEventDescriptionTextView.setTextColor(colorWhite);
            newEventDescriptionTextView.setMaxLines(1);
            newEventDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);

            // Set up inner row format
            TableLayout.LayoutParams newEventRow = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT);
            newEventTitleRow.setLayoutParams(newEventRow);
            newEventDescriptionRow.setLayoutParams(newEventRow);

            // Set up main layout format
            TableRow.LayoutParams mainLayoutParams = new TableRow.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT);
            newEventMainLayout.setLayoutParams(mainLayoutParams);
            
            // Set up main container
            TableLayout.LayoutParams mainContainerParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT);
            newEventMainContainer.setLayoutParams(mainContainerParams);
            newEventMainContainer.setTag(allEvents.get(count).getId());
            newEventMainContainer.setBackgroundColor(colorPrimary3);
            newEventMainContainer.setClickable(true);
            newEventMainContainer.setOnClickListener(view -> {
                MainActivity mainActivity = (MainActivity) getActivity();
                DatabaseHelper database1 = activity.getCurrentDatabase();
                long eventID =  (Long)view.getTag();
                assert mainActivity != null;
                mainActivity.setSelectedEvent(database1.getEvent(eventID));
                Navigation.findNavController(view).navigate(R.id.action_navigation_all_events_to_navigation_view);
            });
            
            // Set up hierarchical structure of event XML
            newEventTitleRow.addView(newEventTitleTextView);
            newEventTitleRow.addView(newEventTimeTextView);
            newEventDescriptionRow.addView(newEventDescriptionTextView);
            newEventMainLayout.addView(newEventTitleRow);
            newEventMainLayout.addView(newEventDescriptionRow);
            newEventMainContainer.addView(newEventMainLayout);
            m_MainTable.addView(newEventMainContainer);

            // Add Horizontal Rule for aesthetics

            View horizontalRule = new View(requireContext());
            TableLayout.LayoutParams horizontalRuleParam = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(2));
            horizontalRule.setLayoutParams(horizontalRuleParam);
            horizontalRule.setBackgroundColor(colorBlack);
            m_MainTable.addView(horizontalRule);
        }



        return rootView;
    }


    //------------- Internal Functions ----------------//

    // This is a simple utility function that converts dp-scale values
    // into pixels that can be used when created views programmatically
    private int dpToPx(int dp)
    {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // This utility function retrieves the current screen width of the
    // device being used
    private int getScreenWidth()
    {
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(requireContext().WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

}