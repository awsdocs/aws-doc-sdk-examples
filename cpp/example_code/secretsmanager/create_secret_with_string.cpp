// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/secretsmanager/SecretsManagerClient.h>
#include <aws/secretsmanager/model/CreateSecretRequest.h>

using namespace Aws;

// snippet-start:[cpp.example_code.secrets_manager.create_secret_with_string]
int main(int argc, const char *argv[])
{
    if (argc != 3) {
        std::cout << "Usage:\n" <<
        "    <secretName> <secretValue> \n\n" <<
        "Where:\n" <<
        "    secretName - The name of the secret (for example, tutorials/MyFirstSecret). \n" <<
        "    secretValue - The secret value. " << std::endl;
        return 0;
    }

    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Debug;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;

        //TODO(user): Enter the region where you want to create the secret..
        String region = "us-east-1";
        if (!region.empty())
        {
                config.region = region;
        }
        SecretsManager::SecretsManagerClient sm_client(config);

        String secretName = argv[1];
        String secretString = argv[2];
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
// snippet-end:[cpp.example_code.secrets_manager.create_secret_with_string]
