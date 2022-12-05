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
#include <aws/iam/model/ListAttachedRolePoliciesRequest.h>
#include <aws/lambda/LambdaClient.h>
#include <aws/lambda/model/CreateFunctionRequest.h>
#include <aws/lambda/model/DeleteFunctionRequest.h>
#include <aws/lambda/model/GetFunctionRequest.h>
#include <aws/lambda/model/GetFunctionConfigurationRequest.h>
#include <aws/lambda/model/InvokeRequest.h>
#include <aws/lambda/model/ListFunctionsRequest.h>
#include <aws/lambda/model/UpdateFunctionCodeRequest.h>
#include <aws/lambda/model/UpdateFunctionConfigurationRequest.h>
#include <aws/core/utils/HashingUtils.h>
#include <fstream>
#include "lambda_samples.h"

namespace AwsDoc {
    namespace Lambda {
        static Aws::String ROLE_NAME("doc_example_lambda_calculator_cpp_role");
        static Aws::String LAMBDA_NAME("doc_example_lambda_calculator_cpp");
        static Aws::String LAMBDA_DESCRIPTION("AWS C++ Get started with functions.");
        static Aws::String LAMBDA_HANDLER_NAME("doc_example_lambda_calculator.lambda_handler");
        static Aws::String INCREMENT_LAMBDA_CODE(SOURCE_DIR "/doc_example_lambda_increment.zip");
        static Aws::String CALCULATOR_LAMBDA_CODE(SOURCE_DIR "/doc_example_lambda_calculator.zip");
        static Aws::String ROLE_POLICY_ARN("arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole");
        Aws::String INCREMENT_RESUlT_PREFIX( "The result of the increment is ");
        Aws::String ARITHMETIC_RESUlT_PREFIX("The result of the operation ");

        static bool getIamRoleArn(Aws::String &roleARN,
                           const Aws::Client::ClientConfiguration &clientConfig);

        static bool deleteIamRole(const Aws::Client::ClientConfiguration &clientConfig);

        static Aws::String askQuestion(const Aws::String &string,
                                                const std::function<bool(
                                                        Aws::String)> &test = [](
                                                        const Aws::String &) -> bool { return true; });

        static int askQuestionForInt(const Aws::String &string);

        static int askQuestionForIntRange(const Aws::String &string, int low,
                                                   int high);

        static bool invokeLambdaFunction(const Aws::Utils::Json::JsonValue &jsonPayload,
                                         Aws::Lambda::Model::LogType logType,
                Aws::Lambda::Model::InvokeResult& invokeResult,
                const Aws::Lambda::LambdaClient& client);
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

    int seconds = 0;
    do
    {
        Aws::Lambda::Model::CreateFunctionRequest request;
        request.SetFunctionName(LAMBDA_NAME);
        request.SetDescription(LAMBDA_DESCRIPTION); // Optional.
        request.SetRuntime(Aws::Lambda::Model::Runtime::python3_8);
        request.SetRole(roleArn);
        request.SetHandler(LAMBDA_HANDLER_NAME);
        request.SetPublish(true);
        Aws::Lambda::Model::FunctionCode code;
        std::ifstream ifstream(INCREMENT_LAMBDA_CODE.c_str(), std::ios_base::in | std::ios_base::binary);
        Aws::StringStream buffer;
        buffer << ifstream.rdbuf();

        code.SetZipFile(Aws::Utils::ByteBuffer((unsigned char*)buffer.str().c_str(),
                                                       buffer.str().length()));
        request.SetCode(code);

        Aws::Lambda::Model::CreateFunctionOutcome outcome = client.CreateFunction(request);

        if (outcome.IsSuccess()) {
            std::cout << "The lambda function was successfully created. " << seconds << " seconds elapsed." << std::endl;
            break;
        }
        else if (outcome.GetError().GetErrorType() == Aws::Lambda::LambdaErrors::INVALID_PARAMETER_VALUE &&
                outcome.GetError().GetMessage().find("role") >= 0)
        {
            if ((seconds % 5) == 0) { // Log status every 10 seconds.
                std::cout << "Waiting for the IAM role to become available as a CreateFunction parameter. " << seconds
                          << " seconds elapsed." << std::endl;
            }
        }
        else {
            std::cerr << "Error with CreateFunction. " << outcome.GetError().GetMessage()
                      << std::endl;
            deleteIamRole(clientConfig);
            return false;
        }
        ++seconds;
        std::this_thread::sleep_for(std::chrono::seconds(1));
    } while (60 > seconds);

    std::cout << "The current Lambda function increments 1 by an input." << std::endl;

    {
        int increment = askQuestionForInt("Enter an increment integer ");

        Aws::Lambda::Model::InvokeResult invokeResult;
        Aws::Utils::Json::JsonValue jsonPayload;
        jsonPayload.WithString("action", "increment");
        jsonPayload.WithInteger("number", increment);
        if (invokeLambdaFunction(jsonPayload, Aws::Lambda::Model::LogType::Tail,
                                 invokeResult, client)) {
            Aws::Map<Aws::String, Aws::Utils::Json::JsonView> values =
                    Aws::Utils::Json::JsonView(
                            invokeResult.GetPayload()).GetAllObjects();
            auto iter = values.find("result");
            if (iter != values.end() && iter->second.IsIntegerType()) {
                std::cout << INCREMENT_RESUlT_PREFIX
                          << iter->second.AsInteger() << std::endl;
            }
            else {
                std::cout << "There was an error in execution. Here is the log."
                          << std::endl;
                Aws::Utils::ByteBuffer buffer = Aws::Utils::HashingUtils::Base64Decode(
                        invokeResult.GetLogResult());
                std::cout << "With log " << buffer.GetUnderlyingData() << std::endl;
            }
        }
    }

    std::cout << "The Lambda function will now be updated with new code. Press return to continue, ";
         Aws::String answer;
         std::getline(std::cin, answer);
    {
        Aws::Lambda::Model::UpdateFunctionCodeRequest request;
        request.SetFunctionName(LAMBDA_NAME);
         std::ifstream ifstream(CALCULATOR_LAMBDA_CODE.c_str(), std::ios_base::in | std::ios_base::binary);
        Aws::StringStream buffer;
        buffer << ifstream.rdbuf();
        request.SetZipFile(Aws::Utils::ByteBuffer((unsigned char*)buffer.str().c_str(),
                                                  buffer.str().length()));
        request.SetPublish(true);

        Aws::Lambda::Model::UpdateFunctionCodeOutcome outcome = client.UpdateFunctionCode(request);

        if (outcome.IsSuccess()) {
            std::cout << "The lambda code was successfully updated." << std::endl;
        }
        else {
            std::cerr << "Error with Lambda::UpdateFunctionCode. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    std::cout << "This function uses an environment variable to control the logging level." << std::endl;
    std::cout << "UpdateFunctionConfiguration will be used to set the LOG_LEVEL to DEBUG." << std::endl;
    seconds = 0;
    do
    {
        ++seconds;
        std::this_thread::sleep_for(std::chrono::seconds(1));
        Aws::Lambda::Model::UpdateFunctionConfigurationRequest request;
        request.SetFunctionName(LAMBDA_NAME);
        Aws::Lambda::Model::Environment environment;
        environment.AddVariables("LOG_LEVEL", "DEBUG");
        request.SetEnvironment(environment);

        Aws::Lambda::Model::UpdateFunctionConfigurationOutcome outcome = client.UpdateFunctionConfiguration(request);

        if (outcome.IsSuccess()) {
            std::cout << "The lambda configuration was successfully updated." << std::endl;
            break;
        }
        else if (outcome.GetError().GetErrorType() != Aws::Lambda::LambdaErrors::RESOURCE_IN_USE)
        {
            if ((seconds % 10) == 0) { // Log status every 10 seconds.
                std::cout << "Lambda function update in progress . After " << seconds
                          << " seconds elapsed." << std::endl;
            }
        }
        else {
            std::cerr << "Error with Lambda::UpdateFunctionConfiguration. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    } while (0 < seconds);

    if (0 > seconds) {
        std::cerr << "Function failed to become active." << std::endl;
    }
    else
    {
        std::cout << "Updated function active after " << seconds << " seconds." << std::endl;
    }

    std::cout << "\nThe new code applies an arithmetic operator to two variables, x an y."  << std::endl;
    std::vector<Aws::String> operators = {"plus", "minus", "times", "divided-by"};
    for (size_t i = 0; i < operators.size(); ++i)
    {
        std::cout << "   " << i + 1 << " " << operators[i] << std::endl;
    }

    do
    {
        int operatorIndex = askQuestionForIntRange("Select an operator index 1 - 4 ", 1,
                                                   4);
        int x = askQuestionForInt("Enter an integer for the x value ");
        int y = askQuestionForInt("Enter an integer for the y value ");

        Aws::Utils::Json::JsonValue calculateJsonPayload;
        calculateJsonPayload.WithString("action", operators[operatorIndex - 1]);
        calculateJsonPayload.WithInteger("x", x);
        calculateJsonPayload.WithInteger("y", y);
        Aws::Lambda::Model::InvokeResult calculatedResult;
        if (invokeLambdaFunction(calculateJsonPayload,
                                 Aws::Lambda::Model::LogType::Tail,
                                 calculatedResult, client)) {
            Aws::Map<Aws::String, Aws::Utils::Json::JsonView> values =
                    Aws::Utils::Json::JsonView(
                            calculatedResult.GetPayload()).GetAllObjects();
            auto iter = values.find("result");
            if (iter != values.end() && iter->second.IsIntegerType()) {
                std::cout << ARITHMETIC_RESUlT_PREFIX << x << " "
                          << operators[operatorIndex - 1] << " "
                          << y << " is " << iter->second.AsInteger() << std::endl;
            }
            else if (iter != values.end() && iter->second.IsFloatingPointType()) {
                std::cout << ARITHMETIC_RESUlT_PREFIX << x << " "
                          << operators[operatorIndex - 1] << " "
                          << y << " is " << iter->second.AsDouble() << std::endl;
            }
            else {
                std::cout << "There was an error in execution. Here is the log."
                          << std::endl;
                Aws::Utils::ByteBuffer buffer = Aws::Utils::HashingUtils::Base64Decode(
                        calculatedResult.GetLogResult());
                std::cout << "With log " << buffer.GetUnderlyingData() << std::endl;
            }
        }

        answer = askQuestion("Would you like to try another operation? (y/n) ");
    } while (answer == "y");

    std::cout << "A list of the lambda functions will be retrieved. Press return to continue, ";
    std::getline(std::cin, answer);
    Aws::String marker;
    do
    {
        Aws::Lambda::Model::ListFunctionsRequest request;
        if(!marker.empty())
        {
            request.SetMarker(marker);
        }

        Aws::Lambda::Model::ListFunctionsOutcome outcome = client.ListFunctions(request);

        if (outcome.IsSuccess()) {
            const Aws::Lambda::Model::ListFunctionsResult& result = outcome.GetResult();
            std::cout << result.GetFunctions().size() << " lambda functions were retrieved." << std::endl;
            for (const Aws::Lambda::Model::FunctionConfiguration& functionConfiguration : result.GetFunctions())
            {
                std::cout << "   " << functionConfiguration.GetDescription() << std::endl;
                std::cout << "   " << Aws::Lambda::Model::RuntimeMapper::GetNameForRuntime(
                functionConfiguration.GetRuntime()) << ": " << functionConfiguration.GetHandler()
                << std::endl;
            }
            marker = result.GetNextMarker();
        }
        else {
            std::cerr << "Error with Lambda::ListFunctions. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    } while (!marker.empty());

    std::cout << "The resources will be deleted. Press return to continue, ";
    std::getline(std::cin, answer);

    {
        Aws::Lambda::Model::DeleteFunctionRequest request;
        request.SetFunctionName(LAMBDA_NAME);

        Aws::Lambda::Model::DeleteFunctionOutcome outcome = client.DeleteFunction(request);

        if (outcome.IsSuccess()) {
            std::cout << "The lambda function was successfully deleted." << std::endl;
        }
        else {
            std::cerr << "Error with Lambda::DeleteFunction. " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return deleteIamRole(clientConfig);
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
        attachRolePolicyRequest.WithPolicyArn(ROLE_POLICY_ARN);

        Aws::IAM::Model::AttachRolePolicyOutcome attachRolePolicyOutcome = client.AttachRolePolicy(
                attachRolePolicyRequest);
        if (attachRolePolicyOutcome.IsSuccess()) {
            std::cout << "Successfully attached the role policy" << std::endl;
        }
        else
        {
            std::cerr << "Error creating policy. " <<
                      attachRolePolicyOutcome.GetError().GetMessage() << std::endl;
            return false;

        }
    }

 //   std::cout << "Waiting 10 seconds for role to become active." << std::endl;
//    std::this_thread::sleep_for(std::chrono::seconds(10));

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

    (void)argc;  // Suppress unused warnings
    (void)argv;  // Suppress unused warnings

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


//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::Lambda::askQuestion(const Aws::String &string,
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

//! Command line prompt/response utility function for an integer result.
/*!
 \sa askQuestionForInt()
 \param string: A question prompt.
 \return int: User's response.
 */
int AwsDoc::Lambda::askQuestionForInt(const Aws::String &string) {
    Aws::String resultString = askQuestion(string,
                                           [](const Aws::String &string1) -> bool {
                                                   try {
                                                       (void) std::stoi(string1);
                                                       return true;
                                                   }
                                                   catch (const std::invalid_argument &) {
                                                       return false;
                                                   }
                                           });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForInt string not an int "
                  << resultString << std::endl;
    }
    return result;
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \sa askQuestionForIntRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int AwsDoc::Lambda::askQuestionForIntRange(const Aws::String &string, int low,
                                             int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                return number >= low && number <= high;
            }
            catch (const std::invalid_argument &) {
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


bool AwsDoc::Lambda::invokeLambdaFunction(const Aws::Utils::Json::JsonValue &jsonPayload,
                                          Aws::Lambda::Model::LogType logType,
                                          Aws::Lambda::Model::InvokeResult& invokeResult,
                                          const Aws::Lambda::LambdaClient &client) {
    int seconds = 0;
    bool result = false;
    /*
     * In this example, the Invoke function can be called before recently created resources are
     * available.  The Invoke function is called repeatedly until the resources are
     * available.
     */
    do
    {
        Aws::Lambda::Model::InvokeRequest request;
        request.SetFunctionName(LAMBDA_NAME);
        request.SetLogType(logType);
        std::shared_ptr<Aws::IOStream> payload = Aws::MakeShared<Aws::StringStream>("FunctionTest");
           *payload << jsonPayload.View().WriteReadable();
        request.SetBody(payload);
        request.SetContentType("application/json");
        Aws::Lambda::Model::InvokeOutcome outcome = client.Invoke(request);

        if (outcome.IsSuccess()) {
            invokeResult = std::move(outcome.GetResult());
            result = true;
            break;
        }
        // ACcESS_DENIED because the role is not available yet.
        // RESOURCE_CONFLICT because the Lambda function is being created or updated.
        else if ((outcome.GetError().GetErrorType() == Aws::Lambda::LambdaErrors::ACCESS_DENIED) ||
                 (outcome.GetError().GetErrorType() == Aws::Lambda::LambdaErrors::RESOURCE_CONFLICT))
        {
            if ((seconds % 5) == 0) { // Log status every 10 seconds.
                std::cout << "Waiting for the invoke api to be available, status " <<
                          ((outcome.GetError().GetErrorType() == Aws::Lambda::LambdaErrors::ACCESS_DENIED ?
                            "ACCESS_DENIED" : "RESOURCE_CONFLICT")) << ". " << seconds
                          << " seconds elapsed." << std::endl;
            }
        }
        else {
            std::cerr << "Error with Lambda::InvokeRequest. " << outcome.GetError().GetMessage()
                      << std::endl;
            break;
        }
        ++seconds;
        std::this_thread::sleep_for(std::chrono::seconds(1));
    } while (seconds < 60);

    return result;
}
