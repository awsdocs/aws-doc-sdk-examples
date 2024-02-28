// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * This workflow demonstrates how to create and modify AWS IoT things and shadows,
 *
 * 1. Create an AWS IoT thing.
 * 2. Perform operations on the AWS IoT thing and get the thing's endpoint.
 * 3. Perform operations on the AWS Iot thing's shadow.
 * 4. Work with rules and search for AWS IoT things.
 * 5. Cleanup.
 *
 */


#include <iostream>
#include <iomanip>
#include <filesystem>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DeleteStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <aws/core/utils/UUID.h>
#include "../iot_samples.h"

namespace AwsDoc {
    namespace IoT {
        // Path to the CloudFormation template used by this workflow.
        const char STACK_TEMPLATE_PATH[] = TEMPLATES_PATH
                                           "/cfn_template.yaml";
        const char STACK_NAME[] = "cpp-aws-iot-things-and-shadows";

            // Outputs of the CloudFormation stack defined in cfn_template.yaml.
        const char SNS_TOPIC_ARN_OUTPUT[] = "SNSTopicArn";
        const char ROLE_ARN_OUTPUT[] = "RoleArn";

        const char MQTT_MESSAGE_TOPIC_FILTER[] = "topic/subtopic";

        //! Cleanup routine for the workflow.
        /*!
          \param thingName: An AWS IoT thing name:
          \param certificateARN: The Amazon Resource Name (ARN) of a certificate.
          \param certificateID: The ID of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool cleanup(const Aws::String &thingName, const Aws::String &certificateARN,
                     const Aws::String &certificateID, const Aws::String &stackName,
                     const Aws::String &ruleName, bool askForConfirmation,
                     const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine which waits until a CloudFormation stack is created.
        /*!
           \param cloudFormationClient: A CloudFormation client.
           \param stackName: The stack name.
           \return bool: Function succeeded.
        */
        static bool waitStackCreated(
                Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
                const std::string &stackName,
                Aws::Vector<Aws::CloudFormation::Model::Output> &outputs);

        //! Routine which waits until a CloudFormation stack is deleted.
        /*!
           \param cloudFormationClient: A CloudFormation client.
           \param stackName: The stack name.
           \return bool: Function succeeded.
        */
        static bool waitStackDeleted(
                Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
                const std::string &stackName);

        //! Routine which creates a CloudFormation stack.
        /*!
           \param stackName: The stack name.
           \param dataStoreName: A data store name passed as a parameter.
           \param clientConfiguration: Aws client configuration.
           \return ws::Map<Aws::String, Aws::String>: Map of outputs.
        */
        static Aws::Map<Aws::String, Aws::String>
        createCloudFormationStack(const Aws::String &stackName,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine which deletes a CloudFormation stack.
        /*!
           \param stackName: The stack name.
           \param clientConfiguration: Aws client configuration.
           \return bool: Function succeeded.
        */
        static bool
        deleteStack(const std::string &stackName,
                    const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \param string: A string to test.
         \return bool: True if empty.
         */
        static bool testForEmptyString(const Aws::String &string);

        //! Command line prompt/response utility function.
        /*!
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        static Aws::String askQuestion(const Aws::String &string,
                                       const std::function<bool(
                                               Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        static bool askYesNoQuestion(const Aws::String &string);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        static int askQuestionForIntRange(const Aws::String &string, int low,
                                          int high);

        //! Utility routine to print a line of asterisks to standard out.
        /*!
        \return void:
         */
        static void printAsterisksLine() {
            std::cout << "\n" << std::setfill('*') << std::setw(88) << "\n"
                      << std::endl;
        }

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \return bool: Always true.
         */
        static bool alwaysTrueTest(const Aws::String &) { return true; }

    }
}

//! Workflow which demonstrates multiple operations on IoT things and shadows.
/*!
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */

bool AwsDoc::IoT::IoTBasicsWorkflow(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << "Welcome to the AWS IoT example workflow." << std::endl;
    printAsterisksLine();
    std::cout << R"(This example program demonstrates various interactions with the AWS Internet of things (IoT) Core service. The program guides
you through a series of steps, including creating an IoT thing, generating a device certificate, and updating the thing with
attributes. It utilizes the AWS SDK for C++ and incorporates functionality for creating and managing IoT
things, certificates, rules, shadows, and performing searches. The program aims to showcase AWS IoT capabilities and
provides a comprehensive example for developers working with AWS IoT in a C++ environment.
)" << std::endl;

    askQuestion("Press Enter to continue...", alwaysTrueTest);

    printAsterisksLine();

    std::cout << "First, an IoT thing will be created.\n" << std::endl;
    std::cout
            << "An AWS IoT thing represents a virtual entity in the AWS IoT service that can be "
            << "associated with a physical device." << std::endl;
// snippet-start:[cpp.example_code.iot.iot_things_and_shadows.create_thing]
    Aws::String thingName = askQuestion("Enter a thing name: ");

    if (!createThing(thingName, clientConfiguration)) {
        std::cerr << "Exiting because createThing failed." << std::endl;
        cleanup("", "", "", "", "", false, clientConfiguration);
        return false;
    }
// snippet-end:[cpp.example_code.iot.iot_things_and_shadows.create_thing]

    std::cout << std::endl;
    printAsterisksLine();

    std::cout << "Now a device certificate will be generated for the thing.\n"
              << std::endl;
    std::cout
            << "A device certificate performs a role in securing the communication between devices (things) and the\n"
               "AWS IoT platform." << std::endl;
    std::cout
            << "The 'CreateKeysAndCertificate' API is used to generate a device certificate. This routine returns\n"
            << "a private key. This is the only time AWS IoT issues the private key for this certificate, so it is\n"
            << "important to keep it in a secure location." << std::endl;

    // snippet-start:[cpp.example_code.iot.iot_things_and_shadows.certificates]
    Aws::String certificateARN;
    Aws::String certificateID;
    if (askYesNoQuestion("Would you like to create a certificate for your thing? (y/n) ")) {
        Aws::String outputFolder;
        if (askYesNoQuestion(
                "Would you like to save the certificate and keys to file? (y/n) ")) {
            outputFolder = std::filesystem::current_path();
            outputFolder += "/device_keys_and_certificates";

            std::filesystem::create_directories(outputFolder);

            std::cout << "The certificate and keys will be saved to the folder: "
                      << outputFolder << std::endl;
        }

        if (!createKeysAndCertificate(outputFolder, certificateARN, certificateID,
                                      clientConfiguration)) {
            std::cerr << "Exiting because createKeysAndCertificate failed."
                      << std::endl;
            cleanup(thingName, "", "", "", "", false, clientConfiguration);
            return false;
        }

        std::cout << "\nNext, the certificate will be attached to the thing.\n"
                  << std::endl;
        if (!attachThingPrincipal(certificateARN, thingName, clientConfiguration)) {
            std::cerr << "Exiting because attachThingPrincipal failed." << std::endl;
            cleanup(thingName, certificateARN, certificateID, "", "",
                    false,
                    clientConfiguration);
            return false;
        }
    }
    // snippet-end:[cpp.example_code.iot.iot_things_and_shadows.certificates]

    printAsterisksLine();

    std::cout << "Next, the thing will be updated with some attributes.\n" << std::endl;
    std::cout << "IoT thing attributes, represented as key-value pairs, offer a pivotal advantage in facilitating efficient data \n"
                 "management and retrieval within the AWS IoT ecosystem. " << std::endl;

    askQuestion("Press Enter to continue:", alwaysTrueTest);

    // snippet-start:[cpp.example_code.iot.iot_things_and_shadows.various_operations]
    if (!updateThing(thingName, { {"location", "Office"}, {"firmwareVersion", "v2.0"} }, clientConfiguration)) {
        std::cerr << "Exiting because updateThing failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }

    printAsterisksLine();

    std::cout << "Now an endpoint will be retrieved for your account.\n" << std::endl;
    std::cout << "An IoT Endpoint refers to a specific URL or Uniform Resource Locator that serves as the entry point\n"
    << "for communication between IoT devices and the AWS IoT service." << std::endl;

    askQuestion("Press Enter to continue:", alwaysTrueTest);

    Aws::String endpoint;
    if (!describeEndpoint(endpoint, clientConfiguration)) {
        std::cerr << "Exiting because getEndpoint failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }
    std::cout <<"Your endpoint is " << endpoint << "." << std::endl;
    printAsterisksLine();

    std::cout << "Now the certificates in your account will be listed." << std::endl;
    askQuestion("Press Enter to continue:", alwaysTrueTest);

    if (!listCertificates(clientConfiguration)) {
        std::cerr << "Exiting because listCertificates failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }

    printAsterisksLine();

    std::cout << "Now the shadow for the thing will be updated.\n" << std::endl;
    std::cout << "A thing shadow refers to a feature that enables you to create a virtual representation, or \"shadow,\"\n"
    << "of a physical device or thing. The thing shadow allows you to synchronize and control the state of a device between\n"
    << "the cloud and the device itself. and the AWS IoT service. For example, you can write and retrieve JSON data from a thing shadow." << std::endl;
    askQuestion("Press Enter to continue:", alwaysTrueTest);

    if (!updateThingShadow(thingName, R"({"state":{"reported":{"temperature":25,"humidity":50}}})", clientConfiguration)) {
        std::cerr << "Exiting because updateThingShadow failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }

    printAsterisksLine();

    std::cout << "Now, the state information for the shadow will be retrieved.\n" << std::endl;
    askQuestion("Press Enter to continue:", alwaysTrueTest);

    Aws::String shadowState;
    if (!getThingShadow(thingName, shadowState, clientConfiguration)) {
        std::cerr << "Exiting because getThingShadow failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }
    std::cout << "The retrieved shadow state is: " << shadowState << std::endl;

    printAsterisksLine();

    std::cout << "A rule with now be added to to the thing.\n" << std::endl;
    std::cout << "Any user who has permission to create rules will be able to access data processed by the rule." << std::endl;
    std::cout << "In this case, the rule will use an Simple Notification Service (SNS) topic and an IAM rule." << std::endl;
    std::cout << "These resources will be created using a CloudFormation template." << std::endl;
    std::cout << "Stack creation may take a few minutes." << std::endl;

    askQuestion("Press Enter to continue: ", alwaysTrueTest);
    Aws::Map<Aws::String, Aws::String> outputs =createCloudFormationStack(STACK_NAME,clientConfiguration);
    if (outputs.empty()) {
        std::cerr << "Exiting because createCloudFormationStack failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, "", "", false,
                clientConfiguration);
        return false;
    }

    // Retrieve the topic ARN and role ARN from the CloudFormation stack outputs.
    auto topicArnIter = outputs.find(SNS_TOPIC_ARN_OUTPUT);
    auto roleArnIter = outputs.find(ROLE_ARN_OUTPUT);
    if ((topicArnIter == outputs.end()) || (roleArnIter == outputs.end())) {
        std::cerr << "Exiting because output '" << SNS_TOPIC_ARN_OUTPUT <<
        "' or '" << ROLE_ARN_OUTPUT << "'not found in the CloudFormation stack."  << std::endl;
        cleanup(thingName, certificateARN, certificateID, STACK_NAME, "",
                false,
                clientConfiguration);
        return false;
    }

    Aws::String topicArn = topicArnIter->second;
    Aws::String roleArn = roleArnIter->second;
    Aws::String sqlStatement = "SELECT * FROM '";
    sqlStatement += MQTT_MESSAGE_TOPIC_FILTER;
    sqlStatement += "'";

    printAsterisksLine();

    std::cout << "Now a rule will be created.\n" << std::endl;
    std::cout << "Rules are an administrator-level action. Any user who has permission\n"
                 << "to create rules will be able to access data processed by the rule." << std::endl;
    std::cout << "In this case, the rule will use an SNS topic" << std::endl;
    std::cout << "and the following SQL statement '" << sqlStatement << "'." << std::endl;
    std::cout << "For more information on IoT SQL, see https://docs.aws.amazon.com/iot/latest/developerguide/iot-sql-reference.html" << std::endl;
    Aws::String ruleName = askQuestion("Enter a rule name: ");
    if (!createTopicRule(ruleName, topicArn, sqlStatement, roleArn, clientConfiguration)) {
        std::cerr << "Exiting because createRule failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, STACK_NAME, "",
                false,
                clientConfiguration);
        return false;
    }

    printAsterisksLine();

    std::cout << "Now your rules will be listed.\n" << std::endl;
    askQuestion("Press Enter to continue: ", alwaysTrueTest);
    if (!listTopicRules(clientConfiguration)) {
        std::cerr << "Exiting because listRules failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, STACK_NAME, ruleName,
                false,
                clientConfiguration);
        return false;
    }

    printAsterisksLine();
    Aws::String queryString = "thingName:" + thingName;
    std::cout << "Now the AWS IoT fleet index will be queried with the query\n'"
    << queryString << "'.\n" << std::endl;
    std::cout << "For query information, see https://docs.aws.amazon.com/iot/latest/developerguide/query-syntax.html" << std::endl;

    std::cout << "For this query to work, thing indexing must be enabled in your account.\n"
    << "This can be done with the awscli command line by calling 'aws iot update-indexing-configuration'\n"
       << "or it can be done programmatically." << std::endl;
    std::cout << "For more information, see https://docs.aws.amazon.com/iot/latest/developerguide/managing-index.html" << std::endl;
    if (askYesNoQuestion("Do you want to enable thing indexing in your account? (y/n) "))
    {
        Aws::IoT::Model::ThingIndexingConfiguration thingIndexingConfiguration;
        thingIndexingConfiguration.SetThingIndexingMode(Aws::IoT::Model::ThingIndexingMode::REGISTRY_AND_SHADOW);
        thingIndexingConfiguration.SetThingConnectivityIndexingMode(Aws::IoT::Model::ThingConnectivityIndexingMode::STATUS);
        // The ThingGroupIndexingConfiguration object is ignored if not set.
        Aws::IoT::Model::ThingGroupIndexingConfiguration thingGroupIndexingConfiguration;
        if (!updateIndexingConfiguration(thingIndexingConfiguration, thingGroupIndexingConfiguration, clientConfiguration)) {
            std::cerr << "Exiting because updateIndexingConfiguration failed." << std::endl;
            cleanup(thingName, certificateARN, certificateID, STACK_NAME,
                    ruleName, false,
                    clientConfiguration);
            return false;
        }
    }

    if (!searchIndex(queryString, clientConfiguration)) {

        std::cerr << "Exiting because searchIndex failed." << std::endl;
        cleanup(thingName, certificateARN, certificateID, STACK_NAME, ruleName,
                false,
                clientConfiguration);
        return false;
    }
    // snippet-end:[cpp.example_code.iot.iot_things_and_shadows.various_operations]

    printAsterisksLine();

    std::cout << "This concludes the IoT workflow. You will now be asked about retaining\n"
    << "the resources that were created." << std::endl;
    askQuestion("Press Enter to continue: ", alwaysTrueTest);

    return cleanup(thingName, certificateARN, certificateID, STACK_NAME,
                   ruleName, true,
                   clientConfiguration);
}

//! Cleanup routine for the workflow.
/*!
  \param thingName: An AWS IoT thing name:
  \param certificateARN: The Amazon Resource Name (ARN) of a certificate.
  \param certificateID: The ID of a certificate.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.iot.iot_things_and_shadows.cleanup]
bool
AwsDoc::IoT::cleanup(const Aws::String &thingName, const Aws::String &certificateARN,
                     const Aws::String &certificateID, const Aws::String &stackName,
                     const Aws::String &ruleName, bool askForConfirmation,
                     const Aws::Client::ClientConfiguration &clientConfiguration) {
    bool result = true;

    if (!ruleName.empty() && (!askForConfirmation ||
                               askYesNoQuestion("Delete the rule '" + ruleName +
                                                "'? (y/n) "))) {
        result &= deleteTopicRule(ruleName, clientConfiguration);
    }

    Aws::CloudFormation::CloudFormationClient cloudFormationClient(clientConfiguration);

    if (!stackName.empty() && (!askForConfirmation ||
                               askYesNoQuestion(
                                       "Delete the CloudFormation stack '" + stackName +
                                       "'? (y/n) "))) {
        result &= deleteStack(stackName, clientConfiguration);
    }

    if (!certificateARN.empty() && (!askForConfirmation ||
                                    askYesNoQuestion("Delete the certificate '" +
                                                     certificateARN + "'? (y/n) "))) {
        result &= detachThingPrincipal(certificateARN, thingName, clientConfiguration);
        result &= deleteCertificate(certificateID, clientConfiguration);
    }

    if (!thingName.empty() && (!askForConfirmation ||
                               askYesNoQuestion("Delete the thing '" + thingName +
                                                "'? (y/n) "))) {
        result &= deleteThing(thingName, clientConfiguration);
    }

    return result;
}
// snippet-end:[cpp.example_code.iot.iot_things_and_shadows.cleanup]


#ifndef EXCLUDE_WORKFLOW_MAIN

/*
 *
 *  main function
 *
 *  Usage: 'run_iot_things_and_shadows_workflow'
 *
 */

int main(int argc, const char *argv[]) {

    Aws::SDKOptions options;

    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::IoT::IoTBasicsWorkflow(clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // EXCLUDE_WORKFLOW_MAIN



//! Routine which waits until a CloudFormation stack is created.
/*!
   \param cloudFormationClient: A CloudFormation client.
   \param stackName: The stack name.
   \return bool: Function succeeded.
*/
bool AwsDoc::IoT::waitStackCreated(
        Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
        const std::string &stackName,
        Aws::Vector<Aws::CloudFormation::Model::Output> &outputs) {
    Aws::CloudFormation::Model::DescribeStacksRequest describeStacksRequest;
    describeStacksRequest.SetStackName(stackName);
    Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS;

    int count = 0;
    while (stackStatus ==
           Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS) {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        stackStatus = Aws::CloudFormation::Model::StackStatus::NOT_SET;
        auto outcome = cloudFormationClient.DescribeStacks(describeStacksRequest);
        if (outcome.IsSuccess()) {
            const auto &stacks = outcome.GetResult().GetStacks();
            if (!stacks.empty()) {
                const auto &stack = stacks[0];
                stackStatus = stack.GetStackStatus();
                if (stackStatus ==
                    Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE) {
                    outputs = stack.GetOutputs();
                }
                else if (stackStatus !=
                         Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS) {
                    std::cerr << "Failed to create stack because "
                              << stack.GetStackStatusReason() << std::endl;
                }
                if (count % 5 == 0)
                {
                    std::cout << "Stack status: " << Aws::CloudFormation::Model::StackStatusMapper::GetNameForStackStatus(stackStatus) << std::endl;
                }
                count++;
            }
        }
    }

    if (stackStatus == Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE) {
        std::cout << "Stack creation completed." << std::endl;
    }

    return stackStatus == Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE;
}

//! Routine which waits until a CloudFormation stack is deleted.
/*!
   \param cloudFormationClient: A CloudFormation client.
   \param stackName: The stack name.
   \return bool: Function succeeded.
*/
bool AwsDoc::IoT::waitStackDeleted(
        Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
        const std::string &stackName) {
    Aws::CloudFormation::Model::DescribeStacksRequest describeStacksRequest;
    describeStacksRequest.SetStackName(stackName);
    Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS;

    int count = 0;
    while (stackStatus ==
           Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS) {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        stackStatus = Aws::CloudFormation::Model::StackStatus::NOT_SET;
        auto outcome = cloudFormationClient.DescribeStacks(describeStacksRequest);
        if (outcome.IsSuccess()) {
            const auto &stacks = outcome.GetResult().GetStacks();
            if (!stacks.empty()) {
                const auto &stack = stacks[0];
                stackStatus = stack.GetStackStatus();
                if (stackStatus !=
                    Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS &&
                    stackStatus !=
                    Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE) {
                    std::cerr << "Failed to delete stack because "
                              << stack.GetStackStatusReason() << std::endl;
                }

                if (count % 5 == 0) {
                    std::cout << "Stack status: "
                              << Aws::CloudFormation::Model::StackStatusMapper::GetNameForStackStatus(
                                      stackStatus) << std::endl;
                }
                count++;
            }
            else {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
            }
        }
        else {
            auto &error = outcome.GetError();
            if (error.GetResponseCode() ==
                Aws::Http::HttpResponseCode::BAD_REQUEST &&
                (outcome.GetError().GetMessage().find("does not exist") !=
                 std::string::npos)) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
            }
            else {
                std::cerr << "Failed to describe stack. "
                          << outcome.GetError().GetMessage() << std::endl;
            }
        }
    }

    if (stackStatus == Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE) {
        std::cout << "Stack deletion completed." << std::endl;
    }

    return stackStatus == Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
}


//! Routine which creates a CloudFormation stack.
/*!
   \param stackName: The stack name.
   \param dataStoreName: A data store name passed as a parameter.
   \param clientConfiguration: Aws client configuration.
   \return ws::Map<Aws::String, Aws::String>: Map of outputs.
*/
Aws::Map<Aws::String, Aws::String>
AwsDoc::IoT::createCloudFormationStack(const Aws::String &stackName,
                                       const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::Map<Aws::String, Aws::String> result;
    Aws::CloudFormation::CloudFormationClient cloudFormationClient(
            clientConfiguration);
    Aws::CloudFormation::Model::CreateStackRequest createStackRequest;
    createStackRequest.SetStackName(stackName);
    std::ifstream inFileStream(STACK_TEMPLATE_PATH);

    if (!inFileStream) {
        std::cerr << "Failed to open file" << std::endl;
        return result;
    }

    std::stringstream stringStream;
    stringStream << inFileStream.rdbuf();
    createStackRequest.SetTemplateBody(stringStream.str());

    createStackRequest.SetCapabilities(
            {Aws::CloudFormation::Model::Capability::CAPABILITY_IAM});

    auto outcome = cloudFormationClient.CreateStack(createStackRequest);

    Aws::Vector<Aws::CloudFormation::Model::Output> outputs;
    if (outcome.IsSuccess()) {
        std::cout << "Stack creation initiated." << std::endl;
        std::cout << "Waiting for the stack to be created." << std::endl;
        waitStackCreated(cloudFormationClient, stackName, outputs);
    }
    else {
        std::cerr << "Failed to create stack" << outcome.GetError().GetMessage()
                  << std::endl;
    }

    if (!outputs.empty()) {
        for (auto &output: outputs) {
            result[output.GetOutputKey()] = output.GetOutputValue();
        }
    }
    return result;
}

//! Routine which deletes a CloudFormation stack.
/*!
   \param stackName: The stack name.
   \param clientConfiguration: Aws client configuration.
   \return bool: Function succeeded.
*/
bool AwsDoc::IoT::deleteStack(const std::string &stackName,
                              const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::CloudFormation::CloudFormationClient cloudFormationClient(
            clientConfiguration);
    Aws::CloudFormation::Model::DeleteStackRequest deleteStackRequest;
    deleteStackRequest.SetStackName(stackName);
    auto outcome = cloudFormationClient.DeleteStack(deleteStackRequest);
    bool result = false;
    if (outcome.IsSuccess()) {
        std::cout << "Stack deletion initiated." << std::endl;
        result = waitStackDeleted(cloudFormationClient, stackName);
    }
    else {
        std::cerr << "Failed to delete stack" << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}

//! Test routine passed as argument to askQuestion routine.
/*!
\param string: A string to test.
\return bool: True if empty.
*/
bool AwsDoc::IoT::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::IoT::askQuestion(const Aws::String &string,
                                     const std::function<bool(
                                             Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::IoT::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int
AwsDoc::IoT::askQuestionForIntRange(const Aws::String &string, int low,
                                    int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cerr << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cerr << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

