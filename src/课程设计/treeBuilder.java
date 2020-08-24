package 课程设计;

import java.beans.JavaBean;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class treeBuilder {
    public String getResult(String arg) throws IOException {
        analyser analyser =  new analyser("S`->E\n" +
                "E->E+T | E-T|T\n" +
                "T->T*F | T/F|F\n" +
                "F->(E) | i");
        LinkedHashMap res = new LinkedHashMap();
        res.put(arg,analyser.ana(arg));
        String beautify = JSON.toJSONString(res, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        return beautify;

    }
}

class analyser{
    LR1 sol;
    public analyser(String rule){
        sol = new LR1(rule);
    }
    public Object ana(String exp){
        Stack res = sol.analyse(new Express(InitExp(exp)));
        if ( !((Map)res.pop()).get("action").equals("acc")) {
            return "ExpressionError";
        }
        Node head = new Node(new Cell("s","E"));
        getNodes(res,head);
        simplify(head);
        return simplePrintNode(head);
    }
    private List<Cell> InitExp_2(String exp) {
        List<Cell> result = new ArrayList<>();
        List<String> symbols = Arrays.asList("\\+", "-", "\\*", "/", "\\(", "\\)");
        for (String sym : symbols) {
            exp = exp.replaceAll(sym,"|"+sym+"|");
        }
        exp = exp.replaceAll("\\|\\|","|");
        for (String s : exp.split("\\|")) {//切片问题  ()在首部会导致切片问题 |(|123|+|456|)| -> ["","(","123","+","456",")"]
            if(s.equals("")){
                continue;
            }
            if("+-*/()".contains(s)){
                result.add(new Cell("s",s));
            }
            else{
                result.add(new Cell("i",s));
            }
        }
        return result;
    }
    private List<Cell> InitExp(String exp){
        List cells = new ArrayList();
        LexicalAna lexicalAna = new LexicalAna();
        for (Result result : lexicalAna.Solve(new String[]{exp})) {
            if(result.kind.equals("常数") || result.kind.equals("标识符") ){
                cells.add(new Cell("i",result.word));
            }
            else {
                cells.add(new Cell("s",result.word));
            }
        }
        return cells;
    }
    private void getNodes(Stack processRec,Node head){
        if(processRec.empty()){
            return;
        }
        if(head == null){
            return;
        }
        Map rec = (Map)processRec.pop();
        Object action = rec.get("action");
        if(action.getClass().equals(new Cell().getClass())){
            Cell cell = (Cell)action;
//            System.out.println("回填"+cell.getValue());
            if(cell.getType().equals("i")){//是数据而不是()
                head.cell = cell;//数据回填
            }
        }
        else {
            String act = (String)action;
            String [] expRight = act.split("->")[1].split("");
            if(expRight.length == 3){ //运算操作
                head.left = new Node(new Cell("s",expRight[0]));
                head.center = new Node(new Cell("s",expRight[1]));
                head.right = new Node(new Cell("s",expRight[2]));
            }
            else {//变换操作
                head.left = null;
                head.right = null;
                head.center = new Node(new Cell("s",expRight[0]));
            }
        }
        getNodes(processRec,head.right);
        getNodes(processRec,head.center);
        getNodes(processRec,head.left);
        return;
    }
    private String printNode(Node head){
        if(head == null) {
            return "null";
        }
        return String.format("{\"head\":\"%s\",\"left\":%s,\"center\":%s,\"right\":%s}",head.cell.getValue(),printNode(head.left),printNode(head.center),printNode(head.right) );
    }
    private void  simplify(Node head){
        if(head == null){
            return;
        }
        if(head.left == null && head.right == null && head.center != null) {
            head.cell = head.center.cell;
            head.left = head.center.left;
            head.right = head.center.right;
            head.center = head.center.center;//顺序
        }
        if(head.left!=null&&head.left.cell.getValue().equals("(")){
            head.left = null;
        }
        if(head.right!=null&&head.right.cell.getValue().equals(")")){
            head.right = null;
        }
        simplify(head.left);
        simplify(head.center);
        simplify(head.right);
    }
    private Object simplePrintNode(Node head){
        if( head.left != null && head.right != null && head.center !=null){
            Data data = new Data();
            data.setOp(simplePrintNode(head.center));
            data.setLeft(simplePrintNode(head.left));
            data.setRight(simplePrintNode(head.right));
            return data;
        }
        if( head.left == null && head.right == null && head.center!=null){
            return simplePrintNode(head.center);
        }
        if(head.left == null && head.center == null && head.right == null){
            String value = head.cell.getValue();
            if(head.cell.getType().equals("i")){//数字或者未知数
                if(Pattern.matches("(-?\\d*)\\.?\\d+", head.cell.getValue())){
                    return value.contains(".")? Double.parseDouble(value) : Integer.parseInt(value) ;
                }else {
                    return value;
                }
            }
            else {
                return value;//运算符
            }
        }
        return null;
    }
}

class Cell {
    String type;
    String value;
    int index;
    static int count = 0;
    Cell(){
        this.type = null;
        this.value = null;
        this.index = -1;
    }
    Cell(String type, String value){
        this.type =  type;
        this.value = value;
        if( this.type.equals("i") ){
            index = count;
            count++;
        }
        else{
            index = -1;
        }
    }
    @Override
    public String toString() {
        return type.equals("i")? type : value;
    }
    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
}

class Express{
    List<Cell> cells;
    public Express(List<Cell> cells){
        this.cells = cells;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        cells.forEach(N -> sb.append(N));
        return sb.toString();
    }
    public List<Cell> getCells() {
        return cells;
    }
}

class Node{
    Node left;
    Node center;
    Node right;
    Cell cell;
    public Node(Cell value){
        this.cell = value;
        left = null;
        center = null;
        right = null;
    }
    @Override
    public String toString() {
        return cell.toString();
    }
}

@JavaBean
class Data{
    @JSONField(ordinal = 0)
    private Object op;
    @JSONField(ordinal = 1)
    private Object left;
    @JSONField(ordinal = 2)
    private Object right;
    public void setOp(Object op) {
        this.op = op;
    }
    public void setLeft(Object left) {
        this.left = left;
    }
    public void setRight(Object right) {
        this.right = right;
    }
    public Object getOp() {
        return op;
    }
    public Object getLeft() {
        return left;
    }
    public Object getRight() {
        return right;
    }
    @Override
    public int hashCode() {
        return (op.hashCode() + "/" + left.hashCode() + "/" + right.hashCode()).hashCode();
    }
}

