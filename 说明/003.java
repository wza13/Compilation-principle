/*
    public String grammarText;
    public List<String[]> grammar = new ArrayList<>();//语法 格式:(T->SF)= {T,SF} 
    public Set<String> nonTerminal = new HashSet<>();//非终结符
    public Set<String>  terminal = new HashSet<>();//终结符
    public Set<String> allSymbol = new HashSet<>();//全部符号
    public Map<String,Set<String>> First = new HashMap<>();//First集
    public List<project> projectList = new ArrayList<>();//所有项目
    public List<projectSet> cProjectSets = new ArrayList<>();//项目集C
    public Map<point,String> ActionTable = new HashMap<>();//action表
    public Map<point,String> GOTO = new HashMap<>();//goto表
    public String[][] ActionAndGoTo;//Action和GOTO表
    public String startSymbol = "S`";//文法开始符号
    class project{
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
    }//存储项目,(文法下标,点的位置,展望符)例如:A→α·Bβ,a  grammar: A→α.Bβ  index: location of(.)  extSymbol: a 
    class projectSet {
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
    }//项目集 包含项目的set,以及对应符号的跳转项目集的下标 重写了equals和hashcode,使用set存储,判断是不是生成了重复的对象 


    初始化文法
    读取文法,计算First集(使用实验2的算法即可)
    
    初始化项目List(){
        加入(S`->.S),#和(S`->S.,#)//开始文法 特殊情况 手动添加
        for(文法 A->BC:所有文法){
            for( index : 所有可以插入点的位置){
                for(文法 当前文法:所有文法){
                if(当前文法的右边含有A){
                    把(A->BC,index,First(点后部的字符串))加入到项目集中       
                }
            }
            }
        }
    }
    初始化项目集C(){
        初始化栈
        创建一个项目集
        把开始文法的项目放入其中
        计算它的闭包
        把这个项目放入栈中
        while(栈非空){
            当前项目集 = 栈.pop()
            for(String Symbol : 每个符号){
                创建一个新的项目集
                for(项目 : 当前项目集中的所有项目){
                    if(是形如 A->...•X... 的项目){
                        点向后移动一位创建新的项目,加入到新的项目集中
                    }
                }
                计算闭包(新的项目集)
                if(如果新的项目集为空){
                    标记当前项目集通过Symbol跳转到-1
                }
                else if(新的项目集已经存在){
                    标记当前项目集通过Symbol跳转到 项目集List.indexOf(新的项目集)
                }
                else{
                    把新的项目集加入到项目集List中
                    标记当前项目集通过Symbol跳转到 项目集List.indexOf(新的项目集)
                    栈.push(新的项目集)
                }
            }
        }
    }
    计算闭包(项目集){
        新建栈
        项目集.forEach(栈::push)
        while(栈非空){
            当前项目 = 栈.pop()
            项目.add(当前项目);
            点后符号 = 当前项目的点后的第一个符号
            if(点后符号是非终结符){
                for(当前产生式:每个左边是B的产生式){
                    for(symbol:First(B之后的部分,当前项目的展望符)){
                        new 新项目(当前产生式,0,symbol);
                        if(项目原本不存在项目集中){
                            栈.push(新项目)
                        }
                    }
                }
            }
        }
    }
    Go(项目集,符号){
        if(项目集通过(符号跳转) != -1){
            return 项目集通过(符号跳转)的项目集
        }
        else 
            return 空集
    }
    创建Action和Goto表(){
        for(当前项目集:所有项目集){
            for( 当前项目 : 所有项目 ){
                当前项目 刑如 [A->...•a...,b] 
                if(a是终结符){
                    ActionTable(当前项目.index,a) = S + Go(当前项目集,a).index
                }
                if(a是""){
                    ActionTable(当前项目.index,b) = R + 当前项目集.indexOf语法            
                }
            }
        }
        ActionTable(初始项目.index,#) = acc
        for(当前项目集:所有项目集){
            for(当前符号:所有符号){
                Goto(当前项目集,当前符号) = Go(当前项目集,当前符号)
            }
        }
        其他位置标记为err
    }
    主控函数(){
        初始化输入串栈
        初始化符号栈
        初始化状态栈
        while(结束标记不为结束){
            String i =  状态栈顶
            String a = 输入串栈顶
            String action = ActionTable.(i,a)
            if(action() == null或者er ){
                报错
                标记结束
            }
            else if(action == acc ){
                成功
                标记结束
            }
            else if(action == Si){
                i入状态栈
                a到文法符号栈
            }
            else if(action == Ri){
                用index产生式规约
                符号栈中取出文法的右侧的长度的字符
                再压栈文法左边的符号
                状态栈.push( GOTO ( 状态栈.top() , 符号栈.top() ) )
            }
            else{
                报错
                标记结束
            }
        }
    }


*/
