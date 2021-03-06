import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.mail.Header;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;


import java.util.Enumeration;
import java.util.Properties ; 

/**
 * Jinger: Main class for the indexer server needed in the RSSSearchEngine Project
 * Index all text files in a specified directory
 * @author Romain Bressan
 * @author Anas Alaoui M'Darhri
 */
public class Jinger {

    private final static String INDEX = "index";
    private static boolean psychic_auto = true;

    /**
     * Empty private constructor so the class doesn't have a default public constructor
     */
    private Jinger() {}

    public static void main(String[] args) {
        IndexWriter iw = null;
        try {
            Directory dir = FSDirectory.open(Paths.get(Jinger.INDEX)); 
            Analyzer anal = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(anal);
            iw = new IndexWriter(dir, iwc);

            for (String s : args)  
                Jinger.indexDoc(iw, s);
            iw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void indexDoc(IndexWriter iw, String s) throws IOException {

        MimeMessage message = null;
        Document doc = new Document();
        boolean to_analyze = false;
        try{
            //Getting the Fields of the temporary file
            byte[] encoded = Files.readAllBytes(Paths.get(s));
            String content = new String(encoded, Charset.forName("UTF-8"));
            Session sess = Session.getDefaultInstance(new Properties());
            InputStream is = new ByteArrayInputStream(content.getBytes());
            message = new MimeMessage(sess, is);
            message.getAllHeaderLines();

            //Creating the fields for the lucene indexing
            Field pathField = new StringField("path", s, Field.Store.YES);
            doc.add(pathField);

            if(message != null) {
                Field titleField = new TextField("title", message.getHeader("Title")[0], Field.Store.YES);
                doc.add(titleField);
                Field catField = null;
                // Parsing Category and setting Psychic for later use
                if(message.getHeader("Category")[0].length() == 0) {
                    catField =  new TextField("category", "__NULL__", Field.Store.YES);
                    to_analyze = true;
                } else{
                    catField = new TextField("category", message.getHeader("Category")[0], Field.Store.YES);
                }
                doc.add(catField);
                try{
                    Field linkField = new TextField("link", message.getHeader("Link")[0], Field.Store.YES);
                    doc.add(linkField);
                }catch(NullPointerException e){}
                Field feedField = new TextField("feed", message.getHeader("Feed")[0], Field.Store.YES);
                doc.add(feedField);
            }

        } catch(Exception e) { e.printStackTrace(); }

        doc.add(new TextField("content", new BufferedReader(new FileReader(s))));

        // Using Psychic to determine category if it's null
        if(psychic_auto || to_analyze) {
            try{
                Psychic p = new Psychic(5);
                Field catField =  new TextField("category_pred", p.run(doc), Field.Store.YES);
                doc.add(catField);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        // Printing 
        String c = (doc.get("category").equals("__NULL__") ? doc.get("category_pred") : doc.get("category"));
        System.out.println(doc.get("path") + " Indexed !");
        System.out.println(doc.get("title")+"        " + c);

        //Updating Document
        iw.updateDocument(new Term("path", s), doc);
    }
}
