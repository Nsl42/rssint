import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Psychic {

    public static IndexReader reader = DirectoryReader.open( 
                FSDirectory.open(Paths.get(INDEX)));
    public static int k;
    public static ArrayList<Item> is;

    private Psychic() {}

    
    public static void main(String[] args) {

        Analyzer anal = new StandardAnalyzer();
        QueryParser parser = new QueryParser("path", anal);
        Query q = parser.parse(args[0]);
        IndexSearcher s = new IndexSearcher(reader);
       TopDocs res =  s.search(q, 1);
       Document d = null;
       for(ScoreDoc hit : res.scoreDocs)
           d = hit.doc;
        
       if(d != null)
        vote(d, args[1]);
    }

    private Query getQuery(Document d) {
        MoreLikeThis mlt = new MoreLikeThis(reader); // Pass the index reader
        mlt.setFieldNames(new String[] {"title", "content"}); // specify the fields for similiarity

        Query query = mlt.like(d); // Pass the doc id 
        return query;
}
    private static int vote(Document d, int k) {
        Query q = Psychic.getQuery(d);
            ArrayList<ScoreDoc> scores;
            IndexSearcher s = new IndexSearcher(reader);
            scores = search(s, q);
            for(ScoreDoc sd : scores)
                System.out.println("SD : "+ sd.score);
            //K nearest
    }

    public static ArrayList<ScoreDoc> search(IndexSearcher searcher, Query query) throws IOException {
	    TopDocs results = searcher.search(query, 2*k);
        ArrayList<ScoreDoc> res = new ArrayList<>();
	    for (ScoreDoc hit : results.scoreDocs) {
		    Document doc = searcher.doc(hit.doc);
            res.add(hit);
	    }
        res.sort();
        return res;
    }


}
