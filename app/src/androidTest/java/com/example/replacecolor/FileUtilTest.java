package com.example.replacecolor;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FileUtilTest {

    File file;
    String path;
    private final static String TAG = "FileUtilTest000";

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

//        file = new File(appContext.getAssets().list("")[0]);

        assertTrue(new File(FileUtil.getAssetsCacheFile(appContext,"BTblack3.hwt")).exists());
        path = FileUtil.getAssetsCacheFile(appContext,"BTblack3.hwt");
        Log.e(TAG,"what a fuck\n"+FileUtil.getAssetsCacheFile(appContext,"BTblack3.hwt"));
//        Log.e(TAG,appContext.getAssets().getLocales()+"");
    }

    @After
    public void tearDown() throws Exception {
    }

//    @Test
//    public void zipFiles() {
//    }

    @Test
    public void upZipFile() throws IOException {
        FileUtil.unzip(path,path);

    }

//    @Test
//    public void unzip() {
//    }
}