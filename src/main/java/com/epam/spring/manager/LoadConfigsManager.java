package com.epam.spring.manager;

import com.epam.spring.authenticate.impl.BaseConfigLoadAuthentication;
import com.epam.spring.config.LoadConfigs;
import com.epam.spring.plan.DownloadPlan;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoadConfigsManager {
    @Autowired
    private AuthenticationManager authenticationManager;
    private Map<ClusterType, DownloadPlan> downloadPlanMap;

    public LoadConfigsManager(@Autowired @Qualifier("hdp-plan") DownloadPlan hdpDownloadPlan,
                              @Autowired @Qualifier("cdh-plan") DownloadPlan cdhDownloadPlan) {
        downloadPlanMap = new HashMap<>();
        downloadPlanMap.put(ClusterType.HDP, hdpDownloadPlan);
        downloadPlanMap.put(ClusterType.CDH, cdhDownloadPlan);

    }

    public enum ClusterType {
        CDH, HDP, MAPR, EMR
    }

    public boolean downloadClientConfigs(ClusterType clusterType, LoadConfigs loadConfigs) throws Exception{
        configureSecurity(loadConfigs);
        return downloadPlanMap.containsKey(clusterType) && downloadPlanMap.get(clusterType).downloadConfigs(loadConfigs.getHost(), loadConfigs.getSaveDir());
    }

    private void configureSecurity(LoadConfigs loadConfigs) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext()
                .setAuthentication(authenticationManager.authenticate(
                        new BaseConfigLoadAuthentication(loadConfigs.getHttpCredentials(),
                                loadConfigs.getKrb5Credentials(), loadConfigs.getSshCredentials())));
    }
}
