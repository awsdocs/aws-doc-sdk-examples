// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ParseUri.java demonstrates how to parse a URI for an Amazon Simple Storage Service (Amazon S3) object.]
///snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.scenario_uriparsing.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.S3Utilities;

import java.net.URI;
import java.util.List;
import java.util.Map;
// snippet-end:[s3.java2.scenario_uriparsing.import]

/**
 * Before running this Java V2 code example, set up your development environment, including access to temporary credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ParseUri {
    private static final Logger logger = LoggerFactory.getLogger(ParseUri.class);

    public static void main(String[] args) {

        S3Client s3Client = S3Client.create();
        String url = "https://s3.us-west-1.amazonaws.com/myBucket/resources/doc.txt?versionId=abc123&partNumber=77&partNumber=88";
        parseS3UriExample(s3Client, url);
    }

    // snippet-start:[s3.java2.scenario_uriparsing.main]
    /**
     *
     * @param s3Client - An S3Client through which you acquire an S3Uri instance.
     * @param s3ObjectUrl - A complex URL (String) that is used to demonstrate S3Uri capabilities.
     */
    public static void parseS3UriExample(S3Client s3Client, String s3ObjectUrl) {
        logger.info(s3ObjectUrl);
        //Console output: 'https://s3.us-west-1.amazonaws.com/myBucket/resources/doc.txt?versionId=abc123&partNumber=77&partNumber=88'.

        // Create an S3Utilities object using the configuration of the s3Client.
        S3Utilities s3Utilities = s3Client.utilities();

        // From a String URL create a URI object to pass to the parseUri() method.
        URI uri = URI.create(s3ObjectUrl);
        S3Uri s3Uri = s3Utilities.parseUri(uri);

        // If the URI contains no value for the Region, bucket or key, the SDK returns an empty Optional.
        // The SDK returns decoded URI values.

        Region region = s3Uri.region().orElse(null);
        log("region", region);
        // Console output: 'region: us-west-1'.

        String bucket = s3Uri.bucket().orElse(null);
        log("bucket", bucket);
        // Console output: 'bucket: myBucket'.

        String key = s3Uri.key().orElse(null);
        log("key", key);
        // Console output: 'key: resources/doc.txt'.

        Boolean isPathStyle = s3Uri.isPathStyle();
        log("isPathStyle", isPathStyle);
        // Console output: 'isPathStyle: true'.

        // If the URI contains no query parameters, the SDK returns an empty map.
        Map<String, List<String>> queryParams = s3Uri.rawQueryParameters();
        log("rawQueryParameters", queryParams);
        // Console output: 'rawQueryParameters: {versionId=[abc123], partNumber=[77, 88]}'.

        // Retrieve the first or all values for a query parameter as shown in the following code.
        String versionId = s3Uri.firstMatchingRawQueryParameter("versionId").orElse(null);
        log("firstMatchingRawQueryParameter-versionId", versionId);
        // Console output: 'firstMatchingRawQueryParameter-versionId: abc123'.

        String partNumber = s3Uri.firstMatchingRawQueryParameter("partNumber").orElse(null);
        log("firstMatchingRawQueryParameter-partNumber", partNumber);
        // Console output: 'firstMatchingRawQueryParameter-partNumber: 77'.

        List<String> partNumbers = s3Uri.firstMatchingRawQueryParameters("partNumber");
        log("firstMatchingRawQueryParameter", partNumbers);
        // Console output: 'firstMatchingRawQueryParameter: [77, 88]'.

/*
        Object keys and query parameters with reserved or unsafe characters, must be URL-encoded.
        For example replace whitespace " " with "%20".
        Valid: "https://s3.us-west-1.amazonaws.com/myBucket/object%20key?query=%5Bbrackets%5D"
        Invalid: "https://s3.us-west-1.amazonaws.com/myBucket/object key?query=[brackets]"

        Virtual-hosted-style URIs with bucket names that contain a dot, ".", the dot must not be URL-encoded.
        Valid: "https://my.Bucket.s3.us-west-1.amazonaws.com/key"
        Invalid: "https://my%2EBucket.s3.us-west-1.amazonaws.com/key"
*/
    }

    private static void log(String s3UriElement, Object element) {
        if (element == null) {
            logger.info("{}: {}", s3UriElement, "null");
        } else {
            logger.info("{}: {}", s3UriElement, element.toString());
        }
    }
    // snippet-end:[s3.java2.scenario_uriparsing.main]
}

