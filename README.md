简介：
    Apache的开源项目，全文检索引擎工具包。也就是文本搜索器。
1.hello
    只要认识一下IndexWriter和IndexSearcher
    1.为数据创建索引，IndexWriter，使用完之后需要close一下，close时候才会内存向硬盘写入。
    2.使用索引搜索数据，IndexReader，IndexSearcher，QueryParser
2.IndexWriter写索引
    1.构建索引的时候对文档的简单crud操作
        文档Document：用户提供的源是一条条记录，它们可以是文本文件、字符串或者数据库表的一条记录等等。
            一条记录经过索引之后，就是以一个Document的形式存储在索引文件中的。用户进行搜索，也是以Document列表的形式返回。
        1.增加：索引增加文档，IndexWriter。
        2.读取：读取索引，IndexReader。
        3.删除不合并：只是做了个标记，并没有真正删除，工具软件能看到。
            因为频繁删除会影响性能。
        4.删除后合并：对做了删除标记的文档开始真正删除，工具软件能看不到删除掉的。
            建议：在不忙的时候，在进行合并操作
        5.更新：修改前后的分词都存在，因为曾经拥有。
    2.构建索引的时候对文档域加权
        对某个文档的某个域加权(>1的f)，则在这个域中搜索指定内容时，搜索到数据时，这个文档的排名会靠前：
            Term term = new Term(“域”, searchContent);
            Query query = new TermQuery(term);
            TopDocs hits = indexSearcher.search(query, number);
        1.权：就是比重，默认搜索的内容占搜索字段的百分比大小。默认为1
            field.setBoost(1.5f);
        2.域：文档的字段field，doc.add(field)，字段可以加权，加权后被搜索到的概率就大了。
4.indexSearcher使用索引搜索：
    IndexReader、IndexSearcher、Term、Query、TopDocs、ScoreDoc、Document
    1.对特定项（Field）简单匹配搜索：Term + Query 
    2.对特定项（Field）简单表达式匹配搜索：QueryParser + Analyzer + Query   
    3.分页查询实现：
        思路
            1.直接一次查询取出来，然后按照每页个数分页，效率高，浪费内存。
                TopDocs hits = indexSearcher.search(query, 100);
            2.多次查询取出来，0-10，0-20，等等，多次查询效率低一点。
                TopDocs hits = indexSearcher.search(query, 10);
                TopDocs hits = indexSearcher.search(query, 20);
        选择
            用第二种，因为基本就看第1和2页。并发量大的话，第一种浪费内存太大。
    4.对指定项（Field）的字符串范围查询：TermRangeQuery
        和我想象的不一样，用到的时候再查吧  
    5.对指定项（Field）的数字范围查询：NumericRangeQuery<Integer>
        一般查询id，如果每个文档的域是1、2、3、4.....100，则可以查询1-10的范围的文档
    6.对指定项（Field）以指定字符串开头的查询：PrefixQuery + Term
        StringField类型的项才可以
    7.组合查询：把 4 5 6 查询组合起来
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query1, BooleanClause.Occur.MUST); // MUST且 SHOULD或 MUST_NOT不包含
        booleanQuery.add(query2, BooleanClause.Occur.MUST);
5.其他重要项
    1.中文分词器,要用到新的jar包 lucene-analyzers-smartcn
        标准分词器的话：Analyzer analyzer = new StandardAnalyzer();
            仅仅会对textField项的内容分词，分成一个一个单独的字
        中文分词器的话： SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
            仅仅会对textField项的内容分词，分成一个一个独立的单词
            也会对该项查询的内容分词。
    2.高亮显示,要用到新的jar包 lucene-highlighter
        对搜索到的结果中搜索的内容进行html格式化处理
        SimpleHTMLFormatter、QueryScorer、Fragmenter、Highlighter、TokenStream