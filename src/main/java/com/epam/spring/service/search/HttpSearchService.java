package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HttpSearchService {

    public List<DownloadableFile> searchForConfigsLocation(String remoteUrl, List<DownloadableFile> searchableServiceNames, SearchStrategy searchStrategy) throws Exception {
        HttpResponse response = askForClientsConfigs("http://" + remoteUrl + searchStrategy.getStrategyCommand());

        List<DownloadableFile> files = searchStrategy.resolveCommandResult(new String(IOUtils.toByteArray(response.getEntity().getContent())), searchableServiceNames);
        files.forEach(service -> service.setDownloadPath("http://" + remoteUrl + service.getDownloadPath()));

        return files;
    }

    private HttpResponse askForClientsConfigs(String uri) throws Exception {
        return CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
                .execute(CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest(uri));
    }
}
