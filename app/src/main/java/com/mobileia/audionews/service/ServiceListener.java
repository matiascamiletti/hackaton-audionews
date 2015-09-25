package com.mobileia.audionews.service;

import com.mobileia.audionews.model.LNNews;

import java.util.List;

/**
 * Created by matiascamiletti on 25/9/15.
 */
public interface ServiceListener {
    void onComplete(List<LNNews> list);
}
