/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author admin
 */
@WebServlet(name = "Keks", urlPatterns = {"/Keks"})
public class Keks extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Gson gson = new GsonBuilder().create();
            Map<String, String> encrypted = new HashMap<>();

            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();

            encrypted.put("dni", encrypt(request.getParameter("dni")));
            pw.println(gson.toJson(encrypted));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private String encrypt(String dni) {
        byte b[] = dni.getBytes();
        java.util.zip.CRC32 x = new java.util.zip.CRC32();
        x.update(b);
        return Long.toHexString(x.getValue());
    }

}
