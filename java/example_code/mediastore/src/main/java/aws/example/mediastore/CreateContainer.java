//snippet-sourcedescription:[CreateContainer.java demonstrates how to create an AWS Elemental MediaStore container.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mediastore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[rhcarvalho]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
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
public class CreateContainer
{
    public static void main(String[] args)
    {
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
            .withContainerName(name);
        try {
            final CreateContainerResult result = mediastore.createContainer(request);
            return result.getContainer();
        } catch (AWSMediaStoreException e) {
            System.err.println(e.getErrorMessage());
        }
        return null;
    }
}
