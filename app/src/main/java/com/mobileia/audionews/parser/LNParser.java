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

    public LNNews detailNews(JsonObject object){
        LNNews news = startNews(object);

        news.date = object.get("fecha").getAsString();

        if(object.get("bajada").isJsonArray()){
            JsonArray values = object.get("bajada").getAsJsonArray();
            news.bajada = parserValorToString(values);
        }

        if(object.get("contenido").isJsonArray()){
            JsonArray values = object.get("contenido").getAsJsonArray();
            for (int i = 0; i < values.size(); i++) {
                JsonObject tag = values.get(i).getAsJsonObject();
                news.content.add(parserTagToString(tag));
            }
        }


        return news;
    }

    private String parserTagToString(JsonObject tag){
        String type = tag.get("_t").getAsString();

        if(type.compareTo("p") != 0 && type.compareTo("tag") != 0 && type.compareTo("a") != 0){
            return "";
        }

        if(tag.get("valor").isJsonObject()){
            return parserTagToString(tag.get("valor").getAsJsonObject());
        }else if(tag.get("valor").isJsonArray()){
            String data = "";
            JsonArray values = tag.get("valor").getAsJsonArray();
            for (int i = 0; i < values.size(); i++) {
                if(values.get(i).isJsonPrimitive()){
                    data += values.get(i).getAsString();
                }else if(values.get(i).isJsonObject()){
                    data += parserTagToString(values.get(i).getAsJsonObject());
                }
            }
            return data;
        }else if(tag.get("valor").isJsonPrimitive()){
            return tag.get("valor").getAsString();
        }

        return "";
    }

    private String parserValorToString(JsonArray values){
        String data = "";
        for (int i = 0; i < values.size(); i++) {
            if(values.get(i).isJsonObject()){
                JsonObject item = values.get(i).getAsJsonObject();
                if(item.get("valor").isJsonNull()){
                    continue;
                }
                data += item.get("valor").getAsString();
            }
        }
        return data;
    }

}
