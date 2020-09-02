import com.oracle.bmc.streaming.model.CreateCursorDetails;

public class ConfigHolder {


    static public String ociConfigFilePath="~/.oci/config";

    static public String streamProfileName="MAYUR_ADMIN_SJC";
    static public String streamCompartmentId="ocid1.compartment.oc1..aaaaaaaa2z4wup7a4enznwxi3mkk55cperdk3fcotagepjnan5utdb3tvakq";
    static public String streamName="multistream01";
    static public String streamPoolName="demo-stream-pool";
    static public String streamPoolOcid="ocid1.streampool.oc1.us-sanjose-1.amaaaaaauwpiejqawbcccfmvdkctu5vbmhwlogzsjss4haz7nuepc4ihk3ea";

    static public String streamConsumerGroupName="oem-oci";
    static public String streamConsumerInstanceName="oem-oci-instance-100";
    static public CreateCursorDetails.Type streamConsumerCursorType= CreateCursorDetails.Type.TrimHorizon;

    static public String monitoringProfileName="MAYUR_ADMIN_PHX";
    static public String monitoringServiceEndpoint="https://telemetry-ingestion.us-phoenix-1.oraclecloud.com/";
    static public String monitoringCompartmentId="ocid1.compartment.oc1..aaaaaaaa2z4wup7a4enznwxi3mkk55cperdk3fcotagepjnan5utdb3tvakq";


    static public String namespace;
    static public String resourceGroup;















}
