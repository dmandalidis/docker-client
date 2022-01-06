/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2019-2020 Dimitris Mandalidis
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
*/
package org.mandas.docker.client.builder.resteasy;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.mandas.docker.client.builder.BaseDockerClientBuilder;
import org.mandas.docker.client.builder.ProxyConfiguration;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import jakarta.ws.rs.client.Client;


public class ResteasyDockerClientBuilder extends BaseDockerClientBuilder<ResteasyDockerClientBuilder> {

  @Override
  public ResteasyDockerClientBuilder readTimeoutMillis(long readTimeoutMillis) {
    if (readTimeoutMillis == 0) {
      return super.readTimeoutMillis(-1);
    }
    return super.readTimeoutMillis(readTimeoutMillis);
  }
  
  @Override
  public ResteasyDockerClientBuilder connectTimeoutMillis(long connectTimeoutMillis) {
    if (connectTimeoutMillis == 0) {
      return super.connectTimeoutMillis(-1);
    }
    return super.connectTimeoutMillis(connectTimeoutMillis);
  }
  
  @Override
  protected Client createNoTimeoutClient() {
    ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();

    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectionRequestTimeout((int) connectTimeoutMillis)
      .setConnectTimeout((int) connectTimeoutMillis)
      .setSocketTimeout(-1)
      .build();
    
    Registry<ConnectionSocketFactory> schemeRegistry = getSchemeRegistry(uri, dockerCertificatesStore);
    HttpClientConnectionManager cm = getConnectionManager(uri, schemeRegistry, connectionPoolSize);
    HttpClientBuilder httpClientBuilder = HttpClients.custom()
        .setConnectionManager(cm)
        .setDefaultRequestConfig(requestConfig);
    
    ProxyConfiguration proxyConfiguration = proxyFromEnv();
    if (useProxy && proxyConfiguration != null) {
      if (proxyConfiguration.username() != null && proxyConfiguration.password() != null) {
        Credentials credentials = new UsernamePasswordCredentials(proxyConfiguration.username(), proxyConfiguration.password());
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(proxyConfiguration.host(), Integer.parseInt(proxyConfiguration.port())), credentials);
        httpClientBuilder.setDefaultCredentialsProvider(credProvider);
      }
      httpClientBuilder
        .setProxy(new HttpHost(proxyConfiguration.host(), Integer.parseInt(proxyConfiguration.port())))
        .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    }
    
    ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClientBuilder.build(), false);
    
    if (entityProcessing != null) {
      switch (entityProcessing) {
        case BUFFERED:
          engine.setChunked(false);
          break;
        case CHUNKED:
          engine.setChunked(true);
          break;
        default:
          throw new IllegalArgumentException("Invalid entity processing mode " + entityProcessing);
      }
    }
    builder.httpEngine(engine)
      .register(new JacksonJsonProvider());
    
    return builder.build();
  }
  
  @Override
  protected Client createClient() {
    ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();

    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectionRequestTimeout((int) connectTimeoutMillis)
      .setConnectTimeout((int) connectTimeoutMillis)
      .setSocketTimeout((int) readTimeoutMillis)
      .build();
    
    Registry<ConnectionSocketFactory> schemeRegistry = getSchemeRegistry(uri, dockerCertificatesStore);
    HttpClientConnectionManager cm = getConnectionManager(uri, schemeRegistry, connectionPoolSize);
    HttpClientBuilder httpClientBuilder = HttpClients.custom()
        .setConnectionManager(cm)
        .setDefaultRequestConfig(requestConfig);
    
    ProxyConfiguration proxyConfiguration = proxyFromEnv();
    if (useProxy && proxyConfiguration != null) {
      if (proxyConfiguration.username() != null && proxyConfiguration.password() != null) {
        Credentials credentials = new UsernamePasswordCredentials(proxyConfiguration.username(), proxyConfiguration.password());
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(proxyConfiguration.host(), Integer.parseInt(proxyConfiguration.port())), credentials);
        httpClientBuilder.setDefaultCredentialsProvider(credProvider);
      }
      httpClientBuilder
        .setProxy(new HttpHost(proxyConfiguration.host(), Integer.parseInt(proxyConfiguration.port())))
        .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    }
    
    ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClientBuilder.build(), false);
    
    if (entityProcessing != null) {
      switch (entityProcessing) {
        case BUFFERED:
          engine.setChunked(false);
          break;
        case CHUNKED:
          engine.setChunked(true);
          break;
        default:
          throw new IllegalArgumentException("Invalid entity processing mode " + entityProcessing);
      }
    }
    builder.httpEngine(engine)
      .register(new JacksonJsonProvider());
    
    return builder.build();
  }
}
