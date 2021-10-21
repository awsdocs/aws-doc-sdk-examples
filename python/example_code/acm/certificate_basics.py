# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Certificate Manager (ACM)
to request and manage certificates.
"""

# snippet-start:[python.example_code.acm.imports]
import logging
from pprint import pprint

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# snippet-end:[python.example_code.acm.imports]


# snippet-start:[python.example_code.acm.AcmCertificate]
class AcmCertificate:
    """
    Encapsulates ACM functions.
    """
    def __init__(self, acm_client):
        """
        :param acm_client: A Boto3 ACM client.
        """
        self.acm_client = acm_client

# snippet-end:[python.example_code.acm.AcmCertificate]

# snippet-start:[python.example_code.acm.DescribeCertificate]
    def describe(self, certificate_arn):
        """
        Gets certificate metadata.

        :param certificate_arn: The Amazon Resource Name (ARN) of the certificate.
        :return: Metadata about the certificate.
        """
        try:
            response = self.acm_client.describe_certificate(
                CertificateArn=certificate_arn)
            certificate = response['Certificate']
            logger.info(
                "Got metadata for certificate for domain %s.",
                certificate['DomainName'])
        except ClientError:
            logger.exception("Couldn't get data for certificate %s.", certificate_arn)
            raise
        else:
            return certificate
# snippet-end:[python.example_code.acm.DescribeCertificate]

# snippet-start:[python.example_code.acm.GetCertificate]
    def get(self, certificate_arn):
        """
        Gets the body and certificate chain of a certificate.

        :param certificate_arn: The ARN of the certificate.
        :return: The body and chain of a certificate.
        """
        try:
            response = self.acm_client.get_certificate(CertificateArn=certificate_arn)
            logger.info("Got certificate %s and its chain.", certificate_arn)
        except ClientError:
            logger.exception("Couldn't get certificate %s.", certificate_arn)
            raise
        else:
            return response
# snippet-end:[python.example_code.acm.GetCertificate]

# snippet-start:[python.example_code.acm.ListCertificates]
    def list(
            self, max_items, statuses=None, key_usage=None, extended_key_usage=None,
            key_types=None):
        """
        Lists the certificates for the current account.

        :param max_items: The maximum number of certificates to list.
        :param statuses: Filters the results to the specified statuses. If None, all
                         certificates are included.
        :param key_usage: Filters the results to the specified key usages. If None,
                          all key usages are included.
        :param extended_key_usage: Filters the results to the specified extended key
                                   usages. If None, all extended key usages are
                                   included.
        :param key_types: Filters the results to the specified key types. If None, all
                          key types are included.
        :return: The list of certificates.
        """
        try:
            kwargs = {'MaxItems': max_items}
            if statuses is not None:
                kwargs['CertificateStatuses'] = statuses
            includes = {}
            if key_usage is not None:
                includes['keyUsage'] = key_usage
            if extended_key_usage is not None:
                includes['extendedKeyUsage'] = extended_key_usage
            if key_types is not None:
                includes['keyTypes'] = key_types
            if includes:
                kwargs['Includes'] = includes
            response = self.acm_client.list_certificates(**kwargs)
            certificates = response['CertificateSummaryList']
            logger.info("Got %s certificates.", len(certificates))
        except ClientError:
            logger.exception("Couldn't get certificates.")
            raise
        else:
            return certificates
# snippet-end:[python.example_code.acm.ListCertificates]

# snippet-start:[python.example_code.acm.ImportCertificate]
    def import_certificate(self, certificate_body, private_key):
        """
        Imports a self-signed certificate to ACM.

        :param certificate_body: The body of the certificate, in PEM format.
        :param private_key: The unencrypted private key of the certificate, in PEM
                            format.
        :return: The ARN of the imported certificate.
        """
        try:
            response = self.acm_client.import_certificate(
                Certificate=certificate_body, PrivateKey=private_key)
            certificate_arn = response['CertificateArn']
            logger.info("Imported certificate.")
        except ClientError:
            logger.exception("Couldn't import certificate.")
            raise
        else:
            return certificate_arn
# snippet-end:[python.example_code.acm.ImportCertificate]

# snippet-start:[python.example_code.acm.DeleteCertificate]
    def remove(self, certificate_arn):
        """
        Removes a certificate.

        :param certificate_arn: The ARN of the certificate to remove.
        """
        try:
            self.acm_client.delete_certificate(CertificateArn=certificate_arn)
            logger.info("Removed certificate %s.", certificate_arn)
        except ClientError:
            logger.exception("Couldn't remove certificate %s.", certificate_arn)
            raise
# snippet-end:[python.example_code.acm.DeleteCertificate]

# snippet-start:[python.example_code.acm.AddTagsToCertificate]
    def add_tags(self, certificate_arn, tags):
        """
        Adds tags to a certificate. Tags are key-value pairs that contain custom
        metadata.

        :param certificate_arn: The ARN of the certificate.
        :param tags: A dictionary of key-value tags to add to the certificate.
        """
        try:
            self.acm_client.add_tags_to_certificate(
                CertificateArn=certificate_arn,
                Tags=[{'Key': key, 'Value': value} for key, value in tags.items()])
            logger.info("Added %s tags to certificate %s.", len(tags), certificate_arn)
        except ClientError:
            logger.exception("Couldn't add tags to certificate %s.", certificate_arn)
            raise
# snippet-end:[python.example_code.acm.AddTagsToCertificate]

# snippet-start:[python.example_code.acm.ListTagsForCertificate]
    def list_tags(self, certificate_arn):
        """
        Lists the tags attached to a certificate.

        :param certificate_arn: The ARN of the certificate.
        :return: The dictionary of certificate tags.
        """
        try:
            response = self.acm_client.list_tags_for_certificate(
                CertificateArn=certificate_arn)
            tags = {tag['Key']: tag['Value'] for tag in response['Tags']}
            logger.info("Got %s tags for certificates %s.", len(tags), certificate_arn)
        except ClientError:
            logger.exception("Couldn't get tags for certificate %s.", certificate_arn)
            raise
        else:
            return tags
# snippet-end:[python.example_code.acm.ListTagsForCertificate]

# snippet-start:[python.example_code.acm.RemoveTagsFromCertificate]
    def remove_tags(self, certificate_arn, tags):
        """
        Removes tags from a certificate. If the value of a tag is specified, the tag is
        removed only when the value matches the value of the certificate's tag.
        Otherwise, the tag is removed regardless of its value.

        :param certificate_arn: The ARN of the certificate.
        :param tags: The dictionary of tags to remove.
        """
        try:
            cert_tags = []
            for key, value in tags.items():
                tag = {'Key': key}
                if value is not None:
                    tag['Value'] = value
                cert_tags.append(tag)
            self.acm_client.remove_tags_from_certificate(
                CertificateArn=certificate_arn, Tags=cert_tags)
            logger.info(
                "Removed %s tags from certificate %s.", len(tags), certificate_arn)
        except ClientError:
            logger.exception(
                "Couldn't remove tags from certificate %s.", certificate_arn)
            raise
# snippet-end:[python.example_code.acm.RemoveTagsFromCertificate]

# snippet-start:[python.example_code.acm.RequestCertificate]
    def request_validation(
            self, domain, alternate_domains, method, validation_domains=None):
        """
        Starts a validation request that results in a new certificate being issued
        by ACM. DNS validation requires that you add CNAME records to your DNS
        provider. Email validation sends email to a list of email addresses that
        are associated with the domain.

        For more information, see _Issuing and managing certificates_ in the ACM
        user guide.
            https://docs.aws.amazon.com/acm/latest/userguide/gs.html

        :param domain: The primary domain to associate with the certificate.
        :param alternate_domains: Subject Alternate Names (SANs) for the certificate.
        :param method: The validation method, either DNS or EMAIL.
        :param validation_domains: Alternate domains to use for email validation, when
                                   the email domain differs from the primary domain of
                                   the certificate.
        :return: The ARN of the requested certificate.
        """
        try:
            kwargs = {
                'DomainName': domain,
                'ValidationMethod': method,
                'SubjectAlternativeNames': alternate_domains}
            if validation_domains is not None:
                kwargs['DomainValidationOptions'] = [{
                    'DomainName': key,
                    'ValidationDomain': value
                } for key, value in validation_domains.items()]
            response = self.acm_client.request_certificate(**kwargs)
            certificate_arn = response['CertificateArn']
            logger.info(
                "Requested %s validation for domain %s. Certificate ARN is %s.",
                method, domain, certificate_arn)
        except ClientError:
            logger.exception(
                "Request for %s validation of domain %s failed.", method, domain)
            raise
        else:
            return certificate_arn
# snippet-end:[python.example_code.acm.RequestCertificate]

# snippet-start:[python.example_code.acm.ResendValidationEmail]
    def resend_validation_email(self, certificate_arn, domain, validation_domain):
        """
        Request that validation email is sent again, for a certificate that was
        previously requested with email validation.

        :param certificate_arn: The ARN of the certificate.
        :param domain: The primary domain of the certificate.
        :param validation_domain: Alternate domain to use for determining email
                                  addresses to use for validation.
        """
        try:
            self.acm_client.resend_validation_email(
                CertificateArn=certificate_arn,
                Domain=domain,
                ValidationDomain=validation_domain)
            logger.info(
                "Validation email resent to validation domain %s.", validation_domain)
        except ClientError:
            logger.exception(
                "Couldn't resend validation email to %s.", validation_domain)
            raise
# snippet-end:[python.example_code.acm.ResendValidationEmail]


# snippet-start:[python.example_code.acm.Usage_ImportListRemove]
def usage_demo():
    print('-'*88)
    print("Welcome to the AWS Certificate Manager (ACM) demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    acm_certificate = AcmCertificate(boto3.client('acm'))
    domain = 'example.com'
    sub_domains = [f'{sub}.{domain}' for sub in ['test', 'dev']]
    print(f"Request a certificate for {domain}.")
    certificate_arn = acm_certificate.request_validation(domain, sub_domains, 'DNS')
    print(f"Started validation, got certificate ARN: {certificate_arn}.")

    import_cert_arn = None
    cert_file_name = input(
        "Enter the file name for a self-signed certificate in PEM format. "
        "This certificate will be imported to ACM. Press Enter to skip: ")
    if cert_file_name:
        pk_file_name = input(
            "Enter the file name for the unencrypted private key of the certificate. "
            "This file must also be in PEM format: ")
        if pk_file_name:
            with open(cert_file_name, 'rb') as cert_file:
                import_cert = cert_file.read()
            with open(pk_file_name, 'rb') as pk_file:
                import_pk = pk_file.read()
            import_cert_arn = acm_certificate.import_certificate(import_cert, import_pk)
            print(f"Certificate imported, got ARN: {import_cert_arn}")
        else:
            print("No private key file entered. Skipping certificate import.")
    else:
        print("Skipping self-signed certificate import.")

    print("Getting the first 10 issued certificates.")
    certificates = acm_certificate.list(10, statuses=['ISSUED'])
    print(f"Found {len(certificates)} issued certificates.")

    print(f"Getting metadata for certificate {certificate_arn}")
    cert_metadata = acm_certificate.describe(certificate_arn)
    pprint(cert_metadata)

    if import_cert_arn is not None:
        print(f"Getting certificate for imported certificate {import_cert_arn}")
        import_cert_data = acm_certificate.get(import_cert_arn)
        pprint(import_cert_data)

    print(f"Adding tags to certificate {certificate_arn}.")
    acm_certificate.add_tags(certificate_arn, {
        'purpose': 'acm demo',
        'color': 'green'})
    tags = acm_certificate.list_tags(certificate_arn)
    print(f"Found tags: {tags}")
    acm_certificate.remove_tags(certificate_arn, {key: None for key in tags})
    print("Removed tags.")

    print("Removing certificates added during the demo.")
    acm_certificate.remove(certificate_arn)
    if import_cert_arn is not None:
        acm_certificate.remove(import_cert_arn)

    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.acm.Usage_ImportListRemove]


if __name__ == '__main__':
    usage_demo()
