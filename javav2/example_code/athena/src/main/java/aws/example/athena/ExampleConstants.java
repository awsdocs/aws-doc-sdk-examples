//snippet-sourcedescription:[ExampleConstants.java demonstrates how to query a table]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/30/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright  Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
    public static final String ATHENA_OUTPUT_BUCKET = "s3://bucketscott2"; //change the bucket name to match your environment
    // This example demonstrates how to query a table with a CSV  For information, see
    //https://docs.aws.amazon.com/athena/latest/ug/work-with-data.html
    public static final String ATHENA_SAMPLE_QUERY = "SELECT * FROM scott2;"; //change the Query statement to match your environment
    public static final long SLEEP_AMOUNT_IN_MS = 1000;
    public static final String ATHENA_DEFAULT_DATABASE = "mydatabase"; //Change the database to match your database

}
//snippet-end:[athena.java.ExampleConstants.complete]
//snippet-end:[athena.java2.ExampleConstants.complete]
