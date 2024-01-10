Create table weather
(
    id                    serial primary key not null,
    city                  varchar(25)        not null,
    date_request          date               not null,
    forecast_to_one_day   text               not null,
    forecast_to_two_day   text               not null,
    forecast_to_three_day text               not null,
    forecast_to_four_day  text               not null,
    forecast_to_five_day  text               not null,
    forecast_to_six_day   text               not null,
    forecast_to_seven_day text               not null
);

