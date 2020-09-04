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

        Date timestamp = new Date(payload.getCOLLECTIONTIMESTAMP());
        Long twoHrsBackTimeStamp = System.currentTimeMillis() - 1*60*60*1000;
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

            metricsHolder.metricKey = metricKey;

            metricsHolder.metricName = ociMetricsRegexCompatible(payload.getMETRICLABEL())
                     + "-"
                     + ociMetricsRegexCompatible(payload.getMETRICCOLUMN());

            metricsHolder.metadata.put("unit",
                    ociMetricsRegexCompatible(payload.getCOLUMNLABEL()));

            metricsHolder.dimensions.put( ociMetricsRegexCompatible(payload.getTARGETTYPE()),
                    ociMetricsRegexCompatible(payload.getTARGETNAME()));

            metricKeyToMetricHolderMap.put(metricKey, metricsHolder);
        } else {
            metricsHolder = metricKeyToMetricHolderMap.get(metricKey);
        }

        Datapoint datapoint = Datapoint.builder()
                .timestamp(timestamp)
                .count(1)
                .value(value)
                .build();
        metricsHolder.datapointList.add(datapoint);

    }

    static String ociMetricsRegexCompatible(String input){
        String s1=  input.toLowerCase().trim().replaceAll("[^A-Za-z0-9]","_");

        while(s1.endsWith("_")){
            s1 = s1.substring(0,s1.length()-1);
        }

        while(s1.startsWith("_")){
            s1 = s1.substring(1,s1.length());
        }

        return s1;
    }


}
