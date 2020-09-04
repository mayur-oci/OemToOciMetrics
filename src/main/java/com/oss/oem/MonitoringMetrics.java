package com.oss.oem; /**
 * Copyright (c) 2016, 2020, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import com.oracle.bmc.monitoring.responses.PostMetricDataResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MonitoringMetrics {


    private static MonitoringClient monitoringClient = getMonitoringClient();

    public static void postMetricsToOci(MetricsHolder bulkMetric) {
        final PostMetricDataRequest request =
                PostMetricDataRequest.builder()
                        .postMetricDataDetails(
                                PostMetricDataDetails.builder()
                                        .metricData(
                                                Arrays.asList(
                                                        MetricDataDetails.builder()
                                                                .compartmentId(ConfigHolder.monitoringCompartmentId)
                                                                .namespace(ConfigHolder.namespace)
                                                                .name(bulkMetric.metricName)
                                                                .resourceGroup(ConfigHolder.resourceGroup)
                                                                .datapoints(
                                                                        bulkMetric.datapointList)
                                                                .dimensions(bulkMetric.dimensions)
                                                                .metadata(bulkMetric.metadata)
                                                                .build()))
                                        .build())
                        .build();

        try {
            System.out.printf("Request constructed:\n%s\n\n", request.getPostMetricDataDetails());
            System.out.println("Trying to post metrics...");
            final PostMetricDataResponse response = monitoringClient.postMetricData(request);
            System.out.printf(
                    "\n\nReceived response [opc-request-id: %s]\n", response.getOpcRequestId());
            System.out.printf("%s\n\n", response.getPostMetricDataResponseDetails());
        }catch(Exception exception){
            System.out.println("Error: Could not post these metrics ... Problematic Metrics are " +  bulkMetric.toString() + "\nDue to exception " + exception);
        }

    }

    private static Map<String, String> makeMap(String... data) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < data.length; i += 2) {
            map.put(data[i], data[i + 1]);
        }
        return map;
    }

    private static MonitoringClient getMonitoringClient() {
        final ConfigFile configFile;
        try {
            configFile = ConfigFileReader.parse(ConfigHolder.ociConfigFilePath, ConfigHolder.monitoringProfileName);
            final AuthenticationDetailsProvider provider =
                    new ConfigFileAuthenticationDetailsProvider(configFile);
            final MonitoringClient monitoringClient = new MonitoringClient(provider);
            monitoringClient.setEndpoint(ConfigHolder.monitoringServiceEndpoint);
            return monitoringClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}