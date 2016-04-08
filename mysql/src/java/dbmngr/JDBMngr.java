/*
 ┌────────────────────────────────────────────────────────────────────┐ 
 │ Java DataBase Manager                                              │ 
 ├────────────────────────────────────────────────────────────────────┤ 
 │ Contact: kanduganesh@gmail.com                                             │ 
 ├────────────────────────────────────────────────────────────────────┤ 
 │ Copyright © 2016 Ganesh Kandu                                      │ 
 └────────────────────────────────────────────────────────────────────┘ 
*/
package dbmngr;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mysql.html;
import mysql.mysql;

/**
 *
 * @author ckandu
 */
public class JDBMngr extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String db = request.getParameter("database");
        String Collation = request.getParameter("collation");
        String action = request.getParameter("action")+"_";
        String table = request.getParameter("table");
        boolean q = true;
        String idb = null,itable = null;
        String query = "";
        String c="",v="";
        html h=new html();
        HttpSession session = request.getSession(true);
        mysql my=new mysql();
        if(!action.equals("createdb_")){
            my.setDB(db);
        }
        out.write("wait Redirecting to index.jsp");
        String[] ss = (String[])session.getAttribute("JDBMnger_login_19091991");
        my.connect(ss);
        try {
            if(action.equals("createdb_")){
                my.createdb(db,Collation);
                if(my.Exeption() != null){
                    response.sendRedirect("index.jsp?error="+my.Exeption());
                }else{
                    response.sendRedirect("index.jsp?db="+db);
                }
            }
            if("updatetable_".equals(request.getParameter("jdbmngr_updatetable_12091991")+"_")){
                String[] columns = my.getcolumn(request.getParameter("jdbmngr_db_12091991"),request.getParameter("jdbmngr_table_12091991"));
                query = "UPDATE "+request.getParameter("jdbmngr_db_12091991")+
                        "."+request.getParameter("jdbmngr_table_12091991");
                query +=" SET ";
                for (String column:columns){
                    if(!(request.getParameter(column)).matches("[0-9]+")){
						if(q){
                           query += column+"='"+request.getParameter(column)+"' "; 
						   q=false;
						}else{
                           query += ","+column+"='"+my.tosql(request.getParameter(column))+"' "; 
						}
                    }else{
						if(q){
                        query += column+"="+request.getParameter(column)+" "; 
						   q=false;
						}else{
                           query += ","+column+"='"+my.tosql(request.getParameter(column))+"' "; 
						}
                    }
                }
                query += "WHERE "+request.getParameter("jdbmngr_key_12091991")+"="+request.getParameter("jdbmngr_value_12091991")+";";
                my.execsql(query);
                response.sendRedirect("index.jsp?db="+request.getParameter("jdbmngr_db_12091991")+"&table="+request.getParameter("jdbmngr_table_12091991"));
            }
            if(("inserttable_").equals(request.getParameter("jdbmngr_action_12091991")+"_")){
                idb = request.getParameter("jdbmngr_db_12091991");
                itable = request.getParameter("jdbmngr_table_12091991");
                String[] columns = my.getcolumn(idb,itable);
                q = true;
                query = "INSERT INTO "+idb+"."+itable;
                String value;
                for (String column:columns){
                    value = request.getParameter(column);
                    if((value).matches("[0-9]+")){
                    if(q){
                        c += column;
                        v += value;
                    }else{
                        c += ","+column;
                        v += ","+value;
                    }
                    q = false;
                    }else{
                    if(q){
                        c += column;
                        v += "'"+my.tosql(value)+"'";
                    }else{
                        c += ","+column;
                        if(value == null || value.equals("")){
                            v += ","+"null";
                        }else{
                            v += ","+"'"+my.tosql(value)+"'";
                        }
                    }
                    q = false;
                    }
                }  
                 query =query+ "("+c+") VALUES ("+v+");";
                 my.execsql(query);
                 response.sendRedirect("index.jsp?db="+idb+"&table="+itable+"&error="+my.Exeption());
            }
            
        }catch(Exception e){
            response.sendRedirect("index.jsp?db="+idb+"&table="+itable+"&error="+my.Exeption());
        }
        if(("execute_").equals(request.getParameter("execute")+"_")){
            my.querybox(request.getParameter("query"));
            response.sendRedirect("index.jsp?db="+my.getDB()+"&error="+my.Exeption());
        }
        if(action.equals("cretetable_")){
            response.sendRedirect("index.jsp?db="+db+"&action=cretetable");
        }
        my = null; 
        h = null; 
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "JDBMngr";
    }// </editor-fold>

}
