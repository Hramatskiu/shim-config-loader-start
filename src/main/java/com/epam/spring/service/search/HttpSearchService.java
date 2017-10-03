package com.epam.spring.service.search;

import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HttpSearchService {
    @Autowired
    @Qualifier("HDP") //for test
    private SearchStrategy searchStrategy;

    public List<String> searchForConfigsLocation(String remoteUrl, List<String> searchableServiceNames) throws Exception{
        HttpResponse response = askForClientsConfigs("http://" + remoteUrl + searchStrategy.getStrategyCommand());

        return searchStrategy.resolveCommandResult(new String(IOUtils.toByteArray(response.getEntity().getContent())), searchableServiceNames).stream()
                .map(uri -> "http://" + remoteUrl + uri).collect(Collectors.toList());
    }

    private HttpResponse askForClientsConfigs(String uri) throws Exception{
        return CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
                .execute(CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest(uri));
    }
}
