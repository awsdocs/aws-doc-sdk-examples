/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateServerCertificateRequest.h>

#include <iostream>

/**
 * Updates an server certificate name based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: iam_update_server_cert <old_cert_name> <new_cert_name>" << std::endl;
        return 1;
    }

    Aws::String oldCertName(argv[1]);
    Aws::String newCertName(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iamClient;

        Aws::IAM::Model::UpdateServerCertificateRequest updateServerCertificateRequest;
        updateServerCertificateRequest.SetServerCertificateName(oldCertName);
        updateServerCertificateRequest.SetNewServerCertificateName(newCertName);

        auto updateServerCertificateOutcome = iamClient.UpdateServerCertificate(updateServerCertificateRequest);
        if (updateServerCertificateOutcome.IsSuccess())
        {
            std::cout << "Server certificate " << oldCertName << " successfully renamed as " << newCertName <<
            std::endl;
        }
        else
        {
            std::cout << "Error changing name of server certificate " << oldCertName << " to " << newCertName << ":" <<
            updateServerCertificateOutcome.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}

