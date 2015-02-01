/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ofx_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;




public class OFX_Server {

    /**
     * @param args the command line arguments
     */
    
   public static int [] user_status = new int[101];
    public static String [] login_name = new String[101];
    public static Socket[] chat_soc= new Socket[101];
    public static Socket[] send_soc= new Socket[101];
    public static Socket[] live_update= new Socket[101];
    
    public static ServerSocket log_ser;
    
    public static void Cleanup(){
    
    Thread t = new Thread(){
        
        public void run(){
        
            for(int i=0;i<101;i++){
            
               user_status[i]=0;
               
            }
            
        }
    
    };
    t.start();
            
 }
    
   
    
    
    
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        // TODO code application logic here
        
        Operations.check_status();
        ServerSocket ser = new ServerSocket(12345);
        log_ser = new ServerSocket(12346);
        Socket soc= new Socket();
        int i=0;
        while(true){  
            soc=ser.accept();
            System.out.println(soc.getPort());
            System.out.println("connection accepted and now creating object");
            Operations obj = new Operations(soc);
            System.out.println("Object made");
        }
        
    }
    
}
