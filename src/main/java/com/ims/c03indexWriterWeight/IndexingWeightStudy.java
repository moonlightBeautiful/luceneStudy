package com.ims.c03indexWriterWeight;

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
import java.io.IOException;
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

    private IndexWriter indexWriter;

    /**
     * 初始化并打开索引写入器：索引目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引目录
     * @return
     * @throws Exception
     */
    private IndexWriter getIndexWriter(String indexDir) throws Exception {
        //先删除掉目录中的文件
        File file = new File(indexDir);
        if (file.exists()) {
            for (File fileTemp : file.listFiles()) {
                fileTemp.delete();
            }
        }
        // 索引所在目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器，对文本内容进行分词。比如 英文 is a 空格 去掉
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        // 初始化索引写入器：如果目录不存在，会自动创建
        indexWriter = new IndexWriter(dir, iwc);
        return indexWriter;
    }

    /**
     * 关闭索引写入器：就像是流一样，需要关闭
     *
     * @param indexWriter 索引写入器
     * @throws Exception
     */
    public void closeIndexWriter(IndexWriter indexWriter) throws Exception {
        indexWriter.close();
    }

    /**
     * 为索引数据创建索引：在索引目录中会创建fdt和fdx文件
     *
     * @param indexWriter 索引写入器
     * @throws IOException
     */
    public void createIndex(IndexWriter indexWriter) throws IOException {
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
            doc.add(field);
            // 对某个文档的某个域加权操作 ↑
            doc.add(new TextField("content", contents[i], Field.Store.NO));
            indexWriter.addDocument(doc);
        }
    }


    public static void main(String[] args) {
        IndexingWeightStudy iws = new IndexingWeightStudy();
        String indexDir = "F:\\luceneTest\\indexDir";
        try {
            /*IndexWriter indexWriter = iws.getIndexWriter(indexDir);
            iws.createIndex(indexWriter);
            iws.closeIndexWriter(indexWriter);*/

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
