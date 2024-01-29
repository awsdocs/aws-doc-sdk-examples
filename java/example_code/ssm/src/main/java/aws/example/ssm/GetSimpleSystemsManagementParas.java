// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.Java1.get_params.complete]
package aws.example.ssm;

// snippet-start:[ssm.Java1.get_params.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeParametersResult;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterMetadata;
import java.util.Iterator;
import java.util.List;
import com.amazonaws.AmazonServiceException;
// snippet-end:[ssm.Java1.get_params.import]

public class GetSimpleSystemsManagementParas {
    public static void main(String[] args) {

        // snippet-start:[ssm.Java1.get_params.main]
        AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(Regions.DEFAULT_REGION).build();

        try {
            DescribeParametersRequest desRequest = new DescribeParametersRequest();
            desRequest.setMaxResults(10);

            DescribeParametersResult results = ssm.describeParameters(desRequest);

            List<ParameterMetadata> params = results.getParameters();

            // Iterate through the list
            Iterator<ParameterMetadata> tagIterator = params.iterator();

            while (tagIterator.hasNext()) {

                ParameterMetadata paraMeta = (ParameterMetadata) tagIterator.next();

                System.out.println(paraMeta.getName());
                System.out.println(paraMeta.getDescription());
            }

        } catch (AmazonServiceException e) {
            e.getStackTrace();
        }
        // snippet-end:[ssm.Java1.get_params.main]
    }
}
// snippet-end:[ssm.Java1.get_params.complete]
