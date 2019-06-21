package com.java1234.c01hello;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class Indexer {
    /**
     * index写入器，用来构建index
     */
    private IndexWriter writer;

    /**
     * 构造方法 实例化IndexWriter
     *
     * @param indexDir 索引目录
     * @throws Exception
     */
    public Indexer(String indexDir) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, iwc);
    }

    /**
     * 关闭index写入器
     *
     * @throws Exception
     */
    public void close() throws Exception {
        writer.close();
    }

    /**
     * 索引指定目录
     *
     * @param dataDir 指定目录
     * @throws Exception
     */
    public int index(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File f : files) {
            indexFile(f);
        }
        return writer.numDocs();
    }

    /**
     * 索引指定文件
     *
     * @param f 指定文件
     */
    private void indexFile(File f) throws Exception {
        System.out.println("索引文件：" + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }

    /**
     * 为指定为文件创建文档对象
     *
     * @param f 指定文件
     */
    private Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));
        doc.add(new TextField("fileName", f.getName(), Field.Store.YES));
        doc.add(new TextField("fullPath", f.getCanonicalPath(), Field.Store.YES));
        return doc;
    }

    /**
     * 建立索引
     *
     * @param args
     */
    public static void main(String[] args) {
        String indexDir = "E:\\luceneTest\\dataIndex";
        String dataDir = "E:\\luceneTest\\data";
        Indexer indexer = null;
        int numIndexed = 0;
        long start = System.currentTimeMillis();
        try {
            indexer = new Indexer(indexDir);
            numIndexed = indexer.index(dataDir);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                indexer.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("建立索引，索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }
}
