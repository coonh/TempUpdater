package database;

import java.sql.*;

public class DatabaseConnector {

    private static DatabaseConnector instance;
    private Connection connection;
    private final String host = "coonh.de";
    private final String database = "Hopfentrocknung";
    private final String user = "sql_writer";
    private final String passwd = "sql_writer_123";

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

    private boolean connectToMysql(String host, String database, String user, String passwd){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionCommand = "jdbc:mysql://"+host+"/"+database+"?user="+user+"&password="+passwd;
            connection = DriverManager.getConnection(connectionCommand);
            return true;

        }catch (Exception ex){
            System.out.println("false");
            ex.printStackTrace();
            return false;
        }
    }

}
