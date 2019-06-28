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
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer {

    /**
     * 为索引写入器指定目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引所在的目录
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(String indexDir) throws IOException {
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
        for (File f : files) {
            System.out.println("索引文件：" + f.getCanonicalPath());
            Document doc = new Document();
            doc.add(new TextField("fileName", f.getName(), Field.Store.YES));
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
        String dataDir = "E:\\luceneTest\\dataDir";
        IndexWriter indexWriter = null;
        Indexer indexer = new Indexer();
        try {
            indexWriter = indexer.getIndexWriter(indexDir);
            long start = System.currentTimeMillis();
            indexer.createIndex(indexWriter, dataDir);
            int numIndexed = indexWriter.numDocs();
            indexer.closeIndexWriter(indexWriter);
            long end = System.currentTimeMillis();
            System.out.println("索引了：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}