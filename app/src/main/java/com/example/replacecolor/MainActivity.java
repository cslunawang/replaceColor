package com.example.replacecolor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startBt;
    private String[] fileNames;
    private  static String InPath;;
    private final static String TAG = "MainActivity000";
    private static final String OUT_DIR = "/Huawei/Themes/";
    private static final String OutPath = Environment.getExternalStorageDirectory().getAbsolutePath() + OUT_DIR;
//    private static final String OutPath_new = Environment.getDataDirectory().getAbsolutePath() + OUT_DIR;
    private static final String OPEN_THEME = "com.huawei.android.thememanager";
    private ArrayList<String> strs;
    private ColorAdapter adapter;
    private ListView listView;
    private TextView numberTx;
    private Button saveBt;
    public  static HashMap<String,String> map = new HashMap<String, String>();
    private List<Map.Entry<String,Integer>> list;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        numberTx = findViewById(R.id.number_all);
        saveBt = findViewById(R.id.save_bt);
        saveBt.setOnClickListener(this);
//        InPath = FileUtil.getAssetsCacheFile(this,"BTblack3.hwt");
        InPath = getIntent().getStringExtra("filePath");
        Log.e(TAG,"获取到的路径是--------------------" + InPath);

        try {
            getRes();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        寻求权限
//        try {
//            requestPermission();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        }
    }


    //获取资源
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getRes() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
        Log.e(TAG,"here正在解压缩啦啦啦-------------------------");
        //复制assets文件
//        FileUtil.copyFile(InPath,OutPath + (new File(InPath)).getName());
        //解压缩 到000
        FileUtil.unzip(InPath,  InPath+"000");
        if (!(new File(InPath+"000")).exists()){
            Toast.makeText(this,"抱歉，您当前文件权限太低，不支持解压缩",Toast.LENGTH_SHORT);
            //如果无法解压缩，结束当前activity
            finish();
        }
        //打印文件信息
        ArrayList<File> files = FileUtil.getNames(new File(InPath+"000"));
        Log.e(TAG,"一共有number:"+files.size());
        //解压缩内部文件
        for (int i=0; i<files.size() ; i++){
            //判断是否是压缩包
            if (!files.get(i).getName().endsWith("xml")) {
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
        //开始根据str搜索 color值
        Log.e(TAG,"xml 数量为： "+FileUtil.getNamesByXml(new File(InPath+"000")).size());
        Log.e(TAG, "hwt-------------------------"+FileUtil.colorMap.size());
        int i =0 ;
        list = new ArrayList<>(FileUtil.colorMap.entrySet());
        //统计：comparator比较器出现次数进行排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> t0, Map.Entry<String, Integer> t1) {
                //从大到小排序
                return t0.getValue() >= t1.getValue() ? -1 : 1;
            }
        });
        //获取并存储 key值
        strs = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : list){
            strs.add(entry.getKey());
//            Log.e(TAG,"我是 "+ entry.getKey()+"   我出现了" + entry.getKey()+" 次！");
        }
        //根据key值排名 前5进行索引
        for (int j=0 ; j<5 ; j++){
            Log.e(TAG,"我是 "+ strs.get(j) + "  ，我出现了 " + FileUtil.colorMap.get(strs.get(j)) + "次");
        }
        //显示数量
        numberTx.setText(list.size()+"");
        Log.e(TAG,"list.size()  "+ list.size());
        //adapter
        adapter = new ColorAdapter(this,list,map);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int i, long id) {
                //弹出弹窗、输入修改的颜色值
                //存储到map、显示在原界面
                //i是第几个
                Log.e(TAG,"dialog传入的颜色是 ------------------- "+list.get(i).getKey());
                dialog(list.get(i).getKey(), new ChangeColorItem() {
                    @Override
                    public void change() {
                        //修改原界面的颜色值
                        Log.e(TAG,oldStr + "---change颜色---------------" +newStr);
                        TextView newBg = view.findViewById(R.id.new_bg);
                        TextView newTx = view.findViewById(R.id.new_text);
                        //在这里做 adapter重合的操作，会被listview刷新替代
//                        newBg.setBackgroundColor(Color.parseColor(newStr));
                        newTx.setText("#000000");
                        //直接修改map 中的 adapter
                        adapter.setMap(map);
                        Log.e(TAG,"MAP"+map.size());
                        adapter.notifyDataSetChanged();
                        newTx.setTextColor(Color.RED);
                    }
                });
            }
        });
    }


    private String oldStr;
    private String newStr;
    public interface ChangeColorItem{
        void change() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException;
    }

    /**
     * 弹窗：传入接口、、更新界面
     * @param oldColor 旧颜色
     */
    public void dialog(final String oldColor, final ChangeColorItem changeColorItem){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //标题
        dialog.setTitle("修改颜色");
        //提示信息
        dialog.setMessage("原来的颜色为"+oldColor);
        //首先添加一个edit实例，然后通过dialog设置显示
        final EditText editText = new EditText(MainActivity.this);//键入新的颜色值
        //一行
        editText.setSingleLine();
        //设置默认提示
        editText.setHint("请输入新颜色，记得以 # 开头噢！");
        //设置显示
        dialog.setView(editText);
        //设置按钮对应操作
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //newColor重新赋值
                newStr = editText.getText().toString();
                map.put(oldColor,newStr);
                try {
                    changeColorItem.change();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.create().show();
    }
    /**
     * 弹窗：传入接口、、更新界面
     */
    public void dialogOnlyInfo(final HashMap<String,String> hashMap, final ChangeColorItem changeColorItem){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //标题
        dialog.setTitle("确认修改");
        String s = "您一共要修改"+ hashMap.size() + "条颜色值\n";
        for (Map.Entry<String,String> entry : hashMap.entrySet()){
            s += entry.getKey() + " 修改为 " + entry.getValue() + "\n";
        }
        //提示信息
        dialog.setMessage(s);
        //设置按钮对应操作
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //提交修改
                Toast.makeText(MainActivity.this,"正在进行修改和压缩处理，请耐心等待！修改后 即将打开主题商店",Toast.LENGTH_LONG);
                try {
                    changeColorItem.change();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.create().show();
    }

    /**
     * 替换颜色
     */
    public void replaceColorMul(final HashMap<String,String> changeMap) throws IOException{
        //替换颜色
//        FileUtil.replaceXml(InPath+"000",strs.get(0),"#ff0000");
        Log.e(TAG,"正在替换颜色------------------------共修改了"+changeMap.size()+"条item");
        dialogOnlyInfo(map, new ChangeColorItem() {
            @Override
            public void change() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
                //执行替换操作
                FileUtil.replaceXml(InPath+"000",changeMap);
                //试试单个修改：可是为什么还是不行呢？之前明明已经成功了啊呜呜呜
//                ArrayList<String> keys = new ArrayList();
//                for (Map.Entry<String,String> mapEntry: changeMap.entrySet()){
//                    keys.add(mapEntry.getKey());
//                }
//                Log.e(TAG,keys.get(0)+"-------------------"+changeMap.get(keys.get(0)));
//                FileUtil.replaceXml(InPath+"000",keys.get(0),changeMap.get(keys.get(0)));
                Log.e(TAG,"恭喜您，替换成功！");
                //解压缩 并 打开主题商店
                rar();
            }
        });

    }

    /**
     * 压缩函数
     */
    public void rar()throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
        //开始压缩
        Log.e(TAG,"压缩------------------------------------------------");
        String RealOutPath = OutPath + (new File(InPath)).getName().replace(".hwt","_new.hwt");
        //内部文件压缩
        File[] fileList = (new File(InPath+"000")).listFiles();
        for (File f : fileList){
            if (!f.getName().equals("wallpaper") && !f.getName().equals("preview") &&
                    !f.getName().equals("fonts") && !f.getName().equals("unlock")   && !f.getName().equals("description.xml") ) {
                Log.e(TAG,f.getName());
                FileUtil.zipInter(f.getAbsolutePath(), f.getAbsolutePath());
            }
        }
        //总文件压缩
        FileUtil.zip(InPath + "000", RealOutPath, new ChangeColorItem() {
            @Override
            public void change() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
                //打开主题商店
                try {
                    openApp(OPEN_THEME);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        //把000删除了
        File[] files = (new File(OutPath)).listFiles();
        for (File file: files){
            if (file.getName().endsWith("000")){
                FileUtil.deleteDic(file);
            }
        }
//        FileUtil.copyFile(InPath,OutPath + (new File(InPath)).getName());
//        FileUtil.copyFile(InPath,RealOutPath_new);
        Log.e(TAG,"成功压缩哈哈哈------outpath: "+ RealOutPath + "-----------------------------------------");
    }

    /**
     * 打开主题 商店
     * @param packageName
     * @throws PackageManager.NameNotFoundException
     * @throws ClassNotFoundException
     */
    public void openApp(String packageName) throws PackageManager.NameNotFoundException, ClassNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        for (ResolveInfo info : apps) {
            Log.e("infoName", info.activityInfo.name);
        }
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String pg = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(pg, className);
            Log.e(TAG, className);
            intent.setComponent(cn);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_bt:
                //对map中的进行修改
                Log.e(TAG,"已经点击按钮，正在修改-------------------"+map.size()+map.get(0)+map.get(0));
                try {
                    replaceColorMul(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.start_Bt:
//                //选择文件
//                chooseFile(FILE_MANAGER,this);
////                try {
////                    getRes();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                } catch (PackageManager.NameNotFoundException e) {
////                    e.printStackTrace();
////                } catch (ClassNotFoundException e) {
////                    e.printStackTrace();
////                }
//                break;
//        }
//    }
}
