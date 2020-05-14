package com.ims.c03indexSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author gaoxu
 * @company 北山小旭网络科技有限公司
 * @create 2019-06-29-5:56 PM
 */
public class SearcherStudy {

    public static void main(String[] args) {
        SearcherStudy searcherStudy = new SearcherStudy();
        String indexDir = "F:\\lucene4\\index";
        IndexReader indexReader = null;
        try {
            //创建索引读取器=================================================
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            //对特定项（Field）进行简单搜索===========================================
           /*String searchField = "contents";
            String searchContent = "particular";
            Term term = new Term(searchField, searchContent);
            Query query = new TermQuery(term);
            TopDocs hits = indexSearcher.search(query, 10);
            System.out.println("匹配 '" + searchContent + "'，总共查询到" + hits.totalHits + "个文档");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/

            //对特定项（Field）简单表达式匹配搜索===========================================
            /*Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
            String searchField = "contents";
            QueryParser queryParser = new QueryParser(searchField, analyzer); // 表达式解析器
            String searchContent = "particula~";  //与(AND) 或(OR或者空格） 非(!) 相近查询(~[0-1相近度]) * ?
            Query query = queryParser.parse(searchContent);
            TopDocs hits = indexSearcher.search(query, 100);
            System.out.println("匹配 " + searchContent + "查询到" + hits.totalHits + "个记录");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/

            //对特定项（Field）字符串范围搜索===========================================
           /* TermRangeQuery query = new TermRangeQuery("fileName", new BytesRef("C".getBytes()), new BytesRef("S".getBytes()), true, true);
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/

            //对特定项（Field）数字范围搜索===========================================
           /* NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fileName"));
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/

            //对特定项（Field）指定字符串开头搜索===========================================
           /* PrefixQuery query = new PrefixQuery(new Term("fileName", "JRE"));
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fileName"));
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/

            //混合条件搜索===========================================
           /* NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
            PrefixQuery query2 = new PrefixQuery(new Term("fileName", "JRE"));
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(query1, BooleanClause.Occur.MUST); // MUST 且 SHOULD 或 MUST_NOT不包含
            booleanQuery.add(query2, BooleanClause.Occur.MUST);
            TopDocs hits = indexSearcher.search(booleanQuery.build(), 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fileName"));
                System.out.println(doc.get("fullPath"));
            }
            indexReader.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                indexReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}