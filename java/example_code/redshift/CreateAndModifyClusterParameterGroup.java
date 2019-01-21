/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
*/

// snippet-sourcedescription:[CreateAndModifyClusterParameterGroup demonstrates how to create and modify an Amazon Redshift parameter group.]
// snippet-service:[redshift]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Redshift]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateClusterParameterGroup]
// snippet-keyword:[DescribeClusterParameterGroups]
// snippet-keyword:[ModifyClusterParameterGroup]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2015-02-19]
// snippet-sourceauthor:[AWS]
// snippet-start:[redshift.java.CreateAndModifyClusterParameterGroup.complete]
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.*;

public class CreateAndModifyClusterParameterGroup {

    public static AmazonRedshiftClient client;
    public static String clusterParameterGroupName = "parametergroup1";
    public static String clusterIdentifier = "***provide cluster identifier***";
    public static String parameterGroupFamily = "redshift-1.0";

    public static void main(String[] args) throws IOException {

        AWSCredentials credentials = new PropertiesCredentials(
                CreateAndModifyClusterParameterGroup.class
                        .getResourceAsStream("AwsCredentials.properties"));

        client = new AmazonRedshiftClient(credentials);

        try {
             createClusterParameterGroup();
             modifyClusterParameterGroup();
             associateParameterGroupWithCluster();
             describeClusterParameterGroups();
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static void createClusterParameterGroup() {
       CreateClusterParameterGroupRequest request = new CreateClusterParameterGroupRequest()
           .withDescription("my cluster parameter group")
           .withParameterGroupName(clusterParameterGroupName)
           .withParameterGroupFamily(parameterGroupFamily);
       client.createClusterParameterGroup(request);
       System.out.println("Created cluster parameter group.");
    }

    private static void describeClusterParameterGroups() {
       DescribeClusterParameterGroupsResult result = client.describeClusterParameterGroups();
       printResultClusterParameterGroups(result);
    }

    private static void modifyClusterParameterGroup() {
       List<Parameter> parameters = new ArrayList<Parameter>();
       parameters.add(new Parameter()
           .withParameterName("extra_float_digits")
           .withParameterValue("2"));
       // Replace WLM configuration. The new configuration defines a queue (in addition to the default).
       parameters.add(new Parameter()
       .withParameterName("wlm_json_configuration")
       .withParameterValue("[{\"user_group\":[\"example_user_group1\"],\"query_group\":[\"example_query_group1\"],\"query_concurrency\":7},{\"query_concurrency\":5}]"));

       ModifyClusterParameterGroupRequest request = new ModifyClusterParameterGroupRequest()
           .withParameterGroupName(clusterParameterGroupName)
           .withParameters(parameters);
       client.modifyClusterParameterGroup(request);

    }

    private static void associateParameterGroupWithCluster() {

        ModifyClusterRequest request = new ModifyClusterRequest()
        .withClusterIdentifier(clusterIdentifier)
        .withClusterParameterGroupName(clusterParameterGroupName);

        Cluster result = client.modifyCluster(request);

        System.out.format("Parameter Group %s is used for Cluster %s\n",
                clusterParameterGroupName, result.getClusterParameterGroups().get(0).getParameterGroupName());
    }
    private static void printResultClusterParameterGroups(DescribeClusterParameterGroupsResult result)
    {
        if (result == null)
        {
            System.out.println("\nDescribe cluster parameter groups result is null.");
            return;
        }

        System.out.println("\nPrinting parameter group results:\n");
        for (ClusterParameterGroup group : result.getParameterGroups()) {
            System.out.format("\nDescription: %s\n", group.getDescription());
            System.out.format("Group Family Name: %s\n", group.getParameterGroupFamily());
            System.out.format("Group Name: %s\n", group.getParameterGroupName());
            describeClusterParameters(group.getParameterGroupName());
        }
    }

    private static void describeClusterParameters(String parameterGroupName) {
        DescribeClusterParametersRequest request = new DescribeClusterParametersRequest()
            .withParameterGroupName(parameterGroupName);

        DescribeClusterParametersResult result = client.describeClusterParameters(request);
        printResultClusterParameters(result, parameterGroupName);
    }

    private static void printResultClusterParameters(DescribeClusterParametersResult result, String parameterGroupName)
    {
        if (result == null)
        {
            System.out.println("\nCluster parameters is null.");
            return;
        }

        System.out.format("\nPrinting cluster parameters for \"%s\"\n", parameterGroupName);
        for (Parameter parameter : result.getParameters()) {
            System.out.println("  Name: " + parameter.getParameterName() + ", Value: " + parameter.getParameterValue());
            System.out.println("  DataType: " + parameter.getDataType() + ", MinEngineVersion: " + parameter.getMinimumEngineVersion());
            System.out.println("  AllowedValues: " + parameter.getAllowedValues() + ", Source: " + parameter.getSource());
            System.out.println("  IsModifiable: " + parameter.getIsModifiable() + ", Description: " + parameter.getDescription());
        }
    }
}

// snippet-end:[redshift.java.CreateAndModifyClusterParameterGroup.complete]