package 实验二___LL1分析法;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

class Windows extends JFrame {
    JButton clear,confirm,setG,FF;
    JTextArea textArea;
    JTabbedPane tabbedPane;
    Solution sol;
    Windows(){
        setVisible(false);
        try{
            setIconImage(new ImageIcon("bilibili.PNG").getImage());
            Font f = new Font("Yahei Consolas Hybrid",Font.PLAIN,16);
            String   names[]={ "MenuBar","Menu","MenuItem", "TextArea", "Button", "ScrollPane", "Table","TabbedPane"};
            for (String item : names) {
                UIManager.put(item+ ".font",f);
            }
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch(Exception e){}
        init();
        setSize(800,600);//初始大小
        setLocation(300,200);//初始位置
        setVisible(true);//是否可视
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//X退出
    }
    void init(){
        setTitle("LL(1)分析法");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLayout(null);
        sol = new Solution();
        initButton();
        initText();
        initResult();

    }
    void initButton(){
        class clearListen implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e){
                textArea.setText("");
                tabbedPane.removeAll();
                tabbedPane.updateUI();
            }
        }
        class confirmListen implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e){
                tabbedPane.removeAll();
                for(String text:textArea.getText().split("\n")){
                    if(text.length()<2 || !text.substring(text.length()-1).equals("#") ){
                        JOptionPane.showMessageDialog(null, "格式输入错误", "Error !", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                    addTable(text,sol.Solve(text));
                    tabbedPane.updateUI();
                }


            }
        }
        class MyDialog extends JDialog implements ActionListener{
            JTextArea input;
            JButton confirm,cancel;
            String title;
            MyDialog(Windows f){
                setLayout(null);
                setResizable(false);
                setIconImage(new ImageIcon("bilibili.PNG").getImage());
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("请输入语法");
                input=new JTextArea("E -> TG \n" +
                        "G -> +TG | -TG \n" +
                        "G -> ε \n" +
                        "T -> FS \n" +
                        "S -> *FS | /FS \n" +
                        "S -> ε \n" +
                        "F -> (E) \n" +
                        "F->i \n");
                JScrollPane jScrollPane = new JScrollPane(input);
                jScrollPane.setBounds(10,10,265,200);
                add(jScrollPane);

                class confirmListener implements ActionListener{
                    @Override
                    public void actionPerformed(ActionEvent e){
                        String getInput = input.getText();
                        String setRes = sol.setG(getInput);
                        if(setRes!=null){
                            JOptionPane.showMessageDialog(null, setRes, "Error !", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            setVisible(false);
                        }
                    }
                }
                confirm=new JButton("确定");
                confirm.addActionListener(new confirmListener());
                confirm.setBounds(195,220,80,30);
                add(confirm);

                class cancelListener implements ActionListener{
                    @Override
                    public void actionPerformed(ActionEvent e){
                        input.setText("E -> TG \n" +
                                "G -> +TG | -TG \n" +
                                "G -> ε \n" +
                                "T -> FS \n" +
                                "S -> *FS | /FS \n" +
                                "S -> ε \n" +
                                "F -> (E) \n" +
                                "F->i \n");
                        setVisible(false);
                    }
                }
                cancel=new JButton("取消");
                cancel.addActionListener(new cancelListener());
                cancel.setBounds(105,220,80,30);
                add(cancel);


                setBounds(600,260,300,300);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
            public void actionPerformed(ActionEvent e){
                setVisible(true);
            }
        }
        class FFListen extends JDialog implements ActionListener{
            JTabbedPane jTabbedPane;
            FFListen(){
                jTabbedPane = new JTabbedPane();
                setLayout(null);
                setResizable(false);
                setIconImage(new ImageIcon("bilibili.PNG").getImage());
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("Fist Follow 集");
                setBounds(650,300,300,400);
                jTabbedPane.setBounds(10,7,267,350);
                add(jTabbedPane);

            }
            @Override
            public void actionPerformed(ActionEvent e){
                setVisible(true);
                jTabbedPane.removeAll();
                JTable First,Follow;
                Map<String,Set<String>>[]  res =   sol.getFF();

                Object[][] FirstData = new Object[res[0].size()][2];
                int index = 0;
                for (String s : res[0].keySet()) {
                    FirstData[index][0] = s;
                    FirstData[index][1] = res[0].get(s).toString();
                    index++;
                }
                Object[] columnNames = {"", ""};
                First = new JTable(FirstData, columnNames);
                First.setRowHeight(24);
                First.getTableHeader().setVisible(false);
                JScrollPane FirstScrollable = new JScrollPane(First);
                FirstScrollable.setBorder(null);
                jTabbedPane.addTab("First集",FirstScrollable);

                Object[][] FollowData = new Object[res[1].size()][2];
                index = 0;
                for (String s : res[1].keySet()) {
                    FollowData[index][0] = s;
                    FollowData[index][1] = res[1].get(s).toString();
                    index++;
                }
                Follow = new JTable(FollowData, columnNames);
                Follow.setRowHeight(24);
                Follow.getTableHeader().setVisible(false);
                JScrollPane FollowScrollable = new JScrollPane(Follow);
                FollowScrollable.setBorder(null);
                jTabbedPane.addTab("Follow集",FollowScrollable);

            }
        }


        clear = new JButton("清除");
        clear.setBounds(600,160,80,30);
        clear.addActionListener(new clearListen());

        confirm = new JButton("确认");
        confirm.setBounds(695,160,80,30);
        confirm.addActionListener(new confirmListen());

        setG = new JButton("自定义语法");
        setG.setBounds(460,160,120,30);
        setG.addActionListener(new MyDialog(this));

        FF = new JButton("Fist,Follow集");
        FF.setBounds(260,160,180,30);
        FF.addActionListener(new FFListen());

        class selectListen extends JDialog implements ActionListener{
            JTabbedPane jTabbedPane;
            selectListen(){
                jTabbedPane = new JTabbedPane();
                setLayout(null);
                setResizable(false);
                setIconImage(new ImageIcon("bilibili.PNG").getImage());
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("select集");
                setBounds(650,300,500,300);
                jTabbedPane.setBounds(10,7,467,250);
                add(jTabbedPane);

            }
            @Override
            public void actionPerformed(ActionEvent e){
                setVisible(true);
                jTabbedPane.removeAll();
                JTable First,Follow;
                Map<String,Set<String>>  res =   sol.select;

                Object[][] FirstData = new Object[res.size()][2];
                int index = 0;
                for (String s : res.keySet()) {
                    FirstData[index][0] = s;
                    FirstData[index][1] = res.get(s).toString();
                    index++;
                }
                Object[] columnNames = {"", ""};
                First = new JTable(FirstData, columnNames);
                First.setRowHeight(24);
                First.getTableHeader().setVisible(false);
                JScrollPane FirstScrollable = new JScrollPane(First);
                FirstScrollable.setBorder(null);
                jTabbedPane.addTab("select集",FirstScrollable);

                {
                    Map<String,Integer> Grammap = new HashMap<>();
                    Map<String,Integer>  endGmap = new HashMap<>();


                    int a = 0;
                    for (String s1 : sol.terminal) {
                        //System.out.print("     "+s1);
                        endGmap.put(s1,a);
                        a++;
                    }
                    int b = 0;
                    for (String s : sol.noTerminal) {
                        Grammap.put(s,b);
                        b++;
                    }
                    String[][] map = new String[Grammap.size()][endGmap.size()];
                    //System.out.println();
                    for (String s1 : sol.noTerminal) {
                        //System.out.print(s1+":  ");
                        for (String s2 : sol.terminal) {
                            String resss = sol.AnaTable.get(s1+s2);
                            if(resss == null){
                                //System.out.print("      ");
                                map[Grammap.get(s1)][endGmap.get(s2)] = " ";
                            }
                            else {
                                //System.out.print(resss+"    ");
                                map[Grammap.get(s1)][endGmap.get(s2)] = resss;
                            }
                        }
                        //System.out.println();
                    }
                    //System.out.println("====================");



//                    for (String[] strings : map) {
//                        for (String string : strings) {
//                            System.out.print(string+" ");
//                        }
//                        System.out.println();
//                    }


                    String [] colum = new String[sol.noTerminal.size()+1];
                    int counter = 0;
                    for (String s : sol.noTerminal) {
                        colum[counter] = s;
                        counter++;
                    }
                    String[][] data = new String[map.length][map[0].length+1];
                    for (int j = 0; j < data.length; j++ ) {
                        data[j][0] = colum[j];
                        for (int i = 1; i < data[0].length; i++) {
                            data[j][i] = map[j][i-1];
                        }
                    }
                    String[] name = new String[sol.terminal.size()+1];
                    int i = 1;
                    name[0] = " ";
                    for (String s : sol.terminal) {
                        name[i] = s;
                        i++;
                    }
                    JTable  mmm = new JTable(data, name);
                    mmm.setRowHeight(30);
                    JScrollPane secsa = new JScrollPane(mmm);
                    secsa.setBorder(null);
                    jTabbedPane.addTab("分析表",secsa);

                }




            }
        }


        JButton select;
        select = new JButton("secect集");
        select.setBounds(10,160,180,30);
        select.addActionListener(new selectListen());
        add(select);

        add(clear);
        add(confirm);
        add(setG);
        add(FF);
    }
    void initText(){
        textArea = new JTextArea("i+i*i#\ni*i*i#\ni*(i+i)#\ni^i#");
        JScrollPane textAreaRollPane = new JScrollPane(textArea);
        textAreaRollPane.setBounds(10,10,765,140);
        add(textAreaRollPane);
    }
    void initResult(){
        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 200, 765, 350);
        add(tabbedPane);
    }
    void addTable(String title,Vector vec ){
        TableDataModel tableDataModel = new TableDataModel(vec);
        JTable table = new JTable(tableDataModel);
        table.setVisible(true);
        table.setPreferredScrollableViewportSize(new Dimension(550, 100));
        table.setRowHeight(24);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBounds(10, 200, 765, 350);
        tabbedPane.addTab(title,tablePane);
    }
}

class TableDataModel extends AbstractTableModel {
    private Vector<String[]> TableData;//用来存放表格数据的线性表
    private Vector<String> TableTitle;//表格的 列标题
    public TableDataModel(Vector data){
        String Names[] = {"步骤","分析栈","剩余输入栈","所用产生式","动作"};
        Vector Namessss = new Vector();
        for(String str:Names){
            Namessss.add(str);
        }
        TableTitle = Namessss;
        TableData = data;
    }

    @Override
    public int getRowCount(){
        return TableData.size();
    }
    public int getColumnCount(){
        return TableTitle.size();
    }
    @Override
    public String getColumnName(int colum){
        return TableTitle.get(colum);
    }
    public Object getValueAt(int rowIndex, int columnIndex){
        String LineTemp[] = this.TableData.get(rowIndex);
        return LineTemp[columnIndex];
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){//不允许编辑
        return false;
    }
}