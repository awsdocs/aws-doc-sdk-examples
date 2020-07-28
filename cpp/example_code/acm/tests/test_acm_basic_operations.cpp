// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <string>
#include <aws/core/Aws.h>
#include <aws/acm/ACMClient.h>
#include <aws/acm/model/RequestCertificateRequest.h>
#include <aws/core/utils/Outcome.h>
#include <awsdoc/acm/acm_examples.h>

const char* PROGRAM_NAME = "test_acm_basic_operations";
const char* TEST_SUITE_NAME = "Certificate Manager operations test";

// Prints command-line usage information.
void PrintUsage(const char* programName)
{
    std::cout << "Usage: " << programName << " [OPTION ARGUMENT]" << std::endl;
    std::cout << "--mode issue-enabled: Specify only if you are requesting a production-ready certificate." << std::endl;
    std::cout << "--type private:       Specify only if you are requesting a private certificate. Must be used with --mode." << std::endl;
    std::cout << "--help:               Print this usage message." << std::endl;
    std::cout << "Example 1: " << programName << std::endl;
    std::cout << "Example 2: " << programName << " --mode issue-enabled" << std::endl;
    std::cout << "Example 3: " << programName << " --mode issue-enabled --type private" << std::endl;
    std::cout << "Example 4: " << programName << " --help" << std::endl;

    return;
}

// Prints instructions for cleaning up orphaned AWS resources if any test 
// in this test suite fails.
void PrintCleanUpMessage(const char* unitTestName,
    const Aws::String& certificateArn)
{
    std::cout << "Error: " << TEST_SUITE_NAME << ": " << unitTestName <<
        ". To clean up, delete the certificate with the ARN '" <<
        certificateArn << "' yourself." << std::endl << std::endl;

    return;
}

// All tests in this test suite cannot successfully run by default. To do so, 
// if you have the ability to request production-ready certificates, 
// then call this test executable with the "--mode issue-enabled" option.
// Additionally, if you have the ability to request private certificates, 
// then call this test executable with the 
// "--mode issue-enabled --type private" options.
int main(int argc, char* argv[])
{
    bool issue_enabled = false;
    bool is_private = false;

    // Check to see if and whether correct command-line arguments were supplied.
    if ((argc > 5) ||                                               // More than 5 arguments.
        (argc == 4) ||                                              // Only 4 arguments; not allowed.
        (argc == 2) ||                                              // Seeking help or incorrect syntax.
        ((argc == 3) && (strcmp(argv[1], "--mode") != 0)) ||        // Wrong order of arguments.
        ((argc == 3) && (strcmp(argv[2], "issue-enabled") != 0)) || // Incorrect syntax for --mode.
        ((argc == 5) && (strcmp(argv[3], "--type") != 0) ||         // Incorrect syntax for --type.
        ((argc == 5) && (strcmp(argv[4], "private") != 0))))        // Still incorrect syntax for --type.
    {
        std::cout << "Error: Cannot run tests." << std::endl;

        PrintUsage(PROGRAM_NAME);

        return 1;
    }

    if (argc == 3) // "--mode issue-enabled" specified.
    {
        // Enable unit tests for UpdateCertificateOption, 
        // ResendValidationEmail, GetCertificate, and 
        // RenewCertificate.
        issue_enabled = true;
    }
    
    if (argc == 5) // "--mode issue-enabled --type private" specified.
    {
        // Also enable unit test for ExportCertificate.
        issue_enabled = true;
        is_private = true;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String region = "us-east-1";
        const Aws::String domain_name = "www.example.com";
        const Aws::String idempotency_token = "54321";
        const Aws::String tag_key = "my-key";
        const Aws::String tag_value = "my-value";

        // Request (create) a new certificate.
        std::cout << "Requesting new certificate with domain name '" <<
            domain_name << "'..." << std::endl << std::endl;

        Aws::String certificate_arn;

        Aws::Client::ClientConfiguration config;
        config.region = region;

        Aws::ACM::ACMClient acm_client(config);

        Aws::ACM::Model::RequestCertificateRequest request;
        request.WithDomainName(domain_name)
            .WithIdempotencyToken(idempotency_token);

        Aws::ACM::Model::RequestCertificateOutcome outcome =
            acm_client.RequestCertificate(request);

        if (!outcome.IsSuccess())
        {
            std::cout << "Error: " << TEST_SUITE_NAME << ": RequestCertificate: "
                << outcome.GetError().GetMessage() << std::endl;
            std::cout << "No cleanup needed. Check acm_basic_operations.cpp for "
                "possible issues with the AwsDoc::ACM::RequestCertificate "
                "helper method." << std::endl;

            return 1;
        }
        else
        {
            certificate_arn = outcome.GetResult().GetCertificateArn();

            std::cout << "Certificate successfully requested; ARN is '" <<
                certificate_arn << "'." << std::endl << std::endl;


        }

        // Change the new certificate's logging options.
        if (issue_enabled)
        {
            std::cout << "Updating logging options for certificate with ARN '" <<
                certificate_arn << "'..." << std::endl << std::endl;

            if (!AwsDoc::ACM::UpdateCertificateOption(certificate_arn, region, "logging-enabled"))
            {
                PrintCleanUpMessage("UpdateCertificateOption", certificate_arn);

                return 1;
            }
        }
        else
        {
            std::cout << "Skipping test for UpdateCertificateOption, as issue-enabled option is unset." << 
                std::endl << std::endl;
        }

        // Resend the validation email for the new certificate.
        if (issue_enabled)
        {
            std::cout << "Resending validation email for certificate with ARN '" <<
                certificate_arn << "' to domain '" << domain_name <<
                "'..." << std::endl << std::endl;

            if (!AwsDoc::ACM::ResendValidationEmail(certificate_arn, domain_name,
                domain_name, region))
            {
                PrintCleanUpMessage("ResendValidationEmail", certificate_arn);

                return 1;
            }
        }
        else
        {
            std::cout << "Skipping test for ResendValidationEmail, as issue-enabled option is unset." <<
                std::endl << std::endl;
        }

        // Add a tag to the certificate.
        std::cout << "Adding tag with key '" << tag_key << "' and value '" <<
            tag_value << "' to certificate with ARN '" <<
            certificate_arn << "'..." << std::endl << std::endl;

        if (!AwsDoc::ACM::AddTagToCertificate(certificate_arn, tag_key, tag_value,
            region))
        {

            PrintCleanUpMessage("AddTagToCertificate", certificate_arn);

            return 1;
        }
        
        // Get general information about the certificate.
        std::cout << std::endl << "Getting general information about certificate with ARN '" <<
            certificate_arn << "'..." << std::endl << std::endl;

        if (!AwsDoc::ACM::DescribeCertificate(certificate_arn, region))
        {
            PrintCleanUpMessage("DescribeCertificate", certificate_arn);

            return 1;
        }

        // Get information about available tags for the certificate. 
        std::cout << std::endl << "Getting information about tags for certificate with ARN '" <<
            certificate_arn << "'..." << std::endl << std::endl;

        if (!AwsDoc::ACM::ListTagsForCertificate(certificate_arn, region))
        {
            PrintCleanUpMessage("ListTagsForCertificate", certificate_arn);

            return 1;
        }

        // Get information about the new certificate's certificate and 
        // certificate chain.
        if (issue_enabled)
        {
            std::cout << "Getting information about the certificate and certificate "
                "chain for certificate with ARN '" <<
                certificate_arn << "'..." << std::endl << std::endl;

            if (!AwsDoc::ACM::GetCertificate(certificate_arn, region))
            {
                PrintCleanUpMessage("GetCertificate", certificate_arn);

                return 1;
            }
        }
        else
        {
            std::cout << "Skipping test for GetCertificate, as issue-enabled option is unset." <<
                std::endl << std::endl;
        }

        // Get PEM-based certificate, private key, and certificate chain 
        // information about the certificate. 
        if (is_private)
        {
            std::cout << "Getting PEM-based information about the certificate, "
                "private key, and certificate chain for certificate with ARN '" <<
                certificate_arn << "'..." << std::endl << std::endl;

            if (!AwsDoc::ACM::ExportCertificate(certificate_arn, region))
            {
                PrintCleanUpMessage("ExportCertificate", certificate_arn);

                return 1;
            }
        }
        else
        {
            std::cout << "Skipping test for ExportCertificate, as is-private option is unset." <<
                std::endl << std::endl;
        }

        // Remove the previous tag from the certificate.
        std::cout << "Removing tag with key '" << tag_key << "' from certificate with ARN '" <<
            certificate_arn << "'..." << std::endl << std::endl;

        if (!AwsDoc::ACM::RemoveTagFromCertificate(certificate_arn, tag_key, region))
        {
            PrintCleanUpMessage("RemoveTagFromCertificate", certificate_arn);

            return 1;
        }

        // Import a certificate.
        std::cout << std::endl << "Skipping test for ImportCertificate, as "
            "running this test requires prior availability of a PEM-formatted certificate file or string, "
            "a PEM-formatted private key file or string, and a PEM-formatted certificate chain file or string." << 
            std::endl << std::endl;

        // List available certificates.
        std::cout << "Listing available certificates..." << std::endl << std::endl;

        if (!AwsDoc::ACM::ListCertificates(region))
        {
            PrintCleanUpMessage("ListCertificates", certificate_arn);

            return 1;
        }

        // Renew a certificate.
        if (issue_enabled)
        {
            std::cout << "Renewing certificate with ARN '" <<
                certificate_arn << "'..." << std::endl << std::endl;

            if (!AwsDoc::ACM::RenewCertificate(certificate_arn, region))
            {
                PrintCleanUpMessage("RenewCertificate", certificate_arn);

                return 1;
            }
        }
        else
        {
            std::cout << "Skipping test for RenewCertificate, as issue-enabled option is unset." <<
                std::endl << std::endl;
        }

        // Delete the certificate.
        std::cout << "Deleting certificate with ARN '" <<
            certificate_arn << "'..." << std::endl << std::endl;

        if (!AwsDoc::ACM::DeleteCertificate(certificate_arn, region))
        {
            PrintCleanUpMessage("DeleteCertificate", certificate_arn);

            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}