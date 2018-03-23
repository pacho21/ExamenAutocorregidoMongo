package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class PostAnswer extends HttpServlet {

    private String encrypt(String dni) {
        byte b[] = dni.getBytes();
        java.util.zip.CRC32 x = new java.util.zip.CRC32();
        x.update(b);
        return Long.toHexString(x.getValue());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //va a devolver el examen que se quiera hacer en formato json.
        //este sera pintado por el frontend

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //-> antes mirar si el usuario ha realizado el examen.
        DBManager db = new DBManager();

        // data:{ q_01 : ans_1, q_02 : ans_2, q_03: ans_3, q_04:ans_4, q_05:ans_5 |array, q_06:ans_6 |array ,q_07: ans_7, q_08: ans_8, q_09:ans_9 |array, q_10:ans_10 |array}
        ArrayList<String> ans5 = new ArrayList<String>();
        ArrayList<String> ans6 = new ArrayList<String>();
        ArrayList<String> ans9 = new ArrayList<String>();
        ArrayList<String> ans10 = new ArrayList<String>();

        String dni = encrypt(request.getParameter("dni"));
        String examenID = request.getParameter("examen");
        //strings que guardaran todas las respuestas recibidas.
        String answer1 = request.getParameter("q_01");
        String answer2 = request.getParameter("q_02");
        String answer3 = request.getParameter("q_03");
        String answer4 = request.getParameter("q_04");
        //array para las respuestas multiples
        String[] answer5 = request.getParameterValues("q_05[]");
        String[] answer6 = request.getParameterValues("q_06[]");
        String answer7 = request.getParameter("q_07");
        String answer8 = request.getParameter("q_08");
        //array para las respuestas multiples
        String[] answer9 = request.getParameterValues("q_09[]");
        String[] answer10 = request.getParameterValues("q_10[]");
        for (int i = 0; i < answer5.length; i++) {
            ans5.add(answer5[i]);
        }
        for (int i = 0; i < answer6.length; i++) {
            ans6.add(answer6[i]);
        }
        for (int i = 0; i < answer9.length; i++) {
            ans9.add(answer9[i]);
        }
        for (int i = 0; i < answer10.length; i++) {
            ans10.add(answer10[i]);
        }

        //--------------------------> creamos el documento del examen
        Document user_answers = new Document("dni", dni)
                .append("exam", examenID)
                .append("q_01", answer1)
                .append("q_02", answer2)
                .append("q_03", answer3)
                .append("q_04", answer4)
                .append("q_05", ans5)
                .append("q_06", ans6)
                .append("q_07", answer7)
                .append("q_08", answer8)
                .append("q_09", ans9)
                .append("q_10", ans10);

        //guardamos el examen ->
        db.connectToDb();
        float nota = calcularNota(user_answers, db.getRespuesta(examenID));
        user_answers.append("nota", nota);
        Document user_nota = new Document("dni", dni).append("exam", examenID).append("nota", nota);
        if (db.examenUsuarioExiste(dni, examenID)) {
            Document oldExam = db.getUsuarioExamen(examenID, dni);
            //user_answers.put("_id", oldExam.getObjectId("_id"));
            db.getExamenesUsuarios().getCollection(dni).replaceOne(oldExam, user_answers);

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.append("dni", dni);
            whereQuery.append("exam", examenID);
            db.getDatabase("resultados", db.connectToDb()).getCollection("notas").replaceOne(whereQuery, user_nota);

        } else {
            db.getExamenesUsuarios().getCollection(dni).insertOne(user_answers);
            db.getDatabase("resultados", db.connectToDb()).getCollection("notas").insertOne(user_nota);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> msgHash = new HashMap<>();
        msgHash.put("hash", dni);
        response.setContentType("application/json");
        PrintWriter pw = response.getWriter();
        pw.println(gson.toJson(msgHash));

        db.closeConn();

    }

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.

    }

    public float calcularNota(Document user_answer, Document respuestas) {
        float nota = 0;

        //corregir las simples ->        
        if (user_answer.getString("q_01").toLowerCase().equals(respuestas.getString("q_01").toLowerCase())) {
            nota++;
        }
        if (user_answer.getString("q_02").toLowerCase().equals(respuestas.getString("q_02").toLowerCase())) {
            nota++;
        }
        if (user_answer.getString("q_03").toLowerCase().equals(respuestas.getString("q_03").toLowerCase())) {
            nota++;
        }
        if (user_answer.getString("q_04").toLowerCase().equals(respuestas.getString("q_04").toLowerCase())) {
            nota++;
        }
        if (user_answer.getString("q_07").toLowerCase().equals(respuestas.getString("q_07").toLowerCase())) {
            nota++;
        }
        if (user_answer.getString("q_08").toLowerCase().equals(respuestas.getString("q_08").toLowerCase())) {
            nota++;
        }
        System.out.println(nota);

        ArrayList<String> user_q_05 = (ArrayList<String>) user_answer.get("q_05");
        ArrayList<String> user_q_06 = (ArrayList<String>) user_answer.get("q_06");
        ArrayList<String> user_q_09 = (ArrayList<String>) user_answer.get("q_09");
        ArrayList<String> user_q_10 = (ArrayList<String>) user_answer.get("q_10");

        //corregir multiples -> cacaoooooo
        ArrayList<String> resp_q_05 = (ArrayList<String>) respuestas.get("q_05");
        ArrayList<String> resp_q_06 = (ArrayList<String>) respuestas.get("q_06");
        ArrayList<String> resp_q_09 = (ArrayList<String>) respuestas.get("q_09");
        ArrayList<String> resp_q_10 = (ArrayList<String>) respuestas.get("q_10");

        nota += corregirMultiple(user_q_05, resp_q_05);        
        nota += corregirMultiple(user_q_06, resp_q_06);        
        nota += corregirMultiple(user_q_09, resp_q_09);       
        nota += corregirMultiple(user_q_10, resp_q_10);       

        return nota;
    }

    public float corregirMultiple(ArrayList<String> user, ArrayList<String> answer) {
        int nota = 0;
        int correct = 0;
        for (String ans : answer) {
            for (String usr : user) {

                if (usr.toLowerCase().equals(ans.toLowerCase())) {
                    correct++;
                }
            }
        }
        if (answer.size() == correct && correct == user.size()) {
            return 1;
        } else {
            int a = user.size() - answer.size();
            int b = correct - a;
            if (b > 0 && answer.size() > 0) {
                System.out.println(answer.size());
                System.out.println((float) b / answer.size());
                return (float) b / answer.size();
            } else {
                return 0;
            }

        }

    }
}
