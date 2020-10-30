package com.cg.employeepayroll;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class EmployeePayrollServiceTest {
    @Test
    public void givenEmployeePayrollDB_whenRecieved_shouldMatchemployeeCount() {
        EmployeePayrollServiceDB employeePayrollService=new EmployeePayrollServiceDB();
        ArrayList<EmployeePayroll> employeePayrollArrayList=employeePayrollService.readFromDB();
        Assert.assertEquals(3,employeePayrollArrayList.size());
    }
}