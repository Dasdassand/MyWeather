package org.example.server;

public class City {
    private final String name;
    private final int id;
    private String lat;
    private String lon;

    public City(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public City(String name, int id, String lat, String lon) {
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public static City[] getCities() {
        City[] cities = new City[5];
        cities[0] = new City("Москва", 1220988, "55.7522", "37.6156");
        cities[1] = new City("Санкт-Петербург", 536203, "59.9386", "30.3141");
        cities[2] = new City("Воронеж", 472045, "51.672", "39.1843");
        cities[3] = new City("Липецк", 535121, "52.6031", "39.5708");
        cities[4] = new City("Казань", 551487, "55.7887", "49.1221");
        return cities;
    }
}
