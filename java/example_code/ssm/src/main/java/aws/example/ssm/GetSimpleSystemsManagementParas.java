/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSimpleSystemsManagementParas.java demonstrates how to get information about SSM parameters by using an AWSSimpleSystemsManagement object]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-24]
// snippet-sourceauthor:[AWS - scmacdon]

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
        AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

        try {
            DescribeParametersRequest desRequest = new DescribeParametersRequest();
            desRequest.setMaxResults(10);

            DescribeParametersResult results = ssm.describeParameters(desRequest);

            List<ParameterMetadata> params = results.getParameters();

            //Iterate through the list
            Iterator<ParameterMetadata> tagIterator = params.iterator();

            while(tagIterator.hasNext()) {

                ParameterMetadata paraMeta = (ParameterMetadata)tagIterator.next();

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
