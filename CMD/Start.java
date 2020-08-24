import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("自定义KP？[Y/N]");
        String yon;
        Solution sl = new Solution();
        Boolean iflag = false;
        do{
            yon = sc.nextLine();
            if(yon.equals("y")||yon.equals("Y")||yon.equals("yes")||yon.equals("YES")){
                iflag=true;
                String[] k,p;
                k = sc.nextLine().split(" ");
                p = sc.nextLine().split(" ");
                sl.AnalyseManullySet(k,p);
            }
            else if (yon.equals("n")||yon.equals("N")||yon.equals("no")||yon.equals("NO")){
                iflag=true;
            }
        }while (yon==null || iflag==false);
         System.out.print("输入行数：");
        int linenumber =  sc.nextInt();
        System.out.println("输入内容:");
        String str[] = new String[linenumber+1];
        for (int i = 0; i <= linenumber; i++) {
            str[i] = sc.nextLine();
        }
        sl.Solve(str).forEach(System.out::println);
    }
}

class Solution{
    Analyzer ana = new Analyzer();
    public List<Result> Solve(String[] lines) {
        if(lines==null|| lines.length==0){
            return new ArrayList<>();
        }
        List<String> text = new ArrayList<>();
        for(String line:lines){
            line = line.replaceAll("\t"," ");
            if(line.length()>0)
                text.add(line);
        }
        List<Result> res = new ArrayList<>();
        int l = 1;
        for(String str:text){
            if(str.length()>2 && str.substring(0,2).equals("//"))
                continue;
            ana.resultList.clear();
            res.addAll(ana.LineAnalyse(str+"\n",l,1));
            l++;
        }
        return res;
    }
    public void AnalyseManullySet(String k[] , String p[] ){
        ana.setKS(k,p);
    }
}

class Analyzer{
    String alphabet = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    String number = "0123456789";
    String keyword[] = {"auto","break","case","char","const ","continue","default","do ",
            "double ","else ","enum ","extern","float","for","goto","if","int",
            "long","register","return","short","signed","sizeof","static","struct","switch",
            "typedef","unsigned","union","void","volatile","while"};
    String operator[] = {"<<=",">>=","&&","||","<=","|=","*=","^=","==","++","--","/=","-=","+=","%=","!=",">=","[","]","!","%","(",")","*","+",",","-","/",";","<","=",">"} ;//运算符
    String arithmetic[] = {"++","--","+","-","*","/","%"};
    String relational[] = {"<=","<",">=",">","==","!="};
    String logical[] = {"&&","||","!"};
    String delimiter[] = {";",",","(",")","[","]"};
    String assignment[] ={"=","+=","-=","*=","/=","%=","<<=",">>=","%=","^=","|="};
    Map<String,String> singleOperatorToTypeName;
    Map<String,String[]> kindToOperatorArrary;
    List<Result> resultList = new ArrayList<>();
    public Analyzer(){
        kindToOperatorArrary = new HashMap<>();
        kindToOperatorArrary.put("算术运算符",arithmetic);
        kindToOperatorArrary.put("关系运算符",relational);
        kindToOperatorArrary.put("逻辑运算符",logical);
        kindToOperatorArrary.put("分界符",delimiter);
        kindToOperatorArrary.put("赋值运算符",assignment);
        singleOperatorToTypeName = new HashMap<>();
        for(String kindStr:kindToOperatorArrary.keySet()){
        	for(String string:Arrays.asList(kindToOperatorArrary.get(kindStr))){
        		singleOperatorToTypeName.put(string,kindStr);
        	}
        }
    }
    public void setKS(String k[] , String p[] ){
        this.keyword = k;
        this.operator = p;
    }
    public List<Result> LineAnalyse(String line,int L,int C){
        if(line == null || line.length()==0 || line.equals("\n") || (line.length()>=2&&line.substring(0,2).equals("//"))){
            return null;
        }
        if(line.substring(0,1).equals(" ")){
            LineAnalyse(line.substring(1),L,C);
            return resultList;
        }
        Result CurrentResult = new Result();
        String start = line.substring(0,1);
        int i = 0;
        if(alphabet.contains(start)){
            while(i!=line.length() &&  (alphabet+number).contains(line.substring(i,i+1))){
                i++;
            }
            String charater = line.substring(0,i);
            Boolean MatchEd = false;
            int counter = 0;
            for(String str:keyword){
                if(charater.equals(str)){
                    MatchEd = true;
                    CurrentResult.Type("关键字");
                    CurrentResult.Sequence(counter+","+charater);
                    break;
                }
                counter++;
            }
            if(!MatchEd){
                CurrentResult.Type("标识符");
                CurrentResult.Sequence(resultsave.IDNumber(charater)+","+ charater);
            }
            CurrentResult.Word(charater);
        }
        else if(number.contains(start)){
            while (i!=line.length() && (number+".").contains(line.substring(i,i+1))){
                i++;
            }
            String number = line.substring(0,i);
            CurrentResult.Word(number);
            CurrentResult.Type("常数");
            CurrentResult.Sequence(resultsave.ciNumber(line.substring(0,i))+","+ line.substring(0,i));
        }
        else {
            Boolean MatchEd = false;
            for(String str:operator){
                if(str.length() > line.length())
                    continue;
                if(str.equals( line.substring(0,str.length()) )){
                    CurrentResult.Word(str);
                    String tpyename = singleOperatorToTypeName.get(str);
                    CurrentResult.Type(tpyename);
                    int counter = Arrays.asList(kindToOperatorArrary.get(tpyename)).indexOf(str);
                    CurrentResult.Sequence(counter+","+str);
                    MatchEd = true;
                    i+=str.length();
                    break;
                }
            }
            if(!MatchEd){
                CurrentResult.Word(line.substring(i,i+1));
                CurrentResult.Type("error");
                CurrentResult.Sequence("error"+resultsave.errorNumber(line.substring(i,i+1)));
                i++;
            }
        }
        CurrentResult.Location(L+","+C);
        resultList.add(CurrentResult);
        C++;
        LineAnalyse(line.substring(i),L,C);
        return resultList;
    }
}

class Result{
    public String word;
    public String binarySequence;
    public String type;
    public String location;
    public Result(){
        this.word = "";
        this.binarySequence = "";
        this.type = "";
        this.location = "";
    }
    public void Word(String word){
        this.word = word;
    }
    public void Sequence(String Sequence){
        this.binarySequence = Sequence;
    }
    public void Type(String type){
        this.type = type;
    }
    public void Location(String location){
        this.location = location;
    }
    public String[] toStringArrary(){
        String strings[] = {word,binarySequence,type,location};
        return strings;
    }
    @Override
    public String toString(){
        String strs[] = {word,binarySequence,type,location};
        StringBuffer sb = new StringBuffer();
        for(String string:strs){
            sb.append(string+"        ");
        }
        return sb.toString();
    }
}

class resultsave{
    static List<String> idList = new ArrayList<>(),ciList = new ArrayList<>(),errorList = new ArrayList<>();//标识符 常数
    public static int IDNumber(String str){
        if(idList.contains(str)){
            return idList.indexOf(str);
        }
        else{
            idList.add(str);
            return idList.size()-1;
        }
    }
    public static int ciNumber(String str){
        if(ciList.contains(str)){
            return ciList.indexOf(str);
        }
        else{
            ciList.add(str);
            return ciList.size()-1;
        }
    }
    public static int errorNumber(String str){
        if(errorList.contains(str)){
            return errorList.indexOf(str);
        }
        else{
            errorList.add(str);
            return errorList.size()-1;
        }
    }
}