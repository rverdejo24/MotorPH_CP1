package hoursworkedcalculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CalculateHoursWorkedTest {
    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Test
    void TestCalculateRegularHours() {
        System.out.println("Starting test for regular hours calculation.");
        double[] dailyHour = {12, 0, 8, 10, 12, 12, 0};
        double[] expectedRegularHours = {8, 0, 8, 8, 8, 0, 0};

        for (int i = 0; i < dailyHour.length; i++) {
            System.out.println("Test number: " + (i + 1));
            System.out.println("expected: " +  expectedRegularHours[i]);
            System.out.println("actual: " +  CalculateHoursWorked.calculateRegularHours(dailyHour[i], daysOfWeek[i]));
            Assertions.assertEquals(expectedRegularHours[i], CalculateHoursWorked.calculateRegularHours(dailyHour[i], daysOfWeek[i]));
        }
    }

    @Test
    void TestCalculateOvertimeHours() {
        System.out.println("Starting test for overtime hours calculation.");
        double[] dailyHour = {12, 0, 8, 10, 12, 12, 0};
        double[] expectedOvertimeHours = {4, 0, 0, 2, 4, 12, 0};

        for (int i = 0; i < dailyHour.length; i++) {
            System.out.println("Test number: " + (i + 1));
            System.out.println("expected: " +  expectedOvertimeHours[i]);
            System.out.println("actual: " + CalculateHoursWorked.calculateOvertimeHours(dailyHour[i], daysOfWeek[i]));
            Assertions.assertEquals(expectedOvertimeHours[i], CalculateHoursWorked.calculateOvertimeHours(dailyHour[i], daysOfWeek[i]));
        }
    }

}