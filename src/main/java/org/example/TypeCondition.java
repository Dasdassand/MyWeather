package org.example;

import java.util.HashMap;

public class TypeCondition {
    private final HashMap<String, String> conditions = new HashMap<>();

    {
        conditions.put("\"clear\"", "ясно");
        conditions.put("\"partly-cloudy\"", "малооблачно");
        conditions.put("\"cloudy\"", "облачно с прояснениями");
        conditions.put("\"overcast\"", "пасмурно");
        conditions.put("\"light-rain\"", "небольшой дождь");
        conditions.put("\"rain\"", "дождь");
        conditions.put("\"heavy-rain\"", "сильный дождь");
        conditions.put("\"showers\"", "ливень");
        conditions.put("\"wet-snow\"", "дождь со снегом");
        conditions.put("\"light-snow\"", "небольшой снег");
        conditions.put("\"snow\"", "снег");
        conditions.put("\"snow-showers\"", "снегопад");
        conditions.put("\"hail\"", "град");
        conditions.put("\"thunderstorm\"", "гроза");
        conditions.put("\"thunderstorm-with-rain\"", "дождь с грозой");
        conditions.put("\" thunderstorm-with-hail\"", "гроза с градом");
    }

    public String getConditions(String cond) {
        return conditions.get(cond);
    }
}
