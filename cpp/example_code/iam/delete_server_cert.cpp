// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_server_cert.cpp demonstrates how to delete an IAM SSL/TLS server certificate.]
*/
//snippet-start:[iam.cpp.delete_server_cert.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeleteServerCertificateRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.delete_server_cert.inc]

/**
 * Deletes an IAM server certificate, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_server_cert <cert_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String cert_name(argv[1]);

        // snippet-start:[iam.cpp.delete_server_cert.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::DeleteServerCertificateRequest request;
        request.SetServerCertificateName(cert_name);

        const auto outcome = iam.DeleteServerCertificate(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error deleting server certificate " << cert_name <<
                ": " << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted server certificate " << cert_name
                << std::endl;
        }
        // snippet-end:[iam.cpp.delete_server_cert.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

