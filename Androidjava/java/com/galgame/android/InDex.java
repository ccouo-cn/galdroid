package com.galgame.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;

public class InDex extends Activity {

    private LinearLayout navContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		
		
		
		
		
		
		
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
		
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v1);
		
		
		
		
        navContainer = findViewById(R.id.nav_container);
        loadJson();
    }

    private void loadJson() {
		new Thread(new Runnable() {
				@Override
				public void run() {
					FileInputStream fis = null;
					BufferedReader br = null;

					try {
						File dir = new File(
							"/storage/emulated/0/Android/data/com.galgame.android"
						);

						File file = new File(dir, "gal.json");

						if (!file.exists()) {
							return; // 结束
						}

						fis = new FileInputStream(file);
						br = new BufferedReader(new InputStreamReader(fis));

						StringBuilder sb = new StringBuilder();

						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}

						if (sb.length() == 0) {
							return; //             文件内容为空
						}

						JSONObject json = new JSONObject(sb.toString());

						if (!json.has("publishers")) {
							return; 
						}

						JSONArray arr = json.getJSONArray("publishers");

						if (arr == null || arr.length() == 0) {
							return; // 数组为空
						}

						final List<Publisher> list = new ArrayList<>();

						for (int i = 0; i < arr.length(); i++) {
							JSONObject o = arr.getJSONObject(i);

							if (o == null) {
								continue;
							}

							Publisher p = new Publisher();
							p.name = o.optString("name", "");
							p.gameData = o.optString("gameData", "");

							if (!p.name.isEmpty()) {
								list.add(p);
							}
						}

						if (!list.isEmpty()) {
							runOnUiThread(new Runnable() {
									@Override
									public void run() {
										createNav(list);
									}
								});
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (br != null) {
								br.close();
							}

							if (fis != null) {
								fis.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
	}
	

    private void createNav(List<Publisher> list) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < list.size(); i++) {
       final  Publisher p = list.get(i);

            TextView btn = (TextView) inflater.inflate(R.layout.v1_1, navContainer, false);
            btn.setText(p.name);

            btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(p);
					}
				});

            navContainer.addView(btn);

            if (i == 0) {
                switchFragment(p);
            }
        }
    }

    private void switchFragment(Publisher p) {
        Fragment m = MaIn.newInstance(p);
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.container, m);
        t.commit();
    }

    static class Publisher {
        String name;
        String gameData;
    }
}

