package org.example.client;


import com.google.gson.Gson;
import org.example.server.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Dasdassand
 */
public class Main {
    public static void main(String[] args) throws IOException {
        var cities = City.getCities();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Возможный список городов:");
        for (City c :
                City.getCities()) {
            System.out.println(c.getName());
        }
        System.out.println("Введите название города:");
        String name = scanner.nextLine();
        for (City c :
                cities) {
            if (Objects.equals(c.getName(), name))
                System.out.println(send(c));
        }
        scanner.close();

    }

    private static StringBuilder send(City city) throws IOException {
        var request = "http://localhost:8090/weather";
        URL url = new URL(request);
        Gson gson = new Gson();
        String json = gson.toJson(city);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("city", json);
        var buf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = buf.readLine()) != null) {
            response.append(line).append("\n");
        }
        buf.close();
        return response;
    }

/**
 @TODO add parser!
 private static void getWater(int id) throws IOException {
 var request = "http://api.openweathermap.org/data/2.5/forecast?id=" + id + "&appid=1e7a3d72c547bbada51f45186b16660a";
 System.out.println(request);
 URL url = new URL(request);
 URLConnection connection = url.openConnection();
 BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
 String line;
 StringBuilder response = new StringBuilder();
 while ((line = reader.readLine()) != null) {
 response.append(line);
 }
 reader.close();
 System.out.println(parse(response.toString()));
 }

 private static String parse(String s) {
 System.out.println(s);
 return "";
 }
 */
}