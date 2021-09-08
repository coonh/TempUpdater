package database;

import java.sql.*;

public class DatabaseConnector {

    private static DatabaseConnector instance;
    private Connection connection;
    private final String host = "192.168.178.98";
    private final String database = "Hopfentrocknung";
    private final String user = "admin";
    private final String passwd = "sql_adm1n";

    private DatabaseConnector(){
        connectToMysql(host, database, user, passwd);
    }

    public int sqlInsertRequest(String s){
        try {
            return connection.createStatement().executeUpdate(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet sqlSelectRequest(String s) {
        try {
            return connection.createStatement().executeQuery(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DatabaseConnector getInstance(){
        if(DatabaseConnector.instance==null){
            DatabaseConnector.instance = new DatabaseConnector();
        }
        return instance;
    }

    private void connectToMysql(String host, String database, String user, String passwd){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionCommand = "jdbc:mysql://"+host+"/"+database+"?user="+user+"&password="+passwd;
            System.out.println("Try to connect to MySQL Database: ");
            connection = DriverManager.getConnection(connectionCommand);
            System.out.println("Connection successful!");

        }catch (Exception ex){
            try {
                System.out.println("Connection to MySQL failed!");
                System.out.println("Try to connect to MariaDB Database");
                Class.forName("org.mariadb.jdbc.Driver");
                String mariaDBConnection = "jdbc:mariadb://"+host+":3306/"+database+"?user="+user+"&password="+passwd;
                connection = DriverManager.getConnection(mariaDBConnection);
                System.out.println("Connection successful!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
