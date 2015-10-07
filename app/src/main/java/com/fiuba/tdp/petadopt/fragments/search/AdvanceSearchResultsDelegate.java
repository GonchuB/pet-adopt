package com.fiuba.tdp.petadopt.fragments.search;

import org.json.JSONArray;

/**
 * Created by tomas on 04/10/15.
 */
public interface AdvanceSearchResultsDelegate {
    public void resultsAvailable(JSONArray body);
}
