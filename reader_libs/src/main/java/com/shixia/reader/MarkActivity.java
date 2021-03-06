package com.shixia.reader;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.shixia.reader.adapter.MyPagerAdapter;
import com.shixia.reader.base.BaseActivity;
import com.shixia.reader.db.BookCatalogue;
import com.shixia.reader.utils.Config;
import com.shixia.reader.utils.FictionFileUtils;
import com.shixia.reader.utils.PageFactory;

import java.util.ArrayList;

public class MarkActivity extends BaseActivity {

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;


    private PageFactory pageFactory;
    private Config config;
    private Typeface typeface;
    private ArrayList<BookCatalogue> catalogueList = new ArrayList<>();
    private DisplayMetrics dm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_mark;
    }

    @Override
    protected void initData() {
        initViews();
        pageFactory = PageFactory.getInstance();
        config = Config.getInstance();
        dm = getResources().getDisplayMetrics();
        typeface = config.getTypeface();

        setSupportActionBar(toolbar);
        //设置导航图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.mipmap.icon_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(FictionFileUtils.getInstance().getFileName(pageFactory.getBookPath()));
        }

        setTabsValue();
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), pageFactory.getBookPath()));
        tabs.setViewPager(pager);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        appbar = findViewById(R.id.appbar);
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);

    }

    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);//所有初始化要在setViewPager方法之前
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        //设置Tab标题文字的字体
        tabs.setTypeface(typeface, 0);
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);

        // pagerSlidingTabStrip.setDividerPadding(18);
    }

    @Override
    protected void initListener() {

    }
}
