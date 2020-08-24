package 实验三_LR1分析法;

public class test {
    public static void main(String[] args) {


        Solution sol = new Solution("S`->E\nE->E+T | T\nT->T*F | F\nF->P↑F | P\nP->(E) | i\n");



        for (String[] strings : sol.analyse("(i*i)+i")) {
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println();
        }





    }
}
