/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DeleteStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/Model/GetRoleRequest.h>
#include <fstream>
#include "glue_samples.h"


namespace AwsDoc {
    namespace Glue {
         static const Aws::String CDK_TOOLKIT_TEMPLATE(
                SOURCE_DIR "/bootstrap-template.yaml");
    } // Glue
} // AwsDoc

const Aws::String AwsDoc::Glue::CDK_TOOLKIT_STACK_NAME("CDKToolkit");

bool AwsDoc::Glue::getRoleArn(const Aws::String &roleName, Aws::String &roleArn,
                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient client(clientConfig);

    Aws::IAM::Model::GetRoleRequest request;
    request.SetRoleName(roleName);

    Aws::IAM::Model::GetRoleOutcome outcome = client.GetRole(request);


    if (outcome.IsSuccess()) {
        std::cout << "Successfully retrieved role." << std::endl;
        roleArn = outcome.GetResult().GetRole().GetArn();
    }
    else {
        std::cerr << "Error retrieving role. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDoc::Glue::bootstrapCDK(bool &cdkBootstrapCreated,
                                const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::Model::Stack stack = getStackDescription(
            CDK_TOOLKIT_STACK_NAME,
            clientConfig);

    cdkBootstrapCreated = false;
    bool result = true;
    if (stack.GetStackName().empty()) {
        std::cout << "Creating CDK toolkit stack." << std::endl;

        std::vector<Aws::CloudFormation::Model::Output> outputs;
        Aws::String roleArn;
        result = createCloudFormationResource(CDK_TOOLKIT_STACK_NAME,
                                              CDK_TOOLKIT_TEMPLATE,
                                              outputs, clientConfig);
        if (result) {
            cdkBootstrapCreated = true;
        }
    }

    return result;
}


bool AwsDoc::Glue::deleteCloudFormationResource(const Aws::String &stackName,
                                                const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::CloudFormationClient client(clientConfig);
    Aws::CloudFormation::Model::DeleteStackRequest request;
    request.SetStackName(stackName);

    Aws::CloudFormation::Model::DeleteStackOutcome outcome = client.DeleteStack(
            request);

    if (outcome.IsSuccess()) {
        Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS;
        int iterations = 0;
        do {
            ++iterations;
            Aws::CloudFormation::Model::Stack stack = getStackDescription(stackName,
                                                                          clientConfig);
            if (!stack.GetStackName().empty()) {
                if (stack.GetStackStatus() != stackStatus || ((iterations % 10) == 0)) {
                    std::cout << "Stack " << stackName << " status is '";
                    switch (stack.GetStackStatus()) {
                        case Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS:
                            std::cout << "DELETE_IN_PROGRESS";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::DELETE_FAILED:
                            std::cout << "DELETE_FAILED";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE:
                            std::cout << "DELETE_COMPLETE";
                            break;
                        default:
                            std::cout << static_cast<int>(stack.GetStackStatus());
                            break;

                    }
                    std::cout << "' after " << iterations << " seconds." << std::endl;
                }
                stackStatus = stack.GetStackStatus();

                if (stackStatus ==
                    Aws::CloudFormation::Model::StackStatus::DELETE_FAILED) {
                    std::cerr << "Delete of stack failed. "
                              << stack.GetStackStatusReason() <<
                              std::endl;
                }

            }
            else {
                break;
            }
            if (iterations > 300) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_FAILED;
            }
        } while (Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS ==
                 stackStatus);
    }
    else {
        std::cerr << "Delete stack failed "
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}


Aws::CloudFormation::Model::Stack
AwsDoc::Glue::getStackDescription(const Aws::String &stackName,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::Model::Stack result;

    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::DescribeStacksRequest request;
    request.SetStackName(stackName);
    Aws::CloudFormation::Model::DescribeStacksOutcome outcome = client.DescribeStacks(
            request);
    if (outcome.IsSuccess()) {
        auto stacks = outcome.GetResult().GetStacks();
        for (auto &stack: stacks) {
            if (stack.GetStackName() == stackName) {
                result = stack;
                break;
            }
        }
    }
    else {
        std::cerr << "DescribeStacks failed " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}


bool
AwsDoc::Glue::createCloudFormationResource(const Aws::String &stackName,
                                           const Aws::String &templateFilePath,
                                           std::vector<Aws::CloudFormation::Model::Output> &outputs,
                                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::CreateStackRequest request;

    std::ifstream ifstream(templateFilePath);
    if (!ifstream) {
        std::cerr << "Could not load file '" << templateFilePath << "'" << std::endl;
        return false;
    }
    std::ostringstream templateStream;
    templateStream << ifstream.rdbuf();
    request.SetTemplateBody(templateStream.str());
    request.SetStackName(stackName);
    request.SetCapabilities(
            {Aws::CloudFormation::Model::Capability::CAPABILITY_NAMED_IAM});

    Aws::CloudFormation::Model::CreateStackOutcome outcome = client.CreateStack(
            request);

    bool result = false;
    if (outcome.IsSuccess() || outcome.GetError().GetErrorType() ==
                               Aws::CloudFormation::CloudFormationErrors::ALREADY_EXISTS) {
        Aws::CloudFormation::Model::DescribeStacksRequest waitRequest;
        waitRequest.SetStackName(stackName);

        Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS;
        int iterations = 0;
        do {
            ++iterations;
            Aws::CloudFormation::Model::Stack stack = getStackDescription(stackName,
                                                                          clientConfig);
            if (!stack.GetStackName().empty()) {
                if (stack.GetStackStatus() != stackStatus || ((iterations % 10) == 0)) {
                    std::cout << "Stack " << stackName << " status ";
                    switch (stack.GetStackStatus()) {
                        case Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS:
                            std::cout << "CREATE_IN_PROGRESS";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::CREATE_FAILED:
                            std::cout << "CREATE_FAILED";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE:
                            std::cout << "CREATE_COMPLETE";
                            break;
                        default:
                            std::cout << "stack status reason ."
                                      << stack.GetStackStatusReason() << "' status id ";
                            std::cout << static_cast<int>(stack.GetStackStatus());
                            break;

                    }
                    std::cout << " after " << iterations << " seconds." << std::endl;
                }
                stackStatus = stack.GetStackStatus();
                if (Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE ==
                    stackStatus) {
                    outputs = stack.GetOutputs();
                    result = true;
                }
            }
            else {
                break;
            }
            if (iterations > 900) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_FAILED;
            }
        } while ((Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS ==
                  stackStatus) ||
                 (Aws::CloudFormation::Model::StackStatus::ROLLBACK_IN_PROGRESS ==
                  stackStatus));
    }
    else {
        std::cerr << "Create stack failed " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}


int AwsDoc::Glue::askQuestionForIntRange(const Aws::String &string, int low,
                                         int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });
    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}


Aws::String AwsDoc::Glue::askQuestion(const Aws::String &string,
                                      const std::function<bool(
                                              Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            result.clear();
        }
    } while (result.empty());

    return result;
}

