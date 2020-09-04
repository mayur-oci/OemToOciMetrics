package com.oss.oem;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamAdminClient;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.CreateCursorDetails;
import com.oracle.bmc.streaming.model.Message;
import com.oracle.bmc.streaming.model.Stream;
import com.oracle.bmc.streaming.model.Stream.LifecycleState;
import com.oracle.bmc.streaming.requests.CreateCursorRequest;
import com.oracle.bmc.streaming.requests.GetMessagesRequest;
import com.oracle.bmc.streaming.requests.GetStreamRequest;
import com.oracle.bmc.streaming.requests.ListStreamsRequest;
import com.oracle.bmc.streaming.responses.CreateCursorResponse;
import com.oracle.bmc.streaming.responses.GetMessagesResponse;
import com.oracle.bmc.streaming.responses.GetStreamResponse;
import com.oracle.bmc.streaming.responses.ListStreamsResponse;
import com.oss.oem.pojo.MetricMessage;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;


public class OemMetricStreamReader {

    public static void main(String[] args) throws Exception {

        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(ConfigHolder.ociConfigFilePath, ConfigHolder.streamProfileName);
        try {
            readOemMessagesFromOss(provider);
        }catch(Exception e){
            throw e;
        }
    }

    private static String getCursorByPartition(
            StreamClient streamClient, String streamId, String partition) {
        System.out.println(String.format("Creating a cursor for partition %s.", partition));

        CreateCursorDetails cursorDetails =
                CreateCursorDetails.builder().partition(partition).offset(ConfigHolder.offset).type(ConfigHolder.streamConsumerCursorType).build();

        CreateCursorRequest createCursorRequest =
                CreateCursorRequest.builder()
                        .streamId(streamId)
                        .createCursorDetails(cursorDetails)
                        .build();

        CreateCursorResponse cursorResponse = streamClient.createCursor(createCursorRequest);
        return cursorResponse.getCursor().getValue();
    }

    private static Stream getStream(StreamAdminClient adminClient, String streamId) {
        GetStreamResponse getResponse =
                adminClient.getStream(GetStreamRequest.builder().streamId(streamId).build());
        return getResponse.getStream();
    }

    private static Stream getStreamOfOemMetrics(
            StreamAdminClient adminClient)
            throws Exception {

        ListStreamsRequest listRequest =
                ListStreamsRequest.builder()
                        .compartmentId(ConfigHolder.streamCompartmentId)
                        .lifecycleState(LifecycleState.Active)
                        .streamPoolId(ConfigHolder.streamPoolOcid)
                        .name(ConfigHolder.streamName)
                        .build();

        ListStreamsResponse listResponse = adminClient.listStreams(listRequest);

        if (!listResponse.getItems().isEmpty()) {
            // if we find an active stream with the correct name, we'll use it.
            System.out.println(String.format("An active stream named %s was found.", ConfigHolder.streamName));

            String streamId = listResponse.getItems().get(0).getId();
            return getStream(adminClient, streamId);
        }

        System.out.println(
                String.format("No active stream named %s was found in stream pool %s", ConfigHolder.streamPoolName, ConfigHolder.streamPoolName));

        return null;
    }


    private static void readOemMessagesFromOss(AbstractAuthenticationDetailsProvider provider) throws Exception {
        final StreamAdminClient adminClient = StreamAdminClient.builder().build(provider);
        Stream stream = getStreamOfOemMetrics(adminClient);
        StreamClient streamClient = StreamClient.builder().stream(stream).build(provider);
        String streamId = stream.getId();
        String partitionCursor = getCursorByPartition(streamClient, streamId, "0");

        int messageOffset = 0;
        Gson gson = new GsonBuilder().create();
        do {
            GetMessagesRequest getRequest =
                    GetMessagesRequest.builder()
                            .streamId(streamId)
                            .cursor(partitionCursor)
                            .limit(ConfigHolder.streamReadChunkSize)
                            .build();

            GetMessagesResponse getResponse = streamClient.getMessages(getRequest);

            // process the messages
            System.out.println(String.format("Read %s messages.", getResponse.getItems().size()));
            OemMetricsToOciMetricsConversion ociMetricsMap = new OemMetricsToOciMetricsConversion();
            for (Message message : getResponse.getItems()) {
                String streamMessage = new String(message.getValue(), UTF_8);
                MetricMessage metricMessage = gson.fromJson(streamMessage, MetricMessage.class);
                System.out.println(
                        String.format(metricMessage + "Message offset %s number %d: Message value %s", message.getOffset(), messageOffset++,
                                streamMessage));
                ociMetricsMap.addSingleMetric(metricMessage.getPayload());
            }

            // getMessages is a throttled method; clients should retrieve sufficiently large message
            // batches, as to avoid too many http requests.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

            for(Map.Entry bulkMetric:ociMetricsMap.metricKeyToMetricHolderMap.entrySet()){
                   MetricsHolder metricsHolder = (MetricsHolder) bulkMetric.getValue();
                if(metricsHolder.datapointList.size() > 0) {
                    System.out.println(metricsHolder);
                    MonitoringMetrics.postMetricsToOci(metricsHolder);
               }
            }

            // use the next-cursor for iteration
            partitionCursor = getResponse.getOpcNextCursor();
        } while (ConfigHolder.runStreamConsumerTillEternity);
    }
}
