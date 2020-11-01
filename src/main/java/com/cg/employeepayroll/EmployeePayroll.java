package com.cg.employeepayroll;

import java.time.LocalDate;

public class EmployeePayroll {
    private int empID;
    private  String name;
    private char gender;
    private String address;
    private LocalDate date;
    private double salary;


    public EmployeePayroll(int empID, String name,char gender, String address, LocalDate date, double salary) {
        this.empID = empID;
        this.name = name;
        this.gender=gender;
        this.address = address;
        this.date = date;
        this.salary = salary;
    }

    public int getEmpID() {
        return empID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getSalary() {
        return salary;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "EmployeePayroll{" +
                "empID=" + empID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", date=" + date +
                ", salary=" + salary +
                '}';
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
}
