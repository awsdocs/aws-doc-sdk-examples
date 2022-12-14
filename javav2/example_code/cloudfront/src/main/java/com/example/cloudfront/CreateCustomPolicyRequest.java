//snippet-sourcedescription:[CreateCustomPolicyRequest.java demonstrates how to use the CustomSignerRequest class to specify the required properties to sign URLs or cookies to access Amazon CloudFront.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createcustompolicyrequest.import]
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
// snippet-end:[cloudfront.java2.createcustompolicyrequest.import]

// snippet-start:[cloudfront.java2.createcustompolicyrequest.main]
public class CreateCustomPolicyRequest {

    public static CustomSignerRequest createRequestForCustomPolicy(String distributionDomainName, String fileNameToUpload,
                                                                   String privateKeyFullPath, String publicKeyId) throws Exception {
        String protocol = "https";
        String resourcePath = "/" + fileNameToUpload;

        String cloudFrontUrl = new URL(protocol, distributionDomainName, resourcePath).toString();
        Instant expireDate = Instant.now().plus(7, ChronoUnit.DAYS);
        // URL will be accessible tomorrow using the signed URL.
        Instant activeDate = Instant.now().plus(1, ChronoUnit.DAYS);
        Path path = Paths.get(privateKeyFullPath);

        return CustomSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(path)
                .keyPairId(publicKeyId)
                .expirationDate(expireDate)
                .activeDate(activeDate)      // Optional.
                //.ipRange("192.168.0.1/24") // Optional.
                .build();
    }
}
// snippet-end:[cloudfront.java2.createcustompolicyrequest.main]