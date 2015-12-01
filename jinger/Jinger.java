
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

import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

/**
 * Jinger: Main class for the indexer server needed in the RSSSearchEngine Project
 * Index all text files in a specified directory
 * @author Romain Bressan
 * @author Anas Alaoui M'Darhri
 */
public class Jinger {

    private final static String INDEX = "index";

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
        Document doc = new Document();

        Field pathField = new StringField("path", s, Field.Store.YES);
        doc.add(pathField);

        doc.add(new TextField("content", new BufferedReader(new FileReader(s))));

        System.out.println("Updating " + s);
        iw.updateDocument(new Term("path" + s), doc);
    }
}
