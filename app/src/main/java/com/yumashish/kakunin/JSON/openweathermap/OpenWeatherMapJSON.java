package com.yumashish.kakunin.JSON.openweathermap;

import com.google.api.client.util.Key;

/**
 * Created by lightning on 12/22/15.
 */
public class OpenWeatherMapJSON {
    @Key public Coord coord;
    @Key public Weather weather;
    @Key public String base;
    @Key public Main main;
    @Key public Wind wind;
    @Key public Clouds clouds;
    @Key public Rain rain;
    @Key public Snow snow;
    @Key public int dt;
    @Key public Temp temp;
    @Key public Sys sys;
    @Key public int id;
    @Key public String name;
    @Key public int cod;


    public static class Coord {
        @Key public double lon;
        @Key public double lat;
    }

    public static class Weather {
        @Key public int id;
        @Key public String main;
        @Key public String description;
        @Key public String icon;
    }

    public static class Main {
        @Key public double temp;
        @Key public double pressure;
        @Key public double humidity;
        @Key public double temp_min;
        @Key public double temp_max;
        @Key public double sea_level;
        @Key public double grnd_level;
    }

    public static class Temp {
        @Key public double day;
        @Key public double min;
        @Key public double max;
        @Key public double night;
        @Key public double eve;
        @Key public double morn;
    }

    public static class Wind {
        @Key public double speed;
        @Key public double deg;
    }

    public static class Clouds {
        @Key public double all;
    }

    public static class Rain {
        @Key("3h") public double value;
    }

    public static class Snow {
        @Key("3h") public double value;
    }

    public static class Sys {
        @Key public String type;
        @Key public int id;
        @Key public double message;
        @Key public String country;
        @Key public int sunrise;
        @Key public int sunset;
    }
}
