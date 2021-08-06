package aws.example.emr;

// snippet-start:[emr.java2._create_hive.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.*;
import java.util.HashMap;
import java.util.Map;
// snippet-end:[emr.java2._create_hive.import]

/*
 *   Ensure that you have setup your development environment, including your credentials.
 *   For information, see this documentation topic:
 *
 *   https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class CreateHiveCluster {

    public static void main(String[] args){

        final String USAGE = "\n" +
                "Usage: " +
                "   <jar> <myClass> <keys> <logUri> <name>\n\n" +
                "Where:\n" +
                "   jar - a path to a JAR file run during the step. \n\n" +
                "   myClass - the name of the main class in the specified Java file. \n\n" +
                "   keys - The name of the EC2 key pair. \n\n" +
                "   logUri - The Amazon S3 bucket where the logs are located (for example,  s3://<BucketName>/logs/). \n\n" +
                "   name - The name of the job flow. \n\n" ;

        if (args.length != 5) {
              System.out.println(USAGE);
              System.exit(1);
          }

        String jar = args[0] ;
        String myClass = args[1] ;
        String keys = args[2] ;
        String logUri  = args[3] ;
        String name = args[4] ;
        Region region = Region.US_WEST_2;
        EmrClient emrClient = EmrClient.builder()
                .region(region)
                .build();


        String jobFlowId = createCluster(emrClient, jar, myClass, keys, logUri, name);
        System.out.println("The job flow id is " +jobFlowId);
        emrClient.close();
    }

    // snippet-start:[emr.java2._create_hive.main]
    public static String createCluster( EmrClient emrClient, String jar,
                                      String myClass,
                                      String keys,
                                      String logUri,
                                      String name) {

        try {

            HadoopJarStepConfig jarStepConfig = HadoopJarStepConfig.builder()
                    .jar(jar)
                    .mainClass(myClass)
                    .build();

            Application hiveApp = Application.builder()
                    .name("Hive")
                    .build();

            Map hiveProperties = new HashMap<String,String>();
            hiveProperties.put("hive.join.emit.interval","1000");
            hiveProperties.put("hive.merge.mapfiles","true");


            Configuration configuration = Configuration.builder()
                    .classification("hive-site")
                    .properties(hiveProperties)
                    .build();


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
                    .configurations(configuration)
                    .releaseLabel("emr-5.20.0")
                    .steps(enabledebugging)
                    .applications(hiveApp)
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
// snippet-end:[emr.java2._create_hive.main]
 }



