package com.cg.employeepayroll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmployeePayrollServiceDB {
    ArrayList<EmployeePayroll> employeePayrollArrayList=new ArrayList<EmployeePayroll>();

    public ArrayList<EmployeePayroll> readFromDB() {
        String query="select employee_details.emp_id,employee_details.name,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id;";
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

    public int updateEmployeeSalaryInDB(String name, double salary) {
        int result = 0;
        String query=" update payroll_details inner join employee_details on employee_details.emp_id=payroll_details.emp_id set payroll_details.basic_pay="+salary+" where employee_details.name='"+name+"';";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            Statement statement=connection.createStatement();
           result= statement.executeUpdate(query);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  result;
    }

    public void updateEmployeeSalary(String name, double salary) {
        updateEmployeeSalaryInDB(name,salary);
        updateEmployeeSalaryInObject(name,salary);
    }

    private void updateEmployeeSalaryInObject(String name, double salary) {
        employeePayrollArrayList=readFromDB();
        employeePayrollArrayList.stream()
                .filter(p->p.getName().equals(name))
                .forEach(p->p.setSalary(salary));
    }

    public boolean checkSalarySyncWithDB(String name) {
       ArrayList<EmployeePayroll> employeePayrollArrayListDB=new ArrayList<EmployeePayroll>( employeePayrollArrayList.stream().
               filter(p->p.getName().
                       equals(name)).collect(Collectors.toList()));
//        employeePayrollArrayListDB.forEach(p-> System.out.println(p.toString()));
//        checkSalaryRecordInDB(name).forEach(p-> System.out.println(p.toString()));
//        System.out.println(employeePayrollArrayListDB.toString());
       return employeePayrollArrayListDB.toString().equals(checkSalaryRecordInDB(name).toString());
    }

    private ArrayList<EmployeePayroll> checkSalaryRecordInDB(String name) {
        ArrayList<EmployeePayroll> employeePayrollupdatedlist=new ArrayList<EmployeePayroll>();
        String query="select employee_details.emp_id,employee_details.name,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where employee_details.name='"+name+"';";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            while(resultSet.next()){
                int id=resultSet.getInt("emp_id");
                String nameemployee=resultSet.getString("name");
                String address=resultSet.getString("address");
                LocalDate date=resultSet.getDate("start").toLocalDate();
                double salary=resultSet.getDouble("basic_pay");
                employeePayrollupdatedlist.add(new EmployeePayroll(id,nameemployee,address,date,salary));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollupdatedlist;
    }
}
