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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DocOperationStudy {

    /**
     * 为索引写入器指定目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引所在的目录
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(String indexDir) throws IOException {
        //先删除掉目录中的文件
    /*    File file = new File(indexDir);
        if (file.exists()) {
            for (File fileTemp : file.listFiles()) {
                fileTemp.delete();
            }
        }*/
        // 索引所在目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器：is a 空格 等等去掉了
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        // 初始化索引写入器：如果目录不存在，则自动穿件
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * 生成索引
     *
     * @throws Exception
     */
    public void createIndex(IndexWriter writer) throws Exception {
        String ids[] = {"1", "2", "3"};
        String cities[] = {"qingdao", "nanjing", "shanghai"};
        String descs[] = {
                "Qingdao is a beautiful city.",
                "Nanjing is a city of culture.",
                "Shanghai is a bustling city."
        };
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));    //存储能提高效率，用空间换时间，能在索引目录中用具查看到
            doc.add(new StringField("city", cities[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.NO));   //索引目录中不存储，查看不到
            writer.addDocument(doc); // ����ĵ�
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
        try {
            // 索引所在目录
            String indexDirStr = "E:\\luceneTest\\indexDir";
            DocOperationStudy dos = new DocOperationStudy();
            IndexWriter indexWriter = dos.getIndexWriter(indexDirStr);
            /*//文档操作：增加==============================================================
            dos.createIndex(indexWriter);
            //返回写入了几个文档
            System.out.println("写入了:" + indexWriter.numDocs() + "个文档");*/


            /*//文档操作：删除不合并=========================================================
            System.out.println("删除不合并之前最大文档数：" + indexWriter.numDocs());
            System.out.println("删除不合并之前实际文档数：" + indexWriter.numDocs());
            indexWriter.deleteDocuments(new Term("id", "1"));
            indexWriter.commit();
            System.out.println("删除不合并之后最大文档数：" + indexWriter.maxDoc());
            System.out.println("删除不合并之后实际文档数：" + indexWriter.numDocs());
            dos.closeIndexWriter(indexWriter);*/


            /*//文档操作：删除后合并=========================================================
            System.out.println("删除后合并之前最大文档数：" + indexWriter.numDocs());
            System.out.println("删除后合并之前实际文档数：" + indexWriter.numDocs());
            indexWriter.deleteDocuments(new Term("id", "1"));
            indexWriter.forceMergeDeletes();    //删除后合并
            indexWriter.commit();
            System.out.println("删除后合并之后最大文档数：" + indexWriter.maxDoc());
            System.out.println("删除后合并之后实际文档数：" + indexWriter.numDocs());
            dos.closeIndexWriter(indexWriter);*/

            //文档操作：更新文档=========================================================
            Document doc = new Document();
            doc.add(new StringField("id", "1", Field.Store.YES));
            doc.add(new StringField("city", "qingdao", Field.Store.YES));
            doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
            indexWriter.updateDocument(new Term("id", "1"), doc);   //更新id是1的文档，主键id
            dos.closeIndexWriter(indexWriter);

           /* //读取索引器
            Directory indexDir = FSDirectory.open(Paths.get(indexDirStr));
            IndexReader indexReader = DirectoryReader.open(indexDir);
            System.out.println("最大文档数：" + indexReader.maxDoc());  //包含被标记的文档
            System.out.println("实际文档数：" + indexReader.numDocs()); //不包含被标记删除的文档
            indexReader.close();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
