/*

Set<String> noTerminal = new HashSet<>();//非终结符
Set<String> terminal = new HashSet<>();//终结符
Map<String,Set<String>> First = new HashMap<>();//First集
Map<String,Set<String>> Follow  = new HashMap<>();//Follow集
Map<String,Set<String>> select = new HashMap<>();//产生式的select集
List<String[]> Symbol_Gram = new ArrayList<>();// { { 符号 , 存在的文法  } *n }
//使用set保存数据,确保无重复元素 在计算的时候无序手动排除重复元素

传入文法G

G按照\n和"->"以及"\\|"分割为单元

if(成功){
    保存并更新语法
}
else{
    弹出语法错误警告
}
if(存在左递归){
    弹出左递归警告
}

计算First集(){
    for(String symbol:终结符){
        First(symbol) = [symbol] 
    }//终结符的First集是本身
    while(First集的大小还在变化){
        for(String 左边->右边 :所有文法){
            取出右边下标为0的符号
            if(当前符号为非终结符){//当前符号的First集除了空都加入到左边符号的First集中
                First(左边).addAll(First(当前符号).except("ε"));//
                if(当前符号的First集合含有空){
                    看向下一个符号 //即下标+1 递归处理
                }
            }
            else if(当前符号是终结符 或者 "ε"){
                把当前符号加入到左边符号的First集中
            }
            else if(是"\0"){
                停止
            }
            else{
                停止
            }
        }
    }
}

计算Follow集(){
    在开始符号的Follow集中加入"#"
    for(String 当前符号:非终结符){
        for(语法 当前语法:所有的含有当前计算Follow集符号的语法){
            String 紧跟符号 = 当前语法中,当前符号之后的一个符号
            if(紧跟符号为终结符){
                把紧跟符号加到当前符号的Follow集中
            }
            else if(紧跟符号为非终结符){
                把紧跟符号的First集-"ε"加入到当前符号的Follow集中
                if(当前符号可以的First集含有空)
                    看向下一个符号//也是递归求解
            }
            else if(当前符号是"\0"){
                把空加入到当前符号的Follow集
            }
            else{
                报错 停止
            }
        }
    }
}

计算Select集(){
    for(String 当前文法(左边->右边) :所有文法){
        String 当前符号 = 右边的第一个符号
        if(当前符号是终结符){
            把当前符号加入Select(当前文法)
        }
        else if(当前符号是"ε"或者是"\0"){
            把Follow(左边)加入到Select(当前文法)
        }
        else if(当前符号是非终结符){
            把First(当前符号).except("ε")加入到Select(当前文法)中
            if(当前符号的First集含有"ε)
                看向下一个符号 递归求解
        }
        else{
            报错 停止
        }
    }
}

计算M表(){
    for(String 当前文法:select集){
        for(String 当前符号:Select(当前文法)){
            M(当前文法 的 左边,当前符号) = 当前文法
        }
    }
}

分析过程(String 输入的内容){
    初始化分析栈
    初始化输入栈
    while(结束标记为未结束){
        if(存在文法){
            if(M(x,a) == "ε"){
                分析栈出栈
            }
            else{
                分析栈.push(M(分析栈.pop(),a))
            }
        }
        else if(匹配到了 且没有结束){
            输入栈.pop()
            分析栈.pop()
        }
        else if(匹配成功 是#){
            结束标记修改为结束
        }
        else{
            报错
            结束标记修改为结束
        }
    }
}



*/
