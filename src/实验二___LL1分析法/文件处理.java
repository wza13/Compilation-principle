package 实验二___LL1分析法;

import java.awt.image.CropImageFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class 文件处理 {
    public static void main(String[] args) {
//        zipComic2PDF conven = new zipComic2PDF();
//        String[] imges = {"D:\\学习资料\\Girls' Frontline\\[윤우연 (ooyun)] 如何使用娃娃 - How to use dolls 05 (少女前線) [中国翻訳]\\001.jpg",
//                "D:\\学习资料\\Girls' Frontline\\[윤우연 (ooyun)] 如何使用娃娃 - How to use dolls 05 (少女前線) [中国翻訳]\\002.PNG",
//                "D:\\学习资料\\Girls' Frontline\\[윤우연 (ooyun)] 如何使用娃娃 - How to use dolls 05 (少女前線) [中国翻訳]\\00000023.jpg",
//                "D:\\学习资料\\Girls' Frontline\\[윤우연 (ooyun)] 如何使用娃娃 - How to use dolls 05 (少女前線) [中国翻訳]\\014.png"
//        };
//        conven.toPDF(imges,"D:\\学习资料\\PDF\\","test");
    }

    public static void toKind(String[] args) {
        String directory;

        //directory = "D:\\Documents\\Tencent Files\\1404935712\\FileRecv\\MobileFile\\[ぽち小屋。 (ぽち。)] 姉なるもの";
        directory = "I:\\Eh9.26\\EhViewer\\download";
        File file = new File(directory);
        String[] list = file.list();
        for (String filepath:list){
            File f = new File(directory+"\\"+filepath);
            if(f.isFile()){
                Boolean archive = false;
                String name = filepath.substring(filepath.lastIndexOf("\\")+1);
                for(int i = 70 ; i < 99 ; i++){
                    if(  name.contains("C"+i) || name.contains("c"+i)  ){
                        if(i>=80){
                            if(! new File(directory+"\\C"+i+"\\").exists()){
                                new File(directory+"\\C"+i+"\\").mkdir();
                            }
                            f.renameTo(new File(directory+"\\"+"C"+i+"\\"+name));
                            System.out.println("归档至"+"C"+i+" : "+name);
                            archive = true;
                        }
                        else {
                            if(! new File(directory+"\\C"+i/10+"n\\").exists()){
                                new File(directory+"\\C"+i/10+"n\\").mkdir();
                            }
                            f.renameTo(new File(directory+"\\"+"C"+i/10+"n\\"+name));
                            System.out.println("归档至"+"C"+i/10+"n : "+name);
                            archive = true;
                        }
                    }
                }

                if(!archive){
                    System.out.println("未归档 : "+name);
                    if(! new File(directory+"\\没有标签\\").exists()){
                        new File(directory+"\\没有标签\\").mkdir();
                    }
                    f.renameTo(new File(directory+"\\没有标签\\"+name));
                }
            }
        }
    }
}


//
//class zipComic2PDF{
//    void toPDF(String[] images,String path,String name){//图片路径s 输出路径 保存文件名
//
//        try{
//
//            float standWidth = Image.getInstance(images[0]).matrix()[6];//标准宽度
//            float standHeight = Image.getInstance(images[0]).matrix()[7];
//            Document document = new Document(new Rectangle(standWidth,standHeight));
//            File file = new File(path + name+".pdf");
//            if(file.exists()){
//                file.createNewFile();
//            }
//            PdfWriter.getInstance(document,new FileOutputStream(file));
//            document.open();
//            for(String imagePath:images){
//                Image image = Image.getInstance(imagePath);
//                float width = image.matrix()[6],height = image.matrix()[7];
//                float cmp = (width/height) / (standHeight/standWidth) - 1;
//                System.out.println("cmp="+cmp);
//                if( cmp < 0.1 || cmp > -0.1 ){//是两个页面拼接
//                    image.setRotation(90);
//                }
//                System.out.println(standWidth+" , " + (standWidth/image.matrix()[6])*image.matrix()[7]);
//                document.newPage();
//                image.setAbsolutePosition(0,standHeight-height*(standWidth/width));
//                image.scaleToFit(standWidth,standHeight);
//                document.add(image);
//            }
//
//
//
//            document.close();
//
//        }
//        catch (Exception e){
//            System.out.println(e.toString());
//        }
//    }
//
//}

