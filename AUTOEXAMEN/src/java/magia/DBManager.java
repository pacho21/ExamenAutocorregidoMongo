package magia;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DBManager {

    private MongoClient mongoClient = null;

    public void addExamen(Document examen) {
        if (this.mongoClient == null) {
            connectToDb();
        }
        this.getExamenes().insertOne(examen);
        this.closeConn();
    }

    /**
     * @return Devuelve una colección de examenes
     */
    public MongoCollection<Document> getExamenes() {
        if (this.mongoClient == null) {
            connectToDb();
        }
        MongoCollection<Document> examenes = getDatabase("examenes", connectToDb()).getCollection("examen");
        return examenes;
    }

    public MongoCollection<Document> getRespuestas() {
        if (this.mongoClient == null) {
            connectToDb();
        }
        MongoCollection<Document> respuestas = getDatabase("respuestas", connectToDb()).getCollection("respuesta");
        return respuestas;
    }

    public MongoDatabase getExamenesUsuarios() {
        if (this.mongoClient == null) {
            connectToDb();
        }
        MongoDatabase examenes_usuarios = getDatabase("examenes_usuarios", mongoClient);
        return examenes_usuarios;
    }

    public boolean examenUsuarioExiste(String dni, String id_examen) {
        if (this.mongoClient == null) {
            connectToDb();
        }
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("exam", id_examen);

        return getExamenesUsuarios().getCollection(dni).count(whereQuery) > 0;

    }

    public MongoCollection<Document> getListadoNotas() {
        if (this.mongoClient == null) {
            connectToDb();
        }
        MongoCollection<Document> listado = getDatabase("resultados", this.mongoClient).getCollection("notas");   
       
        return listado;
    }
    
    public Document getListado(){
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", "listado");

        FindIterable<Document> cursor = getRespuestas().find(whereQuery);
        
        return cursor.first();
    }
    

    public Document getUsuarioExamen(String id_exam, String dni) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("exam", id_exam);

        FindIterable<Document> cursor = getExamenesUsuarios().getCollection(dni).find(whereQuery);

        return cursor.first();
    }

    /**
     * @param id el identificador del examen a recoger
     * @return Devuelve un examen
     */
    public Document getExamen(String id) {

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);

        FindIterable<Document> cursor = getExamenes().find(whereQuery);

        return cursor.first();
    }

    public Document getRespuesta(String id) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);

        FindIterable<Document> cursor = getRespuestas().find(whereQuery);
        return cursor.first();
    }

    public MongoDatabase getDatabase(String db, MongoClient client) {
        return client.getDatabase(db);
    }

    public MongoClient connectToDb() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://admin:admin@examen-yfcq2.mongodb.net/test");
        this.mongoClient = new MongoClient(uri);

        return this.mongoClient;
    }

    public void closeConn() {
        if (this.mongoClient != null) {
            try {
                this.mongoClient.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
