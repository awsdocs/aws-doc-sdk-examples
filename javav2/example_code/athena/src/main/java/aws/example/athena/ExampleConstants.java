//snippet-sourcedescription:[ExampleConstants.java demonstrates how to query a table created by the "Getting Started" tutorial in Athena]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
//snippet-start:[athena.java2.ExampleConstants.complete]
package aws.example.athena;

public class ExampleConstants {

    public static final int CLIENT_EXECUTION_TIMEOUT = 100000;
    public static final String ATHENA_OUTPUT_BUCKET = "s3://my-athena-bucket";
    // This example demonstrates how to query a table created by the "Getting Started" tutorial in Athena
    public static final String ATHENA_SAMPLE_QUERY = "SELECT elb_name, "
            + " count(1)"
            + " FROM elb_logs"
            + " Where elb_response_code = '200'"
            + " GROUP BY elb_name"
            + " ORDER BY 2 DESC limit 10;";
    public static final long SLEEP_AMOUNT_IN_MS = 1000;
    public static final String ATHENA_DEFAULT_DATABASE = "default";

}
//snippet-end:[athena.java2.ExampleConstants.complete]