package com.ims.c03indexSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 1.建立索引
 */
public class Indexer {

    /**
     * 初始化并打开索引写入器：索引目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引目录
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(String indexDir) throws Exception {
        // 索引目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器，对文本内容进行分词。比如 英文 is a 空格 去掉
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        // 初始化索引写入器：如果目录不存在，会自动创建
        IndexWriter indexWriter = new IndexWriter(dir, iwc);
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
     * @param dataDir     索引数据目录
     * @throws Exception
     */
    public void createIndex(IndexWriter indexWriter, String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        int i = 1;
        for (File file : files) {
            System.out.println("索引文件：" + file.getCanonicalPath());
            Document doc = new Document();
            //Field.Store.YES 把文件名存储到索引文件中
            doc.add(new IntField("id", i++, Field.Store.YES));
            doc.add(new TextField("fileName", file.getName(), Field.Store.YES));
            doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES));
            doc.add(new TextField("contents", new FileReader(file)));
            indexWriter.addDocument(doc);
        }
    }


    /**
     * 索引测试：初始化索引写入器(指定目录和分词器)、构建索引（索引写入器添加文档(文档有字段和值组成)）、关闭索引写入器
     *
     * @param args
     */
    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        try {
            String indexDir = "F:\\lucene4\\index";
            String dataDir = "F:\\lucene4\\data";
            IndexWriter indexWriter = indexer.getIndexWriter(indexDir);
            long start = System.currentTimeMillis();
            indexer.createIndex(indexWriter, dataDir);
            long end = System.currentTimeMillis();
            System.out.println("索引了" + indexWriter.numDocs() + " 个文件，花费了" + (end - start) + " 毫秒");
            indexer.closeIndexWriter(indexWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}