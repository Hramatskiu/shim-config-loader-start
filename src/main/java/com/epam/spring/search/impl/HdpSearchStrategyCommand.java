package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("HDP")
public class HdpSearchStrategyCommand implements SearchStrategy {
    @Override
    public String getStrategyCommand() {
        return "clusters/";
    }

    @Override
    public List<DownloadableFile> resolveCommandResult(String commandResult, List<DownloadableFile> searchableServiceNames) throws Exception{
        String clusterName = extractClusterNameFromCommandResult(commandResult);
        if (!clusterName.isEmpty()) {
            searchableServiceNames.forEach(service -> service.setDownloadPath("clusters/" + clusterName + "/services/" + service.getServiceName().toUpperCase()
                    + "/components/" + service.getServiceName().toUpperCase() + "_CLIENT?format=client_config_tar"));

            return searchableServiceNames;
        }

        return Collections.emptyList();
    }

    private String extractClusterNameFromCommandResult(String commandResult) throws Exception{
        return new JSONObject( commandResult ).getJSONArray( "items" ).getJSONObject( 0 ).getJSONObject( "Clusters" ).getString( "cluster_name" );
    }
}
