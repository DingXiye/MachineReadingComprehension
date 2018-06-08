# coding utf-8
from nltk.translate.bleu_score import sentence_bleu
import jieba

if __name__ == '__main__':
    list1 = jieba.lcut("举个简单例子，安装好java环境及eclipse之后，copy如下代码即可运行。注释部分为运行文件的方式，需要新建文件后去掉注释执行")
    list2 = jieba.lcut("举个简单例子，安装好java环境及eclipse之后，copy如下代码即可运行。")
    reference = ['this', 'is', 'a', 'test']
    candidate = ['this', 'is', 'a', 'test']
    score = sentence_bleu(list2, list1)
    print(score)
