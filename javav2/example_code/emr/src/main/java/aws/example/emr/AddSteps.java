//snippet-sourcedescription:[AddSteps.java demonstrates how to add new steps to a running cluster.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package aws.example.emr;

// snippet-start:[emr.java2._add_steps.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.AddJobFlowStepsRequest;
import software.amazon.awssdk.services.emr.model.EmrException;
import software.amazon.awssdk.services.emr.model.HadoopJarStepConfig;
import software.amazon.awssdk.services.emr.model.StepConfig;
// snippet-end:[emr.java2._add_steps.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class AddSteps {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <jar> <myClass> <jobFlowId> \n\n" +
            "Where:\n" +
            "   jar - A path to a JAR file run during the step. \n\n" +
            "   myClass - The name of the main class in the specified Java file. \n\n" +
            "   jobFlowId - The id of the job flow. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String jar = args[0] ;
        String myClass = args[1] ;
        String jobFlowId = args[2] ;
        Region region = Region.US_WEST_2;
        EmrClient emrClient = EmrClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        addNewStep(emrClient, jobFlowId, jar, myClass);
        emrClient.close();
    }

    // snippet-start:[emr.java2._add_steps.main]
    public static void addNewStep(EmrClient emrClient, String jobFlowId, String jar, String myClass) {

        try {
            HadoopJarStepConfig jarStepConfig = HadoopJarStepConfig.builder()
                .jar(jar)
                .mainClass(myClass)
                .build();

            StepConfig stepConfig = StepConfig.builder()
                .hadoopJarStep(jarStepConfig)
                .name("Run a bash script")
                .build();

            AddJobFlowStepsRequest jobFlowStepsRequest = AddJobFlowStepsRequest.builder()
                .jobFlowId(jobFlowId)
                .steps(stepConfig)
                .build();

            emrClient.addJobFlowSteps(jobFlowStepsRequest);
            System.out.println("You have successfully added a step!");

        } catch (EmrException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[emr.java2._add_steps.main]
}
