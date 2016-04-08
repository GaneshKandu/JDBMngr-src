/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbmngr;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mysql.mysql;

/**
 *
 * @author ckandu
 */
public class export extends HttpServlet {

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
        response.setContentType("text/sql");
        PrintWriter out = response.getWriter();
        mysql my=new mysql();
        Date date = new Date();
        HttpSession session = request.getSession(true);
        String db = request.getParameter("db");
        response.setHeader("Content-Disposition","attachment; filename="+db+".sql");
        String table = request.getParameter("table");
        my.setDB(db);
        String[] ss = (String[])session.getAttribute("JDBMnger_login_19091991");
        my.connect(ss);
        try {
                out.println("--");
                out.println("-- JDBMngr");
                out.println("-- "+date.toString());
                out.println("-- Host: "+InetAddress.getLocalHost());
                out.println("--\n\n");
                out.println("--");
                out.println("-- Database: "+db);
                out.println("--");
                String[] tables = my.getTBLList();
                for(String tbl:tables){
                    out.println(my.showtbl(tbl));
                    if(my.getrowcount(tbl) > 0){
                    out.println(my.showinsert(tbl));
                    }
                }
        } finally {
            out.close();
        }
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
        return "Short description";
    }// </editor-fold>

}
