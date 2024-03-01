/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/acm/ACMClient.h>
#include <aws/acm/model/RemoveTagsFromCertificateRequest.h>
#include "acm_samples.h"

bool AwsDoc::ACM::RemoveTagFromCertificate(const Aws::String& certificateArn,
                                           const Aws::String& tagKey,
                                           const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::Vector<Aws::ACM::Model::Tag> tags;

    Aws::ACM::Model::Tag tag;
    tag.SetKey(tagKey);

    tags.push_back(tag);

    Aws::ACM::Model::RemoveTagsFromCertificateRequest request;
    request.WithCertificateArn(certificateArn)
            .WithTags(tags);

    Aws::ACM::Model::RemoveTagsFromCertificateOutcome outcome =
            acm_client.RemoveTagsFromCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: RemoveTagFromCertificate: " <<
                  outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Tag with key '" << tagKey << "' removed from "
                  << "certificate with ARN '" << certificateArn << "'." << std::endl;

        return true;
    }
}

/*
*
*  main function
*
*  Usage: 'run_'
*
*  Prerequisites: .
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

