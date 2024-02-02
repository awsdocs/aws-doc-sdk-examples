# Performing device management use cases using the AWS Iot SDK technical specification

Overview
This example shows how to use AWS SDKs to perform device management use cases using the AWS Iot SDK.

The AWS Iot API provides secure, bi-directional communication between Internet-connected devices (such as sensors, actuators, embedded devices, or smart appliances) and the Amazon Web Services cloud. This example shows some typical use cases such as creating things, creating certifications, applying the certifications to the IoT Thing and so on. 

The IotClient service client is used in this example and the following service operations are covered:

1. Creates an AWS IoT Thing using the createThing().
2. Generate a device certificate using the createKeysAndCertificate().
3. Attach the certificate to the AWS IoT Thing using attachThingPrincipal().
4. Update an AWS IoT Thing with Attributes using updateThingShadow().
5. Get an AWS IoT Endpoint using describeEndpoint().
6. List your certificates using listCertificates().
7. Detach and delete the certificate from the AWS IoT thing.
8. Updates the shadow for the specified thing.
9. Write out the state information, in JSON format.
10. Creates a rule
11. List rules
12. Search things
13. Delete Thing.

 Note: We have buy off on these operations from IoT SME. 

Prerequisites
If you need to, install or update the latest version of the AWS CLI.

Deploy resources
To deploy the stack using the template, run the following command:

aws cloudformation deploy --template-file stack.yaml --stack-name LargeQueryStack
Destroy resources
To destroy the stack, run the following command:

aws cloudformation delete-stack --stack-name LargeQueryStack
Sample logs
A lot of logs are needed to make a robust example. If you happen to have a log group with over 10,000 logs at the ready, great! If not, there are two resources that can help:

Resources
make-log-files.sh will create 50,000 logs and divide them among 5 files of 10,000 logs each (the maximum for each call to 'PutLogEvents'). Two timestamps will output to the console. These timestamps can be used to configure the query. Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.
put-log-events.sh will use the AWS CLI to put the created files from Step 1 into the log group/stream created by the CloudFormation template.
Implementations
This example is implemented in the following languages:

JavaScript
Additional reading
CloudWatch Logs Insights query syntax