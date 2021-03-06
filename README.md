This is simple Oracle Streaming Service consumer in Java 8.
This consumer reads messages representing metric records from Oracle Enterprise Manager(OEM) repository database. The consumer converts these metric messages into OCI monitoring service compatible messages and post the data in OCI monitoring service. 

Once the OEM metric are in OCI Monitoring Service, they can be easily analysed and obserbed. We can even set up alarm and notifications for further automation of operations.

The schema of the messages is as per the examples in the file https://github.com/mayur-oci/OemToOciMetrics/blob/schema/src/main/resources/MessageExamples.txt
The pojos for the same Messages is in package https://github.com/mayur-oci/OemToOciMetrics/tree/schema/src/main/java/com/oss/oem/pojo

The conversion/transformation logic of OEM Metric to OCI Metrics for these messages is in the class OemMetricsToOciMetricsConversion, specifically in method addSingleMetric.

All the config values for consumer and monitoring service are in the well commented Java class file
https://github.com/mayur-oci/OemToOciMetrics/blob/master/src/main/java/com/oss/oem/ConfigHolder.java. This class is simple static class holding all the config values, that will want to tweak as per his/her environment. 

For more information on Oracle Cloud Service SDK for Oracle Streaming Service and Monitoring Service, please take a look at 
https://github.com/oracle/oci-java-sdk/tree/master/bmc-streaming and https://github.com/oracle/oci-java-sdk/tree/master/bmc-monitoring respectively.


Deployment Instructions: 

This is simple single module maven based Java 8 project. For building it you need Java 8 and mvn 3.6+ in the environment path variable. 
After downloding this repo, cd into root pom directory(https://github.com/mayur-oci/OemToOciMetrics/blob/master/pom.xml),  then
for compilation just do 
```
mvn clean install 
```

After compilation you can use standard mvn command as follows for launching it.
```
mvn exec:java 
```
