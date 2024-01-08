package org.example.server;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Dasdassand
 */
@WebServlet("/weather")
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var json = req.getHeader("city");
        City city = new Gson().fromJson(json, City.class);
        var res = getYandex(city);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        try (var writer = resp.getWriter()) {
            writer.println(res);
        }

    }

    /**
     * @param city - город
     * @return res - строка с описанием погоды на неделю
     * @throws IOException
     */
    private String getYandex(City city) throws IOException {
        return parserYandex(city);
    }

    /**
     * @param city - город
     * @return reader - ответ с api yandex в формате json
     * @throws IOException
     */
    private BufferedReader getBufferedReaderYandex(City city) throws IOException {
        String request = "https://api.weather.yandex.ru/v2/forecast?lat=" + city.getLat() + "&lon=" +
                city.getLon() + "&lang=Ru-ru&limit=7&hours=false&extra=false";
        URL url = new URL(request);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("X-Yandex-API-Key", "e8dc4242-1276-45bb-afdd-4c0c5ac10142");
        return new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
    }

    /**
     * @param city - Город
     * @return res - выдержка из json с основной информацией неедльной температурой
     * @throws IOException
     */
    private String parserYandex(City city) throws IOException {
        BufferedReader reader = getBufferedReaderYandex(city);
        String line;
        boolean flag = false;
        String res = "";
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
                res += "\n";
            }
            if (lineS.contains("\"day\""))
                flag = true;
            if (lineS.contains("temp_min") && flag)
                res += "Минимальная температура - " + lineS.split(":")[1] + ("°") + ("\n");
            if (lineS.contains("temp_max") && flag)
                res += "Максимальная температура - " + lineS.split(":")[1] + ("°") + ("\n");
            if (lineS.contains("wind_speed") && flag)
                res += "Скорость ветра - " + lineS.split(":")[1] + (" м/с") + ("\n");
            if (lineS.contains("condition") && flag) {
                res += "Состояние - " +
                        condition.getConditions(lineS.split(":")[1]) + ("\n") + ("\n");
                flag = false;
            }

        }
        System.out.println(response);
        return res;
    }
}
