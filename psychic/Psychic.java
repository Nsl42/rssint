import java.util.ArrayList;  
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;
import java.io.FileNotFoundException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Psychic {

    
    private final String INDEX = "index";
    public IndexReader reader;
    public int k;

    private Psychic() {}

    public Psychic(int k) throws IOException {
        super();
        this.k = k;
       this.reader  = DirectoryReader.open( 
                FSDirectory.open(Paths.get(INDEX)));

    }

    public void run(String id) throws ParseException, IOException{
        Analyzer anal = new StandardAnalyzer();
        QueryParser parser = new QueryParser("path", anal);
        Query q = parser.parse(id);
        IndexSearcher s = new IndexSearcher(reader);
       TopDocs res =  s.search(q, 1);
       ScoreDoc sd = null;
       for(ScoreDoc hit : res.scoreDocs)
           sd = hit;
       
       if(sd == null)
           throw new FileNotFoundException();

        vote(sd);
    }

    private Query getQuery(ScoreDoc sd) throws IOException {
        MoreLikeThis mlt = new MoreLikeThis(reader); // Pass the index reader
        mlt.setFieldNames(new String[] {"title", "content"}); // specify the fields for similiarity

        Query query = mlt.like(sd.doc); // Pass the doc id 
        return query;
}
    private int vote(ScoreDoc sd) throws ParseException, IOException {
        Query q = this.getQuery(sd);
        ArrayList<ScoreDoc> scores;
        IndexSearcher s = new IndexSearcher(reader);
        scores = search(s, q);
        for(ScoreDoc sd2 : scores)
            System.out.println("SD : "+ sd2.score);
        
        //TODO Return
        return 0;
    }

    public ArrayList<ScoreDoc> search(IndexSearcher searcher, Query query) throws IOException {
	    TopDocs results = searcher.search(query, 2*k);
        ArrayList<ScoreDoc> res = new ArrayList<>();
	    for (ScoreDoc hit : results.scoreDocs) {
		    Document doc = searcher.doc(hit.doc);
            res.add(hit);
	    }
        return res;
    }


}
