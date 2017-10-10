package com.epam.spring.function;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;

public abstract class DownloadFunction {
  public abstract void downloadConfigs( DownloadConfigsCondition downloadConfigsCondition,
                                        SearchStrategy searchStrategy,
                                        DownloadPlan.LoadPathConfig loadPathConfig );
}
