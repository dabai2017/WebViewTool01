package com.dabai.tool03;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    WebView webview;
    ProgressBar pro;
    String last;
    ConstraintLayout cons;
    TextView webtip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl(webview.getUrl());
                //Snackbar.make(view, "刷新命令执行~", Snackbar.LENGTH_LONG).show();
            }
        });

    }


    //初始化
    private void init() {

        //dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        pro = findViewById(R.id.progressBar);
        webview = findViewById(R.id.webview);
        webtip = findViewById(R.id.webtip);
        cons = findViewById(R.id.cons);


        //声明WebSettings子类
        WebSettings webSettings = webview.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
// 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式


        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress < 100) {
                    webtip.setText(newProgress + "%");

                } else {
                    webtip.setVisibility(View.GONE);
                }

            }


        });


        webview.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                if (url.startsWith("http:") || url.startsWith("https:")) {


                    Snackbar.make(cons, "想要打开其他页面嘛?", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.loadUrl(url);
                        }
                    }).setActionTextColor(Color.WHITE).show();


                    return false;//返回false 意思是不拦截，让webview自己处理
                } else {
                    // Otherwise allow the OS to handle things like tel, mailto, etc.
                    try {

                        Snackbar.make(cons, "想要打开其他页面嘛?", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                                finish();
                            }
                        }).setActionTextColor(Color.WHITE).show();


                    } catch (Exception e) {
                        Snackbar.make(cons, "异常:" + e.toString(), Snackbar.LENGTH_SHORT).show();

                    }

                    return true;
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();


        String link = null;
        try {
            Intent intent = getIntent();
            link = intent.getStringExtra("link");


            if (link == null) {
                intent = getIntent();
                link = "" + intent.getData();
                webview.loadUrl(link);

                if (link.equals("null")) {


                    new AlertDialog.Builder(this).setTitle("提示").setMessage("请通过其他APP的 '在浏览器打开' 功能来启动本程序")
                            .setCancelable(false)
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();

                }

            }

            last = link;
            webview.loadUrl(link);

        } catch (Exception e) {
            Toast.makeText(this, "程序错误", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    boolean isRun;
    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 5 * 1000);//设置循环时间，此处是5秒
            if (isRun) {
                webview.loadUrl(webview.getUrl());
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Switch switchShop = (Switch) menu.findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.menusw);
        switchShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                if (isChecked) {
                    isRun = true;
                } else {
                    isRun = false;
                }
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();//返回上个页面
            return true;
        } else {
            Snackbar.make(cons, "退出?", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).setActionTextColor(Color.WHITE).show();
            return false;
        }

    }


}
