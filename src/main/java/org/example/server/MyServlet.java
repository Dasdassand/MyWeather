package org.example.server;

import com.google.gson.Gson;
import org.example.db.Repository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 * @author Dasdassand
 */
@WebServlet("/weather")
public class MyServlet extends HttpServlet {
    private final Repository repository = new Repository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var json = req.getHeader("city");
        City city = new Gson().fromJson(json, City.class);
        var cities = City.getCities();

        for (City c :
                cities) {
            if (city.getId() == c.getId())
                city.setName(c.getName());
        }

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
        var weathers = parserYandex(city);
        StringBuilder res = new StringBuilder();
        for (String s : weathers) {
            res.append(s).append("\n").append("\n");
        }
        addValueToDB(weathers, city.getName());
        return res.toString();
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
    private String[] parserYandex(City city) throws IOException {
        BufferedReader reader = getBufferedReaderYandex(city);
        String line;
        boolean flag = false;
        String tmp = "";
        String[] res = new String[7];
        StringBuilder response = new StringBuilder();
        int count = 0;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        int index = 0;
        TypeCondition condition = new TypeCondition();
        String[] resp = response.toString().split(",");

        for (String lineS :
                resp) {
            if (lineS.contains("date") && !lineS.contains("date_ts")) {
                tmp += "Дата : ";
                tmp += count == 0
                        ? lineS.split(":")[2].replaceAll("\"", "").replaceAll(" ", "")
                        : lineS.split(":")[1].replaceAll("\"", "").replaceAll(" ", "");
                count = 1;
                tmp += "\n";
            }
            if (lineS.contains("\"day\""))
                flag = true;
            if (lineS.contains("temp_min") && flag)
                tmp += "Минимальная температура : " + lineS.split(":")[1] + ("°") + ("\n");
            if (lineS.contains("temp_max") && flag)
                tmp += "Максимальная температура : " + lineS.split(":")[1] + ("°") + ("\n");
            if (lineS.contains("wind_speed") && flag)
                tmp += "Скорость ветра - " + lineS.split(":")[1] + (" м/с") + ("\n");
            if (lineS.contains("condition") && flag) {
                tmp += "Состояние - " +
                        condition.getConditions(lineS.split(":")[1]);
                res[index] = tmp;
                index++;
                flag = false;
                tmp = "";
            }

        }
        return res;
    }

    private void addValueToDB(String[] values, String nameCity) {
        try {
            var date = values[0].split("\n")[0].split(":")[1].replaceAll(" ", "");
            String query = " INSERT INTO weather(city, date_request, forecast_to_one_day, forecast_to_two_day," +
                    " forecast_to_three_day, forecast_to_four_day, forecast_to_five_day," +
                    " forecast_to_six_day, forecast_to_seven_day) VALUES ('" + nameCity + "',to_date('" + date + "','YYYY-MM-DD'),'"
                    + values[0] + "','" + values[1] + "','" + values[2] + "','" + values[3] + "','"
                    + values[4] + "','" + values[5] + "','" + values[6] + "');";
            repository.addValue(query);
            repository.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

}
