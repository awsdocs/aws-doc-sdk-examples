// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
Purpose: 

Demonstrates how to use the AWS SDK for C++ to automate the various operations 
for AWS Certificate Manager.

Prerequisites: 

* An existing AWS account. For more information, see "How do I create and 
  activate a new AWS account" on the AWS Premium support website, at 
  https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/
* AWS credentials. For more information, see the "AWS security credentials" 
  topic in the AWS General Reference Guide, at 
  https://docs.aws.amazon.com/general/latest/gr/aws-security-credentials.html
  See also the "Providing AWS Credentials" topic in the 
  AWS SDK for C++ Developer Guide.
* The AWS SDK for C++. To install it, see the "Setting Up the AWS SDK for C++"
  topic in the AWS SDK for C++ Developer Guide.

Building the code:

To build the following code, using CMake version 3.8 or later, generate this
project's makefiles by running the cmake command from the same directory as 
this file. Then build this project's makefiles by running the make command 
from the same directory as this file. For more information, see the 
"Building Your Application with CMake" topic in the 
AWS SDK for C++ Developer Guide.

Running the code:

For usage instructions, run the acm_basic_operations executable that was built,
either without command-line arguments or with the "--help" option.
//////////////////////////////////////////////////////////////////////////// */

// General includes.
#include <iostream>
#include <string>
#include <aws/core/Aws.h>
#include <aws/acm/ACMClient.h>
#include <aws/core/utils/Outcome.h>
#include <awsdoc/acm/acm_examples.h>
// Additional includes for AddTagToCertificate.
#include <aws/acm/model/AddTagsToCertificateRequest.h>
#include <aws/acm/model/Tag.h>
// Additional includes for DeleteCertificate.
#include <aws/acm/model/DeleteCertificateRequest.h>
// Additional includes for DescribeCertificate.
#include <aws/acm/model/DescribeCertificateRequest.h>
#include <aws/acm/model/CertificateDetail.h>
#include <aws/acm/model/CertificateStatus.h>
#include <aws/core/utils/DateTime.h>
#include <aws/acm/model/DomainValidation.h>
#include <aws/acm/model/ResourceRecord.h>
#include <aws/acm/model/RecordType.h>
#include <aws/acm/model/ValidationMethod.h>
#include <aws/acm/model/DomainStatus.h>
#include <aws/acm/model/ExtendedKeyUsage.h>
#include <aws/acm/model/ExtendedKeyUsageName.h>
#include <aws/acm/model/FailureReason.h>
#include <aws/acm/model/RevocationReason.h>
#include <aws/acm/model/CertificateType.h>
#include <aws/acm/model/KeyAlgorithm.h>
#include <aws/acm/model/CertificateTransparencyLoggingPreference.h>
// Additional includes for ExportCertificate.
#include <aws/core/utils/Array.h>
#include <aws/core/utils/crypto/Cipher.h>
#include <aws/acm/model/ExportCertificateRequest.h>
// Additional includes for GetCertificate.
#include <aws/acm/model/GetCertificateRequest.h>
// Additional includes for ImportCertificate.
#include <fstream>
#include <aws/acm/model/ImportCertificateRequest.h>
// Additional includes for ListCertificates.
#include <aws/acm/model/ListCertificatesRequest.h>
// Additional includes for ListTagsForCertificate.
#include <aws/acm/model/ListTagsForCertificateRequest.h>
// Additional includes for RemoveTagFromCertificate.
#include <aws/acm/model/RemoveTagsFromCertificateRequest.h>
// Additional includes for RenewCertificate.
#include <aws/acm/model/RenewCertificateRequest.h>
// Additional includes for RequestCertificate.
#include <aws/acm/model/RequestCertificateRequest.h>
// Additional includes for ResendValidationEmail.
#include <aws/acm/model/ResendValidationEmailRequest.h>
// Additional includes for UpdateCertificateOptions.
#include <aws/acm/model/UpdateCertificateOptionsRequest.h>

// Begin helper functions to demonstrate the various operations 
// for AWS Certificate Manager.

// Helper function for Aws::ACM::ACMClient::AddTagsToCertificate.
bool AwsDoc::ACM::AddTagToCertificate(const Aws::String& certificateArn,
    const Aws::String& tagKey, const Aws::String& tagValue, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::AddTagsToCertificateRequest request;
    Aws::Vector<Aws::ACM::Model::Tag> tags;
    Aws::ACM::Model::Tag tag;

    tag.WithKey(tagKey).WithValue(tagValue);
    tags.push_back(tag);

    request.WithCertificateArn(certificateArn).WithTags(tags);

    Aws::ACM::Model::AddTagsToCertificateOutcome outcome =
        acm_client.AddTagsToCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: AddTagToCertificate: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Tag with key '" << tagKey <<
            "' and value '" << tagValue << 
            "' added to certificate with ARN '" << 
            certificateArn << "'." << std::endl;

        return true;
    }

}

// Helper function for Aws::ACM::ACMClient::DeleteCertificate.
bool AwsDoc::ACM::DeleteCertificate(const Aws::String& certificateArn,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::DeleteCertificateRequest request;
    request.WithCertificateArn(certificateArn);

    Aws::ACM::Model::DeleteCertificateOutcome outcome =
        acm_client.DeleteCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: DeleteCertificate: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: The certificate with the ARN '" <<
            certificateArn << "' is deleted." << std::endl;

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::DescribeCertificate.
bool AwsDoc::ACM::DescribeCertificate(const Aws::String& certificateArn,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::DescribeCertificateRequest request;
    request.WithCertificateArn(certificateArn);

    Aws::ACM::Model::DescribeCertificateOutcome outcome =
        acm_client.DescribeCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: DescribeCertificate: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        Aws::ACM::Model::CertificateDetail certificate =
            outcome.GetResult().GetCertificate();

        std::cout << "Success: Information about certificate " 
            "with ARN '" << certificateArn << "':" << std::endl << std::endl;

        std::cout << "ARN:                 " << certificate.GetCertificateArn() 
            << std::endl;
        std::cout << "Authority ARN:       " << 
            certificate.GetCertificateAuthorityArn() << std::endl;
        std::cout << "Created at (GMT):    " << 
            certificate.GetCreatedAt().ToGmtString(Aws::Utils::DateFormat::ISO_8601) 
            << std::endl;
        std::cout << "Domain name:         " << certificate.GetDomainName() 
            << std::endl;
        
        Aws::Vector<Aws::ACM::Model::DomainValidation> options = 
            certificate.GetDomainValidationOptions();

        if (options.size() > 0)
        {
            std::cout << std::endl << "Domain validation information: " 
                << std::endl << std::endl;

            for (auto it = options.begin(); it != options.end(); it++)
            {
                Aws::ACM::Model::DomainValidation validation = *it;

                std::cout << "  Domain name:              " << 
                    validation.GetDomainName() << std::endl;

                Aws::ACM::Model::ResourceRecord record = 
                    validation.GetResourceRecord();

                std::cout << "  Resource record name:     " << 
                    record.GetName() << std::endl;

                Aws::ACM::Model::RecordType record_type = record.GetType();
                Aws::String type;

                switch (record_type)
                {
                case Aws::ACM::Model::RecordType::CNAME:
                    type = "CNAME";
                    break;
                case Aws::ACM::Model::RecordType::NOT_SET:
                    type = "Not set";
                    break;
                default:
                    type = "Cannot determine.";
                    break;
                }

                std::cout << "  Resource record type:     " << type << 
                    std::endl;

                std::cout << "  Resource record value:    " << 
                    record.GetValue() << std::endl;

                std::cout << "  Validation domain:        " << 
                    validation.GetValidationDomain() << std::endl;

                Aws::Vector<Aws::String> emails = 
                    validation.GetValidationEmails();

                if (emails.size() > 0)
                {
                    std::cout << "  Validation emails:" << std::endl << 
                        std::endl;

                    for (auto it = emails.begin(); it != emails.end(); it++)
                    {
                        Aws::String email = *it;
                        std::cout << "    " << email << std::endl;
                    }

                    std::cout << std::endl;
                }

                Aws::ACM::Model::ValidationMethod validation_method = 
                    validation.GetValidationMethod();
                Aws::String method;
                
                switch (validation_method)
                {
                case Aws::ACM::Model::ValidationMethod::DNS:
                    method = "DNS";
                    break;
                case Aws::ACM::Model::ValidationMethod::EMAIL:
                    method = "Email";
                    break;
                case Aws::ACM::Model::ValidationMethod::NOT_SET:
                    method = "Not set";
                    break;
                default:
                    method = "Cannot determine";
                }

                std::cout << "  Validation method:        " << 
                    method << std::endl;

                Aws::ACM::Model::DomainStatus domain_status = 
                    validation.GetValidationStatus();
                Aws::String status; 

                switch (domain_status)
                {
                case Aws::ACM::Model::DomainStatus::FAILED:
                    status = "Failed";
                    break;
                case Aws::ACM::Model::DomainStatus::NOT_SET:
                    status = "Not set";
                    break;
                case Aws::ACM::Model::DomainStatus::PENDING_VALIDATION:
                    status = "Pending validation";
                    break;
                case Aws::ACM::Model::DomainStatus::SUCCESS:
                    status = "Success";
                    break;
                default:
                    status = "Cannot determine";
                }

                std::cout << "  Domain validation status: " << status << 
                    std::endl << std::endl;
                
            }
        }

        Aws::Vector<Aws::ACM::Model::ExtendedKeyUsage> usages = 
            certificate.GetExtendedKeyUsages();

        if (usages.size() > 0)
        {
            std::cout << std::endl << "Extended key usages:" << 
                std::endl << std::endl;

            for (auto it = usages.begin(); it != usages.end(); it++)
            {
                Aws::ACM::Model::ExtendedKeyUsage usage = *it;

                Aws::ACM::Model::ExtendedKeyUsageName usage_name = 
                    usage.GetName();
                Aws::String name;

                switch (usage_name)
                {
                case Aws::ACM::Model::ExtendedKeyUsageName::ANY:
                    name = "Any";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::CODE_SIGNING:
                    name = "Code signing";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::CUSTOM:
                    name = "Custom";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::EMAIL_PROTECTION:
                    name = "Email protection";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::IPSEC_END_SYSTEM:
                    name = "IPSEC end system";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::IPSEC_TUNNEL:
                    name = "IPSEC tunnel";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::IPSEC_USER:
                    name = "IPSEC user";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::NONE:
                    name = "None";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::NOT_SET:
                    name = "Not set";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::OCSP_SIGNING:
                    name = "OCSP signing";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::TIME_STAMPING:
                    name = "Time stamping";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::TLS_WEB_CLIENT_AUTHENTICATION:
                    name = "TLS web client authentication";
                    break;
                case Aws::ACM::Model::ExtendedKeyUsageName::TLS_WEB_SERVER_AUTHENTICATION:
                    name = "TLS web server authentication";
                    break;
                default:
                    name = "Cannot determine";
                }

                std::cout << "  Name: " << name << std::endl;
                std::cout << "  OID:  " << usage.GetOID() << 
                    std::endl << std::endl;
            }

            std::cout << std::endl;
        }
        
        Aws::ACM::Model::CertificateStatus certificate_status = 
            certificate.GetStatus();
        Aws::String status;

        switch (certificate_status)
        {
        case Aws::ACM::Model::CertificateStatus::EXPIRED:
            status = "Expired";
            break;
        case Aws::ACM::Model::CertificateStatus::FAILED:
            status = "Failed";
            break;
        case Aws::ACM::Model::CertificateStatus::INACTIVE:
            status = "Inactive";
            break;
        case Aws::ACM::Model::CertificateStatus::ISSUED:
            status = "Issued";
            break;
        case Aws::ACM::Model::CertificateStatus::NOT_SET:
            status = "Not set";
            break;
        case Aws::ACM::Model::CertificateStatus::PENDING_VALIDATION:
            status = "Pending validation";
            break;
        case Aws::ACM::Model::CertificateStatus::REVOKED:
            status = "Revoked";
            break;
        case Aws::ACM::Model::CertificateStatus::VALIDATION_TIMED_OUT:
            status = "Validation timed out";
            break;
        default:
            status = "Cannot determine";
        }

        std::cout << "Status:              " << status << std::endl;

        if (certificate.GetStatus() == 
            Aws::ACM::Model::CertificateStatus::FAILED)
        {
            Aws::ACM::Model::FailureReason failure_reason = 
                certificate.GetFailureReason();
            Aws::String reason;

            switch (failure_reason)
            {
            case Aws::ACM::Model::FailureReason::ADDITIONAL_VERIFICATION_REQUIRED:
                reason = "Additional verification required";
                break;
            case Aws::ACM::Model::FailureReason::CAA_ERROR:
                reason = "CAA error";
                break;
            case Aws::ACM::Model::FailureReason::DOMAIN_NOT_ALLOWED:
                reason = "Domain not allowed";
                break;
            case Aws::ACM::Model::FailureReason::DOMAIN_VALIDATION_DENIED:
                reason = "Domain validation denied";
                break;
            case Aws::ACM::Model::FailureReason::INVALID_PUBLIC_DOMAIN:
                reason = "Invalid public domain";
                break;
            case Aws::ACM::Model::FailureReason::NOT_SET:
                reason = "Not set";
                break;
            case Aws::ACM::Model::FailureReason::NO_AVAILABLE_CONTACTS:
                reason = "No available contacts";
                break;
            case Aws::ACM::Model::FailureReason::OTHER:
                reason = "Other";
                break;
            case Aws::ACM::Model::FailureReason::PCA_ACCESS_DENIED:
                reason = "PCA access denied";
                break;
            case Aws::ACM::Model::FailureReason::PCA_INVALID_ARGS:
                reason = "PCA invalid args";
                break;
            case Aws::ACM::Model::FailureReason::PCA_INVALID_ARN:
                reason = "PCA invalid ARN";
                break;
            case Aws::ACM::Model::FailureReason::PCA_INVALID_DURATION:
                reason = "PCA invalid duration";
                break;
            case Aws::ACM::Model::FailureReason::PCA_INVALID_STATE:
                reason = "PCA invalid state";
                break;
            case Aws::ACM::Model::FailureReason::PCA_LIMIT_EXCEEDED:
                reason = "PCA limit exceeded";
                break;
            case Aws::ACM::Model::FailureReason::PCA_NAME_CONSTRAINTS_VALIDATION:
                reason = "PCA name constraints validation";
                break;
            case Aws::ACM::Model::FailureReason::PCA_REQUEST_FAILED:
                reason = "PCA request failed";
                break;
            case Aws::ACM::Model::FailureReason::PCA_RESOURCE_NOT_FOUND:
                reason = "PCA resource not found";
                break;
            default:
                reason = "Cannot determine";
            }

            std::cout << "Failure reason:      " << reason << std::endl;
        } 
        
        if (certificate.GetStatus() == Aws::ACM::Model::CertificateStatus::REVOKED)
        {
            std::cout << "Revoked at (GMT):    " <<
                certificate.GetRevokedAt().ToGmtString(Aws::Utils::DateFormat::ISO_8601)
                << std::endl;
            
            Aws::ACM::Model::RevocationReason revocation_reason = 
                certificate.GetRevocationReason();
            Aws::String reason;

            switch (revocation_reason)
            {
            case Aws::ACM::Model::RevocationReason::AFFILIATION_CHANGED:
                reason = "Affiliation changed";
                break;
            case Aws::ACM::Model::RevocationReason::A_A_COMPROMISE:
                reason = "AA compromise";
                break;
            case Aws::ACM::Model::RevocationReason::CA_COMPROMISE:
                reason = "CA compromise";
                break;
            case Aws::ACM::Model::RevocationReason::CERTIFICATE_HOLD:
                reason = "Certificate hold";
                break;
            case Aws::ACM::Model::RevocationReason::CESSATION_OF_OPERATION:
                reason = "Cessation of operation";
                break;
            case Aws::ACM::Model::RevocationReason::KEY_COMPROMISE:
                reason = "Key compromise";
                break;
            case Aws::ACM::Model::RevocationReason::NOT_SET:
                reason = "Not set";
                break;
            case Aws::ACM::Model::RevocationReason::PRIVILEGE_WITHDRAWN:
                reason = "Privilege withdrawn";
                break;
            case Aws::ACM::Model::RevocationReason::REMOVE_FROM_CRL:
                reason = "Revoke from CRL";
                break;
            case Aws::ACM::Model::RevocationReason::SUPERCEDED:
                reason = "Superceded";
                break;
            case Aws::ACM::Model::RevocationReason::UNSPECIFIED:
                reason = "Unspecified";
                break;
            default:
                reason = "Cannot determine";
            }

            std::cout << "Revocation reason:   " << reason << std::endl;
        }

        if (certificate.GetType() == Aws::ACM::Model::CertificateType::IMPORTED)
        {
            std::cout << "Imported at (GMT):   " <<
                certificate.GetImportedAt().ToGmtString(Aws::Utils::DateFormat::ISO_8601) 
                << std::endl;
        }
        
        Aws::Vector<Aws::String> in_use_bys = certificate.GetInUseBy();

        if (in_use_bys.size() > 0)
        {
            std::cout << std::endl << "In use by:" << std::endl << std::endl;

            for (auto it = in_use_bys.begin(); it != in_use_bys.end(); it++)
            {
                Aws::String in_use_by = *it;
                std::cout << "  " << in_use_by << std::endl;
            }

            std::cout << std::endl;
        }
        
        if (certificate.GetType() == Aws::ACM::Model::CertificateType::AMAZON_ISSUED && 
            certificate.GetStatus() == Aws::ACM::Model::CertificateStatus::ISSUED)
        {
            std::cout << "Issued at (GMT):     " << 
                certificate.GetIssuedAt().ToGmtString(Aws::Utils::DateFormat::ISO_8601) 
                << std::endl;
        }
        
        std::cout << "Issuer:              " << certificate.GetIssuer() << 
            std::endl;
        
        Aws::ACM::Model::KeyAlgorithm key_algorithm = 
            certificate.GetKeyAlgorithm();
        Aws::String algorithm;

        switch (key_algorithm)
        {
        case Aws::ACM::Model::KeyAlgorithm::EC_prime256v1:
            algorithm = "P-256 (secp256r1, prime256v1)";
            break;
        case Aws::ACM::Model::KeyAlgorithm::EC_secp384r1:
            algorithm = "P-384 (secp384r1)";
            break;
        case Aws::ACM::Model::KeyAlgorithm::EC_secp521r1:
            algorithm = "P-521 (secp521r1)";
            break;
        case Aws::ACM::Model::KeyAlgorithm::NOT_SET:
            algorithm = "Not set";
            break;
        case Aws::ACM::Model::KeyAlgorithm::RSA_1024:
            algorithm = "RSA 1024";
            break;
        case Aws::ACM::Model::KeyAlgorithm::RSA_2048:
            algorithm = "RSA 2048";
            break;
        case Aws::ACM::Model::KeyAlgorithm::RSA_4096:
            algorithm = "RSA 4096";
            break;
        default:
            algorithm = "Cannot determine";
        }

        std::cout << "Key algorithm:       " << algorithm << std::endl;
        
        if (certificate.GetStatus() == Aws::ACM::Model::CertificateStatus::ISSUED)
        {
            std::cout << "Not valid after (GMT): " <<
                certificate.GetNotAfter().ToGmtString(Aws::Utils::DateFormat::ISO_8601)
                << std::endl;
            std::cout << "Not valid before (GMT): " <<
                certificate.GetNotBefore().ToGmtString(Aws::Utils::DateFormat::ISO_8601)
                << std::endl;
        }
        
        Aws::ACM::Model::CertificateTransparencyLoggingPreference logging_preference = 
            certificate.GetOptions().GetCertificateTransparencyLoggingPreference();
        Aws::String preference;

        switch (logging_preference)
        {
        case Aws::ACM::Model::CertificateTransparencyLoggingPreference::DISABLED:
            preference = "Disabled";
            break;
        case Aws::ACM::Model::CertificateTransparencyLoggingPreference::ENABLED:
            preference = "Enabled";
            break;
        case Aws::ACM::Model::CertificateTransparencyLoggingPreference::NOT_SET:
            preference = "Not set";
            break;
        default:
            preference = "Cannot determine";
        }
        
        std::cout << "Logging preference:  " << preference << std::endl;

        std::cout << "Serial:              " << certificate.GetSerial() << 
            std::endl;
        std::cout << "Signature algorithm: " 
            << certificate.GetSignatureAlgorithm() << std::endl;
        std::cout << "Subject:             " << certificate.GetSubject() << 
            std::endl;
        
        Aws::ACM::Model::CertificateType certificate_type = certificate.GetType();
        Aws::String type;

        switch (certificate_type)
        {
        case Aws::ACM::Model::CertificateType::AMAZON_ISSUED:
            type = "Amazon issued";
            break;
        case Aws::ACM::Model::CertificateType::IMPORTED:
            type = "Imported";
            break;
        case Aws::ACM::Model::CertificateType::NOT_SET:
            type = "Not set";
            break;
        case Aws::ACM::Model::CertificateType::PRIVATE_:
            type = "Private";
            break;
        default:
            type = "Cannot determine";
        }

        std::cout << "Type:                " << type << std::endl;

        Aws::Vector<Aws::String> alt_names = 
            certificate.GetSubjectAlternativeNames();

        if (alt_names.size() > 0)
        {
            std::cout << std::endl << "Alternative names:" << 
                std::endl << std::endl;

            for (auto it = alt_names.begin(); it != alt_names.end(); it++)
            {
                Aws::String alt_name = *it;
                std::cout << "  " << alt_name << std::endl;
            }

            std::cout << std::endl;
        }

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::ExportCertificate.
bool AwsDoc::ACM::ExportCertificate(const Aws::String& certificateArn,
    const Aws::String& region)
{
    Aws::Utils::CryptoBuffer passphrase = 
        Aws::Utils::Crypto::SymmetricCipher::GenerateKey();

    Aws::Client::ClientConfiguration config;
    config.region = region;
    
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ExportCertificateRequest request;
    request.WithCertificateArn(certificateArn).WithPassphrase(passphrase);

    Aws::ACM::Model::ExportCertificateOutcome outcome =
        acm_client.ExportCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ExportCertificate: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Information about certificate with ARN '" 
            << certificateArn << "':" << std::endl << std::endl;

        auto result = outcome.GetResult();

        std::cout << "Certificate:       " << std::endl << std::endl <<
            result.GetCertificate() << std::endl << std::endl;
        std::cout << "Certificate chain: " << std::endl << std::endl <<
            result.GetCertificateChain() << std::endl << std::endl;
        std::cout << "Private key:       " << std::endl << std::endl <<
            result.GetPrivateKey() << std::endl;

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::GetCertificate.
bool AwsDoc::ACM::GetCertificate(const Aws::String& certificateArn,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;
    
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::GetCertificateRequest request;
    request.WithCertificateArn(certificateArn);

    Aws::ACM::Model::GetCertificateOutcome outcome =
        acm_client.GetCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: GetCertificate: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Information about certificate with ARN '"
            << certificateArn << "':" << std::endl << std::endl;

        auto result = outcome.GetResult();

        std::cout << "Certificate: " << std::endl << std::endl <<
            result.GetCertificate() << std::endl;
        std::cout << "Certificate chain: " << std::endl << std::endl <<
            result.GetCertificateChain() << std::endl;

        return true;
    }
}

// Helper function for ImportCertificate function, immediately following.
bool FileExists(const char* fileName)
{
    std::ifstream ifile;
    ifile.open(fileName);

    if (ifile)
    {
        return true;
    }
    else
    {
        return false;
    }
}

// Helper function for Aws::ACM::ACMClient::ImportCertificate.
bool AwsDoc::ACM::ImportCertificate(const Aws::String& certificateFile,
    const Aws::String& privateKeyFile,
    const Aws::String& certificateChainFile,
    const Aws::String& region,
    const Aws::String& certificateArn)
{
    if (!FileExists(certificateFile.c_str()))
    {
        std::cout << "Error: The certificate file '" << certificateFile <<
            "' does not exist." << std::endl;

        return false;
    }

    if (!FileExists(privateKeyFile.c_str()))
    {
        std::cout << "Error: The private key file '" << privateKeyFile <<
            "' does not exist." << std::endl;

        return false;
    }

    if (!FileExists(certificateChainFile.c_str()))
    {
        std::cout << "Error: The certificate chain file '" 
            << certificateChainFile << "' does not exist." << std::endl;

        return false;
    }

    std::ifstream cert_ifs(certificateFile.c_str());
    std::ifstream pk_ifs(privateKeyFile.c_str());
    std::ifstream cert_chain_ifs(certificateChainFile.c_str());

    Aws::String certificate;
    certificate.assign(std::istreambuf_iterator<char>(cert_ifs), 
        std::istreambuf_iterator<char>());

    Aws::String privateKey;
    privateKey.assign(std::istreambuf_iterator<char>(pk_ifs),
        std::istreambuf_iterator<char>());

    Aws::String certificateChain;
    certificateChain.assign(std::istreambuf_iterator<char>(cert_chain_ifs),
        std::istreambuf_iterator<char>());

    Aws::Client::ClientConfiguration config;
    config.region = region;
 
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ImportCertificateRequest request;

    request.WithCertificate(Aws::Utils::ByteBuffer((unsigned char*)
            certificate.c_str(), certificate.size()))
        .WithPrivateKey(Aws::Utils::ByteBuffer((unsigned char*)
            privateKey.c_str(), privateKey.size()))
        .WithCertificateChain(Aws::Utils::ByteBuffer((unsigned char*)
            certificateChain.c_str(), certificateChain.size()));

    if (!certificateArn.empty())
    {
        request.SetCertificateArn(certificateArn);
    }

    Aws::ACM::Model::ImportCertificateOutcome outcome =
        acm_client.ImportCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ImportCertificate: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Certificate associated with ARN '" <<
            outcome.GetResult().GetCertificateArn() << "' imported."
            << std::endl;
            
        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::ListCertificates.
bool AwsDoc::ACM::ListCertificates(const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;
    
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ListCertificatesRequest request;

    Aws::ACM::Model::ListCertificatesOutcome outcome = 
        acm_client.ListCertificates(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ListCertificates: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Information about certificates: " 
            << std::endl << std::endl;

        auto result = outcome.GetResult();

        Aws::Vector<Aws::ACM::Model::CertificateSummary> certificates = 
            result.GetCertificateSummaryList();

        if (certificates.size() > 0)
        {
            for (const Aws::ACM::Model::CertificateSummary& certificate : certificates)
            {
                std::cout << "Certificate ARN: " <<
                    certificate.GetCertificateArn() << std::endl;
                std::cout << "Domain name:     " <<
                    certificate.GetDomainName() << std::endl << std::endl;
            }
        }
        else
        {
            std::cout << "No available certificates found in AWS Region '" << 
                region << "'." << std::endl;
        }

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::ListTagsForCertificate.
bool AwsDoc::ACM::ListTagsForCertificate(const Aws::String& certificateArn, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;
    
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ListTagsForCertificateRequest request;
    request.WithCertificateArn(certificateArn);

    Aws::ACM::Model::ListTagsForCertificateOutcome outcome =
        acm_client.ListTagsForCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ListTagsForCertificate: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Information about tags for " 
            "certificate with ARN '"
            << certificateArn << "':" << std::endl << std::endl;

        auto result = outcome.GetResult();

        Aws::Vector<Aws::ACM::Model::Tag> tags =
            result.GetTags();

        if (tags.size() > 0)
        {
            for (const Aws::ACM::Model::Tag& tag : tags)
            {
                std::cout << "Key:   " << tag.GetKey() << std::endl;
                std::cout << "Value: " << tag.GetValue() 
                    << std::endl << std::endl;
            }
        }
        else
        {
            std::cout << "No tags found." << std::endl;
        }

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::RemoveTagsFromCertificate.
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

// Helper function for Aws::ACM::ACMClient::RenewCertificate.
bool AwsDoc::ACM::RenewCertificate(const Aws::String& certificateArn, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::RenewCertificateRequest request;
    request.SetCertificateArn(certificateArn);

    Aws::ACM::Model::RenewCertificateOutcome outcome =
        acm_client.RenewCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: RenewCertificate: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Renewed certificate with ARN '" 
            << certificateArn << "'." << std::endl;

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::RequestCertificate.
bool AwsDoc::ACM::RequestCertificate(const Aws::String& domainName,
    const Aws::String& idempotencyToken,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;
    
    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::RequestCertificateRequest request;
    request.WithDomainName(domainName)
        .WithIdempotencyToken(idempotencyToken);

    Aws::ACM::Model::RequestCertificateOutcome outcome = 
        acm_client.RequestCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "RequestCertificate error: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: The newly requested certificate's "
            "ARN is '" <<
            outcome.GetResult().GetCertificateArn() <<
            "'." << std::endl;

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::ResendValidationEmail.
bool AwsDoc::ACM::ResendValidationEmail(const Aws::String& certificateArn,
    const Aws::String& domainName,
    const Aws::String& validationDomain,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ResendValidationEmailRequest request;
    request.WithCertificateArn(certificateArn)
        .WithDomain(domainName)
        .WithValidationDomain(validationDomain);

    Aws::ACM::Model::ResendValidationEmailOutcome outcome =
        acm_client.ResendValidationEmail(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "ResendValidationEmail error: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: The validation email has been resent."
            << std::endl;

        return true;
    }
}

// Helper function for Aws::ACM::ACMClient::UpdateCertificateOptions.
bool AwsDoc::ACM::UpdateCertificateOption(const Aws::String& certificateArn,
    const Aws::String& region,
    const Aws::String& option)
{
    if (option != "logging-enabled" &&
        option != "logging-disabled")
    {
        std::cout << "UpdateCertificateOption error: "
            "The option '" << option << "' is not valid. "
            "You must specify an option "
            "of 'logging-enabled' or 'logging-disabled'." << std::endl;

        return false;
    }

    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::UpdateCertificateOptionsRequest request;
    request.SetCertificateArn(certificateArn);

    Aws::ACM::Model::CertificateOptions options;

    if (option == "logging-enabled")
    {
        options.SetCertificateTransparencyLoggingPreference(
            Aws::ACM::Model::CertificateTransparencyLoggingPreference::ENABLED);
    }
    else
    {
        options.SetCertificateTransparencyLoggingPreference(
            Aws::ACM::Model::CertificateTransparencyLoggingPreference::DISABLED);
    }    

    request.SetOptions(options);

    Aws::ACM::Model::UpdateCertificateOptionsOutcome outcome =
        acm_client.UpdateCertificateOptions(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "UpdateCertificateOption error: " <<
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: The option '" << option << "' has been set for "
            "the certificate with the ARN '" << certificateArn << "'."
            << std::endl;

        return true;
    }
}

// End helper functions for AWS Certificate Manager basic operations.

// Helper function for main function, immediately following.
void PrintUsage(const char* programName)
{
    std::cout << "Usage: " << programName << " OPTION [ARGUMENTS]" << std::endl << std::endl;
    std::cout << "--add-tag <certificate ARN> <tag key> <tag value> <region>" << std::endl;
    std::cout << "--delete <certificate ARN> <region>" << std::endl;
    std::cout << "--describe <certificate ARN> <region>" << std::endl;
    std::cout << "--export <certificate ARN> <region>" << std::endl;
    std::cout << "--get <certificate ARN> <region>" << std::endl;
    std::cout << "--import <certificate> <private key> <certificate chain> <region> [certificate ARN]" << std::endl;
    std::cout << "--list <region>" << std::endl;
    std::cout << "--list-tags <certificate ARN> <region>" << std::endl;
    std::cout << "--remove-tag <certificate ARN> <tag key> <region>" << std::endl;
    std::cout << "--renew <certificate ARN> <region>" << std::endl;
    std::cout << "--request <domain name> <idempotency token> <region>" << std::endl;
    std::cout << "--resend <certificate ARN> <domain name> <validation domain name> <region>" << std::endl;
    std::cout << "--update <certificate ARN> <region> [logging-enabled | logging-disabled]" << std::endl;

    return;
}

int main(int argc, char* argv[])
{
    const char* PROGRAM_NAME = "run_acm_basic_operations";

    if (argc == 1 || (strcmp(argv[1], "--help") == 0))
    {
        PrintUsage(PROGRAM_NAME);

        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (strcmp(argv[1], "--add-tag") == 0)
        {
            if (argc != 6)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --add-tag <certificate ARN> <tag key> <tag value> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << 
                    " --add-tag arn:aws:acm:us-east-1:111111111111:certificate/62a108dc-0b56-49a9-a258-dc022EXAMPLE my-key my-value us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::AddTagToCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/62a108dc-0b56-49a9-a258-dc022EXAMPLE".
                argv[3],  // For example, "my-key".
                argv[4],  // For example, "my-value".
                argv[5])) // For example, "us-east-1".
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--delete") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --delete <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << 
                    " --delete arn:aws:acm:us-east-1:111111111111:certificate/d431cfe8-e627-45fb-9d6a-4410fEXAMPLE us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::DeleteCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/d431cfe8-e627-45fb-9d6a-4410fEXAMPLE".
                argv[3])) // For example, "us-east-1".
            {
                return 1;
            }
        }        
        else if (strcmp(argv[1], "--describe") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --describe <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --describe arn:aws:acm:us-east-1:111111111111:certificate/62a108dc-0b56-49a9-a258-dc022EXAMPLE us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::DescribeCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/62a108dc-0b56-49a9-a258-dc022EXAMPLE".
                argv[3])) // For example, "us-east-1".
            {
                return 1;
            }
        }        
        else if (strcmp(argv[1], "--export") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --export <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --export arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::ExportCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3])) // For example, "us-east-1".
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--get") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --get <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --get arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::GetCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3])) // For example, "us-east-1". 
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--import") == 0)
        {
            if (argc != 7)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --import <certificate file> <private key file> <certificate chain file> <region> [certificate ARN]" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << " --import Certificate.txt decrypted_private_key.txt Certificate_chain.txt us-east-1 arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE" << std::endl;

                return 1;
            }
            
            if (!AwsDoc::ACM::ImportCertificate(
                argv[2],  // For example, "Certificate.txt".
                argv[3],  // For example, "decrypted_private_key.txt". 
                argv[4],  // For example, "Certificate_chain.txt".
                argv[5],  // For example, "us-east-1". 
                argv[6])) // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--list") == 0)
        {
            if (argc != 3)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --list <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --list us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::ListCertificates(
                argv[2])) // For example, "us-east-1". 
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--list-tags") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --list-tags <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --list-tags arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::ListTagsForCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3])) // For example, "us-east-1". 
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--remove-tag") == 0)
        {
            if (argc != 5)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --remove-tag  <certificate ARN> <tag key> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --remove-tag arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE my-key us-east-1" << std::endl; 
                
                return 1;
            }

            if (!AwsDoc::ACM::RemoveTagFromCertificate(
                    argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                    argv[3],  // For example, "my-key". 
                    argv[4])) // For example, "us-east-1". 
                {
                    return 1;
                }
        }
        else if (strcmp(argv[1], "--renew") == 0)
        {
            if (argc != 4)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --renew <certificate ARN> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME <<
                    " --renew arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE us-east-1" << std::endl;

                return 1;
            }
        
            if (!AwsDoc::ACM::RenewCertificate(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3])) // For example, "us-east-1". 
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--request") == 0)
        {
            if (argc != 5)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --request <domain name> <idempotency token> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << " --request www.example.com 54321 us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::RequestCertificate(
                argv[2],  // For example, "www.example.com".
                argv[3],  // For example, "54321".
                argv[4])) // For example, "us-east-1".
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--resend") == 0)
        {
            if (argc != 6)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --resend <certificate ARN> <domain name> <validation domain name> <region>" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << " --resend arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE www.example.com www.example.com us-east-1" << std::endl;

                return 1;
            }

            if (!AwsDoc::ACM::ResendValidationEmail(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3],  // For example, "www.example.com".
                argv[4],  // For example, "www.example.com".
                argv[5])) // For example, "us-east-1".
            {
                return 1;
            }
        }
        else if (strcmp(argv[1], "--update") == 0)
        {
            if (argc != 5)
            {
                std::cout << "Usage: " << PROGRAM_NAME << " --update <certificate ARN> <region> [logging-enabled | logging-disabled]" << std::endl;
                std::cout << "Example: " << PROGRAM_NAME << " --update arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE us-east-1 logging-enabled" << std::endl;

                return 1;
            }
            
            if (!AwsDoc::ACM::UpdateCertificateOption(
                argv[2],  // For example, "arn:aws:acm:us-east-1:111111111111:certificate/9301d141-c7f8-4b02-9fb7-78eb2EXAMPLE".
                argv[3],  // For example, "us-east-1".
                argv[4])) // For example, "logging-enabled". 
            {
                return 1;
            }
        }
        else
        {
            PrintUsage(PROGRAM_NAME);

            return 1;
        }
    }
    Aws::ShutdownAPI(options);
    
    return 0;
}