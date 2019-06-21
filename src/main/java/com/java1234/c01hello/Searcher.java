package com.java1234.c01hello;

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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class Searcher {

    /**
     * 在建立的索引上搜索
     *
     * @param args
     */
    public static void main(String[] args) {
        String indexDir = "E:\\luceneTest\\dataIndex";
        String q = "License";
        try {
            Directory dir = FSDirectory.open(Paths.get(indexDir));

            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher is = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("contents", analyzer);
            Query query = parser.parse(q);
            long start = System.currentTimeMillis();
            TopDocs hits = is.search(query, 10);
            long end = System.currentTimeMillis();
            System.out.println("匹配 " + q + " ，总共花费" + (end - start) + "毫秒" + "查询到了" + hits.totalHits + "个记录");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = is.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }
            reader.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
