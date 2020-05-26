# AWS SDK for Ruby Code Examples for Amazon SES

## Purpose

These examples demonstrate how to perform several Amazon SES operations. Learn how to
send emails, list emails, get send statistics, and send verifications.

## Prerequisites 

- You must have an AWS account, and have your default credentials and AWS Regions configured
as described in [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html)
in the AWS SDK for Ruby Developer Guide. 
- RubyGems 3.1.2 or later.
- AWS SDK for Ruby. For download and installation instructions, see [Installing the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html).
- Ruby 2.6 or later. After you install Ruby, add the path to Ruby in your environment
 variables so that you can run Ruby from any command prompt. 
- RSpec 4.0 or later (to run unit tests).
- Verify your email address with Amazon SES. Before you can send an email with Amazon 
SES, you must verify that you own the sender's email address. If your account is still 
in the Amazon SES sandbox, you must also verify the recipient email address. The easiest
way to verify email addresses is by using the Amazon SES console. For more information,
see [Verifying Email Addresses in Amazon SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/verify-email-addresses.html).
- Get your AWS credentials—You need an AWS access key ID and AWS secret access key to 
access Amazon SES using an SDK. You can find your credentials by using the Security 
Credentials page of the AWS Management Console. For more information about credentials,
see [Using Credentials With Amazon SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/using-credentials.html).
- Create a shared credentials file—For the sample code in this section to function 
properly, you must create a shared credentials file. For more information, see 
[Using Credentials With Amazon SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/create-shared-credentials-file.html).

##  Running the code 

### ses_get_statistics/ses_get_statistics.rb

This example shows how to use the AWS SDK for Ruby to get statistics about 
Amazon SES. You can use this information to protect your reputation when emails 
are bounced or rejected. 


####Response elements

`SendDataPoints.member.N`
- *DataPoints* - A list of data points, each of which represents 15 minutes of activity.
- *Type* - An array of SendDataPoint objects.


### ses_list_emails/ses_list_emails.rb

This example explains how to use the AWS SDK for Ruby to list valid Amazon SES email
addresses.

####Response elements
`VerifiedEmailAddresses.member.N`
- *VerifiedEmailAddresses* - A list of email addresses that have already been verified.
- *Type*  - An array of strings.



### ses_send_email/ses_send_email.rb

This example shows how to use the AWS SDK for Ruby to send a message to an Amazon
SES email address. 

####Response Elements
`MessageId`
- The unique message identifier returned from the `SendEmail` action.
- *Type* -  String.


### ses_send_verification/ses_send_verification.rb

This example provides instruction on how to use the AWS SDK for Ruby to verify an
Amazon SES email address.

####Response Elements
`MessageId`
- The unique message identifier returned from the `SendEmail` action.
- *Type* String


## Testing the Amazon SES Ruby files
You can test the Amazon SES Ruby code examples by running RSpec 4.0, which is located in the ses/spec directory.

You can execute the RSpec tests from a Ruby IDE, such as RubyMine, or from the command
line using a command line console. There is a green dot for every passing spec, and one
red `F` for every failing test. For example, the following message informs you that a 
test has passed.

    Finished in 0.00217 seconds (files took 0.13631 seconds to load)
    3 examples, 0 failures
    $ echo $?
    0
    $ 
    
### Command line 
To execute RSpec tests from the command line, you can save the example files into a 
directory on your local machine, `cd` into the directory in which it is saved, and 
type `rspec` followed by the name of the spec file.

     $ rspec ses_get_statistics_spec.rb
     .
        
### Additional information
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  "AWS Regional Table" on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0