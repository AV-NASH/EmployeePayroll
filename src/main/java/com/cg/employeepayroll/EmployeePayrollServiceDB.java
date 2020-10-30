package com.cg.employeepayroll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeePayrollServiceDB {

    public ArrayList<EmployeePayroll> readFromDB() {
        String query="select employee_details.emp_id,employee_details.name,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id;";
        ArrayList<EmployeePayroll> employeePayrollArrayList=new ArrayList<EmployeePayroll>();
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            while(resultSet.next()){
                int id=resultSet.getInt("emp_id");
                String name=resultSet.getString("name");
                String address=resultSet.getString("address");
                LocalDate date=resultSet.getDate("start").toLocalDate();
                double salary=resultSet.getDouble("basic_pay");
                employeePayrollArrayList.add(new EmployeePayroll(id,name,address,date,salary));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  employeePayrollArrayList;
    }
}
