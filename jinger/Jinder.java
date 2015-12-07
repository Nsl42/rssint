import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Jinder{

    private final static String INDEX = "index";
    private final static int MAX_RESULTS = 100;

    private Jinder() {}

    public static void main(String[] args) throws Exception {
        IndexReader reader = DirectoryReader.open( 
                FSDirectory.open(Paths.get(INDEX)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer anal = new StandardAnalyzer();

        String keyword = args[0];
        String[] keywor = fieldSelection(args[0]);
        QueryParser parser;
        if (keywor.length == 2) {
            keyword = keywor[1];
            parser = new QueryParser(keywor[0], anal);
        } else {
            parser = new QueryParser("content", anal);
	}

        Query  query = parser.parse(keyword);
        searcher.search(query, 100);

        search(searcher, query);

        reader.close();
    }
    
    public static String[] fieldSelection(String arg) {
        return arg.split(":");
    }

    public static void search(IndexSearcher searcher, Query query) throws IOException {
	    TopDocs results = searcher.search(query, MAX_RESULTS);

	    print(results.totalHits + " matching documents.");
	    for (ScoreDoc hit : results.scoreDocs) {
		    Document doc = searcher.doc(hit.doc);
		    print(doc.get("path") + " [" + hit.score + "] ->\n\t"
				    + doc.get("title"));
	    }
    }

    public static void print(String line) {
	    System.out.println(line);
    }
}
