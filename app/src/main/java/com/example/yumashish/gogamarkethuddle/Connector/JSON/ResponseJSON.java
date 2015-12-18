package com.example.yumashish.gogamarkethuddle.Connector.JSON;

import com.google.api.client.util.Key;

import java.util.List;
import java.util.Objects;

/**
 * Created by yumashish on 10/31/15.
 */
public class ResponseJSON {
    @Key("html_attributions")
    private List<Objects> html_attributions;

    @Key("next_page_token")
    private String next_page_token;

    @Key("results")
    private List<PlaceJSON> results;

    public List<PlaceJSON> getResults() {
        return results;
    }
}
