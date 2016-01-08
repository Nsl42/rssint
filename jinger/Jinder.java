import java.io.IOException;
import java.nio.file.Paths;

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

/**
 * Searcher module for the Jinger indexer
 * @author Anas Alaoui M'Darhri
 * @author Romain Bressan
 */
public class Jinder {

    private final static String INDEX = "index";
    private final static int MAX_RESULTS = 100;

    private Jinder() {}

    public static void main(String[] args) throws Exception {
        IndexReader reader = DirectoryReader.open( 
                FSDirectory.open(Paths.get(INDEX)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer anal = new StandardAnalyzer();

        String keyword = "";
        //Getting args
	for (String arg : args)
		keyword += arg + " ";

    //preparing query over content
	QueryParser parser = new QueryParser("content", anal);
        Query query = parser.parse(keyword);
        search(searcher, query);

	anal.close();
        reader.close();
    }

    public static void search(IndexSearcher searcher, Query query) throws IOException {
	    TopDocs results = searcher.search(query, MAX_RESULTS);

	    print(results.totalHits + " matching documents.");
        //Doing the research and printing the result
	    for (ScoreDoc hit : results.scoreDocs) {
		    Document doc = searcher.doc(hit.doc);
		    print(doc.get("path") + " [" + hit.score + "] ->\n\t"
				    + doc.get("title"));
	    }
    }

    /**
     * Display results
     */
    public static void print(String line) {
	    System.out.println(line);
    }
}
