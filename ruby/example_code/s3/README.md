# AWS SDK for Ruby code examples for Amazon S3

## Purpose

This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate 
Amazon Simple Storage Service (Amazon S3).

## Code examples

### Usage examples
- [Managing access permissions](./s3-ruby-example-access-permissions.rb)
- [Working with buckets](./s3_ruby_create_bucket.rb)

### API examples
- [Allows federated to list objects](./auth_federation_token_request_test.rb)
- [Prints list of objects](./auth_request_object_keys.rb)
- [Create user access keys](./auth_session_token_request_test.rb)
- [Copy objects between buckets](./copy_object_between_buckets.rb)
- [Copy an object between buckets, changing its server-side encryption state](./copy_object_encrypt_copy.rb)
- [Create a bucket](./create_bucket_snippet.rb)
- [Create RSA keys](./create_rsa_keys.rb)
- [Determine an object's encryption state](./determine_object_encryption_state.rb)
- [List your buckets](./s3.rb)
- [Upload an object to a bucket, encrypting the contents with an RSA public key](./s3-ruby-example-add-cspk-item.rb)
- [Add an event notification to a bucket](./s3-ruby-example-add-notification.rb)
- [Check whether a bucket exists in an AWS Region](./s3-ruby-example-bucket-accessible.rb)
- [Check whether a bucket exists](./s3-ruby-example-bucket-exists.rb)
- [Create a bucket](./s3-ruby-example-create-bucket.rb)
- [Create RSA keys](./s3-ruby-example-create-rsa-keys.rb)
- [Check which buckets have public read access](./s3-ruby-example-find-open-buckets.rb)
- [Download an object from a bucket, where the object's contents were  encrypted with an RSA public key](./s3-ruby-example-get-cspk-item.rb)
- [Downloads an object from a bucket](./s3-ruby-example-get-item.rb)
- [Determine whether a bucket exists and you have permission to access it](./s3-ruby-example-head-bucket.rb)
- [List objects in a bucket](./s3-ruby-example-list-bucket-items.rb)
- [Copy an object from bucket to another, optionally setting the object's (ACL) and storage class](./s3-ruby-example-set-item-props.rb)
- [Lists the buckets owned by the authenticated sender of the request](./s3-ruby-example-show-50-buckets.rb)
- [Checks to see which buckets are accessible to you, with the target AWS Region specified](./s3-ruby-example-show-buckets-in-region.rb)
- [Uploads an object to a bucket](./s3-ruby-example-upload-item.rb)
- [Upload an item (file) to a folder within a bucket](./s3-ruby-example-upload-item-to-folder.rb)
- [Upload an object to a bucket, and associate specified metadata with the object](./s3-ruby-example-upload-item-with-metadata.rb)
- [Upload multiple items to a bucket ](./s3-ruby-example-upload-multiple-items.rb)
- [Deny uploads of unencrypted objects to a bucket](./s3_add_bucket_ssekms_encryption_policy.rb)
- [Deny uploads of objects without server-side AWS KMS encryption to a bucket.](./s3_add_bucket_sses3_encryption_policy.rb)
- [Upload an encrypted object to a bucket (AES256-GCM key)](./s3_add_csaes_encrypt_item.rb)
- [Upload an encrypted object to a bucket (KMS key)](./s3_add_cskms_encrypt_item.rb)
- [Upload an encrypted object to a bucket (public/private key-pair strings)](./s3_add_cspk_encrypt_item.rb)
- [Sets the default encryption state for a bucket SSE with an AWS KMS customer master key (CMK).](./s3_add_default_sse_encryption.rb)
- [Adds a (CORS) configuration to an Amazon S3 bucket](./s3_ruby_bucket_cors.rb)
- [Configure a bucket as a static website](./s3_ruby_bucket_website.rb)
- [Set the ACL on a bucket for the given owner](./ss3_set_bucket_acls.rb)
- [Sets the ACL on an object in a bucket for the given owner](./s3_set_bucket_object_acls.rb)
- [Upload an object to a bucket (file uploader)](./upload_files_using_managed_file_uploader.rb)
- [Upload an object to a bucket (put object method) ](./upload_files_using_put_object_method.rb)
- [Upload an object to a bucket using a presigned URL](./upload_object_presigned_url.rb)


## Prerequisites

- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see 
  [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the 
  *AWS SDK for Ruby Developer Guide*.
- To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see 
  [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Progamming Language website.
- To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.
- The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see 
  [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the 
  *AWS SDK for Ruby Developer Guide*.

## Cautions

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific AWS Regions. For more information, see the 
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `create_bucket_snippet.rb` file, replace the hard-coded `bucket_name` and `region` variable values in the file with your own values, save the file, and then run the file. For example:

```
ruby create_bucket_snippet.rb
```

Most of these files have been refactored into reusable functions that can be copied into your own code. You can then call those functions directly from your own code without modifying the copied function code itself. For example, you could copy the `bucket_created?` function code from the `create_bucket_snippet.rb` file into your own code. You could then adapt the code in the `run_me` function in that same file as a basis to write your own code to call the copied `bucket_created?` function.

## Running the tests

Most of these code example files have accompanying tests that are written to work with RSpec. These tests are in the `tests` folder and contain the same file name as the corresponding code example file, for example `tests/test_create_bucket_snippet.rb` contains tests for `create_bucket_snippet.rb`.

To use RSpec to run all tests within a file, specify the path to that file, for example:

```
rspec tests/test_create_bucket_snippet.rb
```

To explore additional options for using RSpec to run tests, run the `rspec --help` command. 

Most of these tests are designed to use stubs, to avoid generating unnecessary costs in an AWS account. For more information, see [Stubbing Client Responses and Errors](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/stubbing.html) in the *AWS SDK for Ruby Developer Guide*.


## Additional information

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/dev)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
