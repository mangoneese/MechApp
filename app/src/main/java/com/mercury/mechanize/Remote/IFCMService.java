package com.mercury.mechanize.Remote;

import com.mercury.mechanize.Model.DataMessage;
import com.mercury.mechanize.Model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA6RTb-qE:APA91bEsYl5ytp1vGlD9zrQ580qSqv_3WEzIGNDiH2wo8_TaPP8nl69PiXaOlaMl4lA3Rq8INwA3HFcQfbtV45hoC9mXv-bs2JLLeMeShZ0bIqXbN8CaNw8JiiR0cc426F4TzKWG5bWaaDcbLtrQHxhcS0ygU4MvPg"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage (@Body DataMessage body);
}
