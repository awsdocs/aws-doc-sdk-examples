//snippet-sourcedescription:[ExampleConstants.java demonstrates how to query a table created by the "Getting Started" tutorial in Athena]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
//snippet-start:[athena.java2.ExampleConstants.complete]
//snippet-start:[athena.java.ExampleConstants.complete]
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
//snippet-end:[athena.java.ExampleConstants.complete]
//snippet-end:[athena.java2.ExampleConstants.complete]