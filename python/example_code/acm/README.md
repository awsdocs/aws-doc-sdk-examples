# AWS Certificate Manager (ACM) basics example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Certificate Manager (ACM)
to request, import, and manage certificates.

*ACM makes it easy to provision, manage, and deploy SSL/TLS certificates on AWS 
managed resources.*

## Code examples

* [Adding tags to a certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`add_tags_to_certificate`)
* [Deleting a certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`delete_certificate`)
* [Getting certificate metadata](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`describe_certificate`)
* [Getting the body and certificate chain of a certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`get_certificate`)
* [Importing a self-signed certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`import_certificate`)
* [Listing certificates for the current account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`list_certificates`)
* [Listing tags for a certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`list_tags_for_certificate`)
* [Removing tags from a certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`remove_tags_from_certificate`)
* [Requesting a new certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`request_certificate`)
* [Resending validation email](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/acm/certificate_basics.py)
(`resend_validation_email`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.14.47 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

Run this example at a command prompt with the following command.

```
python certificate_basics.py
```

The example optionally imports a self-signed certificate to ACM. To do this, it asks 
for certificate and private key files in PEM format. You can create these yourself by
using a certificate toolkit, such as OpenSSL. 

### Example structure

The example contains the following file.

**certificate_basics.py**

Shows how to use ACM APIs. The `usage_demo` script requests a new certificate from
ACM, uploads a self-signed certificate, and gets data about certificates from ACM.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/acm 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Certificate Manager (ACM) service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/acm.html)
- [AWS Certificate Manager (ACM) documentation](https://docs.aws.amazon.com/acm)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
