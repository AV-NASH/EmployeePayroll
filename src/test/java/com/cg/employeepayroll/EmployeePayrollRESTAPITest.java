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

    @Test
    public void givenListOfEmployeesWhenAddedMatch201StatusCodeAndCount() throws ThreadInterruptionException {
        EmployeePayroll[] employeePayrolls=getEmployeeList();
        EmployeePayrollRESTAPI employeePayrollRESTAPI=new EmployeePayrollRESTAPI((Arrays.asList(employeePayrolls)));
        EmployeePayroll[] newEmployeePayroll={new EmployeePayroll(5,"Cory",'M',"Madras", LocalDate.now(),65000.0),
                new EmployeePayroll(6,"Sam",'M',"London", LocalDate.now(),43000.0)
        };
        ArrayList<EmployeePayroll> employeePayrollArrayList=new ArrayList<>(Arrays.asList(newEmployeePayroll));
        int[] count=new int[1];
        count[0]=0;
        employeePayrollArrayList.stream().forEach(p->{
            Runnable runnable=()->{
                Response response=addEmployeeToJsonServer(p);
                int status=response.getStatusCode();
                Assert.assertEquals(201,status);
                EmployeePayroll employeePayroll=new Gson().fromJson(response.asString(),EmployeePayroll.class);
                employeePayrollRESTAPI.addEmployeeToList(employeePayroll);
                count[0]++;
                System.out.println( count[0]);
            };
            Thread thread=new Thread(runnable);
            thread.start();
        });
        while(count[0]<employeePayrollArrayList.size()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new ThreadInterruptionException("Process was Interrupted for some reason..");
            }
        }
        long check= employeePayrollRESTAPI.countEntries();
        Assert.assertEquals(6,check);

    }


    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch200Response() {
        EmployeePayroll[] employeePayrolls=getEmployeeList();
        EmployeePayrollRESTAPI employeePayrollRESTAPI=new EmployeePayrollRESTAPI((Arrays.asList(employeePayrolls)));
        employeePayrollRESTAPI.updateEmployeePayrollData("Bill",54000.0);
        EmployeePayroll employeePayroll=employeePayrollRESTAPI.getEmployeePayrollData("Bill");

        String jsonFile=new Gson().toJson(employeePayroll);
        RequestSpecification requestSpecification=RestAssured.given();
        requestSpecification.header("Content-Type","application/json");
        requestSpecification.body(jsonFile);
        Response response= requestSpecification.put("/employees/"+employeePayroll.getId());
        int statuscode=response.getStatusCode();
        Assert.assertEquals(200,statuscode);
    }

}
