/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
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
public class GetExamen extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://admin:admin@examen-yfcq2.mongodb.net/test");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("examenes");
        MongoCollection<Document> examenes = database.getCollection("examen");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", request.getParameter("examID"));

        FindIterable<Document> cursor = examenes.find(whereQuery);
        Document myDoc = cursor.first();

        //Send document
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(myDoc.toJson());
        } catch (Exception e) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, String> emess = new HashMap<>();
            emess.put("error", "Ha ocurrido un error interno a la hora de recuperar el examen.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.println(gson.toJson(emess));
        }
        mongoClient.close();
    }

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.

        ServletContext context = getServletContext();

        String fullPathA = context.getRealPath("/examenes/examenA.json");
        String fullPathB = context.getRealPath("/examenes/examenB.json");
        String fullPathC = context.getRealPath("/examenes/examenC.json");
        String respA = context.getRealPath("/respuestas/answerA.json");
        String respB = context.getRealPath("/respuestas/answerB.json");
        String respC = context.getRealPath("/respuestas/answerC.json");

        Document examA = null;
        Document examB = null;
        Document examC = null;

        Document ansA = null;
        Document ansB = null;
        Document ansC = null;

        DBManager db = new DBManager();

        try {

            String exA = new String(Files.readAllBytes(Paths.get(fullPathA)), StandardCharsets.UTF_8);
            String anA = new String(Files.readAllBytes(Paths.get(respA)), StandardCharsets.UTF_8);
            String exB = new String(Files.readAllBytes(Paths.get(fullPathB)), StandardCharsets.UTF_8);
            String anB = new String(Files.readAllBytes(Paths.get(respB)), StandardCharsets.UTF_8);
            String exC = new String(Files.readAllBytes(Paths.get(fullPathC)), StandardCharsets.UTF_8);
            String anC = new String(Files.readAllBytes(Paths.get(respC)), StandardCharsets.UTF_8);

            examA = Document.parse(exA);
            examB = Document.parse(exB);
            examC = Document.parse(exC);
            ansA = Document.parse(anA);
            ansB = Document.parse(anB);
            ansC = Document.parse(anC);

        } catch (IOException ex) {

            Logger.getLogger(PostAnswer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (examA != null && examB != null && examC != null) {

            if (db.getExamenes().count(examA) < 1) {
                db.getExamenes().insertOne(examA);
            }

            if (db.getExamenes().count(examB) < 1) {
                db.getExamenes().insertOne(examB);
            }

            if (db.getExamenes().count(examC) < 1) {
                db.getExamenes().insertOne(examC);
            }

            if (db.getRespuestas().count(ansA) < 1) {
                db.getRespuestas().insertOne(ansA);
            }
        }
        if (ansA != null && ansB != null && ansC != null) {
            if (db.getRespuestas().count(ansA) < 1) {
                db.getRespuestas().insertOne(ansA);
            }
            if (db.getRespuestas().count(ansB) < 1) {
                db.getRespuestas().insertOne(ansB);
            }
            if (db.getRespuestas().count(ansC) < 1) {
                db.getRespuestas().insertOne(ansC);
            }
        }

        db.closeConn();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
