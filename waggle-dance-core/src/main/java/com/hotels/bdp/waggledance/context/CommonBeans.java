/**
 * Copyright (C) 2016-2022 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.bdp.waggledance.context;

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.springframework.context.annotation.Bean;

import com.hotels.bdp.waggledance.client.CloseableThriftHiveMetastoreIfaceClientFactory;
import com.hotels.bdp.waggledance.client.DefaultMetaStoreClientFactory;
import com.hotels.bdp.waggledance.client.GlueClientFactory;
import com.hotels.bdp.waggledance.client.tunnelling.TunnelingMetaStoreClientFactory;
import com.hotels.bdp.waggledance.conf.WaggleDanceConfiguration;
import com.hotels.bdp.waggledance.core.federation.service.PopulateStatusFederationService;
import com.hotels.bdp.waggledance.mapping.model.ASTQueryMapping;
import com.hotels.bdp.waggledance.mapping.model.QueryMapping;
import com.hotels.bdp.waggledance.mapping.service.PrefixNamingStrategy;
import com.hotels.bdp.waggledance.mapping.service.impl.LowerCasePrefixNamingStrategy;
import com.hotels.bdp.waggledance.mapping.service.impl.PollingFederationService;

@org.springframework.context.annotation.Configuration
public class CommonBeans {

  @Bean
  public HiveConf hiveConf(WaggleDanceConfiguration waggleDanceConfiguration) {
    Map<String, String> confProps = waggleDanceConfiguration.getConfigurationProperties();

    final HiveConf hiveConf = new HiveConf(new Configuration(false), getClass());
    // set all properties specified on the command line
    if (confProps != null) {
      for (Map.Entry<String, String> entry : confProps.entrySet()) {
        hiveConf.set(entry.getKey(), entry.getValue());
      }
    }
    return hiveConf;
  }

  @Bean
  public PrefixNamingStrategy prefixNamingStrategy(WaggleDanceConfiguration waggleDanceConfiguration) {
    return new LowerCasePrefixNamingStrategy();
  }

  @Bean
  public CloseableThriftHiveMetastoreIfaceClientFactory metaStoreClientFactory(
      WaggleDanceConfiguration waggleDanceConfiguration) {
    return new CloseableThriftHiveMetastoreIfaceClientFactory(new TunnelingMetaStoreClientFactory(),
        new DefaultMetaStoreClientFactory(), new GlueClientFactory(), waggleDanceConfiguration);
  }

  @Bean
  public QueryMapping queryMapping() {
    return ASTQueryMapping.INSTANCE;
  }

  @Bean
  public PollingFederationService pollingFederationService(
      PopulateStatusFederationService populateStatusFederationService) {
    return new PollingFederationService(populateStatusFederationService);
  }

}
