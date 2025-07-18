// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DeleteDistributionTenantResponse;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionTenantResponse;

public class DeleteDistributionTenant {
    public static void deleteDistributionTenant(final CloudFrontClient cloudFrontClient, final String distributionTenantId) {
        GetDistributionTenantResponse response = cloudFrontClient.getDistributionTenant(b -> b
                .identifier(distributionTenantId));
        String etag = response.eTag();
        DeleteDistributionTenantResponse deleteResponse = cloudFrontClient.deleteDistributionTenant(b -> b
                .id(distributionTenantId)
                .ifMatch(etag));
    }
}
