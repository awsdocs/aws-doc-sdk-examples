/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to create and invoke an AWS Lambda function.
 *
 * 1. Create Identity and Access Management (IAM) for Lambda function;
 * 1. Create a Lambda function.
 * 2  List the Lambda functions.
 * 2. Invoke the Lambda function.
 * 3
 * 3. Update the Lambda function code.
 * 4. Copy the object to a different "folder" in the bucket.
 * 5. List objects in the bucket.
 * 6. Delete all objects in the bucket.
 * 7. Delete the bucket.
 *
 */

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateRoleRequest.h>
#include <aws/iam/model/AttachRolePolicyRequest.h>
#include <aws/iam/model/DeleteRoleRequest.h>
#include <aws/iam/model/DetachRolePolicyRequest.h>
#include <aws/iam/model/GetRoleRequest.h>
#include <aws/lambda/LambdaClient.h>
#include <aws/lambda/model/CreateFunctionRequest.h>
#include <aws/lambda/model/DeleteFunctionRequest.h>
#include <fstream>

namespace AwsDoc {
    namespace Lambda {
        static Aws::String ROLE_NAME("doc_example_lambda_calculator_role");
        static Aws::String LAMBDA_NAME("doc_example_lambda_calculator");
        static Aws::String LAMBDA_HANDLER_NAME("doc_example_lambda_calculator.lambda_handler");
        static Aws::String BASIC_LAMBDA_CODE(SOURCE_DIR "/lambda_handler_basic.py.zip");


        static Aws::String ROLE_POLICY_ARN("arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole");

        bool getStartedWithFunctionsScenario(
                const Aws::Client::ClientConfiguration &clientConfig);

        bool getIamRoleArn(Aws::String &roleARN,
                           const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteIamRole(const Aws::Client::ClientConfiguration &clientConfig);
    } // Lambda
} // AwsDoc

bool AwsDoc::Lambda::getStartedWithFunctionsScenario(
        const Aws::Client::ClientConfiguration &clientConfig) {

    Aws::Lambda::LambdaClient client(clientConfig);
    Aws::String roleArn;
    if (!getIamRoleArn(roleArn, clientConfig))
    {
        return false;
    }
    {
        Aws::Lambda::Model::CreateFunctionRequest request;
        request.SetFunctionName(LAMBDA_NAME);
        request.SetRuntime(Aws::Lambda::Model::Runtime::python3_8);
        request.SetRole(roleArn);
        request.SetHandler(LAMBDA_HANDLER_NAME);
        request.SetPublish(true);
        Aws::Lambda::Model::FunctionCode code;
        std::ifstream ifstream(BASIC_LAMBDA_CODE.c_str(), std::ios_base::in | std::ios_base::binary);
        Aws::StringStream buffer;
        buffer << ifstream.rdbuf();

        code.SetZipFile(Aws::Utils::ByteBuffer((unsigned char*)buffer.str().c_str(),
                                                       buffer.str().length()));
        request.SetCode(code);

        Aws::Lambda::Model::CreateFunctionOutcome outcome = client.CreateFunction(request);

        if (outcome.IsSuccess()) {
            std::cout << "CreateFunction was successful." << std::endl;
        }
        else {
            std::cerr << "Error with CreateFunction. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    {
        Aws::Lambda::Model::DeleteFunctionRequest request;
        request.SetFunctionName(LAMBDA_NAME);

        Aws::Lambda::Model::DeleteFunctionOutcome outcome = client.DeleteFunction(request);
        
        if (outcome.IsSuccess()) {
            std::cout << "Lambda::DeleteFunction was successful." << std::endl;
        }
        else {
            std::cerr << "Error with Lambda::DeleteFunction. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return true;
}

bool AwsDoc::Lambda::getIamRoleArn(Aws::String &roleARN,
                                   const Aws::Client::ClientConfiguration &clientConfig) {

    Aws::IAM::IAMClient client(clientConfig);

    {
        Aws::IAM::Model::CreateRoleRequest createRoleRequest;
        createRoleRequest.SetRoleName(ROLE_NAME);
        createRoleRequest.SetAssumeRolePolicyDocument(R"({
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "lambda.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        })");

        Aws::IAM::Model::CreateRoleOutcome createRoleOutcome = client.CreateRole(createRoleRequest);


        if (createRoleOutcome.IsSuccess()) {
            std::cout << "IAM::CreateRole was successful." << std::endl;
            roleARN = createRoleOutcome.GetResult().GetRole().GetArn();
        }
        else if (createRoleOutcome.GetError().GetErrorType() == Aws::IAM::IAMErrors::ENTITY_ALREADY_EXISTS)
        {
            Aws::IAM::Model::GetRoleRequest request;
            request.SetRoleName(ROLE_NAME);

            Aws::IAM::Model::GetRoleOutcome outcome = client.GetRole(request);

            if (outcome.IsSuccess()) {
                std::cout << "IAM::GetRole was successful." << std::endl;
                roleARN = outcome.GetResult().GetRole().GetArn();
                return true;
            }
            else {
                std::cerr << "Error with IAM::GetRole. " << outcome.GetError().GetMessage()
                          << std::endl;
                return false;
            }
        }
        else {
            std::cerr << "Error with IAM::CreateRole. "
                      << createRoleOutcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    {
        Aws::IAM::Model::AttachRolePolicyRequest attachRolePolicyRequest;
        attachRolePolicyRequest.SetRoleName(ROLE_NAME);
        attachRolePolicyRequest.WithPolicyArn("arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole");

        Aws::IAM::Model::AttachRolePolicyOutcome attachRolePolicyOutcome = client.AttachRolePolicy(
                attachRolePolicyRequest);
        if (attachRolePolicyOutcome.IsSuccess()) {
            std::cout << "Successfully attached the role policy";
        }
        else
        {
            std::cerr << "Error creating policy. " <<
                      attachRolePolicyOutcome.GetError().GetMessage() << std::endl;
            return false;

        }
    }

    return true;
}

bool AwsDoc::Lambda::deleteIamRole(const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient client(clientConfig);
    // Detach the policy from the role.
    bool result = true;
    {
        Aws::IAM::Model::DetachRolePolicyRequest request;
        request.SetPolicyArn(ROLE_POLICY_ARN);
        request.SetRoleName(ROLE_NAME);

        Aws::IAM::Model::DetachRolePolicyOutcome outcome = client.DetachRolePolicy(
                request);
        if (outcome.IsSuccess()) {
            std::cout << "Successfully detached the IAM role policy."<< std::endl;
        }
        else          {
            std::cerr << "Error Detaching policy from roles. " <<
                      outcome.GetError().GetMessage() << std::endl;
            result = false;
        }
    }

    // Delete the role.
    Aws::IAM::Model::DeleteRoleRequest request;
    request.SetRoleName(ROLE_NAME);

    Aws::IAM::Model::DeleteRoleOutcome outcome = client.DeleteRole(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting role. " <<
                  outcome.GetError().GetMessage() << std::endl;
        result = false;
    }
    else {
        std::cout << "Successfully deleted the IAM role." << std::endl;
    }

    return result;
}

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {

    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::Lambda::getStartedWithFunctionsScenario(clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


