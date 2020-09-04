package com.oss.oem;

import com.oracle.bmc.monitoring.model.Datapoint;
import com.oss.oem.pojo.MetricMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsHolder {

    // COLUMN_LABEL -> metadata unit = group
    // METRIC_LABEL ->  metric name = group
    // metric name and column -> metadata/dimention = group
    // TARGET_NAME and TARGET_TYPE -> dimension = group

    public String metricKey;

    public String metricName;
    public Map<String, String> dimensions = new HashMap<>();
    public Map<String, String> metadata = new HashMap<>();

    public List<Datapoint> datapointList = new ArrayList<>();

    @Override
    public String toString() {
        return "MetricsHolder{" +
                "metricKey='" + metricKey + '\'' +
                ", metricName='" + metricName + '\'' +
                ", dimensions=" + dimensions +
                ", metadata=" + metadata +
                ", datapointList=" + datapointList +
                '}';
    }
}

