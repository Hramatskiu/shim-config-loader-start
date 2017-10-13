package com.epam.loader.plan.function;

import com.epam.loader.config.condition.DownloadConfigsCondition;
import com.epam.loader.plan.plan.DownloadPlan;
import com.epam.loader.plan.strategy.SearchStrategy;

public abstract class DownloadFunction {
  public abstract void downloadConfigs( DownloadConfigsCondition downloadConfigsCondition,
                                        SearchStrategy searchStrategy,
                                        DownloadPlan.LoadPathConfig loadPathConfig );
}
