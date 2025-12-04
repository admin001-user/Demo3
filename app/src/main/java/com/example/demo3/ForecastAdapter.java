package com.example.demo3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.VH> {
    private final List<ForecastItem> items = new ArrayList<>();

    public void setItems(List<ForecastItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ForecastItem it = items.get(position);
        holder.labelText.setText(labelForDate(it.date));
        holder.dateText.setText(shortDate(it.date));
        holder.dayWeatherText.setText(it.dayWeather);
        holder.tempRangeText.setText(it.dayTemp + "° " + it.nightTemp + "°");
        holder.iconText.setText(mapIcon(it.dayWeather));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView labelText;
        final TextView dateText;
        final TextView dayWeatherText;
        final TextView tempRangeText;
        final TextView iconText;

        VH(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.labelText);
            dateText = itemView.findViewById(R.id.dateText);
            dayWeatherText = itemView.findViewById(R.id.dayWeatherText);
            tempRangeText = itemView.findViewById(R.id.tempRangeText);
            iconText = itemView.findViewById(R.id.iconText);
        }
    }

    private String mapIcon(String w) {
        if (w == null) return "";
        String s = w.trim();
        if (s.contains("晴")) return "☀";
        if (s.contains("云") || s.contains("阴")) return "☁";
        if (s.contains("雨")) return "🌧";
        if (s.contains("雪")) return "❄";
        return "⛅";
    }

    private String labelForDate(String dateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA);
            java.util.Date d = sdf.parse(dateStr);
            java.util.Calendar c = java.util.Calendar.getInstance();
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.set(java.util.Calendar.MINUTE, 0);
            today.set(java.util.Calendar.SECOND, 0);
            today.set(java.util.Calendar.MILLISECOND, 0);
            c.setTime(d);
            c.set(java.util.Calendar.HOUR_OF_DAY, 0);
            c.set(java.util.Calendar.MINUTE, 0);
            c.set(java.util.Calendar.SECOND, 0);
            c.set(java.util.Calendar.MILLISECOND, 0);
            long diffDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(c.getTimeInMillis() - today.getTimeInMillis());
            if (diffDays == 0) return "今天";
            if (diffDays == 1) return "明天";
            int dow = c.get(java.util.Calendar.DAY_OF_WEEK);
            switch (dow) {
                case java.util.Calendar.MONDAY: return "星期一";
                case java.util.Calendar.TUESDAY: return "星期二";
                case java.util.Calendar.WEDNESDAY: return "星期三";
                case java.util.Calendar.THURSDAY: return "星期四";
                case java.util.Calendar.FRIDAY: return "星期五";
                case java.util.Calendar.SATURDAY: return "星期六";
                case java.util.Calendar.SUNDAY: return "星期日";
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String shortDate(String dateStr) {
        try {
            java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA);
            java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("MM-dd", java.util.Locale.CHINA);
            java.util.Date d = in.parse(dateStr);
            return out.format(d);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
