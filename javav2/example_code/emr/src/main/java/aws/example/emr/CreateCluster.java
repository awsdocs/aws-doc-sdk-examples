//snippet-sourcedescription:[CreateCluster.java demonstrates how to create and start running a new cluster (job flow).]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package aws.example.emr;

// snippet-start:[emr.java2._create_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.HadoopJarStepConfig;
import software.amazon.awssdk.services.emr.model.Application;
import software.amazon.awssdk.services.emr.model.StepConfig;
import software.amazon.awssdk.services.emr.model.JobFlowInstancesConfig;
import software.amazon.awssdk.services.emr.model.RunJobFlowRequest;
import software.amazon.awssdk.services.emr.model.RunJobFlowResponse;
import software.amazon.awssdk.services.emr.model.EmrException;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[emr.java2._create_cluster.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateCluster {

    public static void main(String[] args){

        final String usage = "\n" +
                "Usage: " +
                "   <jar> <myClass> <keys> <logUri> <name>\n\n" +
                "Where:\n" +
                "   jar - A path to a JAR file run during the step. \n\n" +
                "   myClass - The name of the main class in the specified Java file. \n\n" +
                "   keys - The name of the Amazon EC2 key pair. \n\n" +
                "   logUri - The Amazon S3 bucket where the logs are located (for example,  s3://<BucketName>/logs/). \n\n" +
                "   name - The name of the job flow. \n\n" ;

         if (args.length != 5) {
              System.out.println(usage);
              System.exit(1);
         }

        String jar = args[0] ;
        String myClass = args[1] ;
        String keys = args[2] ;
        String logUri = args[3] ;
        String name = args[4] ;
        Region region = Region.US_WEST_2;
        EmrClient emrClient = EmrClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String jobFlowId = createAppCluster(emrClient, jar, myClass, keys, logUri, name);
        System.out.println("The job flow id is " +jobFlowId);
        emrClient.close();
    }
    // snippet-start:[emr.java2._create_cluster.main]
    public static String createAppCluster( EmrClient emrClient,
                                        String jar,
                                        String myClass,
                                        String keys,
                                        String logUri,
                                        String name) {

        try {
            HadoopJarStepConfig jarStepConfig = HadoopJarStepConfig.builder()
                .jar(jar)
                .mainClass(myClass)
                .build();

            Application spark = Application.builder()
                .name("Spark")
                .build();

            Application hive = Application.builder()
                .name("Hive")
                .build();

            Application zeppelin = Application.builder()
                .name("Zeppelin")
                .build();

            List <Application> apps = new ArrayList<>();
            apps.add(spark);
            apps.add(hive);
            apps.add(zeppelin);

            StepConfig enabledebugging = StepConfig.builder()
                .name("Enable debugging")
                .actionOnFailure("TERMINATE_JOB_FLOW")
                .hadoopJarStep(jarStepConfig)
                .build();

            JobFlowInstancesConfig instancesConfig = JobFlowInstancesConfig.builder()
                .ec2SubnetId("subnet-206a9c58")
                .ec2KeyName(keys)
                .instanceCount(3)
                .keepJobFlowAliveWhenNoSteps(true)
                .masterInstanceType("m4.large")
                .slaveInstanceType("m4.large")
                .build();

            RunJobFlowRequest jobFlowRequest = RunJobFlowRequest.builder()
                .name(name)
                .releaseLabel("emr-5.20.0")
                .steps(enabledebugging)
                .applications(apps)
                .logUri(logUri)
                .serviceRole("EMR_DefaultRole")
                .jobFlowRole("EMR_EC2_DefaultRole")
                .instances(instancesConfig)
                .build();

            RunJobFlowResponse response = emrClient.runJobFlow(jobFlowRequest);
            return response.jobFlowId();

        } catch(EmrException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }
    // snippet-end:[emr.java2._create_cluster.main]
}
