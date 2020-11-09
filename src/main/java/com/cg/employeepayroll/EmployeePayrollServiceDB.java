package com.cg.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EmployeePayrollServiceDB {
    ArrayList<EmployeePayroll> employeePayrollArrayList = new ArrayList<EmployeePayroll>();
    static PreparedStatement preparedStatement;
    static Statement statement;

    public ArrayList<EmployeePayroll> readFromDB() throws RollBackFailedException, ConnectionNotClosedException, SqlDataOperationException {
        ResultSet resultSet1 = null,resultSet2;

        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        String query1 = "select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where is_active=ture;";
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
             resultSet1 = statement.executeQuery(query1);
            connection.commit();

        } catch (SQLException throwables) {

            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }
        String query2="select emp_id, department_name from department_details inner join employee_department on department_details.dep_id=employee_department.dep_id where emp_id in (select emp_id from employee_details where is_active=true)";
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet2 = statement.executeQuery(query2);
            connection.commit();
            employeePayrollArrayList=getEmployeeListFromResultSet(resultSet1,resultSet2,employeePayrollArrayList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new ConnectionNotClosedException("error occurred while closing the connection");
            }
        }

        return employeePayrollArrayList;
    }

    private ArrayList<EmployeePayroll> getEmployeeListFromResultSet(ResultSet resultSet1,ResultSet resultSet2, ArrayList<EmployeePayroll> employeePayrollArrayList) throws SqlDataOperationException {
        try {
        if(resultSet1.next()&&resultSet2.next()){
            resultSet1.beforeFirst();resultSet2.beforeFirst();
            while (resultSet1.next()) {
                int id = resultSet1.getInt("emp_id");
                String name = resultSet1.getString("name");
                char gender = resultSet1.getString("gender").charAt(0);
                String address = resultSet1.getString("address");
                LocalDate date = resultSet1.getDate("start").toLocalDate();
                double salary = resultSet1.getDouble("basic_pay");
                employeePayrollArrayList.add(new EmployeePayroll(id, name, gender, address, date, salary));
            }

            while(resultSet2.next()){
                int id=resultSet2.getInt(1);
                String dep=resultSet2.getString(2);
                employeePayrollArrayList.stream().filter(p->p.getId()==id).forEach(p->p.getDepartment().add(dep));
            }
        }
        } catch (SQLException throwables) {
           throw new SqlDataOperationException("Some error occurred while performing dql operation");
        }
        return employeePayrollArrayList;
    }

    public int updateEmployeeSalaryInDB(String name, double salary) throws SqlDataOperationException {
        int result = 0;
        String query = " update payroll_details inner join employee_details on employee_details.emp_id=payroll_details.emp_id" +
                " set payroll_details.basic_pay=" + salary + ",payroll_details.deductions=" + (0.2 * salary) +
                ",payroll_details.taxable_pay=" + (salary - (0.2 * salary)) + ",payroll_details.income_tax=" + ((salary - (0.2 * salary)) * 0.1) +
                ",payroll_details.net_pay=" +(salary - ((salary - (0.2 * salary)) * 0.1)) +
                " where employee_details.name='" + name + "';";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        try {
            statement = connection.createStatement();
            result = statement.executeUpdate(query);

        } catch (SQLException throwables) {
            throw new SqlDataOperationException("Some error occurred while performing sql operation");
        }
        return result;
    }

    public void updateEmployeeSalary(String name, double salary) {
        int check = 0;
        try {
            check = updateEmployeeSalaryInDB(name, salary);
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        if (check != 0)
            updateEmployeeSalaryInObject(name, salary);
        else System.out.println("value not updated in database");
    }

    private void updateEmployeeSalaryInObject(String name, double salary) {
        try {
            employeePayrollArrayList = readFromDB();
        } catch (RollBackFailedException e) {
            e.getMessage();
        } catch (ConnectionNotClosedException e) {
            e.getMessage();
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        employeePayrollArrayList.stream()
                .filter(p -> p.getName().equals(name))
                .forEach(p -> p.setSalary(salary));
    }

    public boolean checkSalarySyncWithDB(String name) throws RollBackFailedException, ConnectionNotClosedException {
        ArrayList<EmployeePayroll> employeePayrollArrayListDB = new ArrayList<EmployeePayroll>(employeePayrollArrayList.stream().
                filter(p -> p.getName().
                        equals(name)).collect(Collectors.toList()));

        return employeePayrollArrayListDB.toString().equals(checkSalaryRecordInDB(name).toString());
    }

    private ArrayList<EmployeePayroll> checkSalaryRecordInDB(String name) throws RollBackFailedException, ConnectionNotClosedException {
        ResultSet resultSet1 = null,resultSet2;
        ArrayList<EmployeePayroll> employeePayrollUpdatedList = new ArrayList<EmployeePayroll>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        String query1 = "select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where employee_details.name='" + name + "' and is_active=true;";

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
             resultSet1 = statement.executeQuery(query1);
            connection.commit();
            //employeePayrollUpdatedList = getEmployeeListFromResultSet(resultSet, employeePayrollUpdatedList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }

        String query2="select emp_id, department_name from department_details inner join employee_department on department_details.dep_id=employee_department.dep_id where emp_id in (select emp_id from employe_details where name='"+name+"' and is_active=true)";
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet2 = statement.executeQuery(query2);
            connection.commit();
            employeePayrollUpdatedList=getEmployeeListFromResultSet(resultSet1,resultSet2,employeePayrollUpdatedList);
            } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        } catch (SqlDataOperationException e) {
            e.getMessage();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new ConnectionNotClosedException("error occurred while closing the connection");
            }
        }
        return employeePayrollUpdatedList;
    }

    public void updateEmployeeSalaryPreparedStatement(String name, double salary) {
        int check = 0;
        try {
            check = updateEmployeeSalaryInDBPreparedStatement(name, salary);
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        if (check != 0)
            updateEmployeeSalaryInObject(name, salary);
        else System.out.println("query was not executed");
    }

    private int updateEmployeeSalaryInDBPreparedStatement(String name, double salary) throws SqlDataOperationException {
        int result = 0;
        String query = " update payroll_details inner join employee_details on employee_details.emp_id=payroll_details.emp_id" +
                " set payroll_details.basic_pay=?,payroll_details.deductions=?" +
                ",payroll_details.taxable_pay=?,payroll_details.income_tax=?" +
                ",payroll_details.net_pay=? where employee_details.name=? and is_active=true;";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, salary);
            preparedStatement.setDouble(2, (0.2 * salary));
            preparedStatement.setDouble(3, (salary - (0.2 * salary)));
            preparedStatement.setDouble(4, ((salary - (0.2 * salary)) * 0.1));
            preparedStatement.setDouble(5, (salary - ((salary - (0.2 * salary)) * 0.1)));
            preparedStatement.setString(6, name);
            result = preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new SqlDataOperationException("Some error occurred while performing sql operation");
        }
        return result;
    }

    public boolean checkSalarySyncWithDBPreparedStatement(String name) throws RollBackFailedException, ConnectionNotClosedException {
        ArrayList<EmployeePayroll> employeePayrollArrayListDB = new ArrayList<EmployeePayroll>(employeePayrollArrayList.stream().
                filter(p -> p.getName().
                        equals(name)).collect(Collectors.toList()));
        return employeePayrollArrayListDB.toString().equals(checkSalaryRecordInDBPreparedStatement(name).toString());
    }

    public ArrayList<EmployeePayroll> checkSalaryRecordInDBPreparedStatement(String name) throws RollBackFailedException, ConnectionNotClosedException {
        ResultSet resultSet1 = null,resultSet2;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        ArrayList<EmployeePayroll> employeePayrollUpdatedList = new ArrayList<EmployeePayroll>();
        String query1 = "select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join" +
                " payroll_details on employee_details.emp_id=payroll_details.emp_id where employee_details.name=? and is_active=true;";
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, name);
            resultSet1 = preparedStatement.executeQuery();
            connection.commit();
        } catch (SQLException throwables) {

            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }

        String query2="select emp_id, department_name from department_details inner join employee_department on department_details.dep_id=employee_department.dep_id where emp_id in (select emp_id from employee_details where name=? and is_active=true);";
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query2);
            preparedStatement.setString(1, name);
            resultSet2 = preparedStatement.executeQuery();
            connection.commit();
            employeePayrollUpdatedList=getEmployeeListFromResultSet(resultSet1,resultSet2,employeePayrollUpdatedList);
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        } catch (SqlDataOperationException e) {
            e.getMessage();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new ConnectionNotClosedException("error occurred while closing the connection");
            }
        }
        return employeePayrollUpdatedList;
    }

    public ArrayList<EmployeePayroll> getEmployeeListBasedOnDate(LocalDate startDate, LocalDate endDate) throws RollBackFailedException, ConnectionNotClosedException {
        ResultSet resultSet1 = null,resultSet2;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        ArrayList<EmployeePayroll> employeePayrollUpdatedList = new ArrayList<EmployeePayroll>();

        String query1 = "select employee_details.emp_id,employee_details.name,employee_details.gender,employee_details.address," +
                "employee_details.start,payroll_details.basic_pay from employee_details inner join " +
                "payroll_details on employee_details.emp_id=payroll_details.emp_id where" +
                " employee_details.start between ? and ? and is_active=true;";
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            resultSet1 = preparedStatement.executeQuery();
            connection.commit();
        } catch (SQLException throwables) {

            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }
        String query2 = "select emp_id, department_name from department_details inner join employee_department on department_details.dep_id=employee_department.dep_id  where emp_id in (select emp_id from employee_details where start between ? and ? and is_active=true);";
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query2);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            resultSet2 = preparedStatement.executeQuery();
            employeePayrollUpdatedList=getEmployeeListFromResultSet(resultSet1,resultSet2,employeePayrollUpdatedList);
        } catch (SQLException throwables) {

            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        } catch (SqlDataOperationException e) {
            e.getMessage();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new ConnectionNotClosedException("error occurred while closing the connection");
            }
        }
        return employeePayrollUpdatedList;
    }

    public TreeMap<String, Double> getSalaryByAvgGender() throws SqlDataOperationException {

        String query = "select avg(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id where is_active=true group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    private TreeMap<String, Double> getMapofGenderSalary(String query) throws SqlDataOperationException {
        TreeMap<String, Double> stringDoubleTreeMap = new TreeMap<String, Double>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Double salary = resultSet.getDouble("salary");
                String gender = resultSet.getString("gender");
                stringDoubleTreeMap.put(gender, salary);
            }
        } catch (SQLException throwables) {
            throw new SqlDataOperationException("Some error occurred while performing sql operation");
        }
        return stringDoubleTreeMap;
    }

    public TreeMap<String, Double> getSalaryBySumGender() throws SqlDataOperationException {
        String query = "select sum(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id where is_active=true group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    public TreeMap<String, Double> getMaxSalaryByGender() throws SqlDataOperationException {
        String query = "select max(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id where is_active=true group by employee_details.gender";
        return getMapofGenderSalary(query);
    }

    public TreeMap<String, Double> getMinSalaryByGender() throws SqlDataOperationException {
        String query = "select min(payroll_details.basic_pay) as salary,employee_details.gender from employee_details inner join payroll_details on employee_details.emp_id=payroll_details.emp_id where is_active=true group by employee_details.gender";
        return getMapofGenderSalary(query);

    }

    public void addEmployeeData(int id, String name, Character gender, String address, LocalDate date, double salary, String department) throws RollBackFailedException, ConnectionNotClosedException {
        int rowaffected1 = 0, rowaffected2=0,rowaffected3=0,rowaffected4;
        try {
            employeePayrollArrayList = readFromDB();
        } catch (RollBackFailedException | ConnectionNotClosedException | SqlDataOperationException e) {
            e.getMessage();
        }
        int dep_id= 0;
        try {
            dep_id = getDepIDFromDB(department);
        } catch (SqlDataOperationException e) {
            e.getMessage();
        }
        String query1 = String.format("insert into employee_details (emp_id,name,gender,address,start) " +
                "values (%s,'%s','%s','%s','%s');", id, name, gender, address, Date.valueOf(date));
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            rowaffected1 = statement.executeUpdate(query1);
            connection.commit();
            if (!(rowaffected1 == 1))
                System.out.println("data not added");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }

        String query2 = String.format("insert into payroll_details  " +
                "values (%s,%s,%s,%s,%s,%s);", id, salary, (0.2 * salary), (salary - (0.2 * salary)), ((salary - (0.2 * salary)) * 0.1), (salary - ((salary - (0.2 * salary)) * 0.1)));
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            rowaffected2 = statement.executeUpdate(query2);
            connection.commit();
            if (rowaffected1 == 1 && rowaffected2 == 1) {
                employeePayrollArrayList.add(new EmployeePayroll(id, name, gender, address, date, salary));
            } else System.out.println("data not added");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }
        String query3=String.format("insert into employee_department values (%s,%s)",id,dep_id);
        String query4=String.format("insert into department_details values ('%s',%s)",department,dep_id);

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            rowaffected3 = statement.executeUpdate(query3);
            connection.commit();
            if (rowaffected1 == 1 && rowaffected2 == 1&&rowaffected3==1) {

            } else System.out.println("data not added");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            rowaffected4 = statement.executeUpdate(query4);
            connection.commit();
            if (rowaffected1 == 1 && rowaffected2 == 1&&rowaffected3==1&&rowaffected4==1) {
                employeePayrollArrayList.stream().filter(p->p.getId()==id).forEach(p->p.getDepartment().add(department));
            } else System.out.println("data not added");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollBackFailedException("Error occurred while rollbacking the data");
            }
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new ConnectionNotClosedException("error occurred while closing the connection");
            }
        }
    }

    private int getDepIDFromDB(String department) throws SqlDataOperationException {
        int dep_id=100;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        String query="select dep_id from department_details where department_name='"+department+"';";
        try {
            statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);
            if(!resultSet.next()) dep_id=101;
            else{
                resultSet.beforeFirst();
                while (resultSet.next()){
                    dep_id++;
                }
                dep_id++;
            }
        } catch (SQLException throwables) {
            throw new SqlDataOperationException("Some error occurred while performing sql operation");
        }
        return dep_id;
    }

    public void deleteEntryFromDataBase(String name) throws SqlDataOperationException {
        String query="update employee_details set is_active=false where name='"+name+"';";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnecton();
        } catch (FailedConnectionException e) {
            e.getMessage();
        }
        try {
            statement = connection.createStatement();
           int rowaffected= statement.executeUpdate(query);
           if(!(rowaffected==1)) System.out.println("operation not successful");

        } catch (SQLException throwables) {
            throw new SqlDataOperationException("Some error occurred while performing sql operation");
        }
    }
}

