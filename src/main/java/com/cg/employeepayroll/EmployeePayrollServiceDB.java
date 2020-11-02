package com.cg.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmployeePayrollServiceDB {
    ArrayList<EmployeePayroll> employeePayrollArrayList=new ArrayList<EmployeePayroll>();
    static PreparedStatement preparedStatement;
    static Statement statement;

    public ArrayList<EmployeePayroll> readFromDB() {
        String query="select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id;";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            employeePayrollArrayList=getEmployeeListFromResultSet(resultSet,employeePayrollArrayList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  employeePayrollArrayList;
    }

    private ArrayList<EmployeePayroll> getEmployeeListFromResultSet(ResultSet resultSet, ArrayList<EmployeePayroll> employeePayrollArrayList) {
      try{  while(resultSet.next()){
                int id=resultSet.getInt("emp_id");
                String name=resultSet.getString("name");
                char gender=resultSet.getString("gender").charAt(0);
                String address=resultSet.getString("address");
                LocalDate date=resultSet.getDate("start").toLocalDate();
                double salary=resultSet.getDouble("basic_pay");
                employeePayrollArrayList.add(new EmployeePayroll(id,name,gender,address,date,salary));}
      } catch (SQLException throwables) {
          throwables.printStackTrace();
      }
      return employeePayrollArrayList;
    }

    public int updateEmployeeSalaryInDB(String name, double salary) {
        int result = 0;
        String query=" update payroll_details inner join employee_details on employee_details.emp_id=payroll_details.emp_id set payroll_details.basic_pay="+salary+" where employee_details.name='"+name+"';";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
             statement=connection.createStatement();
           result= statement.executeUpdate(query);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  result;
    }

    public void updateEmployeeSalary(String name, double salary) {
        int check=updateEmployeeSalaryInDB(name,salary);
        if(check!=0)
        updateEmployeeSalaryInObject(name,salary);
        else System.out.println("value not updated in database");
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
        System.out.println(employeePayrollArrayListDB.toString());
        System.out.println(checkSalaryRecordInDB(name).toString());
       return employeePayrollArrayListDB.toString().equals(checkSalaryRecordInDB(name).toString());
    }

    private ArrayList<EmployeePayroll> checkSalaryRecordInDB(String name) {
        ArrayList<EmployeePayroll> employeePayrollUpdatedList=new ArrayList<EmployeePayroll>();
        String query="select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where employee_details.name='"+name+"';";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            while(resultSet.next()){
                int id=resultSet.getInt("emp_id");
                String empname=resultSet.getString("name");
                char gender=resultSet.getString("gender").charAt(0);
                String address=resultSet.getString("address");
                LocalDate date=resultSet.getDate("start").toLocalDate();

                employeePayrollUpdatedList.add(new EmployeePayroll(id,name,gender,address,date));}
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollUpdatedList;
    }

    public void updateEmployeeSalaryPreparedStatement(String name, double salary) {
        int check= updateEmployeeSalaryInDBPreparedStatement(name,salary);
        if(check!=0)
        updateEmployeeSalaryInObject(name,salary);
        else System.out.println("query was not executed");
    }

    private int updateEmployeeSalaryInDBPreparedStatement(String name, double salary) {
        int result = 0;
        String query=" update payroll_details inner join employee_details on employee_details.emp_id=payroll_details.emp_id set payroll_details.basic_pay=? where employee_details.name=?;";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
             preparedStatement=connection.prepareStatement(query);
            preparedStatement.setDouble(1,salary);
            preparedStatement.setString(2,name);
            result= preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  result;
    }

    public boolean checkSalarySyncWithDBPreparedStatement(String name) {
        ArrayList<EmployeePayroll> employeePayrollArrayListDB=new ArrayList<EmployeePayroll>( employeePayrollArrayList.stream().
                filter(p->p.getName().
                        equals(name)).collect(Collectors.toList()));
        return employeePayrollArrayListDB.toString().equals(checkSalaryRecordInDBPreparedStatement(name).toString());
    }

    private ArrayList<EmployeePayroll> checkSalaryRecordInDBPreparedStatement(String name) {
        ArrayList<EmployeePayroll> employeePayrollUpdatedList=new ArrayList<EmployeePayroll>();
        String query="select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where employee_details.name=?;";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            ResultSet resultSet=preparedStatement.executeQuery();
            employeePayrollUpdatedList=getEmployeeListFromResultSet(resultSet,employeePayrollUpdatedList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollUpdatedList;
    }

    public ArrayList<EmployeePayroll> getEmployeeListBasedOnDate(LocalDate startDate, LocalDate endDate) {
        ArrayList<EmployeePayroll> employeePayrollUpdatedList=new ArrayList<EmployeePayroll>();
        String query="select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join " +
                "payroll_details on employee_details.emp_id=payroll_details.emp_id where" +
                " employee_details.start between ? and ?;";
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try {
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setDate(1,Date.valueOf(startDate));
            preparedStatement.setDate(2,Date.valueOf(endDate));
            ResultSet resultSet=preparedStatement.executeQuery();
          employeePayrollUpdatedList=getEmployeeListFromResultSet(resultSet,employeePayrollUpdatedList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollUpdatedList;
    }

    public TreeMap<String, Double> getSalaryByAvgGender() {

        String query="select avg(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    private TreeMap<String, Double> getMapofGenderSalary(String query) {
        TreeMap<String, Double> stringDoubleTreeMap=new TreeMap<String, Double>();
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try{
            statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            while(resultSet.next()){
                Double salary=resultSet.getDouble("salary");
                String gender=resultSet.getString("gender");
                stringDoubleTreeMap.put(gender,salary);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return stringDoubleTreeMap;
    }

    public TreeMap<String, Double> getSalaryBySumGender() {
        String query="select sum(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    public TreeMap<String, Double> getMaxSalaryByGender() {
        String query="select max(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    public TreeMap<String, Double> getMinSalaryByGender() {
        String query="select min(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id group by employee_details.gender";
        return getMapofGenderSalary(query);

    }

    public void addEmployeeData(int id,String name, Character gender, String address, LocalDate date) {

        String query=String.format("insert into employee_details (emp_id,name,gender,address,start) " +
                "values (%s,'%s','%s','%s','%s');",id,name,gender,address,Date.valueOf(date));
        DatabaseConnection databaseConnection=new DatabaseConnection();
        Connection connection=databaseConnection.getConnecton();
        try{
            statement=connection.createStatement();
        int rowaffected=statement.executeUpdate(query);
        if(rowaffected==1){
            employeePayrollArrayList=readFromDB();
            System.out.println(employeePayrollArrayList.toString());
            employeePayrollArrayList.add(new EmployeePayroll(id,name,gender,address,date));
        }
        else System.out.println("data not added");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
