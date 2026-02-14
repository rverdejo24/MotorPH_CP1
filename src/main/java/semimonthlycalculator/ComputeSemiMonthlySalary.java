/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semimonthlycalculator;

import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author rowel
 */
public class ComputeSemiMonthlySalary {
    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        double[] week1 = {8, 8, 12, 10, 9, 4, 4};
        double[] week2 = {8, 8, 8, 8, 10, 0, 0};
        double[] week3 = {8, 8, 11, 8, 8, 0, 0};
        double[] week4 = {8, 8, 9, 8, 8, 12, 0};
        double[][] monthlyAttendance = {week1, week2, week3, week4};
        double[][] monthlyRegularHours = new double[4][7];
        double[][] monthlyOvertimeHours = new double[4][7];

        // Employee Name
        System.out.println("Enter employee name: ");
        String employeeName = scanner.nextLine();
        System.out.println("Hello " + employeeName);

        // Employee hourly rate
        System.out.println("Enter employee hourly rate: ");
        double salaryRatePerHour = scanner.nextDouble();

        // Employee Allowances
        System.out.println("Enter employee Rice Allowance: ");
        double salaryAllowance = scanner.nextDouble();

        System.out.println("Enter employee Phone Allowance: ");
        double phoneAllowance = scanner.nextDouble();

        System.out.println("Enter employee Clothing Allowance: ");
        double clothingAllowance = scanner.nextDouble();

        for (int i = 0; i < monthlyAttendance.length; i++) {
            System.out.println("Calculating regular hours and overtime hours for week " + (i + 1));

            monthlyRegularHours[i] = regularWeeklyHours(monthlyAttendance[i]);
            monthlyOvertimeHours[i] = overtimeWeeklyHours(monthlyAttendance[i]);

            System.out.println("Regular hours for week " + (i + 1) + ": " + Arrays.toString(monthlyRegularHours[i]));
            System.out.println("Overtime hours for week " + (i + 1) + ": " + Arrays.toString(monthlyOvertimeHours[i]));
        }
    }

    static double[] regularWeeklyHours(double[] week) {
        double[] result = new double[week.length];

        for (int i = 0; i < week.length; i++) {
            if (week[i] > 8 && i < 5) {
                result[i] = 8;
            } else {
                result[i] = week[i];
            }
        }

        return result;
    }

    static double[] overtimeWeeklyHours(double[] week) {
        double[] result = new double[week.length];

        for (int i = 0; i < week.length; i++) {
            if (i >= 5) {
                result[i] = week[i];
            } else {
                result[i] = Math.max(0, week[i] - 8);
            }
        }

        return result;
    }
}
