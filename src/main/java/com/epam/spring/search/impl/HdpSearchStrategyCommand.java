package com.epam.spring.search.impl;

import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("HDP")
public class HdpSearchStrategyCommand implements SearchStrategy {
    @Override
    public String getStrategyCommand() {
        return "clusters/";
    }

    @Override
    public List<String> resolveCommandResult(String commandResult, List<String> searchableServiceNames) {
        return searchableServiceNames.stream().map(serviceName -> "clusters/" + extractClusterNameFromCommandResult(commandResult) + "/services/" + serviceName.toUpperCase()
                + "/components/" + serviceName.toUpperCase() + "_CLIENT?format=client_config_tar").collect(Collectors.toList());
    }

    //Think about
    private String extractClusterNameFromCommandResult(String commandResult) {
        try {
            return new JSONObject( commandResult ).getJSONArray( "items" ).getJSONObject( 0 ).getJSONObject( "Clusters" ).getString( "cluster_name" );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }
}
