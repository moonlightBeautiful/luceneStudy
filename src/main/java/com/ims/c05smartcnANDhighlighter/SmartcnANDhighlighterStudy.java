package com.ims.c05smartcnANDhighlighter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

/**
 * @author gaoxu
 * @company 北山小旭网络科技有限公司
 * @create 2019-06-30-9:33 AM
 */
public class SmartcnANDhighlighterStudy {

    /**
     * 得到索引写入器：为索引写入器指定目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引所在的目录
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(String indexDir) throws IOException {
        //先删除掉目录中的文件
        File file = new File(indexDir);
        if (file.exists()) {
            for (File fileTemp : file.listFiles()) {
                fileTemp.delete();
            }
        }
        // 目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器
        //Analyzer analyzer = new StandardAnalyzer();
        //面对中文，使用中文分词器
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        // 初始化索引写入器：如果目录不存在，则自动穿件
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * 关闭索引写入器
     *
     * @param writer
     * @throws Exception
     */
    public void closeIndexWriter(IndexWriter writer) throws Exception {
        writer.close();
    }

    /**
     * 对指定目录创建索引：在索引目录中创建fdt和fdx文件
     *
     * @throws Exception
     */
    public void createIndex(IndexWriter writer) throws Exception {
        Integer ids[] = {1, 2, 3};
        String citys[] = {"青岛", "南京", "上海"};
        String descs[] = {
                "青岛市一个美丽的城市。",
                "南京市一个有文化的城市。",
                "上海是一个繁华的城市。"
        };
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new IntField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.YES));
            writer.addDocument(doc); // 添加文档
        }
    }

    public static void main(String[] args) {
        String indexDir = "E:\\luceneTest\\indexDir";
        IndexWriter indexWriter = null;
        IndexReader indexReader = null;
        IndexSearcher indexSearcher = null;
        Directory dir = null;
        String queryContent = "南京文化";
        SmartcnANDhighlighterStudy sahs = new SmartcnANDhighlighterStudy();
        try {
            //创建索引===================================================================================================
            /*indexWriter = sahs.getIndexWriter(indexDir);
            long start = System.currentTimeMillis();
            sahs.createIndex(indexWriter);
            int numIndexed = indexWriter.numDocs();
            sahs.closeIndexWriter(indexWriter);
            long end = System.currentTimeMillis();
            System.out.println("索引了：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");*/
            //中文搜索，查询内容要使用中文分词器===================================================================================================
           /* dir = FSDirectory.open(Paths.get(indexDir));
            indexReader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(indexReader);
            // Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); //查询内容要使用中文分词器
            QueryParser parser = new QueryParser("desc", analyzer);
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
            dir = FSDirectory.open(Paths.get(indexDir));
            indexReader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(indexReader);
            // Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); //查询内容要使用中文分词器
            QueryParser parser = new QueryParser("desc", analyzer);
            Query query = parser.parse(queryContent);
            long start = System.currentTimeMillis();
            TopDocs hits = indexSearcher.search(query, 10);
            long end = System.currentTimeMillis();
            System.out.println("匹配 " + queryContent + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
            //对查询到的结果进行格式化
            QueryScorer scorer = new QueryScorer(query);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
