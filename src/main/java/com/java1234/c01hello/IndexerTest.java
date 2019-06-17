package com.java1234.c01hello;

/**
 * @author gaoxu
 * @date 2019-06-17 09:24
 * @description ... 类
 */
public class IndexerTest {

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
        System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }
}
