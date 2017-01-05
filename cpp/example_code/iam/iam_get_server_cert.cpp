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
#include <aws/iam/model/GetServerCertificateRequest.h>
#include <aws/iam/model/GetServerCertificateResult.h>

#include <iostream>

/**
 * Gets a server certificate, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: iam_get_server_cert <cert_name>" << std::endl;
        return 1;
    }

    Aws::String certName(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iamClient;

    Aws::IAM::Model::GetServerCertificateRequest getServerCertificateRequest;
    getServerCertificateRequest.SetServerCertificateName(certName);

    auto getServerCertificateOutcome = iamClient.GetServerCertificate(getServerCertificateRequest);
    if(!getServerCertificateOutcome.IsSuccess())
    {
        std::cout << "Error getting server certificate " << certName << ": " << getServerCertificateOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        const auto& certificate = getServerCertificateOutcome.GetResult().GetServerCertificate();

        std::cout << "Name: " << certificate.GetServerCertificateMetadata().GetServerCertificateName() << std::endl
                  << "Body: " << certificate.GetCertificateBody() << std::endl
                  << "Chain: " << certificate.GetCertificateChain() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



