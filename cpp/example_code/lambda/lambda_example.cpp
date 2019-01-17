 
//snippet-sourcedescription:[lambda_example.cpp demonstrates how to programatically create, invoke, and manage an AWS Lambda function.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Lambda]
//snippet-service:[lambda]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/core/utils/logging/DefaultLogSystem.h>
#include <aws/core/utils/logging/AWSLogging.h>
#include <aws/core/utils/HashingUtils.h>
#include <aws/lambda/LambdaClient.h>
#include <aws/lambda/model/CreateFunctionRequest.h>
#include <aws/lambda/model/DeleteFunctionRequest.h>
#include <aws/lambda/model/InvokeRequest.h>
#include <aws/lambda/model/ListFunctionsRequest.h>
#include <fstream>
#include <iostream>

static const char* ALLOCATION_TAG = "helloLambdaWorld";

static std::shared_ptr<Aws::Lambda::LambdaClient> m_client;

static void CreateFunction(Aws::String functionName, Aws::String handler, 
    Aws::Lambda::Model::Runtime runtime, Aws::String roleARN, Aws::String zipFile)
{
    Aws::Lambda::Model::CreateFunctionRequest createFunctionRequest;
    createFunctionRequest.SetHandler(handler);
    createFunctionRequest.SetFunctionName(functionName);
    createFunctionRequest.SetRole(Aws::String(roleARN));
    Aws::Lambda::Model::FunctionCode functionCode;

    std::ifstream fc(zipFile.c_str(), std::ios_base::in | std::ios_base::binary);
    Aws::StringStream buffer;
    buffer << fc.rdbuf();

    functionCode.SetZipFile(Aws::Utils::ByteBuffer((unsigned char*)buffer.str().c_str(), buffer.str().length()));
    createFunctionRequest.SetCode(functionCode);
    createFunctionRequest.SetRuntime(runtime);

    bool done = false;
    while (!done)
    {
        auto outcome = m_client->CreateFunction(createFunctionRequest);
        if (outcome.IsSuccess())
            done = true;
        else
        {
            // Handles case were ROLE is not yet ready
            if (outcome.GetError().GetMessage().find("assume") != std::string::npos)
                std::this_thread::sleep_for(std::chrono::seconds(2));
            else
            {
                done = true;
                std::cout << "\nCreateFunction error:\n"
                    << outcome.GetError().GetMessage() << "\n\n";
            }
        }
    }
}


void DeleteFunction(Aws::String functionName)
{
    Aws::Lambda::Model::DeleteFunctionRequest deleteFunctionRequest;
    deleteFunctionRequest.SetFunctionName(functionName);
    auto outcome = m_client->DeleteFunction(deleteFunctionRequest);
    if (!outcome.IsSuccess())
        std::cout << "\nDeleteFunction error:\n"
        << outcome.GetError().GetMessage() << "\n\n";
}

void InvokeFunction(Aws::String functionName)
{
    Aws::Lambda::Model::InvokeRequest invokeRequest;
    invokeRequest.SetFunctionName(functionName);
    invokeRequest.SetInvocationType(Aws::Lambda::Model::InvocationType::RequestResponse);
    invokeRequest.SetLogType(Aws::Lambda::Model::LogType::Tail);
    std::shared_ptr<Aws::IOStream> payload = Aws::MakeShared<Aws::StringStream>("FunctionTest");
    Aws::Utils::Json::JsonValue jsonPayload;
    jsonPayload.WithString("key1", "value1");
    jsonPayload.WithString("key2", "value2");
    jsonPayload.WithString("key3", "value3");
    *payload << jsonPayload.View().WriteReadable();
    invokeRequest.SetBody(payload);
    invokeRequest.SetContentType("application/javascript");
    auto outcome = m_client->Invoke(invokeRequest);

    if (outcome.IsSuccess())
    {
        auto &result = outcome.GetResult();

        // Lambda function result (key1 value)
        Aws::IOStream& payload = result.GetPayload();
        Aws::String functionResult;
        std::getline(payload, functionResult);
        std::cout << "Lambda result:\n" << functionResult << "\n\n";

        // Decode the result header to see requested log information 
        auto byteLogResult = Aws::Utils::HashingUtils::Base64Decode(result.GetLogResult());
        Aws::StringStream logResult;
        for (unsigned i = 0; i < byteLogResult.GetLength(); i++)
            logResult << byteLogResult.GetItem(i);
        std::cout << "Log result header:\n" << logResult.str() << "\n\n";
    }
}

void ListFunctions()
{
    Aws::Lambda::Model::ListFunctionsRequest listFunctionsRequest;
    auto listFunctionsOutcome = m_client->ListFunctions(listFunctionsRequest);
    auto functions = listFunctionsOutcome.GetResult().GetFunctions();
    std::cout << functions.size() << " function(s):" << std::endl;
    for(const auto& item : functions)
        std::cout << item.GetFunctionName() << std::endl;
    std::cout << std::endl;
}

int main(int argc, char **argv)
{
    // Configuration Properties|Debug, Environment=AWS_DEFAULT_PROFILE=default$(LocalDebuggerEnvironment)
    const Aws::String USAGE = "\n" \
        "Description\n"
        "     This sample creates a function from a zip file, lists available functions,\n"
        "     invokes the newly created function, and then deletes the function.\n"
        "     The function should take three arguments and return a string, see \n\n"
        "     http://docs.aws.amazon.com/lambda/latest/dg/get-started-create-function.html.\n\n"
        "Usage:\n"
        "     lambda_example name handler runtime rolearn zipfile <region>\n\n"
        "Where:\n"
        "    name   - lambda function name to create\n"
        "    handler- function name in code to call\n"
        "    runtime- runtime to use for function:\n"
        "             nodejs,nodejs4.3,java8,python2.7,dotnetcore1.0,nodejs4.3.edge\n"
        "    rolearn- rule lambda will assume when running functuion\n"
        "    zipfile- zip file containign function and other dependencies\n"
        "    region - optional region, e.g. us-east-2\n\n"
        "Example:\n"
        "    create_function helloLambdaWorld helloLambdaWorld.handler python2_7 ***arn*** helloLambdaWorld.zip\n\n";

    if (argc < 5)
    {
        std::cout << USAGE;
        return 1;
    }
    // Enable logging to help diagnose service issues
    const bool logging = false;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (logging)
            Aws::Utils::Logging::InitializeAWSLogging(
                Aws::MakeShared<Aws::Utils::Logging::DefaultLogSystem>(
                    "create_function", Aws::Utils::Logging::LogLevel::Trace, "create_function_"));

        const Aws::String functionName(argv[1]);
        const Aws::String functionHandler(argv[2]);
        const Aws::Lambda::Model::Runtime functionRuntime = 
            Aws::Lambda::Model::RuntimeMapper::GetRuntimeForName(Aws::String(argv[3]));
        const Aws::String functionRoleARN(argv[4]);
        const Aws::String functionZipFile(argv[5]);
        const Aws::String region(argc > 5 ? argv[6] : "");

        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        m_client = Aws::MakeShared<Aws::Lambda::LambdaClient>(ALLOCATION_TAG, clientConfig);

        CreateFunction(functionName, functionHandler, functionRuntime, functionRoleARN, functionZipFile);

        ListFunctions();

        InvokeFunction(functionName);

        DeleteFunction(functionName);

        m_client = nullptr;

        if(logging)
            Aws::Utils::Logging::ShutdownAWSLogging();
    }
    Aws::ShutdownAPI(options);

    return 0;
}