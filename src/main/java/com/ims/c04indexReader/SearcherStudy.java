package com.ims.c04indexReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
    /**
     * 为索引写入器指定目录和分词器,会在索引目录中创建文件write.lock
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
        Analyzer analyzer = new StandardAnalyzer();
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
     * @param dataDir 数据目录
     * @param dataDir 数据目录
     * @throws Exception
     */
    public void createIndex(IndexWriter writer, String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        int i = 0;
        for (File f : files) {
            System.out.println("索引文件：" + f.getCanonicalPath());
            Document doc = new Document();
            doc.add(new IntField("id", ++i, Field.Store.YES));
            doc.add(new StringField("fileName", f.getName(), Field.Store.YES));
            doc.add(new TextField("fullPath", f.getCanonicalPath(), Field.Store.YES));
            doc.add(new TextField("contents", new FileReader(f)));
            writer.addDocument(doc);
        }
    }

    /**
     * 索引测试：初始化索引写入器(指定目录和分词器)、构建索引（索引写入器添加文档(文档有字段和值组成)）、关闭索引写入器
     *
     * @param args
     */
    public static void main(String[] args) {
        String indexDir = "E:\\luceneTest\\indexDir";
        String dataDir = "E:\\luceneTest\\data";
        IndexWriter indexWriter = null;
        IndexReader indexReader = null;
        SearcherStudy searcherStudy = new SearcherStudy();
        try {
            //创建索引=================================================
            indexWriter = searcherStudy.getIndexWriter(indexDir);
            long start = System.currentTimeMillis();
            searcherStudy.createIndex(indexWriter, dataDir);
            int numIndexed = indexWriter.numDocs();
            searcherStudy.closeIndexWriter(indexWriter);
            long end = System.currentTimeMillis();
            System.out.println("索引了：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");

            //创建索引读取器=================================================
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            /*//对特定项进行简单匹配搜索===========================================
            String searchField = "contents";
            String searchContent = "particular";
            Term term = new Term(searchField, searchContent);
            Query query = new TermQuery(term);
            TopDocs hits = indexSearcher.search(query, 10);
            System.out.println("匹配 '" + searchContent + "'，总共查询到" + hits.totalHits + "个文档");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }*/

            //对特定域进行简单匹配搜索===========================================
            /*Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
            String searchField = "contents";
            String searchContent = "a*";  //与(AND) 或(OR或者空格） 非 相近查询(~[0-1相近度]) * ?
            QueryParser queryParser = new QueryParser(searchField, analyzer); // 表达式解析器
            Query query = queryParser.parse(searchContent);
            TopDocs hits = indexSearcher.search(query, 100);
            System.out.println("匹配 " + searchContent + "查询到" + hits.totalHits + "个记录");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("fullPath"));
            }*/

            //对特定项域字符串范围搜索=========================================== 没看懂，用到问下高手
           /* TermRangeQuery query = new TermRangeQuery("fileName", new BytesRef("b".getBytes()), new BytesRef("c".getBytes()), true, true);
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fullPath"));
            }*/

            //对特定域数字范围搜索===========================================
            /*NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fileName"));
                System.out.println(doc.get("fullPath"));
            }*/

            //对特定域指定字符串开头搜索===========================================
            /*PrefixQuery query = new PrefixQuery(new Term("fileName", "/"));
            TopDocs hits = indexSearcher.search(query, 10);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id"));
                System.out.println(doc.get("fileName"));
                System.out.println(doc.get("fullPath"));
            }*/

            //混合条件搜索===========================================
            NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
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