package 实验二___LL1分析法;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class Solution {
	Map<String,String> AnaTable = new HashMap<>();
	Map<String,Set<String>> select = new HashMap<>();//产生式的select集
	List<String[]> Symbol_Gram = new ArrayList<>();// { { 符号 , 存在的文法  } *n }
    String x;
    String a;
	Set<String> noTerminal = new HashSet<>();
	Set<String> terminal = new HashSet<>();
	Set<String> allG = new HashSet<>();
	Map<String,Set<String>> First = new HashMap<>();
	Map<String,Set<String>> Follow  = new HashMap<>();
	String GStart = "null";
	Solution(){
		setG("E -> TG \n" +
				"G -> +TG | -TG \n" +
				"G -> ε \n" +
				"T -> FS \n" +
				"S -> *FS | /FS \n" +
				"S -> ε \n" +
				"F -> (E) \n" +
				"F->i \n");
    }
	String setG(String G){
			Map<String,String> AnaTable = new HashMap<>();
			Symbol_Gram.clear();
			G = G.replaceAll(" ","");
			String []Gs = G.split("\n");
			if(Gs.length<1){
				return "输入错误";
			}
			GStart =   Gs[0].substring(0,1) ;
			for (String gLine : Gs) {
				if( gLine.split("->").length!=2){
					return "格式错误";
				}
				String split0 = gLine.split("->")[0];
				for (String str : gLine.split("->")[1].split("\\|")) {
					String [] SingleG = {split0,str};
					Symbol_Gram.add(SingleG);
				}
			}
			GStart =   Gs[0].substring(0,1) ;
			MyStack stack = new MyStack();
			for (String[] strings : Symbol_Gram) {
				if(strings[0].equals(strings[1].substring(0,1))){
					return "左递归";
				}
			}
		getFF();//计算First Follow集
		for (String[] strings : Symbol_Gram) {
			select.put(strings[0]+strings[1],new HashSet<>());
		}
		//例如   s->ab   strings[0]  ->  strings[1]
		for (String[] strings : Symbol_Gram) {
			setSelect(strings,0);   //创建select集  key = 产生式 value  = [] Set
		}
		AnaTable.clear();//清空
		terminal.add("#");//在终结符内加入#   以达到
		for (String s0 : select.keySet()) {
			for (String s1 : select.get(s0)) {
				AnaTable.put(s0.substring(0,1)+s1,s0.substring(1));
			}
		}
		this.AnaTable =AnaTable;
		return null;
	}
	Vector<String[]> Solve(String text){
		Vector<String[]> procesList = new Vector();
		int textLength = text.length();
		MyStack AnaStack = new MyStack();//分析栈
		MyStack inputString = new MyStack();//输入串
		AnaStack.push("#","E","S");
		inputString.push( new StringBuffer(text).reverse().toString().split("") );
		Boolean flag = true;
		Boolean matched = true;
		int linenumber = 0;
		while (flag){
			String[] INFO = new String[5];
			INFO[0] = String.valueOf(linenumber++);
			INFO[1] = AnaStack.toString();
			StringBuffer inputsb= new StringBuffer(inputString.toString());
			for(int sblength = inputsb.length() ; sblength < textLength+1 ; sblength++ ){
				inputsb.append(" ");
			}
			INFO[2] = inputsb.reverse().toString();
			x = AnaStack.getTop();//获取分析栈顶
			a = inputString.getTop();//第一个符号读到a
			if(M(x,a)!=null){//存在对应的文法
				if(M(x,a)[0].equals("ε")){//为空
					INFO[3] = x+" -> ε";
					INFO[4] = "POP";
					AnaStack.pop();
				}
				else {
					AnaStack.push(M(AnaStack.pop(), a));
					StringBuffer sb = new StringBuffer();
					for(String string:M(x,a)){
						sb.append(string);
					}
					INFO[3] = x+" -> " + sb.reverse();
					INFO[4] = "POP,PUSH("+sb.reverse()+")";
					//System.out.println("存在文法["+x+","+a+"] -> ["+sb + "]  STACK:" + AnaStack);
				}
			}
			else if( !x.equals("#") && x.equals(a) ){//匹配到了
				//System.out.println("匹配到了"+x+" "+a);
				INFO[4] = "POP,GETNEXT(i)";
				inputString.pop();
				AnaStack.pop();
			}
			else if(x.equals("#") && x.equals(a)){//结束了
				flag=false;
			}
			else{//报错
				flag=false;
				matched=false;
			}
			for(int i = 0 ; i < 5 ; i++ ){
				if(INFO[i]!=null)
					INFO[i] = " "+INFO[i];
				else
					INFO[i] = " ";
			}

			procesList.add(INFO);
		}
		if(matched){
			//System.out.println("匹配成功");
		}
		else{
			//System.out.println("匹配失败");
			String ss[] ={"ERROR","ERROR","ERROR","ERROR","ERROR"};
			procesList.add(ss);
		}

//		for (String s : Grammer) {
//			System.out.print(s+" ");
//		}
		return procesList;
    }
    String[] M(String Line ,String column){
		//倒序 分割
		//System.out.println("查询 " + Line + "<->" + column);
		if(AnaTable.get(Line+column) == null) {
			if ( AnaTable.get(Line+"#")!=null) {
//				System.out.println("返回空");
				String ss[] = {"ε"};
				return ss;
			}
			else {
				return null;
			}
		}
		else{
			return new StringBuffer(AnaTable.get(Line+column)).reverse().toString().split("");
		}
	}
	Map[] getFF(){
		noTerminal.clear();
		First.clear();
		Follow.clear();
		allG.clear();
		terminal.clear();
		for (String[] strings : Symbol_Gram) {
			noTerminal.add(strings[0]);
		}//非终结符
		for (String[] strings : Symbol_Gram) {
			allG.add(strings[0]);
			for (String s : strings[1].split("")) {
				allG.add(s);
			}
		}//所有符
		allG.remove("ε");
		terminal.addAll(allG.stream().filter(S->  !noTerminal.contains(S)).collect(Collectors.toSet()));//终结符 = 所有符号 - 非终结符
		//System.out.println(Grammer+"\n"+EndG);
		for (String s1 : allG) {
			First.put(s1,new HashSet<>());
			Follow.put(s1,new HashSet<>());
		}//终结符的First集是本身终结符的First集是本身
		for (String s1 : terminal) {
			First.get(s1).add(s1);
		}
		int FirstSize = 0;
		do{
			FirstSize = 0;
			for (String s1 : First.keySet()) {
				FirstSize+=First.get(s1).size();
			}
			for (String[] strings : Symbol_Gram) {
				String lam = strings[1];
				String G = strings[0];
				setFirst(lam,G);
			}
			for (String s1 : First.keySet()) {
				FirstSize-=First.get(s1).size();
			}
		}while (FirstSize != 0);
		Follow.get(GStart).add("#");//文法开始符号 Follow加入#

		int  FollowSize = 0;
		do{
			FollowSize = 0;
			for (String s1 : Follow.keySet()) {
				FollowSize+=Follow.get(s1).size();
			}
			for (String[] strings : Symbol_Gram) {
				String lam = strings[1];
				String G = strings[0];
				for (String s2 : noTerminal) {
					setFollow(lam,G,s2);
				}
			}


			for (String s1 : Follow.keySet()) {
				FollowSize-=Follow.get(s1).size();
			}
		}while (FollowSize != 0);
		for (String s1 : terminal) {
			Follow.remove(s1);
		}
		Map[] res = new Map[2];
		res[0] = First;
		res[1] = Follow;
		return res;
	}
	private void setFirst(String lam,String G){
		String first = lam.substring(0,1);
		if(terminal.contains(first)){//终结符
			First.get(G).add(first);
		}
		else if(first.equals("ε")){//符号空
			First.get(G).add("ε");
		}
		else if(noTerminal.contains(first)){//非终结符
			First.get(G).addAll(First.get(first).stream().filter(S->!S.equals("ε")).collect(Collectors.toSet()));
			if(M(first,"ε")!=null){//是否可以推出空
				setFirst(lam.substring(1),G);//扫描下一个
			}
		}
		else {
		}
	}
	private void setFollow(String lam,String G,String sym){//产生式  ->左边的符号  当先所求的非终结符
		if(!lam.contains(sym)){
			return;
		}
		int index = lam.indexOf(sym);//位置
		if(index == lam.length()-1){// 是\0
			Follow.get(sym).add("#");
			Follow.get(sym).addAll(Follow.get(G));//把产生式左边的FOLLOW 加入到其的FOLLOW集中
			return;
		}
		else if(index < lam.length()-1){
			String next = lam.substring(index+1,index+2);//右边的符号
			if(terminal.contains(next)){//是终结符
				Follow.get(sym).add(next);
			}
			else if(noTerminal.contains(next)){//非终结符
				Follow.get(sym).addAll(First.get(next).stream().filter(S->!S.equals("ε")).collect(Collectors.toSet()));//把他的Fist集-ε 加入到当前分析的Follow集中
				if(M(next,"ε")!=null){//检查可否推出空
					//扫描下一个符号
					StringBuffer changedLam = new StringBuffer(lam)	;
					changedLam.deleteCharAt(index+1);//删除 达到左移的效果\
					setFollow(changedLam.toString(),G,sym);
				}
			}


		}

	}
	private void setSelect(String[] strings,int index){
		if(index == strings[1].length()){//是空
			select.get(strings[0]+strings[1]).addAll(Follow.get(strings[0]));
		}
		else {
			String firstSym = strings[1].substring(index,index+1);
			if(terminal.contains(firstSym)){//如果是终结符
				select.get(strings[0]+strings[1]).add(strings[1].substring(0,1));
			}
			else if(firstSym.equals("ε")){//是空
				select.get(strings[0]+strings[1]).addAll(Follow.get(strings[0]));
			}
			else if(noTerminal.contains(firstSym)){//是非终结符
				select.get(strings[0]+strings[1]).addAll(First.get(firstSym).stream().filter(S->!S.equals("ε")).collect(Collectors.toSet()));
				if(M(firstSym,"ε") != null){//可以推空  则扫描下一个
					setSelect(strings,index+1);
				}
			}
		}
	}
}
class MyStack{
	List<String> s;
	MyStack(){
		s = new LinkedList<>();
	}
	void push(String value){
		s.add(value);
	}
	void push(String...values){
		for(String value:values){
			push(value);
		}
	}
	String pop(){
		return s.remove(s.size()-1);
	}
	String getTop(){
		return s.get(s.size()-1);
	}
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(String value:s){
			sb.append(value);
		}
		return sb.toString();
	}
	public Boolean isEmpty(){
		return s.size()==0;
	}
}

