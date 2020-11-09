package com.cg.employeepayroll;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class EmployeePayrollRESTAPITest {
    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=3000;
    }
    private EmployeePayroll[] getEmployeeList() {
        Response response=RestAssured.get("/employees");
        System.out.println(response.asString());
        EmployeePayroll[] employeePayrolls=new Gson().fromJson(response.asString(),EmployeePayroll[].class);
        System.out.println(employeePayrolls[1]);
        return employeePayrolls;
    }

    @Test
    public void givenDataInJsonServerWhenRetrievedShouldMatchTheCount() {
        EmployeePayroll[] employeePayrolls=getEmployeeList();
        EmployeePayrollRESTAPI employeePayrollRESTAPI=new EmployeePayrollRESTAPI(Arrays.asList(employeePayrolls));
        long count= employeePayrollRESTAPI.countEntries();
        Assert.assertEquals(3,count);

    }

}
