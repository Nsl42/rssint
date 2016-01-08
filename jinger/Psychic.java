import java.util.ArrayList;  
import java.util.Hashtable;  
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;

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

/**
 * @author Anas Alaoui M'Darhri
 * @author Romain Bressan
 */
public class Psychic {

    
    private final String INDEX = "index";
    public IndexReader reader;
    public int k;
    private Analyzer anal ;

    private Psychic() {}

    public Psychic(int k) throws IOException {
        super();
        this.k = k;
       this.reader  = DirectoryReader.open( 
                FSDirectory.open(Paths.get(INDEX)));

       this.anal =  new StandardAnalyzer();
    }

    /** 
     * runs the KNN algorithm with the given document as a parameter
     * returns the Category predicted by the algorithm, __NULL__ if something went wrong
     */
    public String run(Document d) throws ParseException, IOException{

        MoreLikeThis mlt = new MoreLikeThis(reader);

        //Getting the query and settings things up
        String titleandcontent = d.get("title") + " " + d.get("content");
        Reader r = new StringReader(titleandcontent);
        mlt.setFieldNames(new String[]{"title", "content"});
        mlt.setAnalyzer(anal);

        Query query = mlt.like("content", r);
        return vote(d, query);
    }

    /**
     * chooses which category to choose by running the KNN
     * returns the Category predicted by the algorithm, __NULL__ if something went wrong
     */
    private String vote(Document d, Query q) throws ParseException, IOException {
        ArrayList<ScoreDoc> scores;
        IndexSearcher s = new IndexSearcher(reader);
        String catmax = "__NULL__";
        try{
            //Get the Nearest neighbors
        scores = search(s, q);
        int i = 0;
        for(ScoreDoc sd : scores)
        {
            i++;
        }
        Hashtable<String, Float> KNNs = new Hashtable<String, Float>();
        //Among the k-NN, Sum of the scores by category in the HashTable
        for(i=0; i<k; i++) {
                KNNs.put(s.doc(scores.get(i).doc).get("category"),Float.valueOf("0"));
        }
        for(i=0; i<k; i++) {
            KNNs.put(s.doc(scores.get(i).doc).get("category"),
                    KNNs.get(s.doc(scores.get(i).doc).get("category"))+scores.get(i).score);
        }
            Float max = Float.valueOf(0);
            //Getting the first category which isn't __NULL__
        for(String cate : KNNs.keySet())
        {
            if(!cate.equals("__NULL__") && max < KNNs.get(cate))
            {
                catmax = cate;
                max = KNNs.get(cate);
            }
            System.out.println("Cate : "+cate+"         SCORE : "+KNNs.get(cate));
        }
        }catch(IndexOutOfBoundsException e){}
        return catmax;
    }

    /**
     * Makes the search and returns an ArrayList containing the k+7 nearest Neighbors
     */
    public ArrayList<ScoreDoc> search(IndexSearcher searcher, Query query) throws IOException {
	    TopDocs results = searcher.search(query, k+7);
        ArrayList<ScoreDoc> res = new ArrayList<>();
	    for (ScoreDoc hit : results.scoreDocs) 
            res.add(hit);
        return res;
    }


}
