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
package org.mandas.docker.client.builder.jersey;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.mandas.docker.client.DockerCertificatesStore;
import org.mandas.docker.client.UnixConnectionSocketFactory;
import org.mandas.docker.client.builder.BaseDockerClientBuilder;
import org.mandas.docker.client.builder.ProxyConfiguration;
import org.mandas.docker.client.npipe.NpipeConnectionSocketFactory;

public class JerseyDockerClientBuilder extends BaseDockerClientBuilder<JerseyDockerClientBuilder> {

  private Registry<ConnectionSocketFactory> getSchemeRegistry(URI uri, DockerCertificatesStore certificateStore) {
    final SSLConnectionSocketFactory https;
    if (dockerCertificatesStore == null) {
      https = SSLConnectionSocketFactory.getSocketFactory();
    } else {
      https = new SSLConnectionSocketFactory(dockerCertificatesStore.sslContext(),
                                             dockerCertificatesStore.hostnameVerifier());
    }

    final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
        .<ConnectionSocketFactory>create()
        .register("https", https)
        .register("http", PlainConnectionSocketFactory.getSocketFactory());

    if (uri.getScheme().equals(UNIX_SCHEME)) {
      registryBuilder.register(UNIX_SCHEME, new UnixConnectionSocketFactory(uri));
    }
    
    if (uri.getScheme().equals(NPIPE_SCHEME)) {
      registryBuilder.register(NPIPE_SCHEME, new NpipeConnectionSocketFactory(uri));
    }

    return registryBuilder.build();
  }
  
  private HttpClientConnectionManager getConnectionManager(URI uri, Registry<ConnectionSocketFactory> schemeRegistry, int connectionPoolSize) {
    if (uri.getScheme().equals(NPIPE_SCHEME)) {
      final BasicHttpClientConnectionManager bm = 
          new BasicHttpClientConnectionManager(schemeRegistry);
      return bm;
    }
    final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(schemeRegistry);
    // Use all available connections instead of artificially limiting ourselves to 2 per server.
    cm.setMaxTotal(connectionPoolSize);
    cm.setDefaultMaxPerRoute(cm.getMaxTotal());
    return cm;
  }
  
  private ClientConfig updateProxy(ClientConfig config) {
    ProxyConfiguration proxyConfiguration = proxyFromEnv();
    if (proxyConfiguration == null) {
      return config;
    }
    
    String proxyHost = proxyConfiguration.host();
    
    config.property(ClientProperties.PROXY_URI, (!proxyHost.startsWith("http") ? "http://" : "")
            + proxyHost + ":" + proxyConfiguration.port());
    
    if (proxyConfiguration.username() != null) {
      config.property(ClientProperties.PROXY_USERNAME, proxyConfiguration.username());
    }
    if (proxyConfiguration.password() != null) {
      config.property(ClientProperties.PROXY_PASSWORD, proxyConfiguration.password());
    }

    //ensure Content-Length is populated before sending request via proxy.
    config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
    return config;
  }
  
  @Override
  protected Client createClient() {
    Registry<ConnectionSocketFactory> schemeRegistry = getSchemeRegistry(uri, dockerCertificatesStore);
    
    final HttpClientConnectionManager cm = getConnectionManager(uri, schemeRegistry, connectionPoolSize);

    final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout((int) connectTimeoutMillis)
        .setConnectTimeout((int) connectTimeoutMillis)
        .setSocketTimeout((int) readTimeoutMillis)
        .build();

    ClientConfig config = new ClientConfig(JacksonFeature.class);
    
    if (useProxy) {
      config = updateProxy(config);
    }
    
    config
      .connectorProvider(new ApacheConnectorProvider())
      .property(ApacheClientProperties.CONNECTION_MANAGER, cm)
      .property(ApacheClientProperties.CONNECTION_MANAGER_SHARED, "true")
      .property(ApacheClientProperties.REQUEST_CONFIG, requestConfig);

    if (entityProcessing != null) {
      switch (entityProcessing) {
        case BUFFERED:
          config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
          break;
        case CHUNKED:
          config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.CHUNKED);
          break;
        default:
          throw new IllegalArgumentException("Invalid entity processing mode " + entityProcessing);
      }
    }

    return ClientBuilder.newBuilder()
        .withConfig(config)
        .build();
  }
}