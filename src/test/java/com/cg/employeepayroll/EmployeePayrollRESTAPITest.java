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
    private Response addEmployeeToJsonServer(EmployeePayroll employeePayroll) {
        String jsonFile=new Gson().toJson(employeePayroll);
        RequestSpecification requestSpecification=RestAssured.given();
        requestSpecification.header("Content-Type","application/json");
        requestSpecification.body(jsonFile);
        return requestSpecification.post("/employees");
    }

    @Test
    public void givenDataInJsonServerWhenRetrievedShouldMatchTheCount() {
        EmployeePayroll[] employeePayrolls=getEmployeeList();
        EmployeePayrollRESTAPI employeePayrollRESTAPI=new EmployeePayrollRESTAPI(Arrays.asList(employeePayrolls));
        long count= employeePayrollRESTAPI.countEntries();
        Assert.assertEquals(3,count);

    }

    @Test
    public void givenNewEmployeeWhenAddedMatch201ResponseCodeAndCount() {
        EmployeePayroll[] employeePayrolls=getEmployeeList();
        EmployeePayrollRESTAPI employeePayrollRESTAPI=new EmployeePayrollRESTAPI((Arrays.asList(employeePayrolls)));
        EmployeePayroll employeePayroll=new EmployeePayroll(4,"Mark",'M',"Ohio", LocalDate.now(),25000.0);
        Response response=addEmployeeToJsonServer(employeePayroll);
        int status=response.getStatusCode();
        Assert.assertEquals(201,status);

        employeePayroll=new Gson().fromJson(response.asString(),EmployeePayroll.class);
        employeePayrollRESTAPI.addEmployeeToList(employeePayroll);
        long count= employeePayrollRESTAPI.countEntries();
        Assert.assertEquals(4,count);
    }


}
