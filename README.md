# rssint Project
    ### AUTHORS : 
* Anas ALAOUI M'DARHRI <nslmdrhr@gmail.com>
* Romain BRESSAN <romain@brss.me>

# Objective

Create a watch system on Internet based on RSS feed, with the following components:
	* RSS parser
	* collector
	* indexer
	* searcher
	* classifier

# Usage
    * ./autocrawl: Executes the crawler (./phelps/phelps.py), and gives the
      sample files given in the samples folder :  categ2.rss having
      categories, and not tinyss.rss ;
    * ./phelps/phelps.py : Crawler, must have a parameter, a file containing
      rss links, as ./phelps/samples/bigrss.rss
    * ./make : Compilation (javac) of all the java modules Jinger/Jinder/Psychic 
            !!! To run before anything else!!!

    * ./index : Indexes automatically the files located in /tmp/rssint,
    the temporary storage directory of the crawler
    * ./search : Executes the request engine, jinger. Must be followed by a
      set of keywords to build the query

# Phelps: collector

Read links to RSS feed from files given by argument. For each item
get the whole article:
	* Query the link to the article
	* Remove all the boilerplate using readability to get only the article
	* Remove HTML tags using html2tew
	* Store the article in a file with a unique id (hash)

# Jinger: indexer

Read files given by argument, i.e. the new files stored by the collector.
Index those files using Lucene. And for each item predict his category,
by calling Psychic class.

# Jinder: searcher

Query Lucene's index with the keywords given by argument.
Support all Lucene syntax, see https://lucene.apache.org/core/3_6_0/queryparsersyntax.html

Print result with the following information:
	* path to the file stored by the crawler
	* title of the article
	* score given by lucene in square bracket ([])

# Psychic: classifier

Java class called by Jinger to predict the category of the document, using the document
already classified. Use KNN algorithm with Lucene's MoreLikeThis class.



# Meta-Data stocked by Lucene dictionnary :

* **Title**: _title of the item_
* **Category**: _category of the item (only for test datasets)
* **Category_pred**: _category predicted by psychic
* **Date**: _last publication/update date_
* **Dateproc**: _processing date (timestamp)_
* **Description**: _short description of the item_
* **Content**: _full content boilerplated and untagged_
* **Link**: _link to the item on publisher's website_
* **Feed**: _link to the feed_
