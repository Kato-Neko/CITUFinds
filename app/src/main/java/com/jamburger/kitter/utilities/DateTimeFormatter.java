package com.jamburger.kitter.utilities;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeFormatter {
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH);

    static {
        format.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Set default time zone to Philippine Time
    }

    public static String getTimeDifference(String dateId, boolean returnShorter) {
        try {
            Date date = format.parse(dateId);
            Date now = new Date();
            assert date != null;
            long differenceInMillis = now.getTime() - date.getTime();
            long differenceInSeconds = differenceInMillis / 1000;
            long differenceInMinutes = differenceInSeconds / 60;
            long differenceInHours = differenceInMinutes / 60;
            long differenceInDays = differenceInHours / 24;
            long differenceInWeeks = differenceInDays / 7;

            String timeText = "";
            if (differenceInMinutes == 0) {
                timeText = differenceInSeconds + " second";
                if (differenceInSeconds > 1) timeText += "s";
                if (returnShorter) timeText = differenceInSeconds + "s";
            } else if (differenceInHours == 0) {
                timeText = differenceInMinutes + " minute";
                if (differenceInMinutes > 1) timeText += "s";
                if (returnShorter) timeText = differenceInMinutes + "m";
            } else if (differenceInDays == 0) {
                timeText = differenceInHours + " hour";
                if (differenceInHours > 1) timeText += "s";
                if (returnShorter) timeText = differenceInHours + "h";
            } else if (differenceInWeeks == 0) {
                timeText = differenceInDays + " day";
                if (differenceInDays > 1) timeText += "s";
                if (returnShorter) timeText = differenceInDays + "d";
            } else {
                timeText = differenceInWeeks + " week";
                if (differenceInWeeks > 1) timeText += "s";
                if (returnShorter) timeText = differenceInWeeks + "w";
            }
            if (!returnShorter) timeText += " ago";
            return timeText;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateMonth(String dateId) {
        try {
            Date date = format.parse(dateId);
            if (date == null) {
                return null;
            }
            SimpleDateFormat monthFormat = new SimpleDateFormat("d MMMM", Locale.ENGLISH);
            monthFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
            return monthFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentTime() {
        format.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Ensure the format generates in Philippine Time
        return format.format(new Date());
    }

    public static String getHoursMinutes(String dateId) {
        try {
            Date date = format.parse(dateId);
            if (date == null) {
                return null;
            }
            SimpleDateFormat hoursMinutesFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            hoursMinutesFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
            return hoursMinutesFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

            Date date = inputFormat.parse(timestamp);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
