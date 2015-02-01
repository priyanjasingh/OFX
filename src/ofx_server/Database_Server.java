/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ofx_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author avishkar
 */
public class Database_Server {
    
    static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    static final String DB_URL="jdbc:mysql://localhost/OFX";
    
    static final String USER="root";
    static final String PASS="";
    
    static Connection conn=null;
    static Statement stmt=null;
    
    public void Start_Connection() throws SQLException, ClassNotFoundException{
    
        Class.forName("com.mysql.jdbc.Driver");
    
        System.out.println("Connecting to database");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    
        stmt=conn.createStatement();
        System.out.println("Connection Established");
        
    
    }
    
    public void Query(String str) throws SQLException{
        
        stmt.executeQuery(str);
    }
    
    public void Make_Table() throws SQLException{
    
        String sql = "CREATE TABLE User_Details " +
                   "(id VARCHAR(255) not NULL, " +
                   " Name VARCHAR(255), " + 
                   " User_Name VARCHAR(255), " + 
                   " Password VARCHAR(255), " + 
                    
                   " Hostel VARCHAR(255), " + 
                   " Email VARCHAR(255), " + 
                   " ConatctNo VARCHAR(255), " +
                   " PRIMARY KEY ( id ))"; 

        stmt.executeUpdate(sql);
    }
    
    public void Make_Entries(String []Str) throws SQLException{
    
    String sql = "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)";
      stmt.executeUpdate(sql);
    }
    
}
