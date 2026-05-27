package com.galgame.android;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MaIn extends Fragment {

    private static final String TAG = "MaIn";
    private static final String ARG_DATA = "data";

    private JSONObject currentGameJson;

    public static MaIn newInstance(InDex.Publisher p) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DATA, p.gameData);

        MaIn fragment = new MaIn();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(
		LayoutInflater inflater,
		ViewGroup container,
		Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.v2, container, false);
        LinearLayout containerLayout = root.findViewById(R.id.game_container);

        String gameDataPath = getArguments().getString(ARG_DATA);
        if (gameDataPath == null || gameDataPath.isEmpty()) {
            TextView tv = new TextView(getActivity());
            tv.setText("暂无游戏数据");
            containerLayout.addView(tv);
            return root;
        }

        loadGames(containerLayout, gameDataPath);
        return root;
    }

    private void loadGames(final LinearLayout container,final String gameDataPath) {
        new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String baseUrl = "https://www.yuzusoft.pw";
						String fullUrl = baseUrl + gameDataPath;

						URL url = new URL(fullUrl);
						HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();

						connection.setRequestMethod("GET");
						connection.setConnectTimeout(8000);
						connection.setReadTimeout(8000);

						BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

						StringBuilder builder = new StringBuilder();
						String line;

						while (true) {
							line = reader.readLine();
							if (line == null) {
								break;
							}
							builder.append(line);
						}

						JSONObject root = new JSONObject(builder.toString());
						JSONArray games = root.getJSONArray("games");

						for (int i = 0; i < games.length(); i++) {
							final JSONObject gameObject = games.getJSONObject(i);

							final String name = gameObject.optString("name", "未知游戏");
							final String iconUrl = gameObject.optString("icon", "");

							final Bitmap icon = loadBitmapFromUrl(iconUrl);

							getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										addGameItem(container, name, icon, gameObject);
									}
								});
						}

					} catch (Exception e) {
						Log.e(TAG, "loadGames error", e);
					}
				}
			}).start();
    }

    private Bitmap loadBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addGameItem(
		LinearLayout container,
		String name,
		Bitmap icon,
	final	JSONObject gameObject) {

        LinearLayout itemLayout = new LinearLayout(getActivity());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 24, 0, 24);

        ImageView iconView = new ImageView(getActivity());
        iconView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

        if (icon != null) {
            iconView.setImageBitmap(icon);
        } else {
            iconView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        TextView nameView = new TextView(getActivity());
        nameView.setText(name);
        nameView.setTextSize(16);
        nameView.setPadding(24, 0, 0, 0);

        itemLayout.addView(iconView);
        itemLayout.addView(nameView);

        itemLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						currentGameJson = new JSONObject();

						currentGameJson.put("id", gameObject.optString("id", ""));
						currentGameJson.put("name", gameObject.optString("name", ""));
						currentGameJson.put("text", gameObject.optString("text", ""));
						currentGameJson.put("icon", gameObject.optString("icon", ""));
						currentGameJson.put("images", gameObject.optJSONArray("images"));
						currentGameJson.put("download_url", gameObject.optString("download_url", ""));

					} catch (Exception e) {
						Log.e(TAG, "build json error", e);
					}

					loadJson();

					Intent intent = new Intent(getActivity(), Key.class);
					intent.putExtra("game_id", gameObject.optString("id", ""));
					startActivity(intent);
				}
			});

        container.addView(itemLayout);
    }

    private void loadJson() {
        new Thread(new Runnable() {
				@Override
				public void run() {

					if (currentGameJson == null) {
						showToast("游戏数据为空");
						return;
					}

					try {
						File dir = new File(
                            "/storage/emulated/0/Android/data/com.galgame.android"
						);

						if (!dir.exists()) {
							dir.mkdirs();
						}

						File file = new File(dir, "v2.json");

						FileWriter writer = new FileWriter(file, false);
						writer.write(currentGameJson.toString());
						writer.flush();
						writer.close();

						showToast("游戏数据已保存");

					} catch (Exception e) {
						Log.e(TAG, "save json error", e);
						showToast("保存失败：" + e.getMessage());
					}
				}
			}).start();
    }

    private void showToast(final String msg) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}
				});
        }
    }
}

