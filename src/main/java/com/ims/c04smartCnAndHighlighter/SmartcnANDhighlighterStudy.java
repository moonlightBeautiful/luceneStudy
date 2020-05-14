package com.ims.c04smartCnAndHighlighter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

/**
 * @author gaoxu
 * @company 北山小旭网络科技有限公司
 * @create 2019-06-30-9:33 AM
 */
public class SmartcnANDhighlighterStudy {

    public static void main(String[] args) {
        String indexDir = "F:\\lucene4\\index";


        try {
            //中文搜索，查询内容要使用中文分词器===================================================================================================
            /*Directory dir = FSDirectory.open(Paths.get(indexDir));
            IndexReader indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); //查询内容要使用中文分词器
            QueryParser parser = new QueryParser("desc", analyzer);
            String queryContent = "南京文化";
            Query query = parser.parse(queryContent);
            long start = System.currentTimeMillis();
            TopDocs hits = indexSearcher.search(query, 10);
            long end = System.currentTimeMillis();
            System.out.println("匹配 " + queryContent + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("city"));
                System.out.println(doc.get("desc"));
            }
            indexReader.close();*/
            //高亮显示，对查询到的内容使用html格式化==================================================================================================
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            IndexReader indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); //查询内容要使用中文分词器
            QueryParser parser = new QueryParser("desc", analyzer);
            String queryContent = "南京文化";
            Query query = parser.parse(queryContent);
            long start = System.currentTimeMillis();
            TopDocs hits = indexSearcher.search(query, 10);
            long end = System.currentTimeMillis();
            System.out.println("匹配 " + queryContent + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
            //对查询到的结果进行格式化
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>"); //默认粗体
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            highlighter.setTextFragmenter(fragmenter);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("city"));
                System.out.println(doc.get("desc"));
                String desc = doc.get("desc");
                if (desc != null) {
                    TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
                    String descHighlighter = highlighter.getBestFragment(tokenStream, desc);
                    System.out.println(descHighlighter);
                }
            }
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
