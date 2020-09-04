This is simple Oracle Streaming Service consumer in Java 8.
This consumer reads messages representing metric records from Oracle Enterprise Manager repository database. The consumer converts these metric messages into OCI monitoring service compatible messages and post the data in OCI monitoring service.


The schema of the messages is as per the examples in the file https://github.com/mayur-oci/OemToOciMetrics/blob/schema/src/main/resources/MessageExamples.txt
The pojos for the same Messages is in package https://github.com/mayur-oci/OemToOciMetrics/tree/schema/src/main/java/com/oss/oem/pojo

The conversion/transformation logic of Oem Metric to OCI Metrics for these messages is in the class OemMetricsToOciMetricsConversion, specifically in method addSingleMetric.

All the config values for consumer and monitoring service are in this file
ConfigHolder.java. This class is simply holding all these config values.  
