// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/secretsmanager/SecretsManagerClient.h>
#include <aws/secretsmanager/model/GetSecretValueRequest.h>

using namespace Aws;

int main()
{

    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Debug;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;

        //TODO(user): Enter the region you create the secret
        String region = "us-east-1";
        if (!region.empty())
        {
                config.region = region;
        }
        SecretsManager::SecretsManagerClient sm_client(config);

        //TODO(user): Enter your secret name shown on the Secrets Manager console
        String secretId = "<EXAMPLE_SECRET_NAME>";
        SecretsManager::Model::GetSecretValueRequest request;
        request.SetSecretId(secretId);

        auto getSecretValueOutcome = sm_client.GetSecretValue(request);
        if(getSecretValueOutcome.IsSuccess()){
                std::cout << "Secret is: " << getSecretValueOutcome.GetResult().GetSecretString() << std::endl;
        }else{
                std::cout << "Failed with Error: " << getSecretValueOutcome.GetError() << std::endl;
        }
    }

    ShutdownAPI(options);
    return 0;
}
