// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createdistributiontenant.import]
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.Distribution;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.ItemSelection;
import software.amazon.awssdk.services.cloudfront.model.Method;
import software.amazon.awssdk.services.cloudfront.model.ViewerProtocolPolicy;
import software.amazon.awssdk.services.cloudfront.waiters.CloudFrontWaiter;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Instant;
// snippet-end:[cloudfront.java2.createdistributiontenant.import]

// snippet-start:[cloudfront.java2.createdistributiontenant.title]
public class CreateMultiTenantDistribution {
    // snippet-end:[cloudfront.java2.createdistributiontenant.title]
// snippet-start:[cloudfront.java2.createdistributiontenant.nocert]
    public static DistributionTenant createDistributionTenantNoCert(CloudFrontClient cloudFrontClient,
                                                                    Route53Client route53Client,
                                                                    String distributionId,
                                                                    String domain,
                                                                    String hostedZoneId) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .enabled(true)
                .name("no-cert-tenant")
        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        final GetConnectionGroupResponse fetchedConnectionGroup = cloudFrontClient.getConnectionGroup(builder -> builder
                .identifier(distributionTenant.connectionGroupId()));

        route53Client.changeResourceRecordSets(builder -> builder
                .hostedZoneId(hostedZoneId)
                .changeBatch(b1 -> b1
                        .comment("ChangeBatch comment")
                        .changes(b2 -> b2
                                .resourceRecordSet(b3 -> b3
                                        .name(domain)
                                        .type("CNAME")
                                        .ttl(300L)
                                        .resourceRecords(b4 -> b4
                                                .value(fetchedConnectionGroup.connectionGroup().routingEndpoint())))
                                .action("CREATE"))
                ));
        return distributionTenant;
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.nocert]

    // snippet-start:[cloudfront.java2.createdistributiontenant.withcert]
    public static DistributionTenant createDistributionTenantWithCert(CloudFrontClient cloudFrontClient,
                                                                      Route53Client route53Client,
                                                                      String distributionId,
                                                                      String domain,
                                                                      String hostedZoneId) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .enabled(true)
                .name("no-cert-tenant")
        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        final GetConnectionGroupResponse fetchedConnectionGroup = cloudFrontClient.getConnectionGroup(builder -> builder
                .identifier(distributionTenant.connectionGroupId()));

        route53Client.changeResourceRecordSets(builder -> builder
                .hostedZoneId(hostedZoneId)
                .changeBatch(b1 -> b1
                        .comment("ChangeBatch comment")
                        .changes(b2 -> b2
                                .resourceRecordSet(b3 -> b3
                                        .name(domain)
                                        .type("CNAME")
                                        .ttl(300L)
                                        .resourceRecords(b4 -> b4
                                                .value(fetchedConnectionGroup.connectionGroup().routingEndpoint())))
                                .action("CREATE"))
                ));
        return distributionTenant;
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.withcert]

    // snippet-start:[cloudfront.java2.createdistributiontenant.cfhosted]
    public static DistributionTenant createDistributionTenantCfHosted(CloudFrontClient cloudFrontClient,
                                                                      Route53Client route53Client,
                                                                      String distributionId,
                                                                      String domain,
                                                                      String hostedZoneId) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .enabled(true)
                .name("no-cert-tenant")
        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        final GetConnectionGroupResponse fetchedConnectionGroup = cloudFrontClient.getConnectionGroup(builder -> builder
                .identifier(distributionTenant.connectionGroupId()));

        route53Client.changeResourceRecordSets(builder -> builder
                .hostedZoneId(hostedZoneId)
                .changeBatch(b1 -> b1
                        .comment("ChangeBatch comment")
                        .changes(b2 -> b2
                                .resourceRecordSet(b3 -> b3
                                        .name(domain)
                                        .type("CNAME")
                                        .ttl(300L)
                                        .resourceRecords(b4 -> b4
                                                .value(fetchedConnectionGroup.connectionGroup().routingEndpoint())))
                                .action("CREATE"))
                ));
        return distributionTenant;
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.cfhosted]

    // snippet-start:[cloudfront.java2.createdistributiontenant.selfhosted]
    public static DistributionTenant createDistributionTenantSelfHosted(CloudFrontClient cloudFrontClient,
                                                                        Route53Client route53Client,
                                                                        String distributionId,
                                                                        String domain,
                                                                        String hostedZoneId) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .enabled(true)
                .name("no-cert-tenant")
        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        final GetConnectionGroupResponse fetchedConnectionGroup = cloudFrontClient.getConnectionGroup(builder -> builder
                .identifier(distributionTenant.connectionGroupId()));

        route53Client.changeResourceRecordSets(builder -> builder
                .hostedZoneId(hostedZoneId)
                .changeBatch(b1 -> b1
                        .comment("ChangeBatch comment")
                        .changes(b2 -> b2
                                .resourceRecordSet(b3 -> b3
                                        .name(domain)
                                        .type("CNAME")
                                        .ttl(300L)
                                        .resourceRecords(b4 -> b4
                                                .value(fetchedConnectionGroup.connectionGroup().routingEndpoint())))
                                .action("CREATE"))
                ));
        return distributionTenant;
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.selfhosted]

// snippet-start:[cloudfront.java2.createdistributiontenant.closebrace]
}
// snippet-end:[cloudfront.java2.createdistributiontenant.closebrace]