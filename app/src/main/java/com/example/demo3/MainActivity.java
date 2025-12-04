package com.example.demo3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView cityText;
    private TextView weatherText;
    private TextView tempText;
    private TextView rangeText;
    
    private String currentCityCode = "440100";
    private String currentCityName = "广州";
    private TextView dayWeatherValue;
    private TextView dayTempValue;
    private TextView dayWindValue;
    private TextView nightWeatherValue;
    private TextView nightTempValue;
    private TextView nightWindValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityText = findViewById(R.id.cityText);
        weatherText = findViewById(R.id.weatherText);
        tempText = findViewById(R.id.tempText);
        rangeText = findViewById(R.id.rangeText);
        
        Button btnBeijing = findViewById(R.id.btnBeijing);
        Button btnShanghai = findViewById(R.id.btnShanghai);
        Button btnGuangzhou = findViewById(R.id.btnGuangzhou);
        Button btnShenzhen = findViewById(R.id.btnShenzhen);
        Button btnCity = findViewById(R.id.btnCity);
        Button btnTrend = findViewById(R.id.btnTrend);
        dayWeatherValue = findViewById(R.id.dayWeatherValue);
        dayTempValue = findViewById(R.id.dayTempValue);
        dayWindValue = findViewById(R.id.dayWindValue);
        nightWeatherValue = findViewById(R.id.nightWeatherValue);
        nightTempValue = findViewById(R.id.nightTempValue);
        nightWindValue = findViewById(R.id.nightWindValue);

        

        btnBeijing.setOnClickListener(v -> { currentCityCode = "110000"; updateCityTab("110000"); fetchWeather(currentCityCode); });
        btnShanghai.setOnClickListener(v -> { currentCityCode = "310000"; updateCityTab("310000"); fetchWeather(currentCityCode); });
        btnGuangzhou.setOnClickListener(v -> { currentCityCode = "440100"; updateCityTab("440100"); fetchWeather(currentCityCode); });
        btnShenzhen.setOnClickListener(v -> { currentCityCode = "440300"; updateCityTab("440300"); fetchWeather(currentCityCode); });

        btnCity.setOnClickListener(v -> Toast.makeText(this, "已在城市页", Toast.LENGTH_SHORT).show());
        btnTrend.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, TrendActivity.class);
            intent.putExtra("cityCode", currentCityCode);
            intent.putExtra("cityName", currentCityName);
            startActivity(intent);
        });

        

        updateCityTab(currentCityCode);
        fetchWeather(currentCityCode);
    }

    private void updateCityTab(String code) {
        Button b = findViewById(R.id.btnBeijing);
        Button s = findViewById(R.id.btnShanghai);
        Button g = findViewById(R.id.btnGuangzhou);
        Button z = findViewById(R.id.btnShenzhen);
        int selBg = R.drawable.capsule_selected;
        int unselBg = R.drawable.capsule_unselected;
        int white = ContextCompat.getColor(this, android.R.color.white);
        b.setBackgroundResource(code.equals("110000") ? selBg : unselBg);
        s.setBackgroundResource(code.equals("310000") ? selBg : unselBg);
        g.setBackgroundResource(code.equals("440100") ? selBg : unselBg);
        z.setBackgroundResource(code.equals("440300") ? selBg : unselBg);
        b.setTextColor(white); s.setTextColor(white); g.setTextColor(white); z.setTextColor(white);

        android.widget.HorizontalScrollView scroll = findViewById(R.id.cityTabScroll);
        android.view.ViewGroup bar = findViewById(R.id.cityTabBar);
        Button target = code.equals("110000") ? b : code.equals("310000") ? s : code.equals("440100") ? g : z;
        bar.post(() -> {
            int center = target.getLeft() + target.getWidth() / 2;
            int half = scroll.getWidth() / 2;
            int dest = Math.max(0, center - half);
            scroll.smoothScrollTo(dest, 0);
        });
    }

    private void fetchWeather(String cityCode) {
        String key = BuildConfig.AMAP_API_KEY;
        if (key == null) key = "";
        if (key.isEmpty()) {
            String msg = "缺少API key，请在local.properties配置 AMAP_API_KEY";
            runOnUiThread(() -> {
                cityText.setText(msg);
                weatherText.setText("");
                tempText.setText("");
                rangeText.setText("");
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            });
            Log.e("Weather", msg);
            return;
        }
        String liveUrl = "https://restapi.amap.com/v3/weather/weatherInfo?extensions=base&city=" + cityCode + "&key=" + key;
        String forecastUrl = "https://restapi.amap.com/v3/weather/weatherInfo?extensions=all&city=" + cityCode + "&key=" + key;
        new Thread(() -> {
            try {
                String live = getJson(liveUrl);
                String forecast = getJson(forecastUrl);

                JSONObject liveObj = new JSONObject(live);
                JSONObject forecastObj = new JSONObject(forecast);

                String city = "";
                String weather = "";
                String temperature = "";
                int dayMaxI = Integer.MIN_VALUE;
                int nightMinI = Integer.MIN_VALUE;
                int todayIndex = -1;
                String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

                if (liveObj.optInt("status") == 1) {
                    JSONArray lives = liveObj.optJSONArray("lives");
                    if (lives != null && lives.length() > 0) {
                        JSONObject l = lives.getJSONObject(0);
                        city = l.optString("city");
                        weather = l.optString("weather");
                        temperature = l.optString("temperature");
                    }
                } else {
                    String info = liveObj.optString("info");
                    Log.e("Weather", "live error: " + info);
                }

                List<ForecastItem> list = new ArrayList<>();
                if (forecastObj.optInt("status") == 1) {
                    JSONArray forecasts = forecastObj.optJSONArray("forecasts");
                    if (forecasts != null && forecasts.length() > 0) {
                        JSONObject f = forecasts.getJSONObject(0);
                        JSONArray casts = f.optJSONArray("casts");
                        if (casts != null) {
                            for (int i = 0; i < casts.length(); i++) {
                                JSONObject c = casts.getJSONObject(i);
                                String date = c.optString("date");
                                String dw = c.optString("dayweather");
                                String nw = c.optString("nightweather");
                                String dt = c.optString("daytemp");
                                String nt = c.optString("nighttemp");
                                String dp = c.optString("daypower");
                                String np = c.optString("nightpower");
                                list.add(new ForecastItem(date, dw, nw, dt, nt));
                                if (date.equals(todayStr) && todayIndex == -1) {
                                    todayIndex = i;
                                    try { dayMaxI = Integer.parseInt(dt); } catch (Exception ignore) {}
                                    try { nightMinI = Integer.parseInt(nt); } catch (Exception ignore) {}
                                    String dwind = dp;
                                    String nwind = np;
                                    String dwF = dw;
                                    String nwF = nw;
                                    String dtF = dt;
                                    String ntF = nt;
                                    runOnUiThread(() -> {
                                        dayWeatherValue.setText(dwF);
                                        dayTempValue.setText(dtF + "°");
                                        dayWindValue.setText(dwind);
                                        nightWeatherValue.setText(nwF);
                                        nightTempValue.setText(ntF + "°");
                                        nightWindValue.setText(nwind);
                                    });
                                }
                            }
                            if (todayIndex == -1 && casts.length() > 0) {
                                JSONObject c0 = casts.getJSONObject(0);
                                try { dayMaxI = Integer.parseInt(c0.optString("daytemp")); } catch (Exception ignore) {}
                                try { nightMinI = Integer.parseInt(c0.optString("nighttemp")); } catch (Exception ignore) {}
                                String dwind = c0.optString("daypower");
                                String nwind = c0.optString("nightpower");
                                String dw = c0.optString("dayweather");
                                String nw = c0.optString("nightweather");
                                String dt = c0.optString("daytemp");
                                String nt = c0.optString("nighttemp");
                                runOnUiThread(() -> {
                                    dayWeatherValue.setText(dw);
                                    dayTempValue.setText(dt + "°");
                                    dayWindValue.setText(dwind);
                                    nightWeatherValue.setText(nw);
                                    nightTempValue.setText(nt + "°");
                                    nightWindValue.setText(nwind);
                                });
                            }
                        }
                    }
                } else {
                    String info = forecastObj.optString("info");
                    Log.e("Weather", "forecast error: " + info);
                }

                String cityFinal = city;
                String weatherFinal = weather;
                String tempFinal = temperature;
                String rangeFinal;
                if (dayMaxI != Integer.MIN_VALUE && nightMinI != Integer.MIN_VALUE) {
                    rangeFinal = "最高: " + dayMaxI + "° 最低: " + nightMinI + "°";
                } else {
                    rangeFinal = "";
                }
                if (cityFinal.isEmpty() && weatherFinal.isEmpty() && tempFinal.isEmpty()) {
                    String info = forecastObj.optString("info");
                    String msg = info.isEmpty() ? "数据为空，请检查key权限或网络" : info;
                    runOnUiThread(() -> {
                        cityText.setText(msg);
                        weatherText.setText("");
                        tempText.setText("");
                        rangeText.setText("");
                        
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        cityText.setText(cityFinal);
                        weatherText.setText(weatherFinal);
                        tempText.setText(tempFinal + "°");
                        rangeText.setText(rangeFinal);
                        
                        currentCityName = cityFinal.isEmpty() ? currentCityName : cityFinal;
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    cityText.setText("网络错误");
                    weatherText.setText("");
                    tempText.setText("");
                    rangeText.setText("");
                    
                    Toast.makeText(this, "网络错误", Toast.LENGTH_LONG).show();
                });
                Log.e("Weather", "exception", e);
            }
        }).start();
    }

    

    private String getJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        conn.disconnect();
        return sb.toString();
    }
}
