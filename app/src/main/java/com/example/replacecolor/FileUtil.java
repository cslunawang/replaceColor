package com.example.replacecolor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {

        private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
        private static final String TAG = "FileUtil"; // 1M Byte
        public  static HashMap<String,Integer> colorMap = new HashMap<String, Integer>();
        public static ArrayList<File> names = new ArrayList<>();;

        /**
         * 批量压缩文件（夹）
         *
         * @param resFileList 要压缩的文件（夹）列表
         * @param zipFile 生成的压缩文件
         * @throws IOException 当压缩过程出错时抛出
         */
        public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
            ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
            for (File resFile : resFileList) {
                zipFile(resFile, zipout, "");
            }
            zipout.close();
        }

        /**
         * 批量压缩文件（夹）
         *
         * @param resFileList 要压缩的文件（夹）列表
         * @param zipFile 生成的压缩文件
         * @param comment 压缩文件的注释
         * @throws IOException 当压缩过程出错时抛出
         */
        public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
                throws IOException {
            ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                    zipFile), BUFF_SIZE));
            for (File resFile : resFileList) {
                zipFile(resFile, zipout, "");
            }
            zipout.setComment(comment);
            zipout.close();
        }

        public static String getAssetsCacheFile(Context context, String fileName)   {
            File cacheFile = new File(context.getCacheDir(), fileName);
            if (cacheFile.exists()){
                return cacheFile.getAbsolutePath();
            }
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                Log.e(TAG,inputStream.available()+" ");
                try {
                    FileOutputStream outputStream = new FileOutputStream(cacheFile);
                    try {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG,getSize(cacheFile)+" ");
            return cacheFile.getAbsolutePath();
        }

        /**
         * 获取大小
         * @param file
         * @return
         */
        public static String  getSize(File file)
        {
            double result=0;
            String unit="字节";
            long length = file.length();
            if(length<1024)
            {
                result= length;
            }
            else if(length<1024*1024)
            {
                result=length/1024.0;
                unit="KB";
            }
            else if(length<1024*1024*1024)
            {
                result=length/1024.0/1024;
                unit="MB";
            }
            else
            {
                result=length/1024.0/1024/1024;
                unit="GB";
            }

            BigDecimal bigDecimal=new BigDecimal(result+"",new MathContext(3));
            return bigDecimal.doubleValue()+unit;

        }




        /**
         * 解压缩一个文件
         *
         * @param zipFile 压缩文件
         * @param folderPath 解压缩的目标目录
         * @throws IOException 当解压缩过程出错时抛出
         */
        public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
            File desDir = new File(folderPath);
            if (!desDir.exists()) {
                desDir.mkdirs();
            }
            ZipFile zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                ZipEntry entry = ((ZipEntry)entries.nextElement());
                if (entry.isDirectory()) {

                    continue;
                }
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes(), "utf-8");
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
            }
        }

        /**
         * 解压文件名包含传入文字的文件
         *
         * @param zipFile 压缩文件
         * @param folderPath 目标文件夹
         * @param nameContains 传入的文件匹配名
         * @throws ZipException 压缩格式有误时抛出
         * @throws IOException IO错误时抛出
         */
        public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath,
                                                        String nameContains) throws ZipException, IOException {
            ArrayList<File> fileList = new ArrayList<File>();

            File desDir = new File(folderPath);
            if (!desDir.exists()) {
                desDir.mkdir();
            }

            ZipFile zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                ZipEntry entry = ((ZipEntry)entries.nextElement());
                if (entry.getName().contains(nameContains)) {
                    InputStream in = zf.getInputStream(entry);
                    String str = folderPath + File.separator + entry.getName();
                    str = new String(str.getBytes("utf-8"), "gbk");
                    // str.getBytes("GB2312"),"8859_1" 输出
                    // str.getBytes("8859_1"),"GB2312" 输入
                    File desFile = new File(str);
                    if (!desFile.exists()) {
                        File fileParentDir = desFile.getParentFile();
                        if (!fileParentDir.exists()) {
                            fileParentDir.mkdirs();
                        }
                        desFile.createNewFile();
                    }
                    OutputStream out = new FileOutputStream(desFile);
                    byte buffer[] = new byte[BUFF_SIZE];
                    int realLength;
                    while ((realLength = in.read(buffer)) > 0) {
                        out.write(buffer, 0, realLength);
                    }
                    in.close();
                    out.close();
                    fileList.add(desFile);
                }
            }
            return fileList;
        }

        /**
         * 获得压缩文件内文件列表
         *
         * @param zipFile 压缩文件
         * @return 压缩文件内文件名称
         * @throws ZipException 压缩文件格式有误时抛出
         * @throws IOException 当解压缩过程出错时抛出
         */
        public static ArrayList<String> getEntriesNames(File zipFile) throws ZipException, IOException {
            ArrayList<String> entryNames = new ArrayList<String>();
            Enumeration<?> entries = getEntriesEnumeration(zipFile);
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry)entries.nextElement());
                entryNames.add(new String(getEntryName(entry).getBytes("GB2312"), "8859_1"));
            }
            return entryNames;
        }

    /**
     * 获取普通文件列表
     * @param
     * @return
     * @throws ZipException
     * @throws IOException
     */
        public static ArrayList<File> getNames(File dic) throws ZipException, IOException {
            ArrayList<File> names = new ArrayList<>();
            if(!dic.exists()){
                Log.e(TAG,"目录不存在");
                return names;
            }
            File files[] = dic.listFiles();
            if (files != null && files.length>0){
                for (File file : files){
                    if (file.isDirectory()){
                        getNames(file);
                    }
                    else{
                        names.add(file);
                    }
                }
            }

            return names;
        }

    /**
     * 传入map修改
     * @param inPath
     * @throws ZipException
     * @throws IOException
     */
    public static void replaceXml(String inPath,HashMap<String,String> changeMap) throws ZipException, IOException {
        File dic = new File(inPath);
        if(!dic.exists()){
            Log.e(TAG,"目录不存在");
        }
        File files[] = dic.listFiles();
        Log.e(TAG,"list files数量为"+files.length);
        if (files != null && files.length>0){
            for (File file : files){
//                Log.e(TAG,"名称为"+file.getName());
//                Log.e(TAG,"file.getPath() 是： "+file.getPath());
                if (file.isDirectory()){
                    replaceXml(file.getPath(),changeMap);
                }
                else{
                    if (file.getName().endsWith("xml")) {
                        replaceColor(file.getPath(),inPath+"000",changeMap);
                    }
                }
            }
        }
    }

    /**
     * 传入单个颜色
     * @param inPath
     * @param inColor
     * @param outColor
     * @throws ZipException
     * @throws IOException
     */
    public static void replaceXml(String inPath,String inColor,String outColor) throws ZipException, IOException {
        File dic = new File(inPath);
        if(!dic.exists()){
            Log.e(TAG,"目录不存在");
        }
        File files[] = dic.listFiles();
        Log.e(TAG,"list files数量为"+files.length);
        if (files != null && files.length>0){
            for (File file : files){
//                Log.e(TAG,"名称为"+file.getName());
//                Log.e(TAG,"file.getPath() 是： "+file.getPath());
                    if (file.isDirectory()){
                    replaceXml(file.getPath(),inColor,outColor);
                }
                else{
                    if (file.getName().endsWith("xml")) {
                        replaceColor(file.getPath(),inPath+"000",inColor,outColor);
                    }
                }
            }
        }
    }

    /**
     * 替换多个
     * @param inPath
     * @param outPath
     * @param changeMap
     * @throws IOException
     */
    public static void replaceColor(String inPath,String outPath,HashMap<String,String> changeMap) throws IOException {
        File f = new File(inPath);
        File new_f = new File(outPath);
        BufferedReader in = new BufferedReader(new FileReader(f));
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new_f)));
        String line;
        while((line = in.readLine())!= null){
            for (Map.Entry<String,String> entry: changeMap.entrySet()){
               line = line.replace(entry.getKey(),entry.getValue());
            }
            out.println(line);

        }
        in.close();
        out.close();
        //删除原来、修改名字
        if (f.exists()){
            f.delete();
        }
        new_f.renameTo(f);
    }
    /**
     * 修改单个
     * @param inPath
     * @param outPath
     * @param inColor
     * @param outColor
     * @throws IOException
     */
        public static void replaceColor(String inPath,String outPath,String inColor,String outColor) throws IOException {
            File f = new File(inPath);
            File new_f = new File(outPath);
            BufferedReader in = new BufferedReader(new FileReader(f));
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new_f)));
            String line;
            while((line = in.readLine())!= null){
                    out.println(line.replace(inColor,outColor));

            }
            in.close();
            out.close();
            //删除原来、修改名字
            if (f.exists()){
                f.delete();
            }
            new_f.renameTo(f);
        }

    /**
     * 复制文件
     * @param inPath
     * @param outPath

     * @throws IOException
     */
    public static void copyFile(String inPath,String outPath) throws IOException {
        File f = new File(inPath);
        File new_f = new File(outPath);
        if (new_f.exists())
            new_f.delete();
        InputStream in = new FileInputStream(inPath);
        OutputStream out = new FileOutputStream(outPath);
//        BufferedReader in = new BufferedReader(new FileReader(f));
//        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new_f)));
//        String line;
//        while((line = in.readLine())!= null){
//            out.println(line);
//        }
        byte[] buf = new byte[1024];
        int len;
        while (0 < (len = in.read(buf))) {
            out.write(buf, 0, len);
        }
        Log.e(TAG,"源文件大小为   "+getSize(f)+"。。。。。新文件大小为："+getSize(new_f));
        in.close();
        out.close();
    }

    /**
     * 寻找xml文件、统计输出
     * @param dic
     * @return
     * @throws ZipException
     * @throws IOException
     */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public static ArrayList<File> getNamesByXml(File dic) throws ZipException, IOException {
            if(!dic.exists()){
                Log.e(TAG,"目录不存在");
                return null;
            }
            File files[] = dic.listFiles();
            if (files != null && files.length>0){
                for (File file : files){
                    if (file.isDirectory()){
                        getNamesByXml(file);
                    }
                    else{
                        if (file.getName().endsWith("xml")) {
                            names.add(file);
//                            Log.e(TAG,"xml文件来啦"+file.toString());
                            putNamesInMap(file);
                        }
                    }
                }
            }
            return names;
        }

    /**
     * 读文件、搜集color的 <颜色 ， 位置arrayList>
     * @param file
     * @return
     * @throws ZipException
     * @throws IOException
     */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public static HashMap<String, Integer> putNamesInMap(File file) throws ZipException, IOException {
            ArrayList<String> names = new ArrayList<>();
            //读文件
            try {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while ((line = br.readLine())!=null){
//                    Log.e(TAG,"我读到文件的一行了");
                    if (line.contains("</color>")){
                        // 获取color
//                        Log.e(TAG,line);
                        String color = TextUtil.getColor(line);
                        String tag = TextUtil.getTag(line);
//                        Log.e(TAG,"COLOR" + "-----------TAG" +color + "----"+tag);
                        colorMap.put(color,colorMap.getOrDefault(color,0)+1);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return colorMap;
        }

        /**
         * 获得压缩文件内压缩文件对象以取得其属性
         *
         * @param zipFile 压缩文件
         * @return 返回一个压缩文件列表
         * @throws ZipException 压缩文件格式有误时抛出
         * @throws IOException IO操作有误时抛出
         */
        public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException,
                IOException {
            ZipFile zf = new ZipFile(zipFile);
            return zf.entries();

        }

        /**
         * 取得压缩文件对象的注释
         *
         * @param entry 压缩文件对象
         * @return 压缩文件对象的注释
         * @throws UnsupportedEncodingException
         */
        public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
            return new String(entry.getComment().getBytes("GB2312"), "8859_1");
        }

        /**
         * 取得压缩文件对象的名称
         *
         * @param entry 压缩文件对象
         * @return 压缩文件对象的名称
         * @throws UnsupportedEncodingException
         */
        public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
            return new String(entry.getName().getBytes("GB2312"), "8859_1");
        }

        /**
         * 压缩文件
         *
         * @param resFile 需要压缩的文件（夹）
         * @param zipout 压缩的目的文件
         * @param rootpath 压缩的文件路径
         * @throws FileNotFoundException 找不到文件时抛出
         * @throws IOException 当压缩过程出错时抛出
         */
        private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
                throws FileNotFoundException, IOException {
            rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                    + resFile.getName();
            rootpath = new String(rootpath.getBytes(), "utf-8");
            if (resFile.isDirectory()) {
                File[] fileList = resFile.listFiles();
                for (File file : fileList) {
                    zipFile(file, zipout, rootpath);
                }
            } else {
                byte buffer[] = new byte[BUFF_SIZE];
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                        BUFF_SIZE);
                zipout.putNextEntry(new ZipEntry(rootpath));
                int realLength;
                while ((realLength = in.read(buffer)) != -1) {
                    zipout.write(buffer, 0, realLength);
                }
                in.close();
                zipout.flush();
                zipout.closeEntry();
            }
        }
        //压缩内部文件
        public static void zipInter(String src, String dest) throws IOException {
            zip(src, dest+"000");
            File sf = new File(dest+"000");
            File df = new File(dest);
            if (df.isDirectory()){
                deleteDic(df);
            }
            sf.renameTo(new File(dest));
        }

        //第二种实现
        public static void zip(String src, String dest) throws IOException {
            // 提供了一个数据项压缩成一个ZIP归档输出流
            ZipOutputStream out = null;
            try {

                //DirTraversal.makeRootDirectory(dest);
                //File outFile = DirTraversal.getFilePath(dest,"cache.zip");

                File outFile = new File(dest);// 源文件或者目录
                File fileOrDirectory = new File(src);// 压缩文件路径
                out = new ZipOutputStream(new FileOutputStream(outFile));
                // 如果此文件是一个文件，否则为false。
                if (fileOrDirectory.isFile()) {
                    zipFileOrDirectory(out, fileOrDirectory, "");
                } else {
                    // 返回一个文件或空阵列。
                    File[] entries = fileOrDirectory.listFiles();
                    for (int i = 0; i < entries.length; i++) {
                        // 递归压缩，更新curPaths
                        zipFileOrDirectory(out, entries[i], "");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // 关闭输出流
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        public static void zip(String src, String dest, MainActivity.ChangeColorItem changeColorItem) throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
            // 提供了一个数据项压缩成一个ZIP归档输出流
            ZipOutputStream out = null;
            try {

                //DirTraversal.makeRootDirectory(dest);
                //File outFile = DirTraversal.getFilePath(dest,"cache.zip");

                File outFile = new File(dest);// 源文件或者目录
                File fileOrDirectory = new File(src);// 压缩文件路径
                out = new ZipOutputStream(new FileOutputStream(outFile));
                // 如果此文件是一个文件，否则为false。
                if (fileOrDirectory.isFile()) {
                    zipFileOrDirectory(out, fileOrDirectory, "");
                } else {
                    // 返回一个文件或空阵列。
                    File[] entries = fileOrDirectory.listFiles();
                    for (int i = 0; i < entries.length; i++) {
                        // 递归压缩，更新curPaths
                        zipFileOrDirectory(out, entries[i], "");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // 关闭输出流
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            changeColorItem.change();
        }

        private static void zipFileOrDirectory(ZipOutputStream out,
                                               File fileOrDirectory, String curPath) throws IOException {
            // 从文件中读取字节的输入流
            FileInputStream in = null;
            try {
                // 如果此文件是一个目录，否则返回false。
                if (!fileOrDirectory.isDirectory()) {
                    // 压缩文件
                    byte[] buffer = new byte[4096];
                    int bytes_read;
                    in = new FileInputStream(fileOrDirectory);
                    // 实例代表一个条目内的ZIP归档
                    ZipEntry entry = new ZipEntry(curPath
                            + fileOrDirectory.getName());
                    // 条目的信息写入底层流
                    out.putNextEntry(entry);
                    while ((bytes_read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytes_read);
                    }
                    out.closeEntry();
                } else {
                    // 压缩目录
                    File[] entries = fileOrDirectory.listFiles();
                    for (int i = 0; i < entries.length; i++) {
                        // 递归压缩，更新curPaths
                        zipFileOrDirectory(out, entries[i], curPath
                                + fileOrDirectory.getName() + "/");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // throw ex;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        public static void deleteDic(File dic){
            File[] files = dic.listFiles();
            for (File file : files){
                if (file.isDirectory()){
                    deleteDic(file);
                }
                else {
                    file.delete();
                }
            }
            dic.delete();
        }

        @SuppressWarnings("unchecked")
        public static void unzip(String zipFileName, String outputDirectory)
                throws IOException {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(zipFileName);
                Enumeration e = zipFile.entries();
                ZipEntry zipEntry = null;
                File dest = new File(outputDirectory);
                if (dest.exists()){
                    deleteDic(dest);
                }
                dest.mkdirs();
                while (e.hasMoreElements()) {
                    zipEntry = (ZipEntry) e.nextElement();
                    String entryName = zipEntry.getName();
                    InputStream in = null;
                    FileOutputStream out = null;
                    try {
                        if (zipEntry.isDirectory()) {
                            String name = zipEntry.getName();
                            name = name.substring(0, name.length() - 1);
                            File f = new File(outputDirectory + File.separator
                                    + name);
                            f.mkdirs();
                        } else {
                            int index = entryName.lastIndexOf("\\");
                            if (index != -1) {
                                File df = new File(outputDirectory + File.separator
                                        + entryName.substring(0, index));
                                df.mkdirs();
                            }
                            index = entryName.lastIndexOf("/");
                            if (index != -1) {
                                File df = new File(outputDirectory + File.separator
                                        + entryName.substring(0, index));
                                df.mkdirs();
                            }
                            File f = new File(outputDirectory + File.separator
                                    + zipEntry.getName());
                            // f.createNewFile();
                            in = zipFile.getInputStream(zipEntry);
                            out = new FileOutputStream(f);
                            int c;
                            byte[] by = new byte[1024];
                            while ((c = in.read(by)) != -1) {
                                out.write(by, 0, c);
                            }
                            out.flush();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        throw new IOException("解压失败：" + ex.toString());
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException ex) {
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new IOException("解压失败：" + ex.toString());
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
}

