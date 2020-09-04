This is simple Oracle Streaming Service consumer in Java 8.
This consumer reads messages  of the schema of the format as in the file MessageExamples.txt


Each of these messages is a metric record from Oracle Enterprise Manager repository database. The consumer converts these metric messages into OCI monitoring service compatible messages and post the data in OCI monitoring service.

The conversion logic for these messages is in the class OemMetricsToOciMetricsConversion specifically in method addSingleMetric.

All the config values for consumer and monitoring service are in this file
ConfigHolder.java. This class is simply holding all these config values.  