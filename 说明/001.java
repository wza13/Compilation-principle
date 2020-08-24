/*



GUI包含一个Solution
分析时 在textArea中输入需要的分析的代码
或者直接打开文件读取到textArea

分析 则使用Solution.Solve 返回分析结果
显示在界面上

Solution 包含一个 Analyzer分析器

调用Solve方法  传入String数组 返回分析结果
for(String line:传入的String数组){
    result.addAll(Analyzer.LineAnalyse(line));Analyzer.LineAnalyse(line)
}
return result



LineAnalyse方法
if(当前分析的是 null,//,\n,或者Length == 0){
    则直接结束
}
else {
    if(字母表含有当前头部的string){
        while(是字母或者是数字){
            继续取出之后的部分
        }
        得到了一个String
        if(单词是关键字){
            标记为 关键字
        }
        else{
            标记为 标识符
        }
    }
    else if(数字表含有当前头部的的string){
        while(是数字或者小数点){
            继续取出之后的部分
        }
        if(数字之后直接追加字母){
            标记错误
            brerak
        }
        标记为 常数//常数的标记使用一个静态方法调用方法返回当前的数目+1 ERROR使用同样的方法编号
    }
    else{
        if(匹配到了符号){//运算符经过按照长度排序 确保长度较长的先匹配到 比如 ++ 会优先于+匹配
            标记 运算符 
        }
        else{
            标记 错误
        }
    }
    递归处理之后的String
    resturn  result.addAll(递归的结果);
}


*/
