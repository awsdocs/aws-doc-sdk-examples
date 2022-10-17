// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/secretsmanager/SecretsManagerClient.h>
#include <aws/secretsmanager/model/GetSecretValueRequest.h>

using namespace Aws;
// snippet-start:[cpp.example_code.secrets_manager.get_secret_value]
int main(int argc, const char *argv[])
{
    if (argc != 2) {
        std::cout << "Usage:\n" <<
                  "    <secretName> \n\n" <<
                  "Where:\n" <<
                  "    secretName - The name of the secret (for example, tutorials/MyFirstSecret). \n"
                  << std::endl;
        return 0;
    }

    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Debug;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;

        //TODO(user): Enter the region where you want to create the secret.
        String region = "us-east-1";
        if (!region.empty())
        {
                config.region = region;
        }
        SecretsManager::SecretsManagerClient sm_client(config);

        String secretId = argv[1];
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
// snippet-end:[cpp.example_code.secrets_manager.get_secret_value]

