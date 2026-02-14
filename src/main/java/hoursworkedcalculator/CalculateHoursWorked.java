package hoursworkedcalculator;

import java.time.Duration;
//import java.time.LocalDate;
import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * CalculateHoursWorked
 * <br/>
 * This program calculates the weekly hours of an employee including regular hours and overtime hours.
 * It accounts for lunch breaks and handles special cases such as weekend attendance, skipped days and skipped breaks.
 * <br/>
 * Features:
 * - Input validation for time-in, time-out, and break times.
 * - Custom exception to handle skipped days.
 * - Calculates regular hours and overtime hours based on weekday/weekend rules.
 * - Implement the 10minute grace period for employee that logs in between 8:00 & 8:10
 * <br/>
 * Example Usage:
 * Employee: John Doe
 * Monday: 08:00 - 17:00 with 1-hour break
 * Output: Regular hours = 8, Overtime = 0
 */

public class CalculateHoursWorked {

    /**
     * Created a custom exception to handle absences
     * This allows control flow to continue to the next day without terminating the program.
     */
     static class SkipDayException extends RuntimeException {}

    /**
     * Main entry point of the program.
     * Prompts the user for employee details and daily work times for a full week,
     * calculates net hours worked, regular hours, and overtime.
     */
     static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        double[] weeklyHours = new double[7];
        double[] regularWeeklyHours = new double[7];
        double[] overtimeWeeklyHours = new double[7];

        // Employee Name
        System.out.println("Enter employee name: ");
        String employeeName = scanner.nextLine();
        System.out.println("Hello " + employeeName);

        // Loop through each day of the week to collect work times
        for (int i = 0; i < daysOfWeek.length; i++) {
            System.out.println("Good morning, today is " + daysOfWeek[i]);
            try {
                // Read time-in from user input (Time in HH:mm format)
                LocalTime timeInParsed = readTime(scanner, "Enter time-in (format(Military-time): 08:00, 14:30): ");

                // Lunch break start
                LocalTime breakTimeStartParsed;
                // while loop was added to handle control flow to repeat the current iteration if ever there's an error in the time input.
                while (true) {
                    breakTimeStartParsed = readBreakTime(scanner, "Enter break time start(format(Military-time): 08:00, 14:30): ");
                    if (breakTimeStartParsed != null && breakTimeStartParsed.isBefore(timeInParsed)) {
                        System.out.println("Break start cannot be before time-in.");
                    } else {
                        break;
                    }
                }

                // Read Lunch break end with validation
                LocalTime breakTimeEndParsed;
                // while loop was added to handle control flow to repeat the current iteration if ever there's an error in the time input.
                while (true) {
                    if (breakTimeStartParsed == null) { // If the user skips the break
                        breakTimeEndParsed = null;
                        break;
                    }

                    breakTimeEndParsed = readBreakTime(scanner, "Enter break time end(format(Military-time)): ");
                    if (breakTimeEndParsed != null && breakTimeEndParsed.isBefore(breakTimeStartParsed)) {
                        System.out.println("Break end cannot be before break time start.");
                    } else {
                        break;
                    }
                }


                // Read time-out with validation against time-in and break end (Time out HH:mm format)
                LocalTime timeOutParsed;
                while (true) {
                    timeOutParsed = readTime(scanner, "Enter time-out (format(Military-time): 17:30, 22:30):");
                    if (timeOutParsed.isBefore(timeInParsed)) {
                        System.out.println("Time out cannot be before time-in.");
                    } else {
                        // Handles over break if it exceeds the time-out
                        if (breakTimeEndParsed != null && breakTimeEndParsed.isAfter(timeOutParsed)) {
                            breakTimeEndParsed = timeOutParsed;
                        }
                        break;
                    }
                }


                // Calculate the total worked hours minus break duration
                Duration workedHoursDuration = Duration.between(timeInParsed, timeOutParsed);
                Duration breakTimeDuration = Duration.ZERO;

                if (breakTimeStartParsed != null && breakTimeEndParsed != null) {
                    breakTimeDuration = Duration.between(breakTimeStartParsed, breakTimeEndParsed);
                }

                Duration netWorkingHours = workedHoursDuration.minusMinutes(breakTimeDuration.toMinutes());
                double totalWorkingHours = (double) netWorkingHours.toMinutes() / 60;

                // Calculate regular and overtime hours based on business rules
                double regularHours = calculateRegularHours(totalWorkingHours, daysOfWeek[i]);
                double overtimeHours = calculateOvertimeHours(totalWorkingHours, daysOfWeek[i]);

                // control flow for handling weekend attendances: all hours are considered overtime
                if (daysOfWeek[i].equals("Saturday") || daysOfWeek[i].equals("Sunday")) {
                    overtimeWeeklyHours[i] = totalWorkingHours;
                    regularWeeklyHours[i] = 0;
                    weeklyHours[i] = totalWorkingHours;
                } else {
                    weeklyHours[i] = regularHours + overtimeHours;
                    regularWeeklyHours[i] = regularHours;
                    overtimeWeeklyHours[i] = overtimeHours;
                }

                // Display daily results
                System.out.println("Total worked hours for: " + daysOfWeek[i] + " " + totalWorkingHours);
                System.out.println("Regular hours: " + regularHours);
                System.out.println("Overtime hours: " + overtimeHours);
                System.out.println("---------\n");

            } catch (SkipDayException e) {
                // Handle skipped day
                System.out.println("Skipping " + daysOfWeek[i]);
            }
        }

         // Weekly summary output
         System.out.println("Record for the week.");
         System.out.println("Total worked hours for: " + Arrays.toString(weeklyHours));
         System.out.println("Regular hours worked: " + Arrays.toString(regularWeeklyHours));
         System.out.println("Overtime hours worked: " + Arrays.toString(overtimeWeeklyHours));

         // Close the scanner to release the underlying input stream resources
         // Although the program is terminating, explicitly closing the Scanner
         // is a good practice to prevent potential resource leaks in larger applications.
         scanner.close();
    }

    /**
     * Calculates regular hours based on weekday rules.
     * Weekdays: first 8 hours are regular, excess are considered overtime.
     * Weekends: regular hours = 0
     *
     * @param wh Total hours worked for the day
     * @param dayOfWeek  Name of the day
     * @return Regular hours
     * @throws IllegalArgumentException if worked hours are negative
     */
    static double calculateRegularHours(double wh, String dayOfWeek) {
         // handle cases where work hours is a negative value.
         if (wh < 0) {
            throw new IllegalArgumentException("Worked hours cannot be negative.");
         }

         switch (dayOfWeek) {
             case "Monday":
             case "Tuesday":
             case "Wednesday":
             case "Thursday":
             case "Friday":
                 if (wh >= 7.8333) {
                     return 8;
                 }
                 return wh; // regular hours capped at 8
             default:
                 return 0; // No regular hours for weekends
         }
    }

    /**
     * Calculates overtime hours based on business rules.
     * Weekdays: any hours above 8 are overtime
     * Weekends: all hours are overtime
     *
     * @param wh Total hours based on business rules.
     * @param dayOfWeek Name of the day
     * @return Overtime hours
     * @throws IllegalArgumentException if worked hours are negative
     */
    static double calculateOvertimeHours(double wh, String dayOfWeek) {
         // handle cases where work hours is a negative value.
         if (wh < 0) {
             throw new IllegalArgumentException("Worked hours cannot be negative.");
         }

         switch (dayOfWeek) {
             case "Saturday":
             case "Sunday":
                 return wh; // All weekend hours count as overtime
             default:
                 return Math.max(0, wh - 8); // Weekday overtime only above 8 hours
         }
    }

    /**
     * Reads a time input from the user and validates it.
     * Allows skipping the day by typing "skip" or pressing enter.
     *
     * @param scanner Scanner object for input
     * @param message Prompt message
     * @return Parsed LocalTime
     * @throws SkipDayException if the user skips the day
     */
    static LocalTime readTime(Scanner scanner, String message) {
         while(true) {
             System.out.println(message + "(Type \"skip\" to skip this day.)");
             String input = scanner.nextLine();

             if (input.equalsIgnoreCase("skip") || input.isEmpty()) {
                throw new SkipDayException();
             }

             try {

                 return LocalTime.parse(input);

             } catch (DateTimeParseException e) {

                 System.out.println("Invalid time format. Please use HH:mm (e.g., 08:00, 14:30 etc.,) " + e.getMessage());

             }
         }
    }

    /**
     * Reads a break time input from the user and validates it.
     * Allows skipping breaks by typing "skip" or pressing enter.
     *
     * @param scanner Scanner object for input
     * @param message Prompt message
     * @return Parsed LocalTime
     */
    static LocalTime readBreakTime(Scanner scanner, String message) {
         while (true) {
             System.out.println(message);
             String input = scanner.nextLine();

             // if ever employees wanted to skip breaks
             if (input.equalsIgnoreCase("skip") || input.isEmpty()) {
                 return null; // If user chooses not to take a break
             }

             try {

                 return LocalTime.parse(input);

             } catch (DateTimeParseException e) {

                 System.out.println("Invalid time format. Please use HH:mm (e.g., 08:00, 14:30 etc.,) " + e.getMessage());

             }

         }
    }
}
