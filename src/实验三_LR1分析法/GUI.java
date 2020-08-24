package 实验三_LR1分析法;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.*;

class Windows extends JFrame {
    Solution sol;
    JButton clear,confirm;
    JTextArea grammarTextArea,inputTextArea,projectArea;
    JScrollPane tablePane;
    JTabbedPane resultTable;
    JScrollPane projectList;

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
        sol = new Solution("S`->E\n"+
                "E->E+T\n" +
                "E->T\n" +
                "T->T*F\n" +
                "T->F\n" +
                "F->(E)\n" +
                "F->i");
        init();
        setSize(1280,720);//初始大小
        setLocation(100,80);//初始位置
        setVisible(true);//是否可视
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//X退出
    }
    void init(){
        setTitle("LR(1)分析法");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLayout(null);
        initGrammarText();
        initActTable();
        initButton();
        initInputArea();
        initResultTable();
        initProjectPane();
    }
    void initGrammarText(){
        grammarTextArea = new JTextArea("当前使用文法:\n"+sol.grammarText);
        grammarTextArea.setEditable(false);
        JScrollPane textAreaRollPane = new JScrollPane(grammarTextArea);
        textAreaRollPane.setBounds(10,10,150,200);
        add(textAreaRollPane);
    }
    private void initActTable(){
        tablePane = new JScrollPane();
        tablePane.setBounds(10,220,500,450);
        add(tablePane);
        updateActTable();
    }
    private void initButton(){
        clear = new JButton("清空");
        clear.setBounds(1120,100,110,50);
        add(clear);
        clear.addActionListener(actionEvent -> {
            inputTextArea.setText("");
            resultTable.removeAll();
        });
        confirm = new JButton("确认");
        confirm.setBounds(1120,160,110,50);
        add(confirm);
        confirm.addActionListener(actionEvent -> {
            resultTable.removeAll();
            for (String inputText : inputTextArea.getText().split("\n")) {
                Vector<String[]> result =  sol.analyse(inputText);
                String[] head = {"步骤 ","状态栈"," 符号栈 ","输入串 ","动作说明 "};
                String[][] data = new String[result.size()][5];
                int i = 0;
                for (String[] strings : result) {
                    data[i] = strings;
                    i++;
                }
                JTable singleResult = new JTable(data,head);
                FitTableColumns(singleResult);
                singleResult.setRowHeight(30);
                JScrollPane resultTablePane = new JScrollPane(singleResult);
                resultTable.addTab("  "+inputText+"     ",resultTablePane);
            }
        });
        JButton G1 = new JButton("G1");
        G1.setBounds(410,10,100,35);
        add(G1);
        G1.addActionListener(actionEvent -> {
            updateGrammar("S`->E\nE->E+T\nE->T\nT->T*F\nT->F\nF->(E)\nF->i");
        });
        JButton G2 = new JButton("G2");
        G2.setBounds(410,65,100,35);
        add(G2);
        G2.addActionListener(actionEvent -> {
            updateGrammar("S`->E\nE->E+T | T\nT->T*F | F\nF->P↑F | P\nP->(E) | i\n");
        });
        JButton G3 = new JButton("G3");
        G3.setBounds(410,120,100,35);
        add(G3);
        G3.addActionListener(actionEvent -> {
            updateGrammar("S`->S\nS->aAd\nS->bAc\nS->aec\nS->bed\nA->e");
        });
        JButton more = new JButton("手动输入");
        more.setBounds(410,175,100,35);
        add(more);
        class MyDialog extends JDialog implements ActionListener{
            JTextArea input;
            JButton confirm,cancel;
            String title;
            MyDialog(){
                setLayout(null);
                setResizable(false);
                setIconImage(new ImageIcon("bilibili.PNG").getImage());
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("输入语法");
                input=new JTextArea();
                JScrollPane jScrollPane = new JScrollPane(input);
                jScrollPane.setBounds(10,10,265,200);
                add(jScrollPane);
                class confirmListener implements ActionListener{
                    @Override
                    public void actionPerformed(ActionEvent e){
                        updateGrammar(input.getText());
                        setVisible(false);
                    }
                }
                confirm=new JButton("确定");
                confirm.addActionListener(new confirmListener());
                confirm.setBounds(195,220,80,30);
                add(confirm);
                class cancelListener implements ActionListener{
                    @Override
                    public void actionPerformed(ActionEvent e){
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
        more.addActionListener(new MyDialog());
    }
    private void initInputArea(){
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        JScrollPane inputAreaPane = new JScrollPane(inputTextArea);
        inputAreaPane.setBounds(520,10,580,200);
        add(inputAreaPane);
    }
    private void initResultTable(){
        resultTable = new JTabbedPane();
        resultTable.setBounds(520,220,735,450);
        add(resultTable);
    }
    private void initProjectPane(){
        projectArea = new JTextArea();
        projectList = new JScrollPane(projectArea);
        projectList.setBounds(170,10,230,200);
        add(projectList);
        updateProjectPane();

    }
    private void updateProjectPane(){
        StringBuffer sb = new StringBuffer();
        int count = 0;
        for (Solution.projectSet projectSet : sol.cProjectSets) {
            sb.append( (count++ )+" : ");
            for (Solution.project project : projectSet.getSet()) {
                sb.append(project+" ");
            }
            sb.append("\n");
        }
        count = 0;
        for (Solution.projectSet projectSet : sol.cProjectSets) {
            sb.append( (count++ )+" : ");
            for (String s : sol.allSymbol) {
                int index = projectSet.sons.get(s);
                if(index !=-1){
                    sb.append(s+"->"+index);
                }
            }
            sb.append("\n");
        }
        projectArea.setText(sb.toString());
        projectList.updateUI();
    }
    private void updateActTable(){
        String[] head =sol.getHeader();
        String [][] data = sol.getActionAndGoTo();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(data[i][j].equals("err")){
                    data[i][j] = "";
                }
            }
        }
        JTable actGoTable;
        actGoTable = new JTable(data,head);
        actGoTable.setRowHeight(30);
        tablePane.setViewportView(actGoTable);
        tablePane.updateUI();
    }//更新ActGo表
    public void updateGrammar(String s){
        sol = new Solution(s);
        grammarTextArea.setText("当前使用文法:\n"+sol.grammarText);
        updateActTable();
        updateProjectPane();
    }//更新语法
    public void FitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier()
                            , false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,
                        myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + myTable.getIntercellSpacing().width+10);
        }
    }
}