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
#include <aws/iam/model/ListServerCertificatesRequest.h>
#include <aws/iam/model/ListServerCertificatesResult.h>

#include <iostream>

static const char* SIMPLE_DATE_FORMAT_STR = "%Y-%m-%d";

/**
 * Lists all server certificates associated with an account
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iam_client;

        Aws::IAM::Model::ListServerCertificatesRequest listServerCertificatesRequest;

        bool done = false;
        bool header = false;
        while (!done)
        {
            auto listServerCertificatesOutcome = iam_client.ListServerCertificates(listServerCertificatesRequest);
            if (!listServerCertificatesOutcome.IsSuccess())
            {
                std::cout << "Failed to list server certificates: " <<
                listServerCertificatesOutcome.GetError().GetMessage() << std::endl;
                break;
            }

            if (!header)
            {
                std::cout << std::left << std::setw(55) << "Name"
                << std::setw(30) << "ID"
                << std::setw(80) << "Arn"
                << std::setw(14) << "UploadDate"
                << std::setw(14) << "ExpirationDate" << std::endl;
                header = true;
            }

            const auto &certificates = listServerCertificatesOutcome.GetResult().GetServerCertificateMetadataList();
            for (const auto &certificate : certificates)
            {
                std::cout << std::left << std::setw(55) << certificate.GetServerCertificateName()
                << std::setw(30) << certificate.GetServerCertificateId()
                << std::setw(80) << certificate.GetArn()
                << std::setw(14) << certificate.GetUploadDate().ToGmtString(SIMPLE_DATE_FORMAT_STR)
                << std::setw(14) << certificate.GetExpiration().ToGmtString(SIMPLE_DATE_FORMAT_STR) << std::endl;
            }

            if (listServerCertificatesOutcome.GetResult().GetIsTruncated())
            {
                listServerCertificatesRequest.SetMarker(listServerCertificatesOutcome.GetResult().GetMarker());
            }
            else
            {
                done = true;
            }
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



