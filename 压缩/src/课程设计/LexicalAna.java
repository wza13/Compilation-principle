package 课程设计;

import java.util.*;

public class LexicalAna{
    Analyzer ana = new Analyzer();
    public List<Result> Solve(String[] lines) {
        List<Result> res = new ArrayList<>();
        if(lines==null|| lines.length==0)
            return res;
        List<String> text = new ArrayList<>();
        for(String line:lines){
            line = line.replaceAll("\t"," ");
            if(line.length()>0)
                text.add(line);
        }
        int l = 1;
        for(String str:text){
            if(str.length()>2 && str.substring(0,2).equals("//"))
                continue;
            ana.result.clear();
            res.addAll(ana.LineAnalyse(str+"\n",l,1));
            l++;
        }
        return res;
    }
}
class Analyzer{//在这里是用C的标准了
    String alphabet = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";//字母
    String number = "0123456789";//数字

    String keyword[] = {"auto","break","case","char","const ","continue","default","do ",
            "double ","else ","enum ","extern","float","for","goto","if","int",
            "long","register","return","short","signed","sizeof","static","struct","switch",
            "typedef","unsigned","union","void","volatile","while"};//关键字
    String operator[] ={"<<=",">>=","&&","||","<=","|=","*=","^=","==","++","--","/=","-=","+=","%=","!=",">=","[","]","!","%","(",")","*","+",",","-","/",";","<","=",">"};//运算符
    String arithmeticOperator[] = {"++","--","+","-","*","/","%"};//算术运算符
    String relationalOperator[] = {"<=","<",">=",">","==","!="};//关系运算符
    String logicalOperator[] = {"&&","||","!"};//逻辑运算符
    String delimiter[] = {";",",","(",")","[","]"};//分界符
    String assignmentOperator[] ={"=","+=","-=","*=","/=","%=","<<=",">>=","%=","^=","|="};//赋值运算符

    Map<String,String> opS;//<单个运算符,种类名>
    Map<String,String[]> KindtoArrary;//<种类名,对应的运算符数组>

    List<Result> result = new ArrayList<>();

    public Analyzer(){
        KindtoArrary = new HashMap<>();
        KindtoArrary.put("算术运算符",arithmeticOperator);
        KindtoArrary.put("关系运算符",relationalOperator);
        KindtoArrary.put("逻辑运算符",logicalOperator);
        KindtoArrary.put("分界符",delimiter);
        KindtoArrary.put("赋值运算符",assignmentOperator);
        opS = new HashMap<>();
        KindtoArrary.keySet().forEach(KindStr->Arrays.asList(KindtoArrary.get(KindStr)).forEach(str->opS.put(str,KindStr)));
    }

    public void setKP(String k[] , String p[] ){
        this.keyword = k;
        this.operator = p;
    }
    public List<Result> LineAnalyse(String line,int L,int C){//当前行 行数 列数
        //System.out.print("当前分析 :"+line+" ");
        if(line == null || line.length()==0 || line.equals("\n") || (line.length()>=2 && line.substring(0,2).equals("//"))){
            return null;//行空 长度为0 回车 注释 行结束
        }
        if(line.substring(0,1).equals(" ")){//是空格 跳过当前单词
            LineAnalyse(line.substring(1),L,C);
            return result;
        }
        Result res = new Result();
        String head = line.substring(0,1);
        int i = 0;
        if(alphabet.contains(head)){//匹配到字母
            while(i!=line.length() &&  (alphabet+number).contains(line.substring(i,i+1))){
                i++;
            }
            String wordGet = line.substring(0,i);
            Boolean ketWordMatch = false;
            int count = 0;
            for(String str:keyword){
                if(wordGet.equals(str)){//是关键字
                    ketWordMatch = true;
                    res.setKind("关键字");
                    res.setSequence("("+count+","+wordGet+")");
                    break;
                }
                count++;
            }
            if(!ketWordMatch){//是标识符
                res.setKind("标识符");
                res.setSequence("("+DataList.getID(wordGet)+","+ wordGet+")");
            }
            res.setWord(wordGet);
        }
        else if(number.contains(head)){//匹配到数字考虑小数，但小数不会以"."开头
            while (i!=line.length() && (number+".").contains(line.substring(i,i+1))){
                i++;
            }
            if(alphabet.contains(line.substring(i,i+1))){//数字之后直接追加字母  非法输入
                while(i!=line.length() &&  (alphabet+number).contains(line.substring(i,i+1))){
                    i++;
                }
                res.setWord(line.substring(0,i+1));
                res.setKind("ERROR");
                res.setSequence("ERROR"+DataList.getERROR(line.substring(0,i+1)));
            }
            else{
                String number = line.substring(0,i);
                res.setWord(number);
                res.setKind("常数");
                res.setSequence("("+DataList.getCI(line.substring(0,i))+","+ line.substring(0,i)+")");
            }

        }
        else{
            Boolean match = false;
            for(String str:operator){//用运算符来匹配而不是去匹配运算符号  避免 ++ 匹配出 +*2
                if(str.length() > line.length())
                    continue;//符号是在尾部 且不会匹配成功 则直接跳过
                //System.out.println(str+"匹配"+line.substring(0,str.length()));
                if(str.equals( line.substring(0,str.length()) )){//是运算符
                    res.setWord(str);
                    res.setKind(opS.get(str));
                    int count = Arrays.asList(KindtoArrary.get(opS.get(str))).indexOf(str);
                    res.setSequence("("+count+","+str+")");
                    match = true;
                    i+=str.length();
                    break;
                }
            }
            if(!match){//没有匹配到
                res.setWord(line.substring(0,1));
                res.setKind("ERROR");
                res.setSequence("ERROR"+DataList.getERROR(line.substring(0,1)));
                i++;
            }
        }
        line = line.substring(i);
        res.setLocation("("+L+","+C+")");
        result.add(res);
        LineAnalyse(line,L,++C);
        return result;
    }
}
class Result{
    public String word;//单词
    public String binarySequence;//二原序列
    public String kind;//类型
    public String location;//位置
    public Result(){
        this.word = "Null";
        this.binarySequence = "Null";
        this.kind = "Null";
        this.location = "Null";
    }
    public void setWord(String word){
        this.word = word;
    }
    public void setSequence(String Sequence){
        this.binarySequence = Sequence;
    }
    public void setKind(String kind){
        this.kind = kind;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public String[] toStringArrary(){
        String stringS[] = {"  "+word,"  "+binarySequence,"  "+kind,"  "+location};
        return stringS;
    }
    @Override
    public String toString(){
        String strs[] = {word,binarySequence,kind,location};
        StringBuffer toString = new StringBuffer();
        for(String str:strs){
            str =  String.format("%-20s", str);
            toString.append(str);
        }
        return toString.toString();
    }
}
class DataList{
    static List<String> id = new ArrayList<>(),ci = new ArrayList<>(),ERROR = new ArrayList<>();//标识符 常数
    public static int getID(String str){//获取标识符位置  存在则返回地址 不存在则存入 返回最后位置
        if(id.contains(str)){
            return id.indexOf(str);
        }
        else{
            id.add(str);
            return id.size()-1;
        }
    }
    public static int getCI(String str){//获取常数位置
        if(ci.contains(str)){
            return ci.indexOf(str);
        }
        else{
            ci.add(str);
            return ci.size()-1;
        }
    }
    public static int getERROR(String str){//获取错误代码
        if(ERROR.contains(str)){
            return ERROR.indexOf(str);
        }
        else{
            ERROR.add(str);
            return ERROR.size()-1;
        }
    }
}