package com.example.replacecolor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class enterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String OUT_DIR = "/Huawei/Themes/";
    private static final String OutPath = Environment.getExternalStorageDirectory().getAbsolutePath() + OUT_DIR;
    private static final String FILE_MANAGER = OutPath;
    private static final int FILE_SELECT_CODE = 0;
    private final static String TAG = "enterActivity000";
    private  static String InPath;;
    private Button button;
    private Button getbutton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        button =  findViewById(R.id.open_Bt);
        getbutton = findViewById(R.id.get_Bt);
        button.setOnClickListener(this);
        getbutton.setOnClickListener(this);
        try {
            requestPermission();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //把我的asset文件写进去
        InPath = FileUtil.getAssetsCacheFile(this,"BTblack3.hwt");
        try {
            FileUtil.copyFile(InPath,OutPath + (new File(InPath)).getName());
        } catch (IOException e) {
            e.printStackTrace();
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



    /**
     * 选择文件
     * @param str
     * @param context
     */
    public void chooseFile(String str, Context context){
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
            //根据路径 做出提示、并跳转activity
            getPathAndStart(InPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 可以在 自己选择、选择其一中 重用
     * @param InPath
     */
    public void getPathAndStart(String InPath){
        //判断是否是hwt文件、提示正在解析、或者无法操作
        File file = new File(InPath);
        if (file.getName().endsWith("hwt")){
            //开始解析
            Toast.makeText(this, "您选择了" + file.getName() + " 主题文件，正在解析颜色信息…………", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("filePath",file.getAbsolutePath());
            startActivity(intent);

        }
        else {
            Toast.makeText(this, "抱歉，您当前选择的不是 hwt 主题文件，请重新选择！", Toast.LENGTH_SHORT).show();
        }
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

    private String path;
    private View getListView;
    /**
     * 弹窗：传入接口、、更新界面
     * @param path 路径
     */
    public void dialog1(final String path){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //标题
        dialog.setTitle("选择主题文件");
        //列表
        //动态加载布局
//        ListView listView = new ListView(enterActivity.this);
        LayoutInflater inflater = LayoutInflater.from(enterActivity.this);
        getListView = inflater.inflate(R.layout.list_item,null);
        ListView listView = (ListView) getListView.findViewById(R.id.theme_list);
        //String[]数组
        final File[] files = (new File(path)).listFiles();
        Log.e(TAG,"一共有   个文件：   "+files.length);
//        ArrayList<String> arrayList = new ArrayList();
        final ArrayList<File> arrayListFile = new ArrayList();
        for (File file : files){
            if (file.getName().endsWith("hwt")){
//                arrayList.add(file.getName());
                arrayListFile.add(file);
                Log.e(TAG,file.getName());
            }
        }
        final String[] strList = new String[arrayListFile.size()];
        for (int i= 0; i <strList.length ; i++){
            strList[i] = arrayListFile.get(i).getName();
        }
        //adapter里面对应单项、不要对应listview！
        //一般加载：android.R.layout.simple_list_item_1
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(enterActivity.this, android.R.layout.simple_list_item_1, strList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String path = strList[i];
                getPathAndStart(arrayListFile.get(i).getAbsolutePath());
                Log.e(TAG,arrayListFile.get(i).getAbsolutePath());
            }
        });
        //添加
        //这里的添加view应该是一共的view呜呜
        dialog.setView(getListView);
        dialog.create().show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.open_Bt://选择文件
                chooseFile(FILE_MANAGER,this);
                break;
            case R.id.get_Bt://选择文件
                dialog1(OutPath);
                break;
        }
    }
}
