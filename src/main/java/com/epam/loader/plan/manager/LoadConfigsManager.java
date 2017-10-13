package com.epam.loader.plan.manager;

import com.epam.loader.config.credentials.LoadConfigs;
import com.epam.loader.plan.plan.DownloadPlan;
import com.epam.spring.security.authenticate.impl.BaseConfigLoadAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope( "prototype" )
public class LoadConfigsManager {
  @Autowired
  private AuthenticationManager authenticationManager;
  private Map<ClusterType, DownloadPlan> downloadPlanMap;

  public LoadConfigsManager( @Autowired @Qualifier( "HDP" ) DownloadPlan hdpDownloadPlan,
                             @Autowired @Qualifier( "CDH" ) DownloadPlan cdhDownloadPlan,
                             @Autowired @Qualifier( "MAPR" ) DownloadPlan maprDownloadPlan,
                             @Autowired @Qualifier( "EMR" ) DownloadPlan emrDownloadPlan ) {
    downloadPlanMap = new HashMap<>();
    downloadPlanMap.put( ClusterType.HDP, hdpDownloadPlan );
    downloadPlanMap.put( ClusterType.CDH, cdhDownloadPlan );
    downloadPlanMap.put( ClusterType.MAPR, maprDownloadPlan );
    downloadPlanMap.put( ClusterType.EMR, emrDownloadPlan );
  }

  public enum ClusterType {
    CDH, HDP, MAPR, EMR
  }

  public boolean downloadClientConfigs( ClusterType clusterType, LoadConfigs loadConfigs ) {
    configureSecurity( loadConfigs );
    return downloadPlanMap.containsKey( clusterType ) && downloadPlanMap.get( clusterType )
      .downloadConfigs( loadConfigs.getHost(), loadConfigs.getSaveDir() );
  }

  private void configureSecurity( LoadConfigs loadConfigs ) {
    SecurityContextHolder.setStrategyName( SecurityContextHolder.MODE_GLOBAL );
    SecurityContextHolder.getContext()
      .setAuthentication( authenticationManager.authenticate(
        new BaseConfigLoadAuthentication( loadConfigs.getHttpCredentials(),
          loadConfigs.getKrb5Credentials(), loadConfigs.getSshCredentials() ) ) );
  }
}
