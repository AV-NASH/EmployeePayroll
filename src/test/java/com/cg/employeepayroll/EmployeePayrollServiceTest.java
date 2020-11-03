package com.cg.employeepayroll;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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

    @Test
    public void retrieveAvgSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderAvg=employeePayrollService.getSalaryByAvgGender();
        Assert.assertEquals(25005.0,genderAvg.get("F"),0.01);
        Assert.assertEquals(14500.0,genderAvg.get("M"),0.01);
    }

    @Test
    public void retrieveSumSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum=employeePayrollService.getSalaryBySumGender();
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(29000.0,genderSum.get("M"),0.01);
    }

    @Test
    public void retrieveMaxSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum=employeePayrollService.getMaxSalaryByGender();
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(19000.0,genderSum.get("M"),0.01);
    }

    @Test
    public void retrieveMinSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum=employeePayrollService.getMinSalaryByGender();
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(10000.0,genderSum.get("M"),0.01);
    }


    @Test
    public void givenNewEmployeewithPayrollWhenAddedSyncWithDB() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        employeePayrollService.addEmployeeData(4,"Jamie",'M',"pune",LocalDate.now(),(double)40000, "Research and development");
        boolean check=employeePayrollService.checkSalarySyncWithDB("Jamie");
        Assert.assertTrue(check);
    }

    @Test
    public void name() {
    }
}