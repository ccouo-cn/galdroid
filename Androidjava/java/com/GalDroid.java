package com;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.InputStream;
	import java.net.URL;

		public class GalDroid extends Activity {

		private WebView webView;

		@Override
			protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
		android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
		android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
                copyAssetToPrivate();copyAssetToPrivate3();
        setContentView(webView);
startDownload2() ;
		
						
                
					new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
					Intent intent = new Intent(
				GalDroid.this,
			com.galgame.android.InDex.class
	);

                startActivity(intent);
                finish();
            }
        }, 5000);
    }
    private void startDownload2() {
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Process process = null;

                    try {
                        File shell = new File(getFilesDir(), "shell127.sh");

                        if (!shell.exists()) {
                            runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("shell127.sh 不存在");
                                    }
                                });
                            return;
                        }

                        process = Runtime.getRuntime().exec(
                            new String[] { "sh", shell.getAbsolutePath() }
                        );

                        int code = process.waitFor(); // ✅ 等 shell 执行完
                        process.destroy();

                        // ✅ 再读取 log.txt
                        loadServerFromLog();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        thread.start();
    }
    
    private void loadServerFromLog() {
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File logFile = new File(
                        "/storage/emulated/0/Android/data/com.galgame.android/log.txt"
                    );

                    if (!logFile.exists()) {
                        return;
                    }

                    try {
                        FileInputStream inputStream = new FileInputStream(logFile);

                        int length = (int) logFile.length();
                        byte[] buffer = new byte[length];

                        inputStream.read(buffer);
                        inputStream.close();

                        String jsonString = new String(buffer, "UTF-8");

                        JSONObject jsonObject = new JSONObject(jsonString);
                   final     String serverUrl = jsonObject.getString("server");

                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(serverUrl);
                                }
                            });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        thread.start();
    }
    
    private void copyAssetToPrivate() {
        InputStream in = null;
        FileOutputStream out = null;

        try {
            String assetName = "max/lib127";
            File outFile = new File(getFilesDir(), "lib127");

            in = getAssets().open(assetName);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[4096];
            int len;

            while (true) {
                len = in.read(buffer);
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }

            out.flush();

            // ✅ 关键：设置可执行权限
            outFile.setExecutable(true, false);

            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showToast("拷贝成功");
                    }
                });

        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("拷贝失败：" + e.getMessage());
                    }
                });
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
    
    private void copyAssetToPrivate3() {
        InputStream in = null;
        FileOutputStream out = null;

        try {
            String assetName = "max/shell127.sh";
            File outFile = new File(getFilesDir(), "shell127.sh");

            in = getAssets().open(assetName);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[4096];
            int len;

            while (true) {
                len = in.read(buffer);
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }

            out.flush();

            // ✅ 关键：设置可执行权限
            outFile.setExecutable(true, false);

            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showToast("拷贝成功");
                    }
                });

        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("拷贝失败：" + e.getMessage());
                    }
                });
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
    
	
	private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = msg;
                    if (text == null) {
                        text = "null";
                    }

                    if (text.length() > 1000) {
                        text = text.substring(0, 1000) + "\n(已截断)";
                    }

                    Toast.makeText(
                        GalDroid.this,
                        text,
                        Toast.LENGTH_LONG
                    ).show();
                }
            });
    }
    
    
    
	private void showDialog(final String msg) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(GalDroid.this)
                        .setTitle("执行结果")
                        .setMessage(msg)
                        .setPositiveButton("确定", null)
                        .show();
                }
            });
    }
    
   

    private void startDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.yuzusoft.pw/app/gal.json");

                    File targetDir = new File(
                        "/storage/emulated/0/Android/data/com.galgame.android"
                    );

                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }

                    File targetFile = new File(targetDir, "gal.json");

                    InputStream inputStream = url.openStream();
                    FileOutputStream outputStream = new FileOutputStream(targetFile);

                    byte[] buffer = new byte[1024];
                    int length;

                    while (true) {
                        length = inputStream.read(buffer);

                        if (length == -1) {
                            break;
                        }

                        outputStream.write(buffer, 0, length);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

