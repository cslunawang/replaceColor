package com.example.replacecolor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private static final String FILE_MANAGER = OutPath;
    private static final int FILE_SELECT_CODE = 0;
    private ArrayList<String> strs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBt = (Button)findViewById(R.id.start_Bt);
        startBt.setOnClickListener(this);
        InPath = FileUtil.getAssetsCacheFile(this,"BTblack3.hwt");
//        try {
//            getRes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
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

    /**
     * 选择文件
     * @param str
     */
    public void chooseFile(String str,Context context){
        File file = new File(str);
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setDataAndType(Uri.fromFile(file),"*/*");
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(context,"亲，木有文件管理器啊-_-!!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取文件信息
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            uri = data.getData();
            Log.e(TAG, "---------------------->" + uri.getPath().toString()+"----------"+getRealPathFromURI(uri));
            InPath = getRealPathFromURI(uri);
            //判断是否是hwt文件、提示正在解析、或者无法操作
            File file = new File(InPath);
            if (file.getName().endsWith("hwt")){
                //开始解析
                Toast.makeText(this, "您选择了" + file.getName() + " 主题文件，正在解析颜色信息…………", Toast.LENGTH_LONG).show();
                try {
                    getRes();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(this, "抱歉，您当前选择的不是 hwt 主题文件，请重新选择！", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 由uri获取path
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    //获取资源
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getRes() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
        Log.e(TAG,"here-------------------------");
        //复制assets文件
//        FileUtil.copyFile(InPath,OutPath + (new File(InPath)).getName());
        //解压缩 到000
        FileUtil.unzip(InPath,  InPath+"000");
        //打印文件信息
        ArrayList<File> files = FileUtil.getNames(new File(InPath+"000"));
        Log.e(TAG,"number:"+files.size());
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
        List<Map.Entry<String,Integer>> list = new ArrayList<>(FileUtil.colorMap.entrySet());
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
        //
    }

    /**
     * 替换颜色
     */
    public void replaceColorMul() throws IOException, PackageManager.NameNotFoundException, ClassNotFoundException {
        //替换颜色
        FileUtil.replaceXml(InPath+"000",strs.get(0),"#ff0000");
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
        FileUtil.zip(InPath+"000",RealOutPath);
//        FileUtil.copyFile(InPath,OutPath + (new File(InPath)).getName());
//        FileUtil.copyFile(InPath,RealOutPath_new);
        Log.e(TAG,"成功压缩哈哈哈------outpath: "+ RealOutPath + "-----------------------------------------");
        //打开主题商店
        try {
            openApp(OPEN_THEME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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


    /**
     * 申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestPermission() throws PackageManager.NameNotFoundException, IOException, ClassNotFoundException {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
//            getRes();
        }
    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_Bt:
                //选择文件
                chooseFile(FILE_MANAGER,this);
//                try {
//                    getRes();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }
}
