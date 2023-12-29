package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        var cities = City.getCities();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите название города:");
        String name = scanner.nextLine();
        for (City c :
                cities) {
            if (Objects.equals(c.getName(), name))
                getWeatherYandex(c);
        }
        scanner.close();

    }

    private static void getWeatherYandex(City city) throws IOException {
        System.out.println(parserYandex(city));
    }

    private static BufferedReader getBufferedReaderYandex(City city) throws IOException {
        String request = "https://api.weather.yandex.ru/v2/forecast?lat=" + city.getLat() + "&lon=" +
                city.getLon() + "&lang=Ru-ru&limit=7";
        URL url = new URL(request);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("X-Yandex-API-Key", "e8dc4242-1276-45bb-afdd-4c0c5ac10142");
        return new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
    }

    private static String parserYandex(City city) throws IOException {
        BufferedReader reader = getBufferedReaderYandex(city);
        String line;
        String res = "";
        boolean flag = false;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        reader.close();
        int count = 0;
        TypeCondition condition = new TypeCondition();
        String[] resp = response.toString().split(",");
        for (String lineS :
                resp) {
            if (lineS.contains("date") && !lineS.contains("date_ts")) {
                res += "Дата - ";
                res += count == 0 ? lineS.split(":")[2] : lineS.split(":")[1];
                count = 1;
                flag = true;
                res += "\n";
            }
            if (lineS.contains("temp_min") && flag)
                res += "Минимальная температура - " + lineS.split(":")[1] + ("°") + ("\n");
            if (lineS.contains("temp_max") && flag)
                res += "Максимальная температура - " + lineS.split(":")[1] +("°") + ("\n");
            if (lineS.contains("wind_speed") && flag)
                res += "Скорость ветра - " + lineS.split(":")[1] + (" м/с") + ("\n");
            if (lineS.contains("condition") && flag) {
                res += "Состояние - " +
                        condition.getConditions(lineS.split(":")[1]) + ("\n") + ("\n");
                flag = false;
            }

        }
        System.out.println(response);
        return res.toString();
    }


    //@TODO add parser!
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
}