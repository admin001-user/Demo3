package com.example.demo3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrendActivity extends AppCompatActivity {
    private TextView titleCity;
    private RecyclerView list;
    private ForecastAdapter adapter;
    private String cityCode;
    private String cityName;
    private TempChartView chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trend);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.trendRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleCity = findViewById(R.id.trendCityText);
        list = findViewById(R.id.trendList);
        chart = findViewById(R.id.tempChart);
        adapter = new ForecastAdapter();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        cityCode = getIntent().getStringExtra("cityCode");
        cityName = getIntent().getStringExtra("cityName");
        if (cityCode == null) cityCode = "440100";
        if (cityName == null) cityName = "广州";
        titleCity.setText(cityName);

        Button btnCity = findViewById(R.id.btnCity);
        btnCity.setOnClickListener(v -> finish());
        btnCity.setBackgroundResource(R.drawable.capsule_unselected);
        btnCity.setTextColor(0xFFFFFFFF);
        Button btnTrend = findViewById(R.id.btnTrend);
        btnTrend.setBackgroundResource(R.drawable.capsule_selected);
        btnTrend.setTextColor(0xFFFFFFFF);
        btnTrend.setOnClickListener(v -> Toast.makeText(this, "已在温度趋势页", Toast.LENGTH_SHORT).show());

        fetchForecast();
    }

    private void fetchForecast() {
        String key = BuildConfig.AMAP_API_KEY;
        String url = "https://restapi.amap.com/v3/weather/weatherInfo?extensions=all&city=" + cityCode + "&key=" + key;
        new Thread(() -> {
            try {
                String json = getJson(url);
                JSONObject obj = new JSONObject(json);
                List<ForecastItem> out = new ArrayList<>();
                if (obj.optInt("status") == 1) {
                    JSONArray forecasts = obj.optJSONArray("forecasts");
                    if (forecasts != null && forecasts.length() > 0) {
                        JSONObject f = forecasts.getJSONObject(0);
                        JSONArray casts = f.optJSONArray("casts");
                        if (casts != null) {
                            int limit = Math.min(7, casts.length());
                            for (int i = 0; i < limit; i++) {
                                JSONObject c = casts.getJSONObject(i);
                                out.add(new ForecastItem(
                                        c.optString("date"),
                                        c.optString("dayweather"),
                                        c.optString("nightweather"),
                                        c.optString("daytemp"),
                                        c.optString("nighttemp")
                                ));
                            }
                        }
                    }
                }
                runOnUiThread(() -> {
                    adapter.setItems(out);
                    chart.setItems(out);
                });
            } catch (Exception e) {
                runOnUiThread(() -> adapter.setItems(new ArrayList<>()));
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
