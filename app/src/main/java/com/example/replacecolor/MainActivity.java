package com.example.replacecolor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startBt;
    private String[] fileNames;
    private  static String InPath;;
    private final static String TAG = "MainActivity000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBt = (Button)findViewById(R.id.start_Bt);
        startBt.setOnClickListener(this);
        InPath = FileUtil.getAssetsCacheFile(this,"BTblack3.hwt");
        try {
            getRes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取资源
    public void getRes() throws IOException {
        Log.e(TAG,"here-------------------------");

        FileUtil.unzip(InPath,  InPath+"000");
        ArrayList<File> files = FileUtil.getNames(new File(InPath+"000"));
        Log.e(TAG,"number:"+files.size());

        for (int i=0; i<files.size() ; i++){
            //判断是否是压缩包
            if (!files.get(i).getName().endsWith("xml")) {
                Log.e(TAG, i + "  " + files.get(i).toString());
                File file = files.get(i);
                FileUtil.unzip(file.toString(), file.toString() + "000");
                //删除原有文件、新文件重新命名
                if(file.exists()){
                    file.delete();
                }
                File file1 = new File(file.toString()+"000");
                file1.renameTo(file);
            }
            else {
                Log.e(TAG, "I am a file");
            }
        }
        //开始搜索
        Log.e(TAG,"xml 数量为： "+FileUtil.getNamesByXml(new File(InPath+"000")).size());


        Log.e(TAG, "hwt-------------------------"+FileUtil.colorMap.size());
        int i =0 ;
        List<Map.Entry<String,Integer>> list = new ArrayList<>(FileUtil.colorMap.entrySet());
        //comparator比较器出现次数进行排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> t0, Map.Entry<String, Integer> t1) {
                //从大到小排序
                return t0.getValue() >= t1.getValue() ? -1 : 1;
            }
        });
        ArrayList<String> strs = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : list){
            strs.add(entry.getKey());
            Log.e(TAG,"我是 "+ entry.getKey()+"   我出现了" + entry.getKey()+" 次！");
        }
//        for (String s : FileUtil.colorMap.get(strs.get(0))){
//            Log.e(TAG,strs.get(0) +"对应的"+FileUtil.colorMap.get(strs.get(0)).size()+"个，现在是  "+s);
//        }
        FileUtil.replaceXml(InPath+"000",strs.get(0),"#888888");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_Bt:
                try {
                    getRes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
