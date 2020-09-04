package com.oss.oem;

import com.oracle.bmc.monitoring.model.Datapoint;
import com.oss.oem.pojo.Payload;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OemMetricsToOciMetricsConversion {

    Map<String, MetricsHolder> metricKeyToMetricHolderMap = new HashMap<>();

    // static Long twoHrsBackTimeStamp = System.currentTimeMillis() - 2*60*60*1000;
    static Long highestTimeStampSeenSoFar = new Date().getTime();

    void addSingleMetric(Payload payload) {

        // COLUMN_LABEL -> metadata unit = group
        // METRIC_LABEL ->  metric name = group
        // metric name and column -> metadata/dimention = group
        // TARGET_NAME and TARGET_TYPE -> dimension = group
        Date timestamp = new Date(payload.getCOLLECTIONTIMESTAMP());
        Long twoHrsBackTimeStamp = System.currentTimeMillis() - 2*60*60*1000;
        if(timestamp.getTime() < twoHrsBackTimeStamp) {
          //skipping older than 2hrs messages since OCI Monitoring endpoint does not accept anyways
          return;
        }

        String metricKey = payload.getTARGETTYPE().trim() + payload.getTARGETNAME().trim() +
                payload.getMETRICLABEL().trim() + payload.getMETRICNAME().trim() + payload.getMETRICCOLUMN().trim() +
                payload.getCOLUMNLABEL().trim();

        Double value;
        try {
            value = Double.parseDouble(payload.getVALUE());
        }catch(NumberFormatException | NullPointerException exception){
            value = 0.0d;
        }

        MetricsHolder metricsHolder;
        if (metricKeyToMetricHolderMap.containsKey(metricKey) == false) {
            metricsHolder = new MetricsHolder();

            metricsHolder.metricName = payload.getMETRICLABEL().toLowerCase().trim().replace(" ","_");

            metricsHolder.metadata.put("unit", payload.getCOLUMNLABEL().toLowerCase().trim().replace(" ","_"));

            metricsHolder.dimensions.put("TARGET_TYPE".toLowerCase(), payload.getTARGETTYPE().toLowerCase().trim().replace(" ","_"));
            metricsHolder.dimensions.put("TARGET_NAME".toLowerCase(), payload.getTARGETNAME().toLowerCase().trim().replace(" ","_"));
            metricsHolder.dimensions.put("METRIC_COLUMN".toLowerCase(), payload.getMETRICCOLUMN().toLowerCase().trim().replace(" ","_"));
            metricsHolder.dimensions.put("METRIC_NAME".toLowerCase(), payload.getMETRICNAME().toLowerCase().trim().replace(" ","_"));

            metricKeyToMetricHolderMap.put(metricKey, metricsHolder);
        } else {
            metricsHolder = metricKeyToMetricHolderMap.get(metricKey);
        }

//        Date timestamp = new Date(payload.getCOLLECTIONTIMESTAMP());
//        if(timestamp.getTime() < twoHrsBackTimeStamp){
//          timestamp = new Date(highestTimeStampSeenSoFar);
//          highestTimeStampSeenSoFar = highestTimeStampSeenSoFar + 60*1000l;
//        }
        Datapoint datapoint = Datapoint.builder()
                .timestamp(timestamp)
                .count(1)
                .value(value)
                .build();
        metricsHolder.datapointList.add(datapoint);

    }


}
