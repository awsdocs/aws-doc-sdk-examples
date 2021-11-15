// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
update_server_cert.cpp demonstrates how to update the name of an IAM SSL/TLS server certificate.]
*/
//snippet-start:[iam.cpp.update_server_cert.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateServerCertificateRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.update_server_cert.inc]

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

        // snippet-start:[iam.cpp.update_server_cert.code]
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
        // snippet-end:[iam.cpp.update_server_cert.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

