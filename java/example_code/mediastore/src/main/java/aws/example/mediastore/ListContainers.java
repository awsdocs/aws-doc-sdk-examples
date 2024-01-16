// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.mediastore;

import com.amazonaws.services.mediastore.AWSMediaStore;
import com.amazonaws.services.mediastore.AWSMediaStoreClientBuilder;
import com.amazonaws.services.mediastore.model.Container;
import com.amazonaws.services.mediastore.model.ListContainersRequest;
import com.amazonaws.services.mediastore.model.ListContainersResult;
import java.util.List;

/**
 * List your AWS Elemental MediaStore containers.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListContainers {
    public static void main(String[] args) {
        final AWSMediaStore mediastore = AWSMediaStoreClientBuilder.defaultClient();
        final ListContainersResult result = mediastore.listContainers(new ListContainersRequest());
        final List<Container> containers = result.getContainers();
        System.out.println("Your AWS Elemental MediaStore containers are:");
        for (Container b : containers) {
            System.out.println("* " + b.getName());
        }
    }
}
