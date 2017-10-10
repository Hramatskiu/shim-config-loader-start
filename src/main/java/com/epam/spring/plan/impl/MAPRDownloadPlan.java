package com.epam.spring.plan.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import org.springframework.stereotype.Component;

@Component("mapr-plan")
public class MAPRDownloadPlan extends DownloadPlan {
    protected MAPRDownloadPlan(DownloadFunction downloadFunction, SearchStrategy searchStrategy) {
        super(downloadFunction, searchStrategy);
    }

    @Override
    protected LoadPathConfig createLoadPathConfig(String hostName, String destPrefix) {
        return null;
    }

    @Override
    protected DownloadConfigsCondition createDownloadConfigsCondition() {
        return null;
    }
}
