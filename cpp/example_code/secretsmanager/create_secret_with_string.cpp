// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/secretsmanager/SecretsManagerClient.h>
#include <aws/secretsmanager/model/CreateSecretRequest.h>

using namespace Aws;

int main()
{

    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Debug;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;

        //TODO(user): Enter the region you create the secret.
        String region = "us-east-1";
        if (!region.empty())
        {
                config.region = region;
        }
        SecretsManager::SecretsManagerClient sm_client(config);

        //TODO(user): Enter your secret name and content.
        String secretName = "<EXAMPLE_SECRET_NAME>";
        String secretString = "<EXAMPLE_SECRET_CONTENT>";
        SecretsManager::Model::CreateSecretRequest request;
        request.SetName(secretName);
        request.SetSecretString(secretString);

        auto createSecretOutcome = sm_client.CreateSecret(request);
        if(createSecretOutcome.IsSuccess()){
                std::cout << "Create secret with name: " << createSecretOutcome.GetResult().GetName() << std::endl;
        }else{
                std::cout << "Failed with Error: " << createSecretOutcome.GetError() << std::endl;
        }
    }

    ShutdownAPI(options);
    return 0;
}
