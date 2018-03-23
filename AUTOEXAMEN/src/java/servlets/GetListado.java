/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import magia.DBManager;
import org.bson.Document;

/**
 *
 * @author admin
 */
public class GetListado extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DBManager db = new DBManager();
        MongoCollection<Document> collectionNotas = db.getDatabase("resultados", db.connectToDb()).getCollection("notas");

        //Mete todos los documentos en una lista
        List<Document> notas = (List<Document>) collectionNotas.find().into(
                new ArrayList<Document>());
        db.closeConn();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(notas);
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
        } catch (Exception e) {
            Map<String, String> emess = new HashMap<>();
            emess.put("error", "Ha ocurrido un error interno.");            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.println(gson.toJson(emess));

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
