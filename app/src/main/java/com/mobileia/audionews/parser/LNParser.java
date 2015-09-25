package com.mobileia.audionews.parser;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mobileia.audionews.model.LNNews;
import com.mobileia.audionews.service.LaNacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiascamiletti on 25/9/15.
 */
public class LNParser {

    private Context mContext;

    public LNParser(Context context){
        mContext = context;
    }

    public List<LNNews> start(JsonObject object){
        List<LNNews> list = new ArrayList<LNNews>();

        JsonArray items = object.get("items").getAsJsonArray();
        for(int i = 0; i < items.size(); i++){
            JsonObject n = items.get(i).getAsJsonObject();
            list.add(startNews(n));
        }

        return list;
    }

    public LNNews startNews(JsonObject object){
        LNNews news = new LNNews();
        news.identifier = object.get("id").getAsInt();

        if(object.has("titulo") && object.get("titulo").getAsJsonArray().size() > 0){
            news.title = object.get("titulo").getAsJsonArray().get(0).getAsJsonObject().get("valor").getAsString();
        }
        if(object.has("categoria")){
            news.category = object.get("categoria").getAsJsonObject().get("valor").getAsString();
        }
        if(object.has("imagenes") && object.get("imagenes").getAsJsonArray().size() > 0){
            news.image = object.get("imagenes").getAsJsonArray().get(0).getAsJsonObject().get("src").getAsString();
        }

        return news;
    }

}
