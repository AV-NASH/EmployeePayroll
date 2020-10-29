import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseConnection {
    public static void main(String[] args) {
        String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName="root";
        String password="Nuzumaki1@";
        Connection connection;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver laded");
        } catch (ClassNotFoundException e) {
            System.out.println("cnnot find classpath");
        }
        listDrivers();
        try {
            System.out.println("Connecting to "+jdbcURL);
            connection=DriverManager.getConnection(jdbcURL);
            System.out.println("connection successful");
        } catch (SQLException throwables) {
            System.out.println("connection failed");
        }
    }
    private static void listDrivers() {
        Enumeration<Driver> driverList= DriverManager.getDrivers();
        while ((driverList.hasMoreElements())){
            Driver driverClass=(Driver)driverList.nextElement();
            System.out.println("  "+driverClass.getClass().getName());
        }
    }

}
