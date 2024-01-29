// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.mediastore;

import com.amazonaws.services.mediastore.AWSMediaStore;
import com.amazonaws.services.mediastore.AWSMediaStoreClientBuilder;
import com.amazonaws.services.mediastore.model.Container;
import com.amazonaws.services.mediastore.model.CreateContainerRequest;
import com.amazonaws.services.mediastore.model.CreateContainerResult;
import com.amazonaws.services.mediastore.model.AWSMediaStoreException;

/**
 * Create an AWS Elemental MediaStore container.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CreateContainer {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateContainer - create an AWS Elemental MediaStore container\n\n" +
                "Usage: CreateContainer <name>\n\n" +
                "Where:\n" +
                "  name - the name of the container to create.\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String name = args[0];

        System.out.format("\nCreating MediaStore container: %s\n", name);
        final Container c = createContainer(name);
        if (c == null) {
            System.out.println("Error creating container!\n");
        } else {
            System.out.println("Done!\n");
        }
    }

    public static Container createContainer(String name) {
        final AWSMediaStore mediastore = AWSMediaStoreClientBuilder.defaultClient();
        final CreateContainerRequest request = new CreateContainerRequest()
                .withContainerName(name.trim());
        try {
            final CreateContainerResult result = mediastore.createContainer(request);
            return result.getContainer();
        } catch (AWSMediaStoreException e) {
            System.err.println(e.getErrorMessage());
        }
        return null;
    }
}
