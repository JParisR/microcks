/*
 * Licensed to Laurent Broudoux (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.lbroudoux.microcks.config;

import com.github.lbroudoux.microcks.web.filter.CorsFilter;
import com.github.lbroudoux.microcks.web.filter.StaticResourcesFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * @author laurent
 */
@Configuration
public class WebConfiguration implements ServletContextInitializer {

   /** A simple logger for diagnostic messages. */
   private static Logger log = LoggerFactory.getLogger(WebConfiguration.class);

   @Autowired
   private Environment env;


   @Override
   public void onStartup(ServletContext servletContext) throws ServletException {
      log.info("Starting web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
      EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
      initCORSFilter(servletContext, disps);
      if (env.acceptsProfiles(ConfigurationConstants.PROFILE_PRODUCTION)) {
         //initStaticResourcesFilter(servletContext, disps);
      }
      log.info("Web application fully configured");
   }

   /** */
   private void initCORSFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
      FilterRegistration.Dynamic corsFilter = servletContext.addFilter("corsFilter", new CorsFilter());
      corsFilter.addMappingForUrlPatterns(disps, true, "/api/*");
      corsFilter.setAsyncSupported(true);
   }

   /** */
   private void initStaticResourcesFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
      log.debug("Registering static resources production Filter");
      FilterRegistration.Dynamic resFilter = servletContext.addFilter("staticResourcesFilter", new StaticResourcesFilter());
      resFilter.addMappingForUrlPatterns(disps, true, "/");
      resFilter.addMappingForUrlPatterns(disps, true, "/index.html");
      resFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
      resFilter.addMappingForUrlPatterns(disps, true, "/images/*");
      resFilter.addMappingForUrlPatterns(disps, true, "/styles/*");
      resFilter.addMappingForUrlPatterns(disps, true, "/views/*");
      resFilter.addMappingForUrlPatterns(disps, true, "/bower_components/*");
      resFilter.setAsyncSupported(true);
   }
}
