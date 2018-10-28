//snippet-sourcedescription:[ListContainers.java demonstrates how to list your AWS Elemental MediaStore containers.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mediastore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-06]
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
import com.amazonaws.services.mediastore.model.ListContainersRequest;
import com.amazonaws.services.mediastore.model.ListContainersResult;
import java.util.List;

/**
 * List your AWS Elemental MediaStore containers.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListContainers
{
    public static void main(String[] args)
    {
        final AWSMediaStore mediastore = AWSMediaStoreClientBuilder.defaultClient();
        final ListContainersResult result = mediastore.listContainers(new ListContainersRequest());
        final List<Container> containers = result.getContainers();
        System.out.println("Your AWS Elemental MediaStore containers are:");
        for (Container b : containers) {
            System.out.println("* " + b.getName());
        }
    }
}
