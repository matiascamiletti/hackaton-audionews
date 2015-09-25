package com.mobileia.audionews.service;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobileia.audionews.model.LNNews;
import com.mobileia.audionews.parser.LNParser;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by matiascamiletti on 25/9/15.
 */
public class LaNacion {

    public static void getLastNews(final Context context, final ServiceListener listener){
        Ion.with(context)
            .load("http://contenidos.lanacion.com.ar/json/acumulado-ultimas")
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    // do stuff with the result or error
                    if(e != null || result == null){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LNParser parser = new LNParser(context);
                    listener.onComplete(parser.start(result));
                }
            });
    }

    public static JsonObject getNews(Context context, int identifier) throws ExecutionException, InterruptedException {
        return Ion.with(context)
                .load("http://contenidos.lanacion.com.ar/json/nota/" + identifier)
                .asJsonObject()
                .get();
    }

}
