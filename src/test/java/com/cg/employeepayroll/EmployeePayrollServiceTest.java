package com.cg.employeepayroll;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeePayrollServiceTest {
    @Test
    public void givenEmployeePayrollDB_whenRecieved_shouldMatchemployeeCount() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        ArrayList<EmployeePayroll> employeePayrollArrayList=employeePayrollService.readFromDB();
        Assert.assertEquals(3,employeePayrollArrayList.size());
    }

    @Test
    public void givenSalaryWhenUpadtedShouldSyncWithDB() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        employeePayrollService.updateEmployeeSalary("Mark",(double)19000);
        boolean check=employeePayrollService.checkSalarySyncWithDB("Mark");
        Assert.assertTrue(check);
    }

    @Test
    public void givenSalaryWhenUpadtedShouldSyncWithDBUsingPreparedStatement() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        employeePayrollService.updateEmployeeSalaryPreparedStatement("Charl",(double)25005);
        boolean check=employeePayrollService.checkSalarySyncWithDBPreparedStatement("Charl");
        Assert.assertTrue(check);
    }

    @Test
    public void givenDateRangeRetrievedRecordCountShouldMatch() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
       ArrayList<EmployeePayroll> employeePayrollArrayList= employeePayrollService
               .getEmployeeListBasedOnDate(LocalDate.of(2018,01,01),LocalDate.of(2019,12,01));
       Assert.assertEquals(2,employeePayrollArrayList.size());
    }
}