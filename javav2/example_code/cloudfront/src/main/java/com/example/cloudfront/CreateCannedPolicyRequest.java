//snippet-sourcedescription:[CreateCannedPolicyRequest.java demonstrates how to use the CannedSignerRequest class to specify the required properties to sign URLs or cookies to access Amazon CloudFront.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createcannedpolicyrequest.import]
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
// snippet-end:[cloudfront.java2.createcannedpolicyrequest.import]

// snippet-start:[cloudfront.java2.createcannedpolicyrequest.main]
public class CreateCannedPolicyRequest {

    public static CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName, String fileNameToUpload,
                                                                   String privateKeyFullPath, String publicKeyId) throws Exception{
        String protocol = "https";
        String resourcePath = "/" + fileNameToUpload;

        String cloudFrontUrl = new URL(protocol, distributionDomainName, resourcePath).toString();
        Instant expirationDate = Instant.now().plus(7, ChronoUnit.DAYS);
        Path path = Paths.get(privateKeyFullPath);

        return CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(path)
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
    }
}
// snippet-end:[cloudfront.java2.createcannedpolicyrequest.main]