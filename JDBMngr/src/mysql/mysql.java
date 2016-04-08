/*
 ┌────────────────────────────────────────────────────────────────────┐ 
 │ Java DataBase Manager                                              │ 
 ├────────────────────────────────────────────────────────────────────┤ 
 │ Contact: kanduganesh@gmail.com                                     │ 
 ├────────────────────────────────────────────────────────────────────┤ 
 │ Copyright © 2016 Ganesh Kandu                                      │ 
 └────────────────────────────────────────────────────────────────────┘ 
*/
package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class mysql {
    html h=new html();
    jdbbean b;
    String html;
    Connection con;
    Statement stmt;
    String error = "";
    String db = null;
    int fpg = 0,lpg = 50;
    private static final String CHAR_LIST =  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 64;
    //ResultSet rs;
    public void setDB(String db){
        this.db = db;
    }
    public void setbean(jdbbean b){
       this.b = b;
    }
    public String getDB(){
        return this.db;
    }
    public void connect(String[] session){
            String driver= "com.mysql.jdbc.Driver";
            String url= "jdbc:mysql://"+session[2]+":"+session[3];
            if(getDB() != null){
                url= "jdbc:mysql://"+session[2]+":"+session[3]+"/"+getDB();
            }
            String username= session[0];
            String password= session[1];
            try {
                Class.forName(driver);
                this.con=DriverManager.getConnection(url,username,password);
                this.stmt = this.con.createStatement();
            } catch (ClassNotFoundException ex) {
                error += ex.getMessage();
            }catch (SQLException ex) {
                error += ex.getMessage();
            }
    }
    public Connection getcon(){
        return this.con;
    }
    public Statement getstmt(){
        return this.stmt;
    }
   public String getDBList(){
      html = "";    
      ResultSet rs;
	try
	{
            html = "<ol>";
                rs = this.stmt.executeQuery("show databases;");
                while ( rs.next() ) {
                String dbs = rs.getString("Database");
    		html += "<li><label for=\"folder1\"><a href=\"index.jsp?db="+dbs+"\">"+dbs+"</a></label></li>";
                }
            html += "</ol>";
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        } 
        System.gc();
       return html;
   }
   public String getTList(String db)
   {
       ResultSet rs;
        html = "";
	html = "<table>";
        html += "<tr>";
        html += "<th>Name</th>";
        html += "<th>Action</th>";
        html += "<th>Engine</th>";
        html += "<th>Rows</th>";
        html += "<th>Size</th>";
        html += "<th>Create_time</th>";
        html += "<th>Collation</th>";
        html += "</tr>"; 
        try
        {
                rs = stmt.executeQuery("SHOW TABLE STATUS FROM `"+db+"`;");
                while ( rs.next() ) {
                    String Name = rs.getString("Name");
                    String Engine = rs.getString("Engine");
                    String Rows = rs.getString("Rows");
                    String Create_time = rs.getString("Create_time");
                    String Collation = rs.getString("Collation");
                    String data_length = rs.getString("data_length");
                    String index_length = rs.getString("index_length");
                    int dl,il;
                    if(data_length == null){  dl = 0; }else{ dl = Integer.parseInt(data_length); }
                    if(index_length == null){  il = 0; }else{ il = Integer.parseInt(index_length); }
                    int size = Math.round(dl + il);
                html+="<tr>";
                html+="<td><img src=\"Images/table.png\"/><a href=\"index.jsp?db="+db+"&table="+Name+"\">"+Name+"</a></td>";
                html+="<td>"
                        + "<a href=\"index.jsp?db="+db+"&table="+Name+"&action=inserttable\" class=\"tooltip\" title=\"Insert Table\"><img src=\"Images/insert_table.png\"/></a>"
                        + "<a href=\"index.jsp?db="+db+"&table="+Name+"&action=trunktable\" class=\"tooltip\" title=\"Trunk Table\"><img src=\"Images/empty_table.png\"/></a>"
                        + "<a href=\"index.jsp?db="+db+"&table="+Name+"&action=droptable\" class=\"tooltip\" title=\"Drop Table\" ><img src=\"Images/drop_table.png\"/></a>"
                        + "</td>";
                html+="<td>"+Engine+"</td>";
                html+="<td>"+Rows+"</td>";
                html+="<td>"+size+"</td>";
                html+="<td>"+Create_time+"</td>";
                html+="<td>"+Collation+"</td>";
                html+="</tr>";
                }
                
	html += "</table>";
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        }
       System.gc();
       return html;
   }
   public String getCollation(){
      html = "";
      ResultSet rs;
	try
	{
            rs = stmt.executeQuery("SHOW COLLATION;");
            String optgrp = "###";
            String Charset = null;
            html = "<select name=\"collation\" ><option></option>";
            while (rs.next()) {
            Charset = rs.getString("Charset").toString();
            String Collation = rs.getString("Collation").toString();
        if(!optgrp.equals(Charset)){
            if(optgrp != "###"){
                html += "</optgroup>";
            }
                html += "<optgroup label="+Charset+">";
                optgrp = Charset;
        }
        html += "<option value="+Collation+">"+Collation+"</option>";
                }
	html += "</optgroup>";
        html += "</select>";
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        } 
       System.gc();
       return html;
   }
    public void setpg(int page){
        this.fpg = (page*100);
        this.lpg = (page*100) + 100;
    } 
    public String getTable(String database,String table){
      html = "";
      ResultSet rs;
	try
	{   
            String key= gettablekey(database,table);
            rs = stmt.executeQuery("select * from "+database+"."+table+" limit "+this.lpg+" offset "+this.fpg+";");
            boolean eud;
            if(key == ""){
                eud = false;
                //html += "<a href=\"index.jsp?db="+database+"&table="+table+"&action=addrowid\" class=\"tooltip\" title=\"Add rowid to enable table action\"><img src=\"Images/alert.png\"/></a>";
            }else{
                eud = true;
            }
            ResultSetMetaData rsmd=rs.getMetaData();
            int cols=rsmd.getColumnCount();
            String c[]=new String[cols];
            html += "<table>";
            html += "<tr>";
            if(eud){
            html += "<th>Action</th>";
            }
            for(int i=0;i<cols;i++){
                c[i]=rsmd.getColumnName(i+1);
            html +="<th>"+c[i]+"</th>";
            }
            html +="</tr>";
            //rows
            Object row[]=new Object[cols];
            while(rs.next()){
            html +="<tr>";
            if(eud){
                html +="<td>";
                html +="<a href=\"index.jsp?db="+database+"&table="+table+"&key="+key+"&value="+rs.getString(key)+"&action=update\" class=\"tooltip\" title=\"Update Row\"><img src=\"Images/edit_table.png\"/></a>";
                html +="<a href=\"index.jsp?db="+database+"&table="+table+"&key="+key+"&value="+rs.getString(key)+"&action=delete\" class=\"tooltip\" title=\"Delete Row\"><img src=\"Images/drop_table.png\"/></a>";
                html +="</td>";
            }
            for(int i=0;i<cols;i++){
                try{
                    row[i]=rs.getString(i+1);
                }catch(Exception e){
                    row[i]= "0000-00-00 00:00:00";
                }
                if(row[i] == null){
                    html +="<td>"+row[i]+"</td>";
                }else{
                    html +="<td>"+h.text2html(row[i].toString())+"</td>";
                }
            }
            html +="</tr>";
            }
            html += "</table>";
        }
        catch(Exception e) 
        {
            error += e.getMessage();
        } 
        System.gc();
   return html;
   }
    
   public String gettablekey(String db,String table){
       ResultSet rs;
        String key = "";
	try
	{   
            rs = stmt.executeQuery("SHOW KEYS FROM "+db+"."+table+" WHERE Key_name = 'PRIMARY';");
            while(rs.next()){
                key = rs.getString("Column_name");
            }
        } catch (SQLException ex) {
            error += ex.getMessage();
        }
        return key;
   }
    public void execsql(String sql){
        try 
        {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            error += ex.getMessage();
        }
    }
    public String inserttable(String db,String table){
        ResultSet rs;
        html = "";
	try
	{
                rs = stmt.executeQuery("SHOW FIELDS FROM "+db+"."+table+";");
                html = "<form action=\"JDBMngr\" method=\"post\" >";
                html += "<input type=\"hidden\" name=\"jdbmngr_table_12091991\" value=\""+table+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_db_12091991\" value=\""+db+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_action_12091991\" value=\"inserttable\"/>";
                html += "<table>";
                html +="<tr>"
                        + "<th>Field</th>"
                        + "<th>Type</th>"
                        + "<th>Value</th>"
                        + "</tr>";
                while (rs.next()){
                String Field = rs.getString("Field");
                String Type = rs.getString("Type");
                html += "<tr>"
                        + "<td>"+Field+"</td>"
                        + "<td>"+Type+"</td>"
                        + "<td><input type=\"text\" name=\""+Field+"\"/>"
                        + "</tr>";
                }
                html +="<tr>"
                        + "<td></td>"
                        + "<td></td>"
                        + "<td><input type=\"submit\" value=\"Insert\"/></td>"
                        + "</tr>";
            html += "</table>";
            html += "</form>";
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        } 
        System.gc();
        return html;
    }
    public String[] getcolumn(String db,String table){
        ResultSet rs;
        String column = "";
	try
	{
            rs = stmt.executeQuery("SHOW FIELDS FROM "+db+"."+table+";");
            while (rs.next()){
                column += rs.getString("Field")+" ";
            }
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        }   
        System.gc();
        return column.split(" ");
    }
    public String updaterow(String db,String table,String key,String value){
        ResultSet rs;
        String fvalue;
        html = "";
        String query = "SELECT * FROM "+db+"."+table+" WHERE "+key+"=";
        if((value.toString()).matches("[0-9]+")){
            query += value+";"; 
        }else{
            query += "'"+value+"';";
        }
	try
	{
                Map keyvalue = new HashMap();
		rs = null;
                rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd=rs.getMetaData();
                int cols=rsmd.getColumnCount();
                while(rs.next())
                for(int i=0;i<cols;i++){
                    keyvalue.put(rsmd.getColumnName(i+1),rs.getString(rsmd.getColumnName(i+1)));
                }
                rs = stmt.executeQuery("SHOW FIELDS FROM "+db+"."+table+";");
                html = "<form action=\"JDBMngr\" method=\"post\" >";
                html += "<input type=\"hidden\" name=\"jdbmngr_table_12091991\" value=\""+table+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_db_12091991\" value=\""+db+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_key_12091991\" value=\""+key+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_value_12091991\" value=\""+value+"\"/>";
                html += "<input type=\"hidden\" name=\"jdbmngr_updatetable_12091991\" value=\"updatetable\"/>";
                html += "<table>";
                html +="<tr>"
                        + "<th>Field</th>"
                        + "<th>Type</th>"
                        + "<th>Value</th>"
                        + "</tr>";
                while (rs.next()){
                String Field = rs.getString("Field");
                String Type = rs.getString("Type");
                try{
                    fvalue = keyvalue.get(Field).toString();
                }catch (Exception e){
                    fvalue = "";
                }
                html += "<tr>"
                        + "<td>"+Field+"</td>"
                        + "<td>"+Type+"</td>";
                        if(fvalue.length() > 32){
                            html += "<td><textarea name=\""+Field+"\" >"+this.toinput(fvalue)+"</textarea>";
                        }else{
                            html += "<td><input type=\"text\" name=\""+Field+"\" value=\""+this.toinput(fvalue)+"\" />";
                        }
                        html += "</tr>";
                }
                html +="<tr>"
                        + "<td></td>"
                        + "<td></td>"
                        + "<td><input type=\"submit\" value=\"Update\"/></td>"
                        + "</tr>";
            html += "</table>";
            html += "</form>";
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        } 
        System.gc();
        return html;
    }
    public String getcolumnno(){
        html = "";
        html += "<input type=\"number\" id=\"columnno\" oninput=\"href();\" placeholder=\"Enter No of Columns\" />";
        html += "<a href=\"index.jsp?db=joomla&action=createtable\" id=\"create_tb\" ><input type=\"button\" value=\"Create Table\" /></a>";
        return html;
    }
    public String getfieldtype(){
    html=""+
"<option >INT</option>" +
"<option >VARCHAR</option>" +
"<option >TEXT</option>" +
"<option >DATE</option>" +
"<optgroup label=\"Numeric\">" +
"<option >TINYINT</option>" +
"<option >SMALLINT</option>" +
"<option >MEDIUMINT</option>" +
"<option >INT</option>" +
"<option >BIGINT</option>" +
"<option >DECIMAL</option>" +
"<option >FLOAT</option>" +
"<option >DOUBLE</option>" +
"<option >REAL</option>" +
"<option >BIT</option>" +
"<option >BOOLEAN</option>" +
"<option >SERIAL</option>" +
"</optgroup>" +
"<optgroup label=\"Date and time\">" +
"<option >DATE</option>" +
"<option >DATETIME</option>" +
"<option >TIMESTAMP</option>" +
"<option >TIME</option>" +
"<option >YEAR</option>" +
"</optgroup>" +
"<optgroup label=\"String\">" +
"<option >CHAR</option>" +
"<option >VARCHAR</option>" +
"<option >TINYTEXT</option>" +
"<option >TEXT</option>" +
"<option >MEDIUMTEXT</option>" +
"<option >LONGTEXT</option>" +
"<option >BINARY</option>" +
"<option >VARBINARY</option>" +
"<option >TINYBLOB</option>" +
"<option >MEDIUMBLOB</option>" +
"<option >BLOB</option>" +
"<option >LONGBLOB</option>" +
"<option >ENUM</option>" +
"<option >SET</option>" +
"</optgroup>" +
"<optgroup label=\"Spatial\">" +
"<option >GEOMETRY</option>" +
"<option >POINT</option>" +
"<option >LINESTRING</option>" +
"<option >POLYGON</option>" +
"<option >MULTIPOINT</option>" +
"<option >MULTILINESTRING</option>" +
"<option >MULTIPOLYGON</option>" +
"<option >GEOMETRYCOLLECTION</option>" +
"</optgroup>";
    return html;
}
public String createtable(String db,int columns){
    html = "<form action=\"JDBMngr\" method=\"post\" >"
            + "<table>"
            + "<tr>"
            + "<td colspan=\"7\" >"
            + "Table Name <input type=\"text\" name=\"jdbmngr_table_name_12091991\"/>"
            + "Comment <input type=\"text\" name=\"jdbmngr_table_comment_12091991\"/>"
            + "Collation "+this.getCollation()
            + "Storage Engine <select name=\"jdbmngr_table_comment_12091991\">"+this.getengine()+"</select>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<th>Name</th>"
            + "<th>Type</th>"
            + "<th>Lenght</th>"
            + "<th>Collation</th>"
            + "<th>Null</th>"
            + "<th>Index</th>"
            + "<th>Comment</th>"
            + "</tr>\n";
            for(int i = 0; i < columns ; i++){
                html += "<tr>"
                + "<td><input type=\"text\" name=\"jdbmngr_column_name_ct_12091991\" /></td>"
                + "<td><select name=\"jdbmngr_column_type_ct_12091991\" >"+this.getfieldtype()+"</select></td>"
                + "<td><input type=\"text\" name=\"jdbmngr_column_lenght_ct_12091991\" /></td>"
                + "<td>"+this.getCollation()+"</td>"
                + "<td><input type=\"checkbox\" name=\"jdbmngr_column_null_ct_12091991\" /></td>"
                + "<td><select name=\"jdbmngr_column_index_ct_12091991\" >"+this.getindex()+"</select></td>"
                + "<td><input type=\"text\" name=\"jdbmngr_column_comment_ct_12091991\" /></td>"
                + "</tr>\n";
            }
            html += "<tr><td colspan=\"7\"><input type=\"submit\" value=\"Create Table\" /></td></tr>\n";
            html += "</table>"
            + "</form>";
    
    return html;
}
   public String getindex(){
        html = "";
        html += "<option value=\"none\"></option>" +
"<option value=\"PRIMARY\">PRIMARY</option>" +
"<option value=\"UNIQUE\">UNIQUE</option>" +
"<option value=\"INDEX\">INDEX</option>" +
"<option value=\"FULLTEXT\">FULLTEXT</option>" +
"<option value=\"SPATIAL\">SPATIAL</option>";
        return html;
    }
   public String getengine(){
       html = "";
       html = "<option value=\"InnoDB\" selected=\"selected\">InnoDB</option>" +
"    <option value=\"MRG_MYISAM\">MRG_MYISAM</option>" +
"    <option value=\"CSV\">CSV</option>" +
"    <option value=\"BLACKHOLE\">BLACKHOLE</option>" +
"    <option value=\"MEMORY\">MEMORY</option>" +
"    <option value=\"ARCHIVE\">ARCHIVE</option>" +
"    <option value=\"MyISAM\">MyISAM</option>";
       return html;
   }
   public boolean isvalid(String user,String pass,String host,String port){
        String url="jdbc:mysql://"+host+":"+port+"/";
        boolean res = false;
        try{
            if(user != null){
            Connection c = DriverManager.getConnection(url,user,pass);
            res = true;
            c.close();
            }
        } catch (SQLException ex){
            error += ex.getMessage();
        }
        return res;
   } 
   public void createdb(String db,String Collation){
        html = "";
	try
	{
            String[] CHARACTER = Collation.split("_");
            if(Collation != ""){
               html +=" CHARACTER SET "+CHARACTER[0]+" COLLATE "+Collation;
            }
            this.stmt.executeUpdate("CREATE DATABASE `"+db+"`"+html+";");
        }catch(SQLException se){
            error += se.getMessage();
        }catch(Exception e){
            error += e.getMessage();
        }
        System.gc();
    }
    public String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
    private int getRandomNumber() {
    int randomInt = 0;
    Random randomGenerator = new Random();
    randomInt = randomGenerator.nextInt(CHAR_LIST.length());
    if (randomInt - 1 == -1) {
        return randomInt;
    } else {
        return randomInt - 1;
    }
    }
    public String Exeption(){
        return this.error.replaceAll("\r", "<br/>").replaceAll("\n", "");
    } 
    public String getERROR(){
        if(this.error != ""){
            this.error = "<script>dialogbox(\"MySQL ERRORS\",\""+this.Exeption()+"\");</script>";
        }
        return this.error;
    }
    public String querybox(String sqls){
        for (String sql: sqls.split(";")){
            try {
                if(sql != null){
                    this.stmt.execute(sql+";");
                }
            } catch (SQLException ex) {
                this.error += ex.getMessage();
            }
        }
        return this.error;
    }
    public String tosql(String text){
        text = text.replace("\\", "\\\\");
        text = text.replaceAll("'", "''");
    return text;
    }
    public String toinput(String text){
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
    return text;
    }
    public void adderror(String error){
        this.error += error;
    }
   public String[] getTBLList(){
      String table = "";    
      ResultSet rs;
        if(this.db != null){
	try
	{
                rs = stmt.executeQuery("SHOW TABLE STATUS FROM `"+this.db+"`;");
                while ( rs.next() ) {
                String tbl = rs.getString("Name");
    		table += " "+tbl;
                }
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        } 
        }
        System.gc();
        return table.split(" ");
   }
   public String showtbl(String table){
       html = "";
       ResultSet rs;
       	try
	{
            if(this.isNotString(table)){
                rs = stmt.executeQuery("SHOW CREATE TABLE "+table+";");
                while ( rs.next() ) {
                    String qtbl = rs.getString(1);
                    String query = rs.getString(2);
                    html += "--\r\n";
                    html += "-- Table dump : "+qtbl+"\r\n";
                    html += "--\r\n\r\n\r\n";
                    html += query+";\r\n\r\n";
                }
            }
        }
        catch (Exception e) 
        {
            error += e.getMessage();
        }
       return html;
   }
   public String showinsert(String table){
       ResultSet rs;
       int count = 0;
       if(this.isNotString(table)){
            html = "";
            html += "\r\n--\r\n";
            html += "-- Inserts of "+table+"\r\n";
            html += "--\r\n\r\n";
           try {
                rs = stmt.executeQuery("select * from "+this.db+"."+table+";");
                ResultSetMetaData rsmd=rs.getMetaData();
                int cols=rsmd.getColumnCount();
                String c[]=new String[cols];
                Object row[]=new Object[cols];
                html += "INSERT INTO `"+table+"` (";
                for(int i=0;i<cols;i++){
                    c[i]=rsmd.getColumnName(i+1);
                    if(i != 0){html += ",";}
                    html +="`"+c[i]+"`";
                }
                html +=") VALUES\r\n";
                boolean rn = true;
                while(rs.next()){
                    if(rn){rn = false;}else{html += ",\r\n";} 
                    html += "(";
                    for(int i=0;i<cols;i++){
                        try{
                            row[i]=rs.getString(i+1);
                        }catch(Exception e){
                            row[i]= "0000-00-00 00:00:00";
                        }
                        if(i != 0){html += ",";}
                        if(row[i] == null){
                            html += row[i];
                        }else{
                            if((row[i].toString()).matches("[0-9]+")){
                                html += this.tosql(row[i].toString());
                            }else{
                                html +="'"+this.tosql(row[i].toString())+"'";
                            }
                        }
                    }
                    html += ")";
                }
                html += ";\r\n\r\n\r\n";
           } catch (SQLException ex) {
               error += ex.getMessage();
           }
        }
       return html;
   }
  public static boolean isNumeric(String str)
  {
    try
    {
      double d = Double.parseDouble(str);
    }
    catch(NumberFormatException nfe)
    {
      return false;
    }
    return true;
  }
  
  public boolean isNotString(String string)
  {
   return string != null && !string.isEmpty() && !string.trim().isEmpty();
  }
  public int getrowcount(String tbl){
      int count = 0;
      ResultSet res;
        try {
            res = this.stmt.executeQuery("SELECT COUNT(*) FROM "+tbl);
            while (res.next()){
              count = res.getInt(1);
            }
        } catch (SQLException ex) {
            error += ex.getMessage();
        }
        return count;
  }
  public void setrowcount(){
      b.setRowCount(this.getrowcount(b.getDb()+"."+b.getTable()));
  }
}
