/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ofx_server;

import com.sun.rowset.CachedRowSetImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author avishkar
 * 
 */

public class Operations extends OFX_Server implements Runnable {
    
   // OFX_Server obj;
    Socket soc;
    public static String [] user_detail = new String[10];
    Thread t;
     public static int [] mark = new int[1000];
    public static String [] search_results = new String[1000];
    public static Thread [] listen_send_t = new Thread[100];
    
    
    private  String su_UserName;
    private  String su_Password;
    private  String su_ConfirmPassword;
    private  String su_RoomNo;
    private  String su_Address;
    private  String su_Email;
    private  String su_ContactNo;
    private  String su_Name;
    private  String su_RegistrationNo;
    
    private String li_UserName;
    private String li_Password;
    
    private String save_username;
     private String save_address;
      private String save_email;
       private String save_mobileno;
       private String username;
       
       private static String category;
       private static String sub_category;
       private String item_name;
       private String base_price;
       private String details;
       private String meeting_place;
       private String time_limit;
      private String user;
       
       private String info;
      private int item_id;
      private String to_search ; 
      BufferedReader buff =null;
     
      
      
    Operations(Socket soc){
        this.soc=soc;
        //this.obj=obj;
        t = new Thread(this);
        System.out.println("in Constructor of Operations");
        t.start();
    }
    
    
    public static void check_status(){
    
        
        Thread t = new Thread(){
        
        public void run(){
        
            
            try {
                
                long now = System.currentTimeMillis();

                java.util.Date d = new java.util.Date(now);
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
               
                System.out.println("checking system status");
                
                String date =dateFormat.format(d).toString();
                //System.out.println(str);
                
                
                Database_Server db = new Database_Server();
                db.Start_Connection();
               
                String sql = "select * from Upload";
                ResultSet rs = db.stmt.executeQuery(sql);
                date = " "+date;
                
                rs.beforeFirst();
                
                while(rs.next()){
                    
                    String temp = rs.getString("time_limit");
                    System.out.println(temp);
                    System.out.println(date);
                    System.out.println("   ");
                    if(temp.equals(date)){
                         System.out.println(temp + date);
                        int item_id = rs.getInt("item_id");
                        String user= rs.getString("User_name");
                        String item =rs.getString("item_name");
                        
                        String st = "Time limit for the item  "+item+" has expired . Go and check out the Bids !!";
                        String str = "update Upload set status = 2 where item_id = "+item_id+"";
                        Database_Server db1 = new Database_Server();
                        db1.Start_Connection();
                        db1.stmt.executeUpdate(str);
                        str = "insert into Notifications (item_id,User_name,statement,type) values("+item_id+",'"+user+"','"+st+"',4)";
                        
                        db1.stmt.executeUpdate(str);
                        
                    }
                    System.out.println("while ending");
                }
                System.out.println("while ended");
                Thread.sleep(1000000);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        };
        t.start();
    
    }
    
    public void run(){
   
                 System.out.println("In thread of Operations");
                String str = null;
              
                try {
                    System.out.println(soc.getPort());
                    System.out.println("try to read from pipe");
                    buff=new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    str = buff.readLine();
                    System.out.println("read from pipe");
                    System.out.println(str);
                    
                } catch (IOException ex) {
                    Logger.getLogger(OFX_Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            
                if(str.equals("SIGNUP")){
                    System.out.println("signup condition satisfied");
                    this.Sign_Up();
                }
                else if(str.equals("SEARCH")){
                    System.out.println("login condition satisfied");
                    this.Search();
                }
                else if(str.equals("SEARCHUSER")){
                    System.out.println("login condition satisfied");
                    this.SearchUser();
                }
                else if(str.equals("LOGIN")){
                    System.out.println("login condition satisfied");
                    this.Log_In();
                }
                else if(str.equals("UPLOAD")){
                    System.out.println("upload condition satisfied");
                    this.Upload();
                }
                else if(str.equals("PROFILE")){
                    System.out.println("profile condition satisfied");
                    this.Update_Profile();
                }       
   
                else if(str.equals("SAVECHANGES")){
                    System.out.println("SAVE THE CHANGES");
                    this.Save_Changes();
                }
                else if(str.equals("DISPLAY")){
                    System.out.println("display the results");
                    this.Display();
                }
                else if(str.equals("CHANGEPASSWORD")){
                    System.out.println("change password condition satisfied");
                    this.Change_Password();
                }
                else if(str.equals("DISPLAYDETAILS")){
                
                    System.out.println("display details condition satisfied");
                    this.Display_Details();
                }
                else if(str.equals("INDIVIDUALITEM")){
                
                    System.out.println("individualitems condition satisfied");
                    this.Individual_item();
                }
                else if(str.equals("BIDITEM")){
                
                    System.out.println("individualitems condition satisfied");
                    this.Bid_item();
                }
                else if(str.equals("USER_UPLOAD"))
                {
                    System.out.println("user uploads condition satisfied");
                    this.User_upload();
                
   
                }
                else if(str.equals("SAVE_ITEM_CHANGES"))
                {
                    System.out.println("change the item details");
                    this.Save_item_changes();
                }
                else if(str.equals("UPLOAD_BID"))
                {
                    System.out.println("fetch the bids coresponding to a upload");
                    this.Upload_bid();
                }
                else if(str.equals("DELETE_ITEM"))
                {
                    System.out.println("delete the corresponding item ");
                    this.Delete_item();
                }
                else if(str.equals("USER_SOLDITEM"))
                {
                    System.out.println("sold items corresponding to a user");
                    this.User_solditems();
                }
                else if(str.equals("SOLDITEM_BUYER"))
                {
                    System.out.println("buyer corresponding to a sold item");
                    this.Sold_item_buyer();
                }
                else if(str.equals("USER_BID"))
                {
                    System.out.println("bids corresponding to user");
                    this.User_bid();
                }
                else if(str.equals("SELLITEM"))
                {
                    System.out.println("sell item function being called");
                   this.Sell_item();
                }
                else if(str.equals("CANCEL_BID"))
                {
                    System.out.println("canel bids corresponding to user");
                    this.Cancel_bid();
                }
                else if(str.equals("NOTIFICATIONS"))
                {
                    System.out.println("canel bids corresponding to user");
                    this.Notify();
                }
                else if(str.equals("DELETE_NOTI"))
                {
                    System.out.println("in delete notification");
                    this.Delete_Noti();
                    
                
                }
                else if(str.equals("RECENT_SALE"))
                {
                    System.out.println("recently added sale");
                    this.Recent_sale();
                }
                else if(str.equals("USER_LIKE"))
                {
                    System.out.println("user_like");
                    this.User_like();
                }
                
                else if(str.equals("INITIAL_IMAGE"))
                {
                    System.out.println("recently added sale");
                    this.Image();
                }
                else if(str.equals("LOGOUT"))
                {
                    System.out.println("logout");
                    this.LogOut();
                }
                else if(str.equals("SHOW_USERS"))
                {
                    System.out.println("show_users");
                    this.ShowUsers();
                }
                else if(str.equals("USERSEARCH"))
                {
                    System.out.println("user ki search");
                    this.UserSearch();
                }
                
                
                   
    }
    
    public void UserSearch(){
    
        Thread t= new Thread(){
        
            public void run(){
                
                try {
                    
                    System.out.println("waiting for string");
                    
                    //ObjectInputStream ob1 = new ObjectInputStream(soc.getInputStream());
                    //to_search =  (String)ob1.readObject();
                    
                  //  BufferedReader b = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                   
                    to_search = buff.readLine();
                    
                    System.out.println("string to be searched "+ to_search);
                    
                    Database_Server db = new Database_Server();
                    db.Start_Connection();
                    
                    String sql="SELECT * FROM User_Details where User_Name LIKE '"+"%"+to_search+"%"+"'";
                    
                    ResultSet rs = db.stmt.executeQuery(sql);
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
            
                    ob.close();crs.close();
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
            
            }
        
        };
        t.start();   
    
    }
    
    
    
    
    private void ShowUsers(){
    
    Thread t = new Thread(){
    
        public void run(){
        
            try {
                
                Database_Server db2 = new Database_Server();
                db2.Start_Connection();
                
                String sql = "select * from User_Details where status = 1";
               
                ResultSet rs2 = db2.stmt.executeQuery(sql);
                CachedRowSetImpl crs = new CachedRowSetImpl();
                crs.populate(rs2);
                
                for(int i=0;i<100;i++){
                
                    if(user_status[i]==1){
                        
                        System.out.println("sending details to......");
                        System.out.println(login_name[i]);
                        ObjectOutputStream ob111 = new ObjectOutputStream(live_update[i].getOutputStream());
                        ob111.writeObject(crs);
                        System.out.println("sent details");
                    }
                    
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    
    };
    t.start();
    
    
    
    }
    
    
    
private void LogOut(){
    
    Thread t =new Thread(){
    
        public void run(){
        
            try {
                
                String user = buff.readLine();
                
                for(int i=0;i<100;i++){
                
                    if(login_name[i].equals(user)){
                    
                        login_name[i]="";
                        user_status[i]=0;
                        chat_soc[i].close();
                        live_update[i].close();
                        listen_send_t[i].stop();
                        break;
                    }
                    
                }
                
                Database_Server db3 = new Database_Server();
                db3.Start_Connection();
                
                String sql= "update User_Details set status = 0 where User_name = '"+user+"'";
                db3.stmt.executeUpdate(sql);
                    
                ShowUsers();
                
            } catch (IOException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    };
    t.start();


}    
    
    
private void User_like() {
          Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    //BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String user = buff.readLine();
                    Database_Server db4 = new Database_Server();
                    try {
                        db4.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("userlike       " + user);
                    String sql = "SELECT * FROM priority WHERE User_name= '"+user+"' order by id desc";
                    ResultSet rs = db4.stmt.executeQuery(sql);
                    /*
                    CachedRowSetImpl crs1 = new CachedRowSetImpl();
                    crs1.populate(rs);
                    ObjectOutputStream ob1 = new ObjectOutputStream(soc.getOutputStream());
                    ob1.writeObject(crs1);
                    */
                    String list= "'aaaa'";
                    
                    int count=0;
                    while(rs.next())
                    {
                       String temp = rs.getString("sub_category");
                       list+=",'" +temp + "'";
                       count+=1;
                    }
                    
                    System.out.println(list);
                    //System.out.println(id);
                    //pr.println(count);
                    System.out.println(count);
                    
                    String sql2 = "SELECT * FROM Upload WHERE sub_category in("+list+") AND status=0 LIMIT 5";
                    ResultSet rs2 = db4.stmt.executeQuery(sql2);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs2);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                    System.out.println("sent the first result set");
                    
             
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    
                }
                
                
            }
        
        };
        t.start();    
    //To change body of generated methods, choose Tools | Templates.
    }
    
    public void Delete_Noti(){
    
        
        Thread t = new Thread(){
                
                public void run(){
                
                    try {
                        System.out.println("in delete not");
                        String name = buff.readLine();
                       String type = buff.readLine();
                        System.out.println(name);
                        System.out.println(type);
                        Database_Server db5 = new Database_Server();
                        db5.Start_Connection();
                        String sql = "delete from Notifications where User_name= '"+name+"' and type = '"+type+"'";
                        
                        db5.stmt.executeUpdate(sql);
                        
                        System.out.println("done");
           
                    } catch (IOException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                
                
                }
            
                
                
            };
            t.start();
    }
    
    
    public void Notify(){
    
        Thread t= new Thread(){
            
            public void run(){
                
                try {
                    Database_Server db6 = new Database_Server();
                    db6. Start_Connection();
                    
                    String name = buff.readLine();
                    
                    String sql = "select * from Notifications where User_name = '"+name+"'";
                    
                    ResultSet rs = db6.stmt.executeQuery(sql);
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            
            }
        
        };
        t.start();
    
    }
    
    public void Sell_item(){
    
        Thread t = new Thread(){
        
            public void run(){
            
                try {
                    System.out.println("in sell item func");
                    //BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String sid= buff.readLine();
                    String buyer= buff.readLine();
                    String price =buff.readLine();
                    
                    int id = Integer.parseInt(sid);
                    Database_Server db7 = new Database_Server();
                    db7.Start_Connection();
                    
                    String sql = "update Upload set status = 1,buyer ='"+buyer+"' where item_id= "+id+"";
                    db7.stmt.executeUpdate(sql);
                    
                    String s = "Your Bid corresponding to item_id = " +sid + " of bid price " + price + " is Successfull";
                    
                    sql ="insert into Notifications (item_id,User_name,statement,type) values("+id+",'"+buyer+"','"+s+"',1 ) ";
                    db7.stmt.executeUpdate(sql);
                    
                     
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        
        };
        t.start();
    }
    
    public void Cancel_bid(){
    
        Thread t=  new Thread(){
            
            public void run(){
            
                try {
  
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("rad kar le bhai");
                        
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    //System.out.println("reading inputs");
                    String name = buff.readLine();
                    //System.out.println("read first input");
                    String sid = buff.readLine();
                    //System.out.println("read both inputs");
                    int id = Integer.parseInt(sid);
                    
                    Database_Server db8 = new Database_Server();
                    db8.Start_Connection();
                   
                   String sql2 = "DELETE FROM Bids WHERE item_id = "+id+" and User_name='"+name+"'";
                    db8.stmt.executeUpdate(sql2);
                    System.out.println("successfully deleted the bid");
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        
        };
        t.start();
    
    }
    
    
    
    private void User_bid() {
          Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String user = buff.readLine();
                    Database_Server db9 = new Database_Server();
                    try {
                        db9.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM Bids WHERE User_name= '"+user+"'";
                    ResultSet rs = db9.stmt.executeQuery(sql);
                    /*
                    CachedRowSetImpl crs1 = new CachedRowSetImpl();
                    crs1.populate(rs);
                    ObjectOutputStream ob1 = new ObjectOutputStream(soc.getOutputStream());
                    ob1.writeObject(crs1);
                    */
                    String id= "null";
                    
                    int count=0;
                    while(rs.next())
                    {
                       int temp = rs.getInt("item_id");
                       id=  id+ ","+temp ;
                       count+=1;
                    }
                    System.out.println(id);
                    //pr.println(count);
                    System.out.println(count);
                    
                    String sql2 = "SELECT * FROM Upload WHERE item_id in("+id+")";
                    ResultSet rs2 = db9.stmt.executeQuery(sql2);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs2);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                    System.out.println("sent the first result set");
                    
                    
                    sql = "SELECT * FROM Bids WHERE User_name= '"+user+"'";
                    rs = db9.stmt.executeQuery(sql);
                    CachedRowSetImpl crs1 = new CachedRowSetImpl();
                    crs1.populate(rs);
                    ob.writeObject(crs1);
                    System.out.println("sent the second result set");
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();    
    //To change body of generated methods, choose Tools | Templates.
    }
    
    private void Sold_item_buyer() {
        //To change body of generated methods, choose Tools | Templates.
        Thread t;
        t = new Thread(){
            
            public void run(){
                System.out.println("in function sold_item_buyer");
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                  int item_id = Integer.parseInt(buff.readLine());
                    System.out.println(item_id);
                    Database_Server db10 = new Database_Server();
                    try {
                        db10.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql2 = "SELECT * FROM Upload WHERE item_id = "+item_id+"";
                    ResultSet rs = db10.stmt.executeQuery(sql2);
                    System.out.println("before wile");
                    
                    while(rs.next())
                    {
                        user = rs.getString("buyer");
                        System.out.println(user);
                    }
                    System.out.println("after while");
                    String sql = "SELECT * FROM User_Details WHERE User_Name= '"+user+"'";
                    rs = db10.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();    
    
    }
    
    
    public void User_upload()
    {
        Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String user = buff.readLine();
                    Database_Server db11 = new Database_Server();
                    try {
                        db11.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM Upload WHERE User_name= '"+user+"' AND status=0";
                    System.out.println("successfully executed query for fetching imgage");
                    ResultSet rs = db11.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();    
    
    }
    
    public void Save_item_changes()
    {
        Thread t= new Thread(){
        
            public void run(){
                BufferedReader rd = null;
                try {
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    rd = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    
                    ObjectInputStream ob = new ObjectInputStream(soc.getInputStream());
                    byte[] ImageData = (byte[]) ob.readObject();
                    
                    base_price = rd.readLine();
                    info = rd.readLine();
                    item_id = Integer.parseInt(rd.readLine());
                    Database_Server db12 = new Database_Server();
                    String str= "UPDATE Upload SET base_price= ? , details = ?, image=? WHERE item_id = "+item_id+"";
                    
                    
                    PreparedStatement ps = db12.conn.prepareStatement(str);
                    
                    ps.setString(1, base_price);
                    ps.setString(2, info);
                    ps.setBytes(3, ImageData);
                    ps.executeUpdate();
                    ps.close();
                    
                    //db.stmt.executeUpdate(str)
                    System.out.println("query executed ");
                    
                    String sql = "select * from Bids where item_id = "+item_id+"";
                    ResultSet rs = db12.stmt.executeQuery(sql);
                    
                    String []names  = new String[100];
                    int i=0;
                    while(rs.next()){
                        
                        String temp = rs.getString("User_name");
                        names[i++]=temp.substring(0);
                    }
                    
                    for(int j=0;j<i;j++){
                    
                        String s = "Item with item id = "+item_id+" and name = has been modified";
                    
                        sql ="insert into Notifications (item_id,User_name,statement,type) values("+item_id+",'"+names[j]+"','"+s+"',2 ) ";
                        db12.stmt.executeUpdate(sql);
                    
                    }
                    
                   
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        rd.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        
        };
        t.start();   
    
    }
    public void Upload_bid()
    {
        Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    System.out.println("in upload_bid");
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    int item_id = Integer.parseInt(buff.readLine());
                    System.out.println(item_id);
                    Database_Server db13 = new Database_Server();
                    try {
                        db13.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM Bids WHERE item_id = "+item_id+"";
                    System.out.println("fetching tbale with item id  =" + item_id);
                    System.out.println("successfully executed query for fetching bids corresponding to upload");
                    ResultSet rs = db13.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start(); 
    }

    private void Delete_item() {
        Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    int item_id = Integer.parseInt(buff.readLine());
                    System.out.println(item_id);
                    Database_Server db14 = new Database_Server();
                     db14.Start_Connection();
                    
                    String sql = "DELETE FROM Upload " + "WHERE item_id = "+item_id+"";
                    
                    db14.stmt.executeUpdate(sql);
                    
                    
                    
                    sql = "select * from Bids where item_id = "+item_id+"";
                    ResultSet rs = db14.stmt.executeQuery(sql);
                    
                    String []names  = new String[100];
                    int i=0;
                    while(rs.next()){
                        
                        String temp = rs.getString("User_name");
                        names[i++]=temp.substring(0);
                    }
                    
                    for(int j=0;j<i;j++){
                    
                        String s = "Item with item id = "+item_id+" has been deleted, Your Bid has been cancelled.";
                    
                        sql ="insert into Notifications (item_id,User_name,statement,type) values("+item_id+",'"+names[j]+"','"+s+"',2 ) ";
                        db14.stmt.executeUpdate(sql);
                        
                    }
                    
                    String sql2 = "DELETE FROM Bids" + "WHERE item_id = "+item_id+"";
                    db14.stmt.executeUpdate(sql2);
                    
                    pr.println("success");
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void User_solditems() {
        Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String user = buff.readLine();
                    Database_Server db15 = new Database_Server();
                    try {
                        db15.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM Upload WHERE User_name= '"+user+"' AND status=1";
                    ResultSet rs = db15.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();    
    
        //To change body of generated methods, choose Tools | Templates.
    }
    
    public void Bid_item(){
    
        Thread t = new Thread(){
            
            public void run(){
            
                try {
                    
                   // BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    System.out.println("in bid");
                    String bid_price=buff.readLine();
                    System.out.println("vfvfhvhfvbfh");
                    String item_id= buff.readLine();
                    String user_name=buff.readLine();
                    String by = buff.readLine();
                    
                    
                    Database_Server db16 = new Database_Server();
                    
                    db16.Start_Connection();
                    
                    String str ="select * from Bids where User_name = '"+user_name+"' and item_id = '"+item_id+"' ";
                    ResultSet rs =db16.stmt.executeQuery(str);
                    System.out.println("in bidite after fetch");
                    boolean flag=false;
                    if(rs.next()){
                    
                        String name = rs.getString("User_name");
                        if(name.equals(user_name))flag=true;
                    }
                    long now = System.currentTimeMillis();

                    java.util.Date d = new java.util.Date(now);
                
                     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
               
                    System.out.println("hiiiii");
                
                     String date =dateFormat.format(d).toString();
                    
                    String sq = "update Bids set bid_price = '"+bid_price+"' where User_name= '"+user_name+"' and item_id = '"+item_id+"'"; 
                    String sql ="insert into Bids (User_name,item_id,bid_price,bid_date) values('"+user_name+"','"+item_id+"','"+bid_price+"','"+date+"')";
                    
                    if(flag==true)db16.stmt.executeUpdate(sq);
                    else db16.stmt.executeUpdate(sql);
                    
                    String s = "A bid against item id = " + item_id +"has been made by " + user_name + " of price " + bid_price; 
                    
                    sql ="insert into Notifications (item_id,User_name,statement,type) values("+item_id+",'"+by+"','"+s+"',3 ) ";
                    db16.stmt.executeUpdate(sql);
                        
                   
                    System.out.println("execution complete");
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            
            
            }
        
        };
        t.start();
    
    }
    
    
    public void Sign_Up(){
    
        Thread t= new Thread(){
            public void run(){
                try {
                    System.out.println("IN signup function");
                    int i=0;
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
          
                    pr.println("OK");
                    System.out.println("written OK to pipe");
                    
                    //Database check for username already present
                    
                    //buff= new BufferedReader( new InputStreamReader(soc.getInputStream()));
                       
                    su_UserName=buff.readLine();
                    
                    Database_Server db17 = new Database_Server();
                    db17.Start_Connection();
                    
                    String sql = "SELECT User_Name FROM User_Details";
                    ResultSet rs=db17.stmt.executeQuery(sql);
                    
                    boolean flag=false;
                    while(rs.next()){
                        String str = rs.getString("User_Name");
                        
                        if(su_UserName.equals(str)){
                            flag=true;
                            break;
                        }
                    }
                    
                    if(flag==true){
                        pr.println("DUPLICATE");
                    }
                    else{
                        System.out.println("proceeding for registration");
                        pr.println("PROCEED");
                        
                        ObjectInputStream ob = new ObjectInputStream(soc.getInputStream());
                        byte[] ImageData = (byte[]) ob.readObject();
                    
                        System.out.println("got the image");
                        
                        su_Password=buff.readLine();
                        su_Name=buff.readLine();
                        su_RegistrationNo=buff.readLine();
                        //su_RoomNo=buff.readLine();
                        su_Address=buff.readLine();
                        su_Email=buff.readLine();
                        su_ContactNo=buff.readLine();
                        
                        PreparedStatement ps = db17.conn.prepareStatement("insert into User_Details (id, Name, User_Name,Password, Address, Email,ContactNo, image) values(?,?,?,?,?,?,?,?)");
                    
                        ps.setString(1, su_RegistrationNo);
                        ps.setString(2, su_Name);
                        ps.setString(3, su_UserName);
                        ps.setString(4, su_Password);
                        ps.setString(5, su_Address);
                        ps.setString(6, su_Email);
                        ps.setString(7, su_ContactNo);
                        ps.setBytes(8, ImageData);

                        ps.executeUpdate();
                        
                        System.out.println("finished uploading");
                        //ps.close();
                       
                        /*
                        sql = "INSERT INTO User_Details " +
                                 "VALUES (" +
                                  "'" +su_RegistrationNo+"'" + "," +
                                  "'"+su_Name+"'" + "," +
                                  "'"+su_UserName+"'" + "," +
                                  "'"+su_Password+"'" + "," +
                                  
                                  "'"+su_Address+"'" + "," +
                                  "'"+su_Email+"'" + "," +
                                  "'"+su_ContactNo+"'" + ")";
                        
                    /*
                    String sql = "INSERT INTO User_Details " +
                                 "VALUES ( '20125135', 'sarang', 'xlr', 'khanpur24', '57', 'Tandon', 'sarang24s@gmai.com', '08173872815' )"; 
                      */            
                       /* db.stmt.executeUpdate(sql);*/  
                        
                    }
                    
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }
        
   public void Log_In(){
        Thread t= new Thread(){
        
            public void run(){
                
                try {
                    PrintWriter pr=new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    li_UserName = buff.readLine();
                    li_Password = buff.readLine();
                    
                    Database_Server db18= new Database_Server();
                    db18.Start_Connection();
                    String sql = "SELECT * FROM User_Details";
                    ResultSet rs = db18.stmt.executeQuery(sql);
                    
                    boolean user_found = false;
                    boolean pass_match = false;
                    int number=0;
                    while(rs.next()){
                            
                        String user=rs.getString("User_Name");
                        String pass=rs.getString("Password");
                        int n=rs.getInt("number");
                        if(user.equals(li_UserName)){
                            user_found=true;
                            if(pass.equals(li_Password)){
                            pass_match=true;
                            number=n;
                            }
                        }
                        System.out.println(user + " " + pass);
                    }
                    
                    pr=new PrintWriter(soc.getOutputStream(),true);
                    
                    if((user_found==true) && (pass_match==true)){
                        pr.println("MATCHFOUND");
                        pr.println(number);
                        
                        //Socket soc= new Socket();
                        
                        int i=0;
                        for(i=0;i<100;i++)if(user_status[i]==0)break;
                        System.out.println("connection");
                        System.out.println(i);
                        
                        chat_soc[i]= new Socket();
                        chat_soc[i]=log_ser.accept();
                        
                        
                        
                        live_update[i]= new Socket();
                        live_update[i]=log_ser.accept();
                        
                        listen_send(i);
                        
                        login_name[i]=li_UserName.substring(0);
                        user_status[i]=1;
                        
                        System.out.println("now established");
                        
                        
                        sql= "update User_Details set status = 1 where User_name = '"+li_UserName+"'";
                        db18.stmt.executeUpdate(sql);
                        
                        
                        
                        ShowUsers();
                    }
                    else if((user_found==true)  &&  (pass_match==false)){
                        System.out.println("    passwrd do not match");
                        pr.println("PASSWORDNOTMATCH");
                    }
                    else if(user_found==false){
                        pr.println("USERNOTFOUND");
                        System.out.println("use nt fund");
                    }
                    
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        
        };
        t.start();       
    
    }
   
    public void listen_send(final int i){
    
        listen_send_t[i] = new Thread(){
        
            public void run(){
            
                try {
                    
                   BufferedReader buff = new BufferedReader(new InputStreamReader(chat_soc[i].getInputStream()));
                    System.out.println("qqq");
                   while(true){
                        
                       System.out.println("bb");
                       String to = buff.readLine();
                       String msg = buff.readLine();
                       String from = buff.readLine();
                       String snum = buff.readLine();
                       
                       
                       
                       System.out.println(to);
                       System.out.println(msg);
                       System.out.println(from);
                       
                       System.out.println("bbbbbbbbbbbbbbbb");
                       for(int j=0;j<100;j++){
                       
                           if(login_name[j].equals(to)){
                               System.out.println("now sending to " + login_name[j]);
                               PrintWriter pr = new PrintWriter(chat_soc[j].getOutputStream(),true);
                               pr.println(from);
                               pr.println(msg);
                               pr.println(snum);
                               System.out.println("sent the msssssssssg");
                               break;
                           }
                       }
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
            
            }
        
        };
        listen_send_t[i].start();
    
    }
   
   
     public  void Save_Changes(){
        Thread t= new Thread(){
        
            public void run(){
                
                try {
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    
                    ObjectInputStream ob = new ObjectInputStream(soc.getInputStream());
                    byte[] ImageData = (byte[]) ob.readObject();
                    
                    BufferedReader rd = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    
                    System.out.println("fetching details from client");
                    //save_username = rd.readLine();
                    save_address = rd.readLine();
                    save_email = rd.readLine();
                    save_mobileno = rd.readLine();
                    username = rd.readLine();
                    
                    System.out.println("fetching image from client");

                    System.out.println("connecting to database");
                    Database_Server db19 = new Database_Server();
                    
                     PreparedStatement ps = db19.conn.prepareStatement("UPDATE User_Details SET Address = ?, Email = ?, ContactNo= ?, image= ? WHERE User_Name = '"+username+"' ");
                    
                    ps.setString(1, save_address);
                    ps.setString(2, save_email);
                    ps.setString(3, save_mobileno);
                    ps.setBytes(4, ImageData);
                    
                    ps.executeUpdate();
                    ps.close();
                    
                  // String str= "update User_Details Set Address='"+save_address+"' , Email='"+save_email+"', ContactNo='"+save_mobileno+"', image= "+ImageData+" where User_Name='"+username+"'";
                    
                  //  db.stmt.executeUpdate(str);
                    
                  
                    System.out.println("successfully updated profile");
                    
                    
                    try {
                        db19.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    //rd.close();
                }
            }
        
        };
        t.start();   
    
    }
    
    
     public void Upload(){
         
         Thread t= new Thread(){
        
            public void run(){
            
                 BufferedReader rd = null;
                try {
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    
                    ObjectInputStream ob = new ObjectInputStream(soc.getInputStream());
                    byte[] ImageData = (byte[]) ob.readObject();
                    
                    rd = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    category = rd.readLine();
                    sub_category = rd.readLine();
                    item_name = rd.readLine();
                    
                    time_limit =rd.readLine();
                    details = rd.readLine();
                    base_price = rd.readLine();
                    meeting_place =rd.readLine();
                    user = rd.readLine();
                
                    //String str= "UPDATE Upload SET base_price= ? , details = ?, image=? WHERE item_id = "+item_id+"";
                    long now = System.currentTimeMillis();

                    java.util.Date d = new java.util.Date(now);
                
                     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
               
                    System.out.println();
                
                     String date = dateFormat.format(d).toString();
                     if(category.equals("Cycles") || category.equals("Others"))
                         sub_category = "";
                    String sql = "INSERT INTO Upload (category,sub_category,item_name,User_name,time_limit,meeting_place,base_price,details,date,image) values(?,?,?,?,?,?,?,?,?,?)" ;
                     
                   Database_Server db20 = new Database_Server();
                    db20.Start_Connection();
                    PreparedStatement ps = db20.conn.prepareStatement(sql);
                    
                    ps.setString(1, category);
                    ps.setString(2, sub_category);
                    ps.setString(3, item_name);
                    ps.setString(4, user);
                    ps.setString(5, time_limit);
                    ps.setString(6, meeting_place);
                    ps.setString(7, base_price);
                    ps.setString(8, details); 
                    ps.setString(9, date);
                    ps.setBytes(10, ImageData);
                    ps.executeUpdate();
                    ps.close();
                   
                    
                } catch (IOException | SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        rd.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            
            }
        
        };
        t.start();
     
     } 
     
     public void Display_Details(){
    
        Thread t =new Thread(){
            
            public void run(){
            
                try {
                    
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    
                   BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                   String user = buff.readLine();
                    Database_Server db21 = new Database_Server();
                    
                    db21.Start_Connection();
                    String sql = "SELECT * FROM User_Details WHERE User_Name= '"+user+"'";
                    System.out.println("successfully executed query for fetching imgage");
                    ResultSet rs = db21.stmt.executeQuery(sql);
                    
                    
                    if(rs.next()){
                        
                        byte[]ImageData = rs.getBytes("image");
                        
                        ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                        ob.writeObject(ImageData);
                        
                        String username = rs.getString("User_Name");
                        String address = rs.getString("Address");
                        String email = rs.getString("Email");
                        String contactno = rs.getString("ContactNo");
                        int num = rs.getInt("number");
                        String name = rs.getString("Name");
                        String snum = ""+num;
                        pr.println(username);
                        pr.println(address);
                        pr.println(email);
                        pr.println(contactno);
                        pr.println(snum);
                        pr.println(name);
                        //format = new ImageIcon(ImageData);
                        
                    }   
                
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
            
        };
        t.start();
    }
    
    public void Change_Password(){
    
        Thread t = new Thread(){
            public void run(){
                
                PrintWriter pr = null;
                try {
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String user  = buff.readLine();
                    
                    Database_Server db22 = new Database_Server();
                    db22.Start_Connection();
                    
                    System.out.println("username = " + user);
                    
                    String sql ="SELECT Password FROM User_Details WHERE User_Name = '"+user+"' ";
                    ResultSet rs =db22.stmt.executeQuery(sql);
                    
                    if(rs.next()){
                    
                    String pass=rs.getString("Password");
                    
                    System.out.println("password = " + pass);
                    
                    pr.println(pass);
                    
                    }
                    
                    
                    String newpass = buff.readLine();
                    
                    System.out.println("updating results");
                    
                    String str= "UPDATE User_Details " + 
                                 "SET Password = " + "'" + newpass + "' " + 
                                 "WHERE User_Name = " + "'" + user + "'";
                                  
                    db22.stmt.executeUpdate(str);
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        };
        t.start();
                
        
    }
    
     
     
     public void Display(){
        Thread t;
        t = new Thread(){
            
            public void run(){
                
                 
                try {
                 
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                    System.out.println("written OK to the pipe");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    
                    System.out.println("reading category");
                    category = rd.readLine();
                    
                    System.out.println("reading subcategory");
                    sub_category = rd.readLine();
                    
                    System.out.println("creating new database obj");
                     Database_Server db23 = new Database_Server();
                    db23.Start_Connection();
                    
                    System.out.println("trying to query");
                    String str = "SELECT item_id,item_name,User_name,base_price,image FROM Upload WHERE category='"+category+"' AND sub_category='"+sub_category+"' and status =0" ;               
                    
                    ResultSet rs = db23.stmt.executeQuery(str);
                    
                    System.out.println("successfully fetched results from server");
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                    System.out.println("cache is wriiten on the socket");
                   /*
                           
                   String cnt = "select count(*) as cnt from Upload";
                   
                   ResultSet rs1 = db.stmt.executeQuery(cnt);
                  
                   int i;
                   if(rs1.next()){
                       i=rs1.getInt("cnt");
                       pr.println(i);
                   }
                   
                   
                  
                  
                  
                   
                  
                  while(rs.next())
                  {
                      String item_name = rs.getString("item_name");
                      String base_price = rs.getString("base_price");
                      String uploaded_by = rs.getString("User_name");
                      
                      pr.println(item_name);
                      pr.println(base_price);
                      pr.println(uploaded_by);
                  }
                  
                   */
                    
                } catch (IOException | SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    //rd.close();
                }
            
            }
        
        };
        t.start();
     
    
    }
    
        public void SearchUser(){
        
            Thread t= new Thread(){
        
            public void run(){
                
                try {
                    
                    System.out.println("waiting for string");
                    
                    //ObjectInputStream ob1 = new ObjectInputStream(soc.getInputStream());
                    //to_search =  (String)ob1.readObject();
                    
                  //  BufferedReader b = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                   
                    to_search = buff.readLine();
                    
                    System.out.println("string to be searched "+ to_search);
                    
                    Database_Server db24 = new Database_Server();
                    db24.Start_Connection();
                    
                    String sql="SELECT * FROM User_Details where User_Name LIKE '"+"%"+to_search+"%"+"' AND status = 1";
                    
                    ResultSet rs = db24.stmt.executeQuery(sql);
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
            
                    ob.close();crs.close();
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
            
            }
        
        };
        t.start();   
        
        }
     
         public void Search(){
             
        Thread t= new Thread(){
        
            public void run(){
                
                try {
                    
                    System.out.println("waiting for string");
                    
                    //ObjectInputStream ob1 = new ObjectInputStream(soc.getInputStream());
                    //to_search =  (String)ob1.readObject();
                    
                  //  BufferedReader b = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                   
                    to_search = buff.readLine();
                    
                    System.out.println("string to be searched "+ to_search);
                    
                    Database_Server db25 = new Database_Server();
                    db25.Start_Connection();
                    
                    String sql="SELECT * FROM Upload where item_name LIKE '"+"%"+to_search+"%"+"' AND status = 0";
                    
                    ResultSet rs = db25.stmt.executeQuery(sql);
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
            
                    ob.close();crs.close();
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
            
            }
        
        };
        t.start();   
    
    }
    
         
    public void Individual_item(){
        Thread t;
        t = new Thread(){
            
            public void run(){
                
                try {
                    System.out.println("trying to get itemid");
                    
                    PrintWriter pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("thi is the server speaking");
                    
                    System.out.println("spoke to client");
                    BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                   
                    String str= buff.readLine();
                    System.out.println(str);
                    int item_id =Integer.parseInt(str);
                    String user_name = buff.readLine();
                    System.out.println("item id = " + item_id);
                    
                    Database_Server db26 = new Database_Server();
                    db26.Start_Connection();
                    
           
                    String sql = "select * from Upload where item_id = "+item_id+" ";
                    ResultSet rs = db26.stmt.executeQuery(sql);
                    
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                    
                    
                    
                    String sql1 = "select * from Bids where item_id = "+item_id+" ";
                    rs = db26.stmt.executeQuery(sql1);
                    
                    CachedRowSetImpl crs1 = new CachedRowSetImpl();
                    crs1.populate(rs);
                    
                    
                    System.out.println("both queries executed successfully");
                    
                    
                                      
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                    ObjectOutputStream obj = new ObjectOutputStream(soc.getOutputStream());
                    obj.writeObject(crs1);
                    
                    System.out.println("both rs sent ");
                    
                    String category = buff.readLine();
                    String sub_category = buff.readLine();
                    System.out.println("category");
                    String sql2 = "SELECT * FROM priority";
                    rs = db26.stmt.executeQuery(sql2);
                    int flag = 0;
                    while(rs.next())
                    {
                        if(rs.getString("User_name").equals(user_name) && rs.getString("category").equals(category) && rs.getString("sub_category").equals(sub_category))
                             flag=1;
                    }
                    if(user_name.equals("null"))
                        flag=1;
                    System.out.println(flag);
                    if(flag == 0)
                    {
                    String sq = "INSERT INTO priority (User_name,category,sub_category) values('"+user_name+"','"+category+"','"+sub_category+"')";
                    System.out.println("updating table");
                    db26.stmt.executeUpdate(sq);
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        
        };
        t.start();    
    
    }
     
    public static void Update_Profile(){
        Thread t= new Thread(){
        
            public void run(){
            
            
            }
        
        };
        t.start();    
    
    }
    
        private void Image() {
        //To change body of generated methods, choose Tools | Templates.
         Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                   
                   // BufferedReader buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    Database_Server db27 = new Database_Server();
                    try {
                        db27.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM image_table";
                    System.out.println("successfully executed query for fetching imgage");
                    ResultSet rs = db27.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                   
                }
                
                
            }
        
        };
        t.start();   
    }
    
    private void Recent_sale() {
        //To change body of generated methods, choose Tools | Templates.
         Thread t;
        t = new Thread(){
            
            public void run(){
                
         
                PrintWriter pr = null;
                try {
                    
                    pr = new PrintWriter(soc.getOutputStream(),true);
                    pr.println("OK");
                     buff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    Database_Server db28 = new Database_Server();
                    try {
                        db28.Start_Connection();
                    } catch (SQLException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String sql = "SELECT * FROM Upload WHERE status=0 ORDER BY item_id DESC LIMIT 5";
                    System.out.println("successfully executed query for fetching imgage");
                    ResultSet rs = db28.stmt.executeQuery(sql);
                    CachedRowSetImpl crs = new CachedRowSetImpl();
                    crs.populate(rs);
                  
                    System.out.println("cache is populated");
                    
                    ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                    ob.writeObject(crs);
                   
                 
                } catch (SQLException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pr.close();
                }
                
                
            }
        
        };
        t.start();   
    }
    
        
    public static void Bid(){
        Thread t= new Thread(){
        
            public void run(){
            
            
            }
        
        };
        t.start();    
    
    }
    
}
