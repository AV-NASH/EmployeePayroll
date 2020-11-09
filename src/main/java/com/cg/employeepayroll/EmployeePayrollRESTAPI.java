package com.cg.employeepayroll;

import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollRESTAPI {
    private ArrayList<EmployeePayroll> employeePayrollArrayList;

    public EmployeePayrollRESTAPI(List<EmployeePayroll> employeePayrollArrayList) {
        this.employeePayrollArrayList =new ArrayList<>(employeePayrollArrayList);
    }

    public long countEntries(){
        return employeePayrollArrayList.stream().count();
    }

    public void setEmployeePayrollArrayList(List<EmployeePayroll> employeePayrollArrayList) {
        this.employeePayrollArrayList = new ArrayList<>(employeePayrollArrayList);
    }

    public void addEmployeeToList(EmployeePayroll employeePayroll) {
        employeePayrollArrayList.add(employeePayroll);
    }

    public EmployeePayroll getEmployeePayrollData(String name) {
       EmployeePayroll employeePayroll = null;
       for(EmployeePayroll employeePayroll1:employeePayrollArrayList){
          if(employeePayroll1.getName().equals(name)) employeePayroll=employeePayroll1;
       }
       return employeePayroll;
    }

    public void updateEmployeePayrollData(String name, double salary) {
        employeePayrollArrayList.stream().filter(p->p.getName().equals(name)).forEach(p->p.setSalary(salary));
    }

    public void removeEmployeePayrollData(String name) {
        employeePayrollArrayList.remove(getEmployeePayrollData(name));
    }
}
