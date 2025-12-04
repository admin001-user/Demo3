package com.example.demo3;

public class ForecastItem {
    public final String date;
    public final String dayWeather;
    public final String nightWeather;
    public final String dayTemp;
    public final String nightTemp;

    public ForecastItem(String date, String dayWeather, String nightWeather, String dayTemp, String nightTemp) {
        this.date = date;
        this.dayWeather = dayWeather;
        this.nightWeather = nightWeather;
        this.dayTemp = dayTemp;
        this.nightTemp = nightTemp;
    }
}
