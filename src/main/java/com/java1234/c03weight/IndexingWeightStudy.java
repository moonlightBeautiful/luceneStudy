package com.java1234.c03weight;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Paths;

public class IndexingWeightStudy {

    /**
     * 测试数据
     */
    private String ids[] = {"1", "2", "3", "4"};
    private String authors[] = {"Jack", "Marry", "John", "Json"};
    private String positions[] = {"accounting", "technician", "salesperson", "boss"};
    private String titles[] = {"Java is a good language.", "Java is a cross platform language", "Java powerful", "You should learn java"};
    private String contents[] = {
            "If possible, use the same JRE major version at both index and search time.",
            "When upgrading to a different JRE major version, consider re-indexing. ",
            "Different JRE major versions may implement different versions of Unicode,",
            "For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
    };

    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private IndexWriter getIndexWriter(String indexDir) throws Exception {
        File file = new File(indexDir);
        if (file.exists()) {
            for (File fileTemp : file.listFiles()) {
                fileTemp.delete();
            }
        }
        // 索引所在目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, iwc);
        return indexWriter;
    }

    /**
     * 生成索引
     *
     * @throws Exception
     */
    public void createIndex(IndexWriter writer) throws Exception {
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("author", authors[i], Field.Store.YES));
            doc.add(new StringField("position", positions[i], Field.Store.YES));
            // 对某个文档的某个域加权操作 ↓
            TextField field = new TextField("title", titles[i], Field.Store.YES);
            if ("boss".equals(positions[i])) {
                field.setBoost(1.5f);
            }
            // 对某个文档的某个域加权操作 ↑
            doc.add(field);
            doc.add(new TextField("content", contents[i], Field.Store.NO));
            writer.addDocument(doc);
        }
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

    public static void main(String[] args) {
        IndexingWeightStudy iws = new IndexingWeightStudy();
        String indexDir = "E:\\luceneTest\\indexDir";
        IndexWriter indexWriter = null;
        try {
            indexWriter = iws.getIndexWriter(indexDir);
            iws.createIndex(indexWriter);
            iws.closeIndexWriter(indexWriter);
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            IndexReader indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            String searchField = "title";
            String searchContent = "java";
            Term term = new Term(searchField, searchContent);
            Query query = new TermQuery(term);
            TopDocs hits = indexSearcher.search(query, 10);
            System.out.println("在字段‘" + searchField + "’查询内容 '" + searchContent + "'，查询到了" + hits.totalHits + "文档");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                System.out.println(doc.get("author"));
            }
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
