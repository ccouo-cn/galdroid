package com.galgame.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class Key extends Activity {
	private Button downloadBtn;
	
    private ImageView gameIcon;
    private TextView gameName;
    private TextView gameDesc;
    private LinearLayout imageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v3);

        gameIcon = findViewById(R.id.game_icon);
        gameName = findViewById(R.id.game_name);
        gameDesc = findViewById(R.id.game_desc);
        imageContainer = findViewById(R.id.image_container);
		downloadBtn = findViewById(R.id.download_btn);
		
        loadGameInfo();
    }
	
	private void downloadGame() {
		new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						File file = new File(
							"/storage/emulated/0/Android/data/com.galgame.android/v2.json"
						);

						if (!file.exists()) {
							showToast("游戏数据不存在");
							return;
						}

						BufferedReader reader = new BufferedReader(new FileReader(file));
						StringBuilder builder = new StringBuilder();
						String line;

						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}

						JSONObject game = new JSONObject(builder.toString());

						String downloadUrl = game.optString("download_url", "");
						if (downloadUrl == null || downloadUrl.isEmpty()) {
							showToast("下载地址为空");
							return;
						}

						String realUrl = "https://www.yuzusoft.pw" + downloadUrl;

						URL url = new URL(realUrl);
						InputStream inputStream = url.openStream();

						File outDir = new File(
							"/storage/emulated/0/Android/data/com.galgame.android/games"
						);

						if (!outDir.exists()) {
							outDir.mkdirs();
						}

						File outFile = new File(outDir, "game.zip");

						java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);

						byte[] buffer = new byte[4096];
						int len;

						while ((len = inputStream.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}

						fos.close();
						inputStream.close();

						showToast("下载完成");

					} catch (Exception e) {
						e.printStackTrace();
						showToast("下载失败：" + e.getMessage());
					}
				}
			}).start();
	}
	
	private void showToast(final String msg) {
    runOnUiThread(new Runnable() {
        @Override
				public void run() {
				Toast.makeText(Key.this, msg, Toast.LENGTH_SHORT).show();
					}
						});
							}
						
    private void loadGameInfo() {
        new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						File file = new File(
                            "/storage/emulated/0/Android/data/com.galgame.android/v2.json"
						);

						if (!file.exists()) {
							return;
						}

						BufferedReader reader = new BufferedReader(new FileReader(file));
						StringBuilder builder = new StringBuilder();
						String line;

						while (true) {
							line = reader.readLine();
							if (line == null) {
								break;
							}
							builder.append(line);
						}

						JSONObject game = new JSONObject(builder.toString());

					final	String name = game.optString("name", "未知游戏");
					final	String desc = game.optString("text", "");
					final	String iconUrl = game.optString("icon", "");

				final		Bitmap icon = loadBitmapFromUrl(iconUrl);

						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									gameName.setText(name);
									gameDesc.setText(desc);

									if (icon != null) {
										gameIcon.setImageBitmap(icon);
									}
								}
							});

						// 下载截图
						loadImages(game);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
    }

    private void loadImages(JSONObject game) {
		try {
			if (!game.has("images")) {
				return;
			}

			JSONArray images = game.getJSONArray("images");

			for (int i = 0; i < images.length(); i++) {
				String imageUrl = images.getString(i);
				Bitmap bitmap = loadBitmapFromUrl(imageUrl);

				if (bitmap == null) {
					continue;
				}

				final Bitmap finalBitmap = bitmap;

				runOnUiThread(new Runnable() {
						@Override
						public void run() {

							ImageView imageView = new ImageView(Key.this);

							
							LinearLayout.LayoutParams params =
								new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								600
                            );

							params.setMargins(12, 0, 12, 0);
							imageView.setLayoutParams(params);

							imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
							imageView.setAdjustViewBounds(true); 
							imageView.setImageBitmap(finalBitmap);

							imageContainer.addView(imageView);
						}
					});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

    private Bitmap loadBitmapFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

