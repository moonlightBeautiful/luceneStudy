package com.ims.c01hello;

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

/**
 * 2.使用索引搜索数据
 */
public class Searcher {

    public static void main(String[] args) {
        String indexDir = "E:\\Lucene\\indexDir";
        String queryContent = "License";
        try {
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            //
            IndexReader indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser queryParser = new QueryParser("contents", analyzer);
            Query query = queryParser.parse(queryContent);
            //
            long start = System.currentTimeMillis();
            TopDocs hits = indexSearcher.search(query, 10); //10，查前10条数据
            long end = System.currentTimeMillis();
            System.out.println("共花费" + (end - start) + "毫秒，" + "查询到了" + hits.totalHits + "个文本文件，包含" + queryContent);
            //
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }
            //
            indexReader.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
