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
        ArrayList<EmployeePayroll> employeePayrollArrayList= null;
        try {
            employeePayrollArrayList = employeePayrollService.readFromDB();
        } catch (RollBackFailedException | ConnectionNotClosedException | SqlDataOperationException e) {
            e.getMessage();
        }
        Assert.assertEquals(3,employeePayrollArrayList.size());
    }

    @Test
    public void givenSalaryWhenUpadtedShouldSyncWithDB() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        employeePayrollService.updateEmployeeSalary("Mark",(double)19000);
        boolean check= false;
        try {
            check = employeePayrollService.checkSalarySyncWithDB("Mark");
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        Assert.assertTrue(check);
    }

    @Test
    public void givenSalaryWhenUpadtedShouldSyncWithDBUsingPreparedStatement() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        employeePayrollService.updateEmployeeSalaryPreparedStatement("Charl",(double)25005);
        boolean check= false;
        try {
            check = employeePayrollService.checkSalarySyncWithDBPreparedStatement("Charl");
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        Assert.assertTrue(check);
    }

    @Test
    public void givenDateRangeRetrievedRecordCountShouldMatch() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        ArrayList<EmployeePayroll> employeePayrollArrayList= null;
        try {
            employeePayrollArrayList = employeePayrollService
                    .getEmployeeListBasedOnDate(LocalDate.of(2018,01,01),LocalDate.of(2019,12,01));
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        Assert.assertEquals(2,employeePayrollArrayList.size());
    }

    @Test
    public void retrieveAvgSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderAvg= null;
        try {
            genderAvg = employeePayrollService.getSalaryByAvgGender();
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        Assert.assertEquals(25005.0,genderAvg.get("F"),0.01);
        Assert.assertEquals(14500.0,genderAvg.get("M"),0.01);
    }

    @Test
    public void retrieveSumSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum= null;
        try {
            genderSum = employeePayrollService.getSalaryBySumGender();
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(29000.0,genderSum.get("M"),0.01);
    }

    @Test
    public void retrieveMaxSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum= null;
        try {
            genderSum = employeePayrollService.getMaxSalaryByGender();
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(19000.0,genderSum.get("M"),0.01);
    }

    @Test
    public void retrieveMinSalaryBasedOnGenderFromDatabase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        TreeMap<String, Double> genderSum= null;
        try {
            genderSum = employeePayrollService.getMinSalaryByGender();
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        Assert.assertEquals(25005.0,genderSum.get("F"),0.01);
        Assert.assertEquals(10000.0,genderSum.get("M"),0.01);
    }


    @Test
    public void givenNewEmployeewithPayrollWhenAddedSyncWithDB() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        try {
            employeePayrollService.addEmployeeData(4,"Jamie",'M',"pune",LocalDate.now(),(double)40000, "Research and development");
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        boolean check= false;
        try {
            check = employeePayrollService.checkSalarySyncWithDB("Jamie");
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        Assert.assertTrue(check);
    }

    @Test
    public void givenNameDeleteTheRecordFromDataBase() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        try {
            employeePayrollService.deleteEntryFromDataBase("Jamie");
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        ArrayList<EmployeePayroll> employeePayrollArrayList= null;
        try {
            employeePayrollArrayList = employeePayrollService.checkSalaryRecordInDBPreparedStatement("Jamie");
        } catch (RollBackFailedException | ConnectionNotClosedException e) {
            e.getMessage();
        }
        Assert.assertTrue(employeePayrollArrayList.isEmpty());


    }
}