package 实验一___词法分析设计;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

class Windows extends JFrame{
    JMenuBar bar;
    JMenu menu;
    JMenuItem file;
    JMenuItem manuallySet;
    JMenuItem exit;
    JTextArea TA;
    JButton clear;
    JButton analyse;
    String[] text;
    JTable table;
    Vector<String[]> vecRes = new Vector<>();
    TableDataModel tableDataModel;
    JScrollPane restablescrollPane;
    Solution sl = new Solution();
    public Windows(){
        try{
            setIconImage(new ImageIcon("bilibili.PNG").getImage());
            Font f = new Font("Yahei Consolas Hybrid",Font.PLAIN,16);
            String   names[]={ "MenuBar","Menu","MenuItem", "TextArea", "Button", "ScrollPane", "Table"};
            for (String item : names) {
                UIManager.put(item+ ".font",f);
            }
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch(Exception e){}
        init();

        setSize(600,800);//初始大小
        setLocation(640,100);//初始位置
        setVisible(true);//是否可视
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//X退出
    }
    public void init(){
        setTitle("词法分析器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLayout(null);
        setBounds(10, 10, 300, 400);
        initMenu();//初始化菜单
        initTextArea();//初始化输入文本
        initButton();//初始化按钮
        initResultTable();//初始化结果区域
    }
    private void initMenu(){
        class fileListen implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e){
                JFileChooser fileChooser = new JFileChooser("D:\\工作\\programs");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showOpenDialog(null);
                File file = fileChooser.getSelectedFile();
                if(file!=null){
                    try{
                        BufferedReader read = new BufferedReader(new FileReader( file ));
                        Object[] lines = read.lines().toArray();
                        StringBuffer bufferTA = new StringBuffer();
                        for(Object line:lines){
                            bufferTA.append(line.toString()+"\n");
                        }
                        TA.setText(bufferTA.toString());
                    }
                    catch (IOException err){
                    }
                }
            }
        }
        class manuallySet implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String [] k ={};
                String [] p ={};
                String kString,pString;
                Boolean changed = true;
                do{
                    kString = JOptionPane.showInputDialog(null,"请输入K[ ]：\n","自定义K,P",JOptionPane.PLAIN_MESSAGE);
                    if(kString==null){
                        changed = false;
                        break;
                    }
                }while (kString.length()<2);
                if(changed){
                    boolean allchanged = true;
                    do{
                        pString = JOptionPane.showInputDialog(null,"请输入P[ ]：\n","自定义K,P",JOptionPane.PLAIN_MESSAGE);
                        if(pString == null){
                            allchanged = false;
                            break;
                        }
                    }while (pString.length()<2);
                    if(allchanged){
                        kString = kString.substring(1,kString.length()-1);
                        pString = pString.substring(1,pString.length()-1);
                        k = kString.split(" ");
                        p = pString.split(" ");
                        if(k.length<2 || p.length<2){//如果输入不规范 警告 不修改kp
                            JOptionPane.showMessageDialog(null, "格式输入错误", "Error !", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                            sl.manullySetKP(k,p);
                    }
                }
            }
        }
        class exitListen implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }

        bar = new JMenuBar();
        setJMenuBar(bar);

        menu = new JMenu("选项");
        bar.add(menu);

        file = new JMenuItem("选择文件");
        file.addActionListener(new fileListen());//读取文件到TA里

        manuallySet = new JMenuItem("手动设定");
        manuallySet.addActionListener(new manuallySet());

        exit = new JMenuItem("退出");
        exit.addActionListener(new exitListen());

        menu.add(file);
        menu.add(manuallySet);
        menu.add(exit);
    }
    private void initTextArea(){
        TA = new JTextArea();
        JScrollPane SP = new JScrollPane(TA);
        TA.setLineWrap(true); // 设置自动换行
        SP.setBounds(10, 10, 565, 300);
        add(SP);
    }
    private void initButton(){
        class clearListen implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e){
                TA.setText("");
                vecRes.clear();
                table.validate();
                table.updateUI();
                restablescrollPane.updateUI();
            }
        }
        class analyseListen implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e){
                text = TA.getText().split("\n");//这样分割后的String没有\n
                //for(String str:text) System.out.println(str);
                vecRes.clear();
                List<Result> resS = sl.Solve(text);
                for(Result result:resS){
                    vecRes.add(result.toStringArrary());
                }
                //vecRes.forEach(Strings -> {for(String str:Strings) System.out.print(str+" ");System.out.println();});
                table.validate();
                table.updateUI();
                restablescrollPane.updateUI();
            }
        }
        clear = new JButton("清空");
        clear.addActionListener(new clearListen());

        analyse = new JButton("分析");
        analyse.addActionListener(new analyseListen());

        clear.setBounds(400,320,70,35);
        analyse.setBounds(500,320,70,35);
        add(clear);
        add(analyse);
    }
    private void initResultTable(){
        tableDataModel = new TableDataModel(vecRes);
        table = new JTable(tableDataModel);
        table.setVisible(true);
        table.setPreferredScrollableViewportSize(new Dimension(550, 100));
        table.setRowHeight(24);
        restablescrollPane = new JScrollPane(table);
        restablescrollPane.setBounds(10, 367, 565, 363);
        add(restablescrollPane);
        pack();
    }
}

class TableDataModel extends AbstractTableModel{
    private Vector<String[]> TableData;//用来存放表格数据的线性表
    private Vector<String> TableTitle;//表格的 列标题
    public TableDataModel(Vector data){
        String Names[] = {"单词","二元序列","类 型","位置（行，列）"};
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
