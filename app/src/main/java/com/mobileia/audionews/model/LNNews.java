package com.mobileia.audionews.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiascamiletti on 25/9/15.
 */
public class LNNews {

    public int identifier;
    public String title;
    public String category;
    public String image = "";
    public boolean isSpeeching = false;

    public String date;
    public String bajada;

    public List<String> content = new ArrayList<String>();
}
