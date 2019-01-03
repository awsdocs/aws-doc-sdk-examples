 
//snippet-sourcedescription:[update_server_cert.cpp demonstrates how to update the name of an IAM SSL/TLS server certificate.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
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
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateServerCertificateRequest.h>
#include <iostream>

/**
 * Updates an server certificate name based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout <<
            "Usage: update_server_cert <old_cert_name> <new_cert_name>"
            << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String old_name(argv[1]);
        Aws::String new_name(argv[2]);

        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::UpdateServerCertificateRequest request;
        request.SetServerCertificateName(old_name);
        request.SetNewServerCertificateName(new_name);

        auto outcome = iam.UpdateServerCertificate(request);
        if (outcome.IsSuccess())
        {
            std::cout << "Server certificate " << old_name
                << " successfully renamed as " << new_name
                << std::endl;
        }
        else
        {
            std::cout << "Error changing name of server certificate " <<
                old_name << " to " << new_name << ":" <<
                outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

