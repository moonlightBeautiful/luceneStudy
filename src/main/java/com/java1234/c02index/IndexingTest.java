package com.java1234.c02index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class IndexingTest {
    private String ids[] = {"1", "2", "3"};
    private String citys[] = {"qingdao", "nanjing", "shanghai"};
    private String descs[] = {
            "Qingdao is a beautiful city.",
            "Nanjing is a city of culture.",
            "Shanghai is a bustling city."
    };

    /**
     * 获取索引读写入取器
     *
     * @param strDir
     * @return
     * @throws Exception
     */

    private IndexWriter getIndexWriter(String strDir) throws Exception {
        System.out.println("获取索引写入器================================");
        Directory dir = FSDirectory.open(Paths.get(strDir));
        Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, iwc);
        return indexWriter;
    }

    /**
     * 获取索引读取器
     *
     * @param strDir
     * @return
     * @throws Exception
     */
    private void indexReader(String strDir) throws Exception {
        System.out.println("获取索引读取器================================");
        Directory dir = FSDirectory.open(Paths.get(strDir));
        IndexReader indexReader = DirectoryReader.open(dir);
        System.out.println("索引读取器，读取到最大文档数：" + indexReader.maxDoc());
        System.out.println("索引读取器，读取到实际文档数：" + indexReader.numDocs());
        indexReader.close();
    }

    /**
     * 使用索引写入器添加文档
     *
     * @param strDir
     * @return
     * @throws Exception
     */
    public void addDocument(String strDir) throws Exception {
        IndexWriter indexWriter = getIndexWriter(strDir);
        System.out.println("添加文档开始==================================");
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.NO));
            indexWriter.addDocument(doc); // 添加文档
        }
        System.out.println("添加文档结束==================================");
        indexWriter.close();
    }

    /**
     * 使用索引写入器删除文档，不合并
     * note:可以重复删除，仅仅是重复做了标记
     *
     * @param strDir
     * @return
     * @throws Exception
     */
    public void deleteWithoutMerge(String strDir) throws Exception {
        IndexWriter indexWriter = this.getIndexWriter(strDir);
        System.out.println("删除文档开始==================================");
        System.out.println("不合并删除前，最大文档数：" + indexWriter.maxDoc());
        System.out.println("不合并删除前，实际文档数：" + indexWriter.numDocs());
        indexWriter.deleteDocuments(new Term("id", "1"));
        indexWriter.commit();
        System.out.println("不合并删除后，最大文档数：" + indexWriter.maxDoc());
        System.out.println("不合并删除后，实际文档数：" + indexWriter.numDocs());
        System.out.println("删除文档结束==================================");
        indexWriter.close();
    }

    /**
     * 使用索引写入器删除文档，合并
     * note:可以重复删除，仅仅是重复做了标记
     *
     * @param strDir
     * @return
     * @throws Exception
     */
    public void deleteWithMerge(String strDir) throws Exception {
        IndexWriter indexWriter = this.getIndexWriter(strDir);
        System.out.println("删除文档开始==================================");
        System.out.println("合并删除前，最大文档数：" + indexWriter.maxDoc());
        System.out.println("合并删除前，实际文档数：" + indexWriter.numDocs());
        indexWriter.deleteDocuments(new Term("id", "1"));
        indexWriter.forceMergeDeletes(); // 强制删除
        indexWriter.commit();
        System.out.println("合并删除后，最大文档数：" + indexWriter.maxDoc());
        System.out.println("合并删除后，实际文档数：" + indexWriter.numDocs());
        System.out.println("删除文档结束==================================");
        indexWriter.close();
    }

    /**
     * 使用索引写入器更新，如果有则更新，如果没有则创建
     * note:更新后，分词器记录的不会被删除，用工具查看，原来的词还在
     *
     * @param strDir
     * @throws Exception
     * @returnu
     */
    public void updateIndex(String strDir) throws Exception {
        IndexWriter indexWriter = this.getIndexWriter(strDir);
        System.out.println("更新文档开始==================================");
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("city", "qingdao", Field.Store.YES));
        doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
        indexWriter.updateDocument(new Term("id", "1"), doc);
        System.out.println("更新文档结束==================================");
        indexWriter.close();
    }

    public static void main(String[] args) {
        try {
//            new IndexingTest().getIndexWriter("D:\\lucene2");
//            new IndexingTest().indexReader("D:\\lucene2");
//            new IndexingTest().addDocument("D:\\lucene2");
//            new IndexingTest().indexReader("D:\\lucene2");
//            new IndexingTest().deleteBeforeMerge("D:\\lucene2");
//            new IndexingTest().indexReader("D:\\lucene2");
//            new IndexingTest().deleteWithMerge("D:\\lucene2");
//            new IndexingTest().indexReader("D:\\lucene2");
            new IndexingTest().updateIndex("D:\\lucene2");
            new IndexingTest().indexReader("D:\\lucene2");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
