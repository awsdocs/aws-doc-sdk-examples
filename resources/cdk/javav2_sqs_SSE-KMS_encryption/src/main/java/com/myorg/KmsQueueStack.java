// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.AccountRootPrincipal;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.kms.Alias;
import software.amazon.awscdk.services.kms.Key;
import software.amazon.awscdk.services.kms.KeySpec;
import software.amazon.awscdk.services.kms.KeyUsage;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

import java.util.List;


public class KmsQueueStack extends Stack {
    public KmsQueueStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public KmsQueueStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Key myKey = Key.Builder.create(this, "MyKey")
                .alias(null)
                .keyUsage(KeyUsage.ENCRYPT_DECRYPT)
                .keySpec(KeySpec.SYMMETRIC_DEFAULT)
                .removalPolicy(RemovalPolicy.DESTROY)
                .pendingWindow(Duration.days(7))
                .policy(PolicyDocument.Builder.create()
                        .statements(
                                List.of(
                                        PolicyStatement.Builder.create()
                                                .actions(List.of("kms:*"))
                                                .resources(List.of("*"))
                                                .principals(List.of(new AccountRootPrincipal()))
                                                .build()
                                )

                        ).build()
                ).build();

        Alias keyAlias = myKey.addAlias("alias/" + myKey.getKeyId());

        Queue myQueue = Queue.Builder.create(this, "MyQueue")
                .queueName(null)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        CfnOutput.Builder.create(this, "KeyAlias")
                .description("The key alias")
                .value(keyAlias.getAliasName())
                .build();

        CfnOutput.Builder.create(this, "QueueName")
                .description("The queue name")
                .value(myQueue.getQueueName())
                .build();
    }
}
