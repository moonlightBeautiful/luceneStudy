package com.ims.c02_01indexWriterCrud;

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

public class DocumentOpStudy {

    /**
     * 测试数据
     */
    private String ids[] = {"1", "2", "3"};
    private String citys[] = {"qingdao", "nanjing", "shanghai"};
    private String descs[] = {
            "Qingdao is a beautiful city.",
            "Nanjing is a city of culture.",
            "Shanghai is a bustling city."
    };

    private IndexWriter indexWriter;

    /**
     * 初始化并打开索引写入器：索引目录和分词器,会在索引目录中创建文件write.lock
     *
     * @param indexDir 索引目录
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(String indexDir) throws Exception {
        //先删除掉目录中的文件
        File file = new File(indexDir);
        if (file.exists()) {
            for (File fileTemp : file.listFiles()) {
                fileTemp.delete();
            }
        }
        // 索引目录
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
     * @throws Exception
     */
    public void createIndex(IndexWriter indexWriter) throws Exception {
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.NO));
            indexWriter.addDocument(doc); // 添加文档
        }
    }


    public static void main(String[] args) {
        try {
            DocumentOpStudy documentOpStudy = new DocumentOpStudy();
            String indexDirStr = "E:\\Lucene\\indexDir";
            /**
             *  构建索引
             */
            /*
            docOperationStudy.indexWriter = docOperationStudy.getIndexWriter(indexDirStr);
            docOperationStudy.createIndex(docOperationStudy.indexWriter);
            docOperationStudy.closeIndexWriter(docOperationStudy.indexWriter);*/

            //文档操作：索引添加文档
           /*
            DocumentOpStudy docOperationStudy = new DocumentOpStudy();
            docOperationStudy.indexWriter = docOperationStudy.getIndexWriter(indexDirStr);
            System.out.println("写入了:" + docOperationStudy.indexWriter.numDocs() + "个文档");
            docOperationStudy.closeIndexWriter(docOperationStudy.indexWriter);*/

            //文档操作：读取索引
            /*Directory indexDir = FSDirectory.open(Paths.get(indexDirStr));
            IndexReader indexReader = DirectoryReader.open(indexDir);
            System.out.println("最大文档数：" + indexReader.maxDoc());  //包含所有被标记的文档
            System.out.println("实际文档数：" + indexReader.numDocs()); //不包含被标记删除的文档
            indexReader.close();*/

            //文档操作：删除不合并，仅仅做个删除标记
           /*  documentOpStudy.indexWriter = documentOpStudy.getIndexWriter(indexDirStr);
            System.out.println("删除不合并之前最大文档数：" + documentOpStudy.indexWriter.maxDoc());
            System.out.println("删除不合并之前实际文档数：" + documentOpStudy.indexWriter.numDocs());
            documentOpStudy.indexWriter.deleteDocuments(new Term("id", "1"));
            documentOpStudy.indexWriter.commit();
            System.out.println("删除不合并之后最大文档数：" + documentOpStudy.indexWriter.maxDoc());
            System.out.println("删除不合并之后实际文档数：" + documentOpStudy.indexWriter.numDocs());
            documentOpStudy.closeIndexWriter(documentOpStudy.indexWriter);*/

            //文档操作：删除后合并
             /*documentOpStudy.indexWriter = documentOpStudy.getIndexWriter(indexDirStr);
            System.out.println("删除不合并之前最大文档数：" + documentOpStudy.indexWriter.maxDoc());
            System.out.println("删除不合并之前实际文档数：" + documentOpStudy.indexWriter.numDocs());
            documentOpStudy.indexWriter.deleteDocuments(new Term("id", "1"));
            documentOpStudy.indexWriter.forceMergeDeletes();    //删除后合并
            documentOpStudy.indexWriter.commit();
            System.out.println("删除不合并之后最大文档数：" + documentOpStudy.indexWriter.maxDoc());
            System.out.println("删除不合并之后实际文档数：" + documentOpStudy.indexWriter.numDocs());
            documentOpStudy.closeIndexWriter(documentOpStudy.indexWriter);
*/
            //文档操作：更新文档
            /*Document doc = new Document();
            doc.add(new StringField("id", "1", Field.Store.YES));
            doc.add(new StringField("city", "qingdao", Field.Store.YES));
            doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
            documentOpStudy.indexWriter = documentOpStudy.getIndexWriter(indexDirStr);
            documentOpStudy.indexWriter.updateDocument(new Term("id", "1"), doc);   //更新id是1的文档，主键id
            documentOpStudy.closeIndexWriter(documentOpStudy.indexWriter);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
