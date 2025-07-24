// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createdistributiontenant.import]

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateConnectionGroupResponse;
import software.amazon.awssdk.services.cloudfront.model.CreateDistributionTenantResponse;
import software.amazon.awssdk.services.cloudfront.model.DistributionTenant;
import software.amazon.awssdk.services.cloudfront.model.GetConnectionGroupResponse;
import software.amazon.awssdk.services.cloudfront.model.ValidationTokenHost;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.RRType;

import java.time.Instant;
// snippet-end:[cloudfront.java2.createdistributiontenant.import]

// snippet-start:[cloudfront.java2.createdistributiontenant.title]
public class CreateDistributionTenant {
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
                .parameters(b2 -> b2
                        .name("tenantName")
                        .value("myTenant"))
                .enabled(false)
                .name("no-cert-tenant")
        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        // Then update the Route53 hosted zone to point your domain at the distribution tenant
        // We fetch the RoutingEndpoint to point to via the default connection group that was created for your tenant
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
                                                                      String hostedZoneId,
                                                                      String certificateArn) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .enabled(false)
                .name("tenant-with-cert")
                .parameters(b2 -> b2
                        .name("tenantName")
                        .value("myTenant"))
                .customizations(b3 -> b3
                        .certificate(b4 -> b4
                                .arn(certificateArn))) // NOTE: Cert must be in Us-East-1 and cover the domain provided in this request

        );

        final DistributionTenant distributionTenant = createResponse.distributionTenant();

        // Then update the Route53 hosted zone to point your domain at the distribution tenant
        // We fetch the RoutingEndpoint to point to via the default connection group that was created for your tenant
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
                                                                      String hostedZoneId) throws InterruptedException {
        CreateConnectionGroupResponse createConnectionGroupResponse = cloudFrontClient.createConnectionGroup(builder -> builder
                .ipv6Enabled(true)
                .name("cf-hosted-connection-group")
                .enabled(true));

        route53Client.changeResourceRecordSets(builder -> builder
                .hostedZoneId(hostedZoneId)
                .changeBatch(b1 -> b1
                        .comment("cf-hosted domain validation record")
                        .changes(b2 -> b2
                                .resourceRecordSet(b3 -> b3
                                        .name(domain)
                                        .type(RRType.CNAME)
                                        .ttl(300L)
                                        .resourceRecords(b4 -> b4
                                                .value(createConnectionGroupResponse.connectionGroup().routingEndpoint())))
                                .action("CREATE"))
                ));

        // Give the R53 record time to propagate, if it isn't being returned by servers yet, the following call will fail
        Thread.sleep(60000);

        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .connectionGroupId(createConnectionGroupResponse.connectionGroup().id())
                .enabled(false)
                .name("cf-hosted-tenant")
                .parameters(b2 -> b2
                        .name("tenantName")
                        .value("myTenant"))
                .managedCertificateRequest(b3 -> b3
                        .validationTokenHost(ValidationTokenHost.CLOUDFRONT)
                )
        );

        return createResponse.distributionTenant();
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.cfhosted]

// snippet-start:[cloudfront.java2.createdistributiontenant.selfhosted]
    public static DistributionTenant createDistributionTenantSelfHosted(CloudFrontClient cloudFrontClient,
                                                                        String distributionId,
                                                                        String domain) {
        CreateDistributionTenantResponse createResponse = cloudFrontClient.createDistributionTenant(builder -> builder
                .distributionId(distributionId)
                .domains(b1 -> b1
                        .domain(domain))
                .parameters(b2 -> b2
                        .name("tenantName")
                        .value("myTenant"))
                .enabled(false)
                .name("self-hosted-tenant")
                .managedCertificateRequest(b3 -> b3
                        .validationTokenHost(ValidationTokenHost.SELF_HOSTED)
                        .primaryDomainName(domain)
                )
        );

        return createResponse.distributionTenant();
    }
// snippet-end:[cloudfront.java2.createdistributiontenant.selfhosted]

// snippet-start:[cloudfront.java2.createdistributiontenant.closebrace]
}
// snippet-end:[cloudfront.java2.createdistributiontenant.closebrace]