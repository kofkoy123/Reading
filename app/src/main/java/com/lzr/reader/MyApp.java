package com.lzr.reader;

import android.content.Context;

import com.shixia.reader.utils.Config;
import com.shixia.reader.utils.PageFactory;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2018/6/25.
 */

public class MyApp extends LitePalApplication{
    public static volatile Context applicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        LitePalApplication.initialize(this);
        Config.createConfig(this);
        PageFactory.createPageFactory(this);
    }
}
