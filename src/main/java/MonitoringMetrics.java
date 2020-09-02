/**
 * Copyright (c) 2016, 2020, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import com.oracle.bmc.monitoring.responses.PostMetricDataResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class demonstrates how to use the Monitoring api in the Java SDK. This will cover:
 *
 * <ul>
 *   <li>Posting metric data</li>
 * </ul>
 *
 * This class makes the following assumptions:
 * <ul>
 *   <li>
 *      The configuration file used by service clients will be sourced from the default
 *      location (~/.oci/config) and the DEFAULT profile will be used
 *   </li>
 *   <li>Region where the example will be run is: us-phoenix-1</li>
 *   <li>
 *      The user has the appropriate permissions to post metrics in the compartment specified.
 *   </li>
 * <ul>
 */
public class MonitoringMetrics {

    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "MAYUR_ADMIN_PHX";
    private static final String compartmentId = "ocid1.compartment.oc1..aaaaaaaa2z4wup7a4enznwxi3mkk55cperdk3fcotagepjnan5utdb3tvakq";
    private static  final String namespaceName = "oem_oci_ns";
    private static  final String metricName = "testMetricName";
    private static final String ociMonitoringService = "https://telemetry-ingestion.us-phoenix-1.oraclecloud.com/";
    private static final String resourceGroupName = "testResourceGroup";

    private static void printUsageAndThrowException(String message) {
        throw new IllegalArgumentException(
                message
                        + "Usage:\n"
                        + "MonitoringMetricPostExample <compartment> <namespace> <metricName> [oci-profile-name]\n");
    }

    /**
     * The entry point for the example.
     */
    public static void main(String[] args) throws Exception {
        try (MonitoringClient monitoringClient = getMonitoringClient(CONFIG_PROFILE)) {
            post(monitoringClient, compartmentId, namespaceName, metricName);
        }
    }

    private static void post(
            MonitoringClient monitoringClient,
            String compartment,
            String namespace,
            String metricName) {
        final PostMetricDataRequest request =
                PostMetricDataRequest.builder()
                        .postMetricDataDetails(
                                PostMetricDataDetails.builder()
                                        .metricData(
                                                Arrays.asList(
                                                        MetricDataDetails.builder()
                                                                .compartmentId(compartment)
                                                                .namespace(namespace)
                                                                .name(metricName)
                                                                .resourceGroup(resourceGroupName)
                                                                .datapoints(
                                                                        Arrays.asList(
                                                                                Datapoint.builder()
                                                                                        .timestamp(
                                                                                                new Date())
                                                                                        .count(1)
                                                                                        .value(
                                                                                                15.25)
                                                                                        .build(),
                                                                                Datapoint.builder()
                                                                                        .timestamp(
                                                                                                new Date())
                                                                                        .count(7)
                                                                                        .value(.007)
                                                                                        .build()))
                                                                .dimensions(
                                                                        makeMap(
                                                                                "region",
                                                                                Region.US_PHOENIX_1
                                                                                        .getRegionId(),
                                                                                "host",
                                                                                "some-host"))
                                                                .build()))
                                        .build())
                        .build();

        System.out.printf("Request constructed:\n%s\n\n", request.getPostMetricDataDetails());

        System.out.println("Sending...");
        final PostMetricDataResponse response = monitoringClient.postMetricData(request);

        System.out.printf(
                "\n\nReceived response [opc-request-id: %s]\n", response.getOpcRequestId());
        System.out.printf("%s\n\n", response.getPostMetricDataResponseDetails());
    }

    private static Map<String, String> makeMap(String... data) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < data.length; i += 2) {
            map.put(data[i], data[i + 1]);
        }
        return map;
    }

    private static MonitoringClient getMonitoringClient(String profile) throws IOException {
        final ConfigFile configFile = ConfigFileReader.parse(CONFIG_LOCATION, profile);
        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configFile);
        final MonitoringClient monitoringClient = new MonitoringClient(provider);
        monitoringClient.setEndpoint(ociMonitoringService);
        return monitoringClient;
    }
}