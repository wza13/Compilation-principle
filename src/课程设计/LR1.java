package 课程设计;

import java.util.*;
import java.util.stream.Collectors;

class LR1 {
    public String grammarText;
    public List<String[]> grammar = new ArrayList<>();//(T->SF)= {T,SF} forAll
    public Set<String> nonTerminal = new HashSet<>();//非终结符
    public Set<String>  terminal = new HashSet<>();//终结符
    public Set<String> allSymbol = new HashSet<>();//全部符号
    public Map<String,Set<String>> First = new HashMap<>();
    public List<project> projectList = new ArrayList<>();//所有项目
    public List<projectSet> cProjectSets = new ArrayList<>();//项目集C
    public Map<point,String> ActionTable = new HashMap<>();//action表
    public Map<point,String> GOTO = new HashMap<>();//goto表
    public String[][] ActionAndGoTo;//Action和GOTO表
    public String startSymbol = "S`";
    class project{//项目  A→α·Bβ,a   grammar: A→αBβ  index: location of(.)  extSymbol: a
        private int indexOfGrammar;
        private int indexOfNode;
        private String extSymbol;
        project(int indexOfGrammar,int index,String extSymbol){
            this.indexOfGrammar = indexOfGrammar;
            this.indexOfNode = index;
            this.extSymbol = extSymbol;
        }
        public String[] getGrammar() {//获取产生式
            return grammar.get(indexOfGrammar);
        }
        public int getIndex() {//获取所用产生式的编号
            return indexOfNode;
        }
        public String getHead(){//获取产生式左边
            return this.getGrammar()[0];
        }
        public String getExtSymbol() {
            return extSymbol;
        }
        public String getRight(){//获取产生式右边点后面的部分
            return this.getGrammar()[1].substring(indexOfNode);
        }
        public String getFirstSymbolAfterNode(){//获取右侧字符串的首字符
            if(this.getRight().length()<1){
                return "";
            }
            else {
                return this.getRight().substring(0,1);
            }
        }
        public String getRestStringAfterFirst(){//获取右侧首个字符的之后的字符串
            if(this.getRight().length()<2){
                return "";
            }
            else {
                return this.getRight().substring(1);
            }
        }
        @Override
        public String toString() {
            StringBuffer str = new StringBuffer(grammar.get(indexOfGrammar)[1]);
            str.insert(indexOfNode,".");
            str.insert(0,"["+grammar.get(indexOfGrammar)[0]+"->");
            str.append(","+extSymbol+"]");
            return str.toString();
        }
        @Override
        public boolean equals(Object obj) {
            if(!obj.getClass().equals(this.getClass())){
                return false;
            }
            project cmp = ((project)obj);
            if(  (cmp.indexOfGrammar == this.indexOfGrammar)  &&  (cmp.indexOfNode==this.indexOfNode)  && (cmp.extSymbol.equals(this.extSymbol)) ){
                return true;
            }
            return false;
        }
        @Override
        public int hashCode() {
            String hash = indexOfGrammar+","+indexOfNode+","+extSymbol;
            return hash.hashCode();
        }
    }//项目
    class projectSet {//项目集(闭包)  重写了equals和hashcode 再加上是set存储 判断是不是生成了重复的对象
        Set<project> projects;//集合
        Set<Integer> indexOfProjects;//
        Map<String,Integer> sons;//孩子S  即 通过String 可以跳转到Integer下标的另一个集合
        public projectSet(){
            projects = new HashSet<>();
            indexOfProjects = new HashSet<>();
            sons = new HashMap<>();
        }
        public boolean add(project project){
            if(projectList.contains(project)){
                indexOfProjects.add(projectList.indexOf(project));
                return projects.add(project);
            }
            else {
                return false;
            }
        }
        public Set<project> getSet() {
            return projects;
        }
        private String toCompare(){
            StringBuffer sb = new StringBuffer();
            projects.forEach(S->sb.append(S.toString()));
            return sb.toString();//比较用  set相同即可认为是相同的集合
        }
        @Override
        public String toString() {
            return cProjectSets.indexOf(this)+"";
        }
        @Override
        public boolean equals(Object obj) {
            if(!obj.getClass().equals(this.getClass())){
                return false;
            }
            return this.toCompare().equals(((projectSet)obj).toCompare()) ;
        }
        @Override
        public int hashCode() {
            return this.toCompare().hashCode();
        }
    }
    public LR1(String text){
        grammarText = text;
        setGrammar(text);
        setFirst();
        setProjectList();
        setCanonicalCollection();
        setActionAndGOTOTable();
//        System.out.println("跳转表");
//        for (projectSet projectSet : cProjectSets) {
//            System.out.println(cProjectSets.indexOf(projectSet));
//            projectSet.getSet().forEach(System.out::print);
//            System.out.println("\n");
//            projectSet.sons.keySet().forEach(S-> System.out.print("["+S+"=>"+projectSet.sons.get(S)+"]"+"  "));
//            System.out.println("\n\n");
//        }
    }
    private void setGrammar(String text){
        for (String s : text.replaceAll(" ","").split("\n")) {
            for (String s1 : s.split("->")[1].split("\\|")) {
                String [] gram = {s.split("->")[0],s1};
                grammar.add(gram);
            }
        }
//        for (String[] strings : grammar) {
//            System.out.println(strings[0]+"->"+strings[1]);
//        }//读取文法
//        System.out.println();
        for (String[] strings : grammar) {
            nonTerminal.add(strings[0]);
            terminal.addAll(Arrays.asList(strings[1].split("")));
        }
        nonTerminal.forEach(S->terminal.remove(S));

        allSymbol.addAll(terminal);
        allSymbol.addAll(nonTerminal);
//        System.out.println("非终结符"+nonTerminal);
//        System.out.println("终结符"+terminal);
//        System.out.println();
    }
    private void setFirst(){
        nonTerminal.forEach(S->First.put(S,new HashSet<>()));
        terminal.forEach(S->First.put(S,new HashSet<>()));
        terminal.forEach(S->First.get(S).add(S));//终结符的First集是本身
        int FirstSize = 0;
        do{
            FirstSize = 0;
            for (String s1 : First.keySet()) {
                FirstSize+=First.get(s1).size();
            }//记录原本大小
            for (String[] strings : grammar) {
                String lam = strings[1];
                String G = strings[0];
                setSingleFirst(lam,G);//计算First集过程
            }
            for (String s1 : First.keySet()) {
                FirstSize-=First.get(s1).size();
            }//计算修改后大小
        }while (FirstSize != 0);//如果大小不在变化 则停下
//        System.out.println("First");
//        for (String s : First.keySet()) {
//            System.out.println(s+":"+First.get(s));
//        }
    }
    private void setSingleFirst(String lam,String G){
        String first = lam.substring(0,1);
        if(terminal.contains(first)){//终结符
            First.get(G).add(first);
        }
        else if(first.equals("ε")){//符号空
            First.get(G).add("ε");
        }
        else if(nonTerminal.contains(first)){//非终结符
            First.get(G).addAll(First.get(first).stream().filter(S->!S.equals("ε")).collect(Collectors.toSet()));
            if(First.get(first).contains("ε")){//是否可以推出空
                setSingleFirst(lam.substring(1),G);//扫描下一个
            }
        }
        else {
//            System.out.println("ERROR");
        }
    }
    private void setProjectList(){
        projectList.add(new project(0,0,"#"));
        projectList.add(new project(0,1,"#"));//开始符号 特殊  手动添加
        for (int indexOfGrammar = 0; indexOfGrammar < grammar.size(); indexOfGrammar++) {
            for (int indexOfNode = 0; indexOfNode <= grammar.get(indexOfGrammar)[1].length() ; indexOfNode++) {
                String A = grammar.get(indexOfGrammar)[0];//A->BC  A
                Set<String> a = new HashSet<>();
                for (String[] strings : grammar) {
                    if(strings[1].contains(A)){
                        int index = strings[1].indexOf(A)+1;
                        String sub = strings[1].substring(index);
                        a.addAll(First(sub));
                    }
                }
                for (String s : a) {
                    projectList.add( new project(indexOfGrammar,indexOfNode,s) );
                }
            }
        }
//        System.out.println("项目s");
//        for (int i = 0; i < projectList.size(); i++) {
//            System.out.println(i+" : " +projectList.get(i));
//        }
    }//读取项目
    private projectSet extendSingleClosure(projectSet closure) {
        Stack<project> stack = new Stack<>();
        closure.getSet().forEach(stack::push);
        while(!stack.empty()){
            project top = stack.pop();
            String B = top.getFirstSymbolAfterNode();
            closure.projects.add(top);
            if(nonTerminal.contains(B)){//如果是 A->...•B...
                List<String[]> GsHeadIsB =  grammar.stream().filter(G->G[0].equals(B)).collect(Collectors.toList());//每个左边是B的产生式
                String beta = top.getRestStringAfterFirst();//获得B之后的部分
                String a = top.getExtSymbol();//产生式之后的符号
                for (String[] strings : GsHeadIsB) {
                    for (String b : First(beta, a)) {
                        project newProject = new project(grammar.indexOf(strings),0,b);
                        if(!closure.getSet().contains(newProject)){
                            stack.push(newProject);
                        }
                    }
                }
            }
        }

        return closure;
    }//传入一个非空的项目集 将其扩充到不改变大小为止  返回此项目集
    private void setCanonicalCollection(){
//        System.out.println("创建集合C:");
        projectSet StartI = new projectSet();
        for (project project : projectList) {
            if(project.getGrammar()[0].equals(startSymbol) && project.getIndex()==0){
                StartI.add(project);
            }
        }
        extendSingleClosure(StartI);
        Stack<projectSet> stack = new Stack();
        stack.push(StartI);
        cProjectSets.add(StartI);
        while (!stack.empty()){//DFS顺序去创建
            projectSet current = stack.pop();
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            //System.out.print(current+" : ");
            //System.out.println(current.getSet().toString());
            for (String s : allSymbol) {
                List<project> temp = new ArrayList<>();
                for (project project : current.getSet()) {
                    if(project.getFirstSymbolAfterNode().equals(s)){//对于每个形如 A->...•X... 的项目
                        //if(s.equals("L"))
                        //System.out.println("使用 : "+project  +  "  s = "+ s);
                        project newProject = new project(project.indexOfGrammar,project.indexOfNode+1,project.getExtSymbol());
                        //if(s.equals("L"))
                        //System.out.println("得到了 : "+newProject);
                        temp.add(newProject);
                    }
                }
                //System.out.println("最终list : "+temp);

                projectSet nextI = new projectSet();
                nextI.projects.addAll(temp);
                extendSingleClosure(nextI);
                //System.out.println("最终set "+nextI.getSet());

                if(nextI.getSet().size()==0){
                    current.sons.put(s,-1);
                    continue;
                }
                if(cProjectSets.contains(nextI)){
                    current.sons.put(s,cProjectSets.indexOf(nextI));
                    continue;
                }
                stack.push(nextI);
                Integer index = cProjectSets.size();
                cProjectSets.add(nextI);
                current.sons.put(s,index);
            }
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }//设定项目集C
    private projectSet GO(projectSet I,String X ){//如果sons可以得出 则直接返回结果 结果应当是扩充过的  如果不能得出 则去计算 返回的也是计算过闭包的

        if(I.sons.containsKey(X) && I.sons.get(X)!=-1){
            return cProjectSets.get(I.sons.get(X));
        }
        return new projectSet();
    }
    private void setActionAndGOTOTable(){
        for (projectSet projectSet : cProjectSets) {
            for (project project : projectSet.getSet()) {
                String a = project.getFirstSymbolAfterNode();
                if(terminal.contains(a)){//项目 [A->...•a...,b] a是终结符
                    ActionTable.put(new point(projectSet.toString(),a),"s"+GO(projectSet,a).toString());
                }
            }
        }//<1>
        for (projectSet projectSet : cProjectSets) {
            for (project project : projectSet.getSet()) {
                if(project.getFirstSymbolAfterNode().equals("")){
                    ActionTable.put(new point(projectSet.toString(),project.getExtSymbol()),"r"+project.indexOfGrammar);
                }
            }
        }//<2>
        for (project project : projectList) {
            if(project.getHead().equals(startSymbol) && project.getFirstSymbolAfterNode().equals("")){
                for (projectSet projectSet : cProjectSets) {
                    if(projectSet.getSet().contains(project)){
                        int k = cProjectSets.indexOf(projectSet);
                        ActionTable.put(new point(String.valueOf(k),"#"),"acc");
                    }
                }
            }
        }//<3>
        for (String A : nonTerminal) {
            for (int k = 0; k < cProjectSets.size(); k++) {
                int j = cProjectSets.indexOf(GO(cProjectSets.get(k),A));
                if(j!=-1){
                    GOTO.put(new point(String.valueOf(k),A),String.valueOf(j));
                }
            }
        }//<4>
        int len = cProjectSets.size();//C集的SIZE
        for (int i = 0; i < len ; i++) {
            for (String s : nonTerminal) {
                if(!ActionTable.containsKey(new point(String.valueOf(i),s))){
                    ActionTable.put(new point(String.valueOf(i),s),"err");
                }
            }
            for (String s : terminal) {
                if(!ActionTable.containsKey(new point(String.valueOf(i),s))){
                    ActionTable.put(new point(String.valueOf(i),s),"err");
                }
            }
            if(!ActionTable.containsKey(new point(String.valueOf(i),"#"))){
                ActionTable.put(new point(String.valueOf(i),"#"),"err");
            }
        }//空位置打上err
        Map<point,String> adder = new HashMap<>();//Action和GOTO合并为一个 方便显示
        adder.putAll(ActionTable);
        adder.putAll(GOTO);
        List<String> tableSymbol = new ArrayList<>();
        tableSymbol.addAll(terminal);
        tableSymbol.add("#");//加上#
        tableSymbol.addAll(nonTerminal);
        tableSymbol.remove(startSymbol);//删掉S`
        ActionAndGoTo = new String[len][tableSymbol.size()+1];
        for (int i = 0; i < len ; i++) {
            ActionAndGoTo[i][0] = String.valueOf(i);
            for (int j = 0; j < tableSymbol.size() ; j++) {
                ActionAndGoTo[i][j+1] = adder.get(new point(String.valueOf(i),tableSymbol.get(j)));
            }
        }
//        System.out.print("\t");
//        for (String s : tableSymbol) {
//            System.out.print(s+"\t");
//        }
//        System.out.println();
//        for (String[] strings : ActionAndGoTo) {
//            for (String string : strings) {
//                System.out.print(string+"\t");
//            }
//            System.out.println();
//        }
    }//创建Action和GOTO表
    public String[][] getActionAndGoTo() {//返回分析表
        return ActionAndGoTo;
    }
    public String[] getHeader(){
        List<String> tableSymbol = new ArrayList<>();
        tableSymbol.addAll(terminal);
        tableSymbol.add("#");//加上#
        tableSymbol.addAll(nonTerminal);
        tableSymbol.remove(startSymbol);//删掉S`
        String[] header = new String[tableSymbol.size()+1];
        header[0] = "";
        for (int i = 0; i < tableSymbol.size(); i++) {
            header[i+1] = tableSymbol.get(i);
        }
        return header;
    }
    public Vector<String[]> analyse(String text){
        Vector<String[]> processRecord = new Vector<>();
        MyStack inputStack = new MyStack();//输入串
        MyStack symbolStack = new MyStack();//符号栈
        MyStack statusStack = new MyStack();//状态栈
        inputStack.push("#");
        inputStack.push(new StringBuffer(text).reverse().toString().split(""));
        symbolStack.push("#");
        statusStack.push("0");
        Boolean iFlag = true;
        int count = 0;
//        System.out.println("分析开始====================");
        while (iFlag){
            String[] currentStep = new String[5];
            currentStep[1] = statusStack.toString()+"\t";
            currentStep[2] = symbolStack.toString()+"\t";
            currentStep[3] = new StringBuffer(inputStack.toString()).reverse()+"\t";
            processRecord.add(currentStep);
            String i =  statusStack.getTop();//状态栈
            String a = inputStack.getTop();//输入串
//            System.out.println("状态栈顶:["+i+"]");
//            System.out.println("输入栈顶:["+a+"]");
            String action = ActionTable.get(new point(i,a));
            if(action == null){
//                System.out.println("nullActErr");
                currentStep[4] = "nullActErr";
                iFlag = false;
            }
            else if(action.equals("err")){
//                System.out.println("EqualsErr");
                currentStep[4] = "EqualsError";
                iFlag = false;
            }
            else if(action.equals("acc")){
//                System.out.println("成功!");
                currentStep[4] = "成功";
                iFlag = false;
            }
            else if(action.substring(0,1).equals("s")){//状态入栈
                Integer j = Integer.valueOf(action.substring(1));
//                System.out.println("当前S"+j);
                statusStack.push(String.valueOf(j));//j入状态栈
                symbolStack.push(inputStack.pop());//a到文法符号栈
                currentStep[4] = "Action["+i+","+a+"]="+action+",状态"+a+"入栈";
            }
            else if(action.substring(0,1).equals("r")){//规约 然后 GOTO入栈
                Integer index = Integer.valueOf(action.substring(1));//用index产生式规约
                for (int times = 0; times < grammar.get(index)[1].length(); times++) {
                    symbolStack.pop();
                    statusStack.pop();
                }//符号栈中取出文法的右侧的长度的字符
                symbolStack.push(grammar.get(index)[0]);//再压栈文法左边的符号

                String nextSta = GOTO.get(new point(statusStack.getTop(),symbolStack.getTop()));
                currentStep[4] = action+":"+grammar.get(index)[0]+"->"+grammar.get(index)[1]+"归约,GOTO("+statusStack.getTop()+","+statusStack.getTop()+")="+nextSta+"入栈";
                statusStack.push(nextSta);
            }
            else {
//                System.out.println("err");
                currentStep[4] = "ERROR";
                iFlag = false;
            }
            currentStep[0] = count+"\t";
            count++;


//            System.out.println("================================");
        }
        return processRecord;
    }
    public Stack analyse(Express express){
        Stack result = new Stack();
        Stack<Cell> inputStack = new Stack();//输入串
        Stack<Cell> symbolStack = new Stack();//符号栈
        MyStack statusStack = new MyStack();//状态栈
        inputStack.push(new Cell("s","#"));
        List<Cell> cells =  express.getCells();
        Collections.reverse(cells);
        cells.forEach(inputStack::push);
        symbolStack.push(new Cell("s","#"));
        statusStack.push("0");
        Boolean iFlag = true;
        while (iFlag){
            Map curProcess = new HashMap();
            String i = statusStack.getTop();
            Cell a = inputStack.peek();
            String action = ActionTable.get(new point(i,a.toString()));
//            System.out.println("Action.get("+i+","+a.toString()+")");
            if(action == null || action.equals("err")){
                curProcess.put("action","err");
                iFlag = false;
            }
            else if (action.equals("acc")){
                curProcess.put("action","acc");
                iFlag = false;
            }
            else if (action.substring(0,1).equals("s")){
                Integer j = Integer.valueOf(action.substring(1));
                statusStack.push(String.valueOf(j));//j入状态栈
                symbolStack.push(inputStack.pop());//a到文法符号栈
                curProcess.put("action",a);
            }
            else if (action.substring(0,1).equals("r")){
                Integer index = Integer.valueOf(action.substring(1));//用index产生式规约
                for (int times = 0; times < grammar.get(index)[1].length(); times++) {
                    symbolStack.pop();
                    statusStack.pop();
                }
                symbolStack.push(new Cell("s",grammar.get(index)[0]));//再压栈文法左边的符号
                String nextSta = GOTO.get(new point(statusStack.getTop(),symbolStack.peek().toString()));
                String currAct = grammar.get(index)[0]+"->"+grammar.get(index)[1];
                statusStack.push(nextSta);
//                System.out.print(currAct);
                curProcess.put("action",currAct);
            }
            else {
                iFlag = false;
            }
            List arr = new ArrayList();
            inputStack.forEach(arr::add);
            Collections.reverse(arr);
//            System.out.print(symbolStack);
//            System.out.println(arr);

            curProcess.put("inputStack",inputStack.clone());
            curProcess.put("symbolStack",symbolStack.clone());

            result.push(curProcess);
        }
        return result;
    }
    Set<String> First(String beta,String a){
        Set<String> res = new HashSet<>();
        if(beta.length() == 0){
            res.add(a);
            return res;
        }
        else {
            res.addAll(First(beta));
            if(First(beta).contains("ε")){
                res.add(a);
                res.remove("ε");
            }
        }
        return res;
    }
    Set<String> First(String s){//单个字串的First集
        Set<String> res = new HashSet<>();
        if(s.length() == 0){
            res.add("#");
            return res;
        }
        for (String symbol : s.split("")) {
            if(First.containsKey(symbol)){
                res.addAll(First.get(symbol));
                if(!First.get(symbol).contains("ε")){//如果 当前的字 可以推出空 看向字串的下一个字
                    break;
                }
            }
        }
        Boolean elicitNull = true;
        for (String symbol : s.split("")){
            if(First.containsKey(symbol)  && !First.get(symbol).contains("ε")){
                elicitNull = false;
                break;
            }
        }
        if(!elicitNull){
            res.remove("ε");
        }//只有 所有的字 都能推出空 这个字串才可以推出空
        return res;
    }
}

class point {
    String head, tail;

    point(String head, String tail) {
        this.head = head;
        this.tail = tail;
    }

    public String getHead() {
        return head;
    }

    public String getTail() {
        return tail;
    }

    @Override
    public int hashCode() {
        return (head + "->" + tail).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        } else {
            return ((point) o).getHead().equals(this.head) && ((point) o).getTail().equals(this.tail);
        }
    }

}

class MyStack {
    List<String> s;

    MyStack() {
        s = new LinkedList<>();
    }

    void push(String value) {
        s.add(value);
    }

    void push(String... values) {
        for (String value : values) {
            push(value);
        }
    }

    String pop() {
        return s.remove(s.size() - 1);
    }

    String getTop() {
        return s.get(s.size() - 1);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String value : s) {
            sb.append(value);
        }
        return sb.toString();
    }

    public Boolean isEmpty() {
        return s.size() == 0;
    }
}

