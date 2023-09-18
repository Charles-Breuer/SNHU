package com.zybooks.projectcs360;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Utility
{


    //------------- External Functionality ----------------//

    // Function retrieves the current date in a format compatible with database values.
    public static String getCurrentDate()
    {
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return currentDate.format(formatter);
    }

    // Function used in AllEvents class to provide cleaner representation of dates
    // for better user experience.
    public static String getRelativeDateDifference(String currentDateStr, String otherDateStr)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate currentDate = LocalDate.parse(currentDateStr, formatter);
        LocalDate otherDate = LocalDate.parse(otherDateStr, formatter);

        long daysDifference = ChronoUnit.DAYS.between(currentDate, otherDate);

        if (daysDifference == 0) {
            return "Today";
        } else if (daysDifference == 1) {
            return "Tomorrow";
        } else if (daysDifference == -1) {
            return "Yesterday";
        } else {
            return convertDateToFullFormat(otherDateStr);
        }
    }

    // Simple conversion function to display dates in easier to read format.
    public static String convertDateToFullFormat(String inputDate)
    {
        String[] months =
                {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
                 };

        String[] dateParts = inputDate.split("/");

        if (dateParts.length != 3)
        {
            throw new IllegalArgumentException("Invalid date format provided");
        }

        // Convert month to its full name
        int monthIndex = Integer.parseInt(dateParts[0]) - 1;  // -1 because array indices start at 0
        String monthName = months[monthIndex];

        // Convert day with its ordinal
        int day = Integer.parseInt(dateParts[1]);
        String dayWithSuffix = getDayWithSuffix(day);

        return monthName + " " + dayWithSuffix + ", " + dateParts[2];
    }

    // Convert 24 to 12 hour time formats.
    public static String convert24To12HourFormat(String time24)
    {
        try
        {
            // Parse the given string with the 24-hour format
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");
            Date date = format24.parse(time24);

            // Convert to 12-hour format
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format12 = new SimpleDateFormat("hh:mm a");
            assert date != null;
            return format12.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    // Convert 12 to 24 hour time formats.
    public static String convert12To24HourFormat(String time12)
    {
        try
        {
            // Parse the given string with the 12-hour format
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format12 = new SimpleDateFormat("h:mm a");
            Date date = format12.parse(time12);

            // Convert to 24-hour format
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");
            assert date != null;
            return format24.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    // Function ensures dates are compatible with Java standard library API when sorting.
    public static String convertToJavaDateFormat(String inputDate)
    {
        // Split the input date string based on "/"
        String[] dateParts = inputDate.split("/");

        if (dateParts.length != 3)
        {
            throw new IllegalArgumentException("Invalid date format provided");
        }

        // Re-arrange the date parts to the desired format

        return dateParts[2] + "-" + dateParts[0] + "-" + dateParts[1];
    }

    // Sort events based on their date and time. Used in AllEvents class.
    public static void sortEventsDescending(List<EventModel> events)
    {
        events.sort(new Comparator<EventModel>() {
            @Override
            public int compare(EventModel o1, EventModel o2) {
                // First, compare the dates
                String o1Date = convertToJavaDateFormat(o2.getDate());
                String o2Date = convertToJavaDateFormat(o1.getDate());
                int dateComparison = o2Date.compareTo(o1Date);

                // If dates are the same, compare the times
                if (dateComparison == 0) {
                    return o2.getTime().compareTo(o1.getTime());
                }

                return -dateComparison;
            }
        });
    }

    //------------- Internal Functionality ----------------//

    // Provide cleaner representation of date data.
    private static String getDayWithSuffix(int day)
    {
        if (day >= 11 && day <= 13)
        {
            return day + "th";
        }
        switch (day % 10)
        {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }
}
