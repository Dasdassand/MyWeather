package org.example.server;

import org.example.db.Repository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @TODO - сделать поддержку основываясь, на запросах прошлых дней
 */
@WebServlet("/analysis")
public class MyServletAnalysis extends HttpServlet {
    private final Repository repository = new Repository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var date = req.getHeader("date");
        var cityID = req.getHeader("city_id");
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        try (var writer = resp.getWriter()) {
            writer.println(analysis(date, Integer.parseInt(cityID)));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    private String analysis(String date, int id) throws SQLException, ClassNotFoundException {
        String cityName = "";
        for (City cityC :
                City.getCities()) {
            if (cityC.getId() == id) {
                cityName = cityC.getName();
                break;
            }
        }
        var data = getData(date, cityName).split("\n");
        var countRecord = data.length / 5;
        int indexTempMin = 2, indexTempMax = 1, maxT, minT;
        //Ходть через 2
        class Temp {
            int minTemp;
            int maxTemp;

            public Temp(int minTemp, int maxTemp) {
                this.minTemp = minTemp;
                this.maxTemp = maxTemp;
            }

            public boolean equals(Temp temp) {
                return (this.maxTemp == temp.maxTemp) && (this.minTemp == temp.minTemp);
            }
        }
        List<Temp> temps = new ArrayList<>();

        for (int i = 0; i < countRecord; i++) {
            maxT = Integer.parseInt(data[indexTempMax]
                    .split(":")[1].replaceAll("°", "")
                    .replaceAll(" ", ""));
            minT = Integer.parseInt(data[indexTempMin]
                    .split(":")[1].replaceAll("°", "")
                    .replaceAll(" ", ""));
            indexTempMax += 5;
            indexTempMin += 5;
            temps.add(new Temp(minT, maxT));
        }
        int tMaxDif = 0, tMinDif = 0;
        boolean flagWhile;
        do {
            flagWhile = false;
            if (temps.size() == 0) {
                break;
            }
            var t = temps.get(0);
            for (int j = 1; j < temps.size(); j++) {
                if (!t.equals(temps.get(j))) {
                    flagWhile = true;
                    var i = temps.get(j);
                    temps.remove(j);
                    if (t.maxTemp != i.maxTemp && (t.maxTemp - i.maxTemp) > tMaxDif) {
                        tMaxDif = t.maxTemp - i.maxTemp;
                    }
                    if (t.minTemp != i.minTemp && (t.minTemp - i.minTemp) > tMinDif) {
                        tMinDif = t.minTemp - i. minTemp ;
                    }
                }
            }
        } while (flagWhile);

        if (tMaxDif == 0 && tMinDif == 0) {
            return "В течение дня средняя темперура не изменялась";
        } else {
            return "В течение дня измениение максимальной температуры составило - " + tMaxDif + "°"
                    + ", а минимальной - " + tMinDif + "°.";
        }
    }

    private String getData(String date, String cityName) throws SQLException, ClassNotFoundException {
        var resultSet = repository.getResultSet("SELECT forecast_to_one_day from weather where  date_request = " +
                "to_date('" + date + "','YYYY-MM-DD') AND city ='" + cityName + "';");
        StringBuilder res = new StringBuilder();
        while (resultSet.next()) {
            res.append(resultSet.getString(1)).append("\n");
        }
        return res.toString();
    }
}
