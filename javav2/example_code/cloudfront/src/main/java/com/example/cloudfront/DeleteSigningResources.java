//snippet-sourcedescription:[DeleteDistributionResources.java demonstrates how to delete ancillary resources used by CloudFront distribution after the distribution is deleted.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.deletedistributionresources.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DeleteKeyGroupResponse;
import software.amazon.awssdk.services.cloudfront.model.DeleteOriginAccessControlResponse;
import software.amazon.awssdk.services.cloudfront.model.DeletePublicKeyResponse;
import software.amazon.awssdk.services.cloudfront.model.GetKeyGroupResponse;
import software.amazon.awssdk.services.cloudfront.model.GetOriginAccessControlResponse;
import software.amazon.awssdk.services.cloudfront.model.GetPublicKeyResponse;
// snippet-end:[cloudfront.java2.deletedistributionresources.import]

// snippet-start:[cloudfront.java2.deletedistributionresources.main]
public class DeleteSigningResources {
    private static final Logger logger = LoggerFactory.getLogger(DeleteSigningResources.class);

    public static void deleteOriginAccessControl(final CloudFrontClient cloudFrontClient, final String originAccessControlId){
        GetOriginAccessControlResponse getResponse = cloudFrontClient.getOriginAccessControl(b -> b.id(originAccessControlId));
        DeleteOriginAccessControlResponse deleteResponse = cloudFrontClient.deleteOriginAccessControl(builder -> builder
                .id(originAccessControlId)
                .ifMatch(getResponse.eTag()));
        if ( deleteResponse.sdkHttpResponse().isSuccessful() ){
            logger.info("Successfully deleted Origin Access Control [{}]", originAccessControlId);
        }
    }

    public static void deleteKeyGroup(final CloudFrontClient cloudFrontClient, final String keyGroupId){

        GetKeyGroupResponse getResponse = cloudFrontClient.getKeyGroup(b -> b.id(keyGroupId));
        DeleteKeyGroupResponse deleteResponse = cloudFrontClient.deleteKeyGroup(builder -> builder
                .id(keyGroupId)
                .ifMatch(getResponse.eTag()));
        if ( deleteResponse.sdkHttpResponse().isSuccessful() ){
            logger.info("Successfully deleted Key Group [{}]", keyGroupId);
        }
    }

    public static void deletePublicKey(final CloudFrontClient cloudFrontClient, final String publicKeyId){
        GetPublicKeyResponse getResponse = cloudFrontClient.getPublicKey(b -> b.id(publicKeyId));

        DeletePublicKeyResponse deleteResponse = cloudFrontClient.deletePublicKey(builder -> builder
                .id(publicKeyId)
                .ifMatch(getResponse.eTag()));

        if (deleteResponse.sdkHttpResponse().isSuccessful()){
            logger.info("Successfully deleted Public Key [{}]", publicKeyId);
        }
    }
}
// snippet-end:[cloudfront.java2.deletedistributionresources.main]
