/**
 * Copyright (c) 2016, 2020, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamAdminClient;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.CreateCursorDetails;
import com.oracle.bmc.streaming.model.CreateCursorDetails.Type;
import com.oracle.bmc.streaming.model.CreateStreamDetails;
import com.oracle.bmc.streaming.model.Message;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.model.PutMessagesResultEntry;
import com.oracle.bmc.streaming.model.Stream;
import com.oracle.bmc.streaming.model.Stream.LifecycleState;
import com.oracle.bmc.streaming.requests.CreateCursorRequest;
import com.oracle.bmc.streaming.requests.CreateStreamRequest;
import com.oracle.bmc.streaming.requests.GetMessagesRequest;
import com.oracle.bmc.streaming.requests.GetStreamRequest;
import com.oracle.bmc.streaming.requests.ListStreamsRequest;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import com.oracle.bmc.streaming.responses.CreateCursorResponse;
import com.oracle.bmc.streaming.responses.CreateStreamResponse;
import com.oracle.bmc.streaming.responses.GetMessagesResponse;
import com.oracle.bmc.streaming.responses.GetStreamResponse;
import com.oracle.bmc.streaming.responses.ListStreamsResponse;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;


public class OemStream {

    public static void main(String[] args) throws Exception {

        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(ConfigHolder.ociConfigFilePath, ConfigHolder.streamProfileName);

        // Create an admin-client for the phoenix region.
        final StreamAdminClient adminClient = StreamAdminClient.builder().build(provider);

        // We want to be good samaritan, so we'll reuse a stream if its already created.
        // This will utilize ListStreams() to determine if a stream exists and return it, or create a new one.
        Stream stream = getStreamOfOemMetrics(adminClient);

        // Streams are assigned a specific endpoint url based on where they are provisioned.
        // Create a stream client using the provided message endpoint.
        StreamClient streamClient = StreamClient.builder().stream(stream).build(provider);

        String streamId = stream.getId();

        // Use a cursor for getting messages; each getMessages call will return a next-cursor for iteration.
        // There are a couple kinds of cursors.

        // A cursor can be created at a given partition/offset.
        // This gives explicit offset management control to the consumer.
        System.out.println("Starting a simple message loop with a partition cursor");
        String partitionCursor = getCursorByPartition(streamClient, streamId, "0");
        simpleMessageLoop(streamClient, streamId, partitionCursor);
    }

    private static String getCursorByPartition(
            StreamClient streamClient, String streamId, String partition) {
        System.out.println(String.format("Creating a cursor for partition %s.", partition));

        CreateCursorDetails cursorDetails =
                CreateCursorDetails.builder().partition(partition).type(Type.TrimHorizon).build();

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


    private static void simpleMessageLoop(
            StreamClient streamClient, String streamId, String initialCursor) {
        String cursor = initialCursor;
        for (int i = 0; i < 10; i++) {

            GetMessagesRequest getRequest =
                    GetMessagesRequest.builder()
                            .streamId(streamId)
                            .cursor(cursor)
                            .limit(10)
                            .build();

            GetMessagesResponse getResponse = streamClient.getMessages(getRequest);

            // process the messages
            System.out.println(String.format("Read %s messages.", getResponse.getItems().size()));
            for (Message message : getResponse.getItems()) {
                System.out.println(
                        String.format(
                                "%s: %s",
                                new String(message.getKey(), UTF_8),
                                new String(message.getValue(), UTF_8)));
            }

            // getMessages is a throttled method; clients should retrieve sufficiently large message
            // batches, as to avoid too many http requests.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

            // use the next-cursor for iteration
            cursor = getResponse.getOpcNextCursor();
        }
    }
}
