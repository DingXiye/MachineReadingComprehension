package com.rengu.machinereadingcomprehension.Utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-07-21 16:43
 **/

public class Metric {
    public static final String BLEU_SCORE = "bleu_score";
    public static final String ROUGE_SCORE = "rouge_score";
    private static final String ARTICLE_ID = "article_id";
    private static final String QUESTIONS = "questions";
    private static final String QUESTION_ID = "questions_id";
    private static final String ANSWER = "answer";
    private static List<Article> refList = null;
    private static byte[] refCached = new byte[1];

    /**
     * 分别计算RougeL分数和Bleu分数，以ConcurrentHashMap返回
     *
     * @param candFile 预测答案文件
     * @return ConcurrentHashMap返回存储方式{"bleu_log": score1, "rouge_log": score2}
     */
    public static ConcurrentHashMap<String, Double> getScore(File refFile, File candFile) throws RuntimeException {

        if (candFile == null) {
            throw new RuntimeException("提交的答案不能为空");
        }

        if (refFile == null && refList == null) {
            throw new RuntimeException("没有初始化参考答案");
        }
        while (true) {
            if (refFile != null && refList == null) {
                setRefList(refFile);
            }
            if (refList != null) {
                break;
            }
        }

        return scoreCandFile(candFile);
    }

    public static ConcurrentHashMap<String, Double> scoreCandFile(File candFile) {
        // 开始进行统计
        JsonFactory jsonFactory = new JsonFactory();

        // 初始化计分器
        Bleu bleuEval = new Bleu();
        RougeL rougeLEval = new RougeL();
        ConcurrentHashMap<String, Double> scoreMap = new ConcurrentHashMap<>();

        int NEXT_BLOCK_STEP = 3;
        int NEXT_ARTICLE_STEP = 2;
        int NEXT_TOKEN_STEP = 1;

        try {
            JsonParser jsonParser = jsonFactory.createParser(candFile);
            for (Article article :
                    refList) {
                String refArticleID = article.getArticleID();
                // 读取答卷键值
                jsonParser = jump(jsonParser, NEXT_BLOCK_STEP);
                String curName = jsonParser.getCurrentName();
                if (!ARTICLE_ID.equals(curName)) {
                    throw new RuntimeException("无法识别article_id键值，JSON文件格式出错");
                }
                // 读取article_id
                jsonParser = jump(jsonParser, NEXT_TOKEN_STEP);
                String candArticleID = jsonParser.getText();
                if (!refArticleID.equals(candArticleID)) {
                    throw new RuntimeException("article_id键值不吻合，请检查JSON文件");
                }
                jsonParser = jump(jsonParser, NEXT_TOKEN_STEP);
                if (!QUESTIONS.equals(jsonParser.getCurrentName())) {
                    throw new RuntimeException("无法识别questions键值，JSON文件格式出错");
                }
                List<Question> refQuestions = article.getQuestions();
                for (Question refQuestion : refQuestions) {
                    String refQuestionID = refQuestion.getQuestionID();
                    String refAnswer = refQuestion.getAnswer();

                    // 获取candidate question id
                    // 读取答卷键值
                    jsonParser = jump(jsonParser, NEXT_BLOCK_STEP);
                    String candQuestionKey = jsonParser.getCurrentName();
                    if (!QUESTION_ID.equals(candQuestionKey)) {
                        throw new RuntimeException("questions_id键值不吻合，请检查JSON文件");
                    }
                    jsonParser = jump(jsonParser, NEXT_TOKEN_STEP);
                    String candQuestionID = jsonParser.getText();
                    if (!refQuestionID.equals(candQuestionID)) {
                        throw new RuntimeException("答卷question_id不吻合");
                    }
                    jsonParser = jump(jsonParser, NEXT_TOKEN_STEP);
                    String candAnswerKey = jsonParser.getCurrentName();
                    if (!ANSWER.equals(candAnswerKey)) {
                        throw new RuntimeException("answer键值不吻合，请检查JSON文件");
                    }
                    jsonParser = jump(jsonParser, NEXT_TOKEN_STEP);
                    String candAnswer = jsonParser.getText();

                    candAnswer = processString(candAnswer);
                    bleuEval.addInstance(candAnswer, refAnswer);
                    rougeLEval.addInstance(candAnswer, refAnswer);
                }
                jsonParser = jump(jsonParser, NEXT_ARTICLE_STEP);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法正确解析答卷JSON文件");
        }

        // 开始进行统计
        scoreMap.put(BLEU_SCORE, bleuEval.getScore());
        scoreMap.put(ROUGE_SCORE, rougeLEval.getScore());
        return scoreMap;
    }

    public static JsonParser jump(JsonParser jsonParser, int jumpStep) {

        int curIDX = 0;
        try {
            while (curIDX < jumpStep && jsonParser.nextToken() != null) {
                curIDX++;
            }
            if (curIDX != jumpStep) {
                throw new RuntimeException("JSON文件格式出错");
            }
        } catch (IOException e) {
            throw new RuntimeException("无法正确解析答卷JSON文件");
        }
        return jsonParser;
    }

    /**
     * 读取参考答案文件，将参考答案加载到内存中去
     *
     * @param refFile 参考答案文件
     * @throws RuntimeException 读取文件时出错抛出错误
     */
    public synchronized static void setRefList(File refFile) throws RuntimeException {
        // 初始化对象
        if (refList != null) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        refList = new ArrayList<>();

        // 读取参考答案数据
        try {
            // 初始化JSON解析方法
            JsonNode rootNode = mapper.readTree(refFile);

            // 初始化数组
            int size = rootNode.size();
            refList = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                JsonNode articleJSON = rootNode.get(i);
                String articleID = articleJSON.path(ARTICLE_ID).asText();
                if (articleID == null) {
                    throw new RuntimeException("参考答案文件不包含article_id键值，请检查该文件");
                }
                JsonNode questionsJson = articleJSON.path(QUESTIONS);
                if (questionsJson == null) {
                    throw new RuntimeException("参考答案文件不包含questions键值，请检查文件");
                }
                int questionSize = questionsJson.size();

                List<Question> questionList = new ArrayList<>(questionSize);
                for (int j = 0; j < questionSize; j++) {
                    JsonNode questionNode = questionsJson.get(j);
                    String questionID = questionNode.path(QUESTION_ID).asText();
                    if (questionID == null) {
                        throw new RuntimeException("参考答案不包含questions_id键值，请检查文件");
                    }
                    String answer = questionNode.path(ANSWER).asText();
                    answer = processString(answer);

                    Question question = new Question(questionID, answer);
                    questionList.add(question);
                }
                Article article = new Article(articleID, questionList);
                refList.add(article);
            }
        } catch (IOException e) {
            throw new RuntimeException("参考答案JSON文件格式有问题，请检查！");
        }
    }

    /**
     * 将短语中的英文单词转化为单个字母，
     * 请去掉换行符等无用字符
     *
     * @param str 字符串
     * @return 处理后的字符串
     */
    public static String processString(String str) {
        if (str == null || str.equals("")) {
            return str;
        }
        String sub = str.replaceAll("\\s+", "");
        return sub;
    }
}

/**
 * Bleu 算法用于评价及其翻译的准确度
 */
class Bleu {
    private static int BLEU_N = 4;

//    static {
//        Properties prop = new Properties();
//        try {
//            //读取属性文件
//            Resource resource = new ClassPathResource("args.properties");
//            InputStream in = new BufferedInputStream(resource.getInputStream());
//            prop.load(in);  //加载属性列表
//            String num = prop.getProperty("ROUGE_N");
//            BLEU_N = Integer.parseInt(num);
////            System.out.println("BLEU_N: " + BLEU_N);
//            in.close();
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

    private long[] matchNGrams;
    private long[] candiNGrams;
    private long BPR;
    private long BPC;

    /**
     * 默认使用BLEU-4计算分数
     */
    public Bleu() {
        this.matchNGrams = new long[BLEU_N + 1];
        this.candiNGrams = new long[BLEU_N + 1];
        this.BPC = 0;
        this.BPR = 0;
    }

    /**
     * 根据预测答案和参考答案更新BP, matchList, candiList，注意
     *
     * @param candSent 预测答案
     * @param refSent  参考答案
     */
    public void addInstance(String candSent, String refSent) {
        // 如果参考答案不存在不需要计算
        if (refSent == null || refSent.equals("")) {
            return;
        }
        // 如果答卷答案为null初始化为""
        if (candSent == null) {
            candSent = "";
        }

        int refSentSize = refSent.length();
        int candSentSize = candSent.length();
        int size = Math.min(BLEU_N, refSentSize);
        size = Math.min(size, candSentSize);
        for (int i = 0; i < size; i++) {
            countNGram(candSent, refSent, i + 1);
        }
        countBP(candSent, refSent);
    }

    /**
     * 更新BP相关的参数r和c
     *
     * @param candSent 预测答案
     * @param refSent  参考答案
     */
    public void countBP(String candSent, String refSent) {
        this.BPC += candSent.length();
        this.BPR += refSent.length();
    }

    /**
     * 计算numSize的条件下计算匹配个数以及参考答案子序列的长度，更鞋雷暴
     *
     * @param candSent 预测答案
     * @param refSent  参考答案
     * @param numSize  子序列大小 > 0
     */
    public void countNGram(String candSent, String refSent, int numSize) {
        assert numSize > 0;
        ConcurrentHashMap<String, Integer> candGramsMap = getNGramMap(candSent, numSize);
        ConcurrentHashMap<String, Integer> refsGramsMap = getNGramMap(refSent, numSize);
        int matchGramSize = getMatchSize(candGramsMap, refsGramsMap);
        int candGramSize = candSent.length() - numSize + 1;
        if (matchGramSize > candGramSize) {
            System.out.println("candidate sentence:" + candSent);
            System.out.println("reference sentence: " + refSent);
        }
        this.matchNGrams[numSize] += matchGramSize;
        this.candiNGrams[numSize] += candGramSize;
    }

    /**
     * 计算n-gram子序列表
     *
     * @param sentence 句子
     * @param numSize  子序列的大小
     * @return 子序列字符串数组, 如果句子的长度小于要求子序列的长度，则返回已经实例化的空的HashMap
     */
    public ConcurrentHashMap<String, Integer> getNGramMap(String sentence, int numSize) {
        // 计算nGram列表的长度
        int nGramSize = sentence.length() - numSize + 1;
//        System.out.println("numSize: " + numSize);
        // 初始化nGramMap
        ConcurrentHashMap<String, Integer> gramMap = new ConcurrentHashMap<>();
        if (nGramSize > 0) {
            for (int i = 0; i < nGramSize; i++) {
                String curGram = sentence.substring(i, i + numSize);
                if (gramMap.containsKey(curGram)) {
                    int countGram = gramMap.get(curGram);
                    countGram++;
                    gramMap.replace(curGram, countGram);
                } else {
                    gramMap.put(curGram, 1);
                }
            }
        }
        return gramMap;
    }

    /**
     * 计算备选答案中ngrams在参考答案中的匹配个数
     *
     * @param candGramsMap 备选答案的n-grams HashMap
     * @param refsGramMap  参考答案的n-grams
     * @return 匹配的个数, 如果candGrams活着refsGram为null返回0
     */
    public int getMatchSize(ConcurrentHashMap<String, Integer> candGramsMap, ConcurrentHashMap<String, Integer> refsGramMap) {
        int matchSize = 0;

        for (String key : candGramsMap.keySet()) {
            if (refsGramMap.containsKey(key)) {
                matchSize += Math.min(candGramsMap.get(key), refsGramMap.get(key));
            }
        }

        return matchSize;
    }

    /**
     * 计算预测答案的Bleu的分数
     *
     * @return Bleu分数
     */
    public double getScore() {
        // 如果存储的答案长度为0，返回0
        if (this.BPC == 0) {
            return 0;
        }

        double[] probArray = new double[BLEU_N];

        for (int i = 0; i < probArray.length; i++) {
            if (this.candiNGrams[i + 1] != 0) {
                double curP = this.matchNGrams[i + 1] * 1.0 / this.candiNGrams[i + 1];
                probArray[i] = curP;
            } else {
                probArray[i] = 0.0;
            }
        }
        // 计算BLEU_n的得分
        double score = probArray[0];
        for (int i = 1; i < BLEU_N; i++) {
            score *= probArray[i];
        }
        if (score != 0) {
            score = Math.pow(score, 1.0 / BLEU_N);
            // 计算BP的质
            double preValue = 1 - this.BPR * 1.0 / this.BPC;
            double BP = Math.exp(Math.min(preValue, 0.0));
            score *= BP;
        }
        return score;
    }

    public void showMatchNGram() {
        System.out.print("match ngrams size: ");
        for (int i = 0; i < this.matchNGrams.length; i++) {
            System.out.print(this.matchNGrams[i] + ", ");
        }
        System.out.println(".");
        System.out.print("candidate ngrams size: ");
        for (int i = 0; i < this.candiNGrams.length; i++) {
            System.out.print(this.candiNGrams[i] + ", ");
        }
        System.out.println(".");
    }

    public void showBP() {
        System.out.println("BP candidate size: " + this.BPC);
        System.out.println("BP reference size: " + this.BPR);
    }
}

class RougeL {

    private static double GAMMA = 1.2;

//    static {
//        Properties prop = new Properties();
//        try {
//            // 加载配置文件
//            Resource resource = new ClassPathResource("args.properties");
//            InputStream in = new BufferedInputStream(resource.getInputStream());
//            prop.load(in);  //加载属性列表c
//            GAMMA = Double.parseDouble(prop.getProperty("GAMMA"));
////            System.out.println("GAMMA: " + GAMMA);
//            in.close();
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

    private double scoreSum;
    private int scoreSize;

    /**
     * RougeL算法默认赋值gamma=1.2
     */
    public RougeL() {
        this.scoreSum = 0.0;
        this.scoreSize = 0;
    }

    /**
     * 计算str和sub的最长公共子串的大小
     *
     * @param str 字符串1
     * @param sub 字符串2
     * @return 最长公共子串的size
     */
    public int getLCS(String str, String sub) {
        // receive the length of tow string
        int strLength = str.length();
        int subLength = sub.length();
        // initialize length to sort midterm info
        int[][] lengths = new int[strLength + 1][subLength + 1];
        // compute the longest size store in lengths[strLength + 1][subLength + 1]
        for (int i = 1; i < strLength + 1; i++) {
            for (int j = 1; j < subLength + 1; j++) {
                char strChar = str.charAt(i - 1);
                char subChar = sub.charAt(j - 1);
                if (strChar == subChar) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                } else {
                    lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
                }
            }
        }

        return lengths[strLength][subLength];
    }

    /**
     * 计算分数并添加到InstanceScore中去
     *
     * @param candSent 预测答案句子
     * @param refSent  参考答案句子
     */
    public void addInstance(String candSent, String refSent) {
        // 判断参考答案是否为空，参考答案为空时，不能进行评测
        if (refSent == null || refSent.equals("")) {
            return;
        }
        // 如果答卷为
        if (candSent == null) {
            candSent = "";
        }

        // 获取字符串长度
        int candLength = candSent.length();
        int refsLength = refSent.length();

        double score = 0;
        if (candLength > 0) {
            // 计算最大公共子序列表
            int lcs = getLCS(candSent, refSent);
            if (lcs != 0) {
                // 计算准确率
                double precs = lcs * 1.0 / candLength;
                // 计算召回率
                double recall = lcs * 1.0 / refsLength;

                // 计算RougeL分数
                score = (1 + Math.pow(GAMMA, 2)) * recall * precs;
                score /= recall + Math.pow(GAMMA, 2) * precs;
            }
        }

        this.scoreSum += score;
        this.scoreSize++;
    }

    /**
     * 计算预测答案的RougeL分数，并返回
     *
     * @return RougeL分数, 如果句子中存在空的字符串返回null
     */
    public double getScore() {
        if (this.scoreSize == 0) {
            return 0;
        } else {
            return this.scoreSum / this.scoreSize;
        }
    }
}

/**
 * 文章对象保存了文章相关信息以及问题列表(使用ArrayList初始化)
 */
class Article {
    private String articleID;
    private String articleType;
    private String articleTitle;
    private String articleContent;
    private List<Question> questions;

    /**
     * 当数据包含于预测结果中使用该构造函数
     *
     * @param articleID 文章ID号码
     * @param questions 问题列表对象
     */
    public Article(String articleID, List<Question> questions) {
        this.articleID = articleID;
        this.questions = questions;
    }

    /**
     * 当数据保存于参考答案中时使用该构造函数
     *
     * @param articleID      文章ID号码
     * @param articleType    文章类型
     * @param articleTitle   文章标题
     * @param articleContent 文章内容
     */
    public Article(String articleID, String articleType, String articleTitle,
                   String articleContent, List<Question> questions) {
        this.articleID = articleID;
        this.articleType = articleType;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.questions = questions;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return articleID.equals(article.articleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleID);
    }
}

class Question {
    private String questionID;
    private String questionContent;
    private String answer;
    private String questionType;

    /**
     * 当输入的数据为指标数据的时候时候这个构造函数
     *
     * @param questionID      问题ID号码
     * @param questionContent 问题的具体内容
     * @param answer          问题答案
     * @param questionType    问题类型
     */
    public Question(String questionID, String questionContent, String answer, String questionType) {
        this.questionID = questionID;
        this.questionContent = questionContent;
        this.answer = answer;
        this.questionType = questionType;
    }

    /**
     * 当输入的数据为预测数据时，使用该构造函数
     *
     * @param questionID 问题Id号码
     * @param answer     问题候选答案
     */
    public Question(String questionID, String answer) {
        this.questionID = questionID;
        this.answer = answer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return questionID.equals(question.questionID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(questionID);
    }
}
