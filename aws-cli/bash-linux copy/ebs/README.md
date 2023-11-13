# Discover EBS volumes type GP2 and modify them to type GP3 with/without snapshot

This example demonstrates how to modify EBS volume type GP2 to GP3 across a given region or all regions where customer has workload. It can auto scan all GP2 volumes or read the volume_ids input from any .text file in case modification needed only on user provided volumes.
Elastic volumes supports online modification of EBS volume type GP2 to GP3, it does not bring any outage to instance. 

Files

    gp2_gp3_migration.sh - main script to perform modification
    gp2_gp3_migration_progress.sh - side script to track progress of modification triggered by main script
   

Purpose

The main script file contains the several function that perform the following tasks based on parameters used with the script :

   - Discover and create list of all GP2, GP3, io1, io2 volumes in any single region or all regions.

   - Modify listed GP2 volumes from a file with or without snapshot.

   - Discover all GP2 volumes in a region and modify all of them with or without snapshot.

   - Discover all GP2 volumes in all regions and modify all of them with or without snapshot.

   - Perform any of the above action on cross accounts where AWS configuration profile, IAM role and permissions are already setup.

Prerequisites

- An Amazon Web Services (AWS) account.
- AWS CLI setup - [Install AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/installing.html)
- IAM policy/role that allows to perform volume discovery, modification and snapshot creation - Refer: IAM_permission.txt).
- Configuration profile for cross account access - refer second part of this README 'Scale the execution across accounts'


Script execution

- To create list of all GP2, GP3, io1, io2 volumes in any single region or all regions ::  gp2_gp3_migration.sh --region <region_name>/all discover no-snapshot

- To migrate listed GP2 volumes from a file w/o snapshot ::  gp2_gp3_migration.sh --region <region_name> <volume_list_file.txt> no-snapshot

- To migrate listed GP2 volumes from a file with snapshot ::  gp2_gp3_migration.sh --region <region_name> <volume_list_file.txt> snapshot

- To migrate all GP2 volumes in a region w/o snapshot ::  gp2_gp3_migration.sh --region <region_name> migrate no-snapshot

- To migrate all GP2 volumes in a region with snapshot ::  gp2_gp3_migration.sh --region <region_name> migrate snapshot

- To migrate all GP2 volumes across all regions w/o snapshot ::  gp2_gp3_migration.sh --region all migrate no-snapshot

- To migrate all GP2 volumes across all regions with snapshot ::  gp2_gp3_migration.sh --region all migrate snapshot

- To perform any of the above action on cross accounts where IAM role, permission and profile is already setup, add last 2 parameters --profile <profile_name>

- To track the progress of volume modification :: gp2_gp3_migration_progress.sh <gp2_vol_id.txt> <region_name>

Note - snapshot created by this script will have description: "Pre GP3 migration" and tag: key=state, value=pre-gp3


# Scale the execution across accounts 

We can scale the execution of this script across accounts by setting the configuration and credentials for all member's account and trigger assume role by selecting the account's profile from one executer account .
 

Install the AWS CLI if not already done preferably on an EC2 instance in any account that will trigger automation action on all member accounts. To set up your default CLI credentials, you should gather the AWS access key and secret key for your script runner user, create an IAM user and then run the aws configure command. You will be prompted for 4 inputs (replace the placeholder keys with your user’s keys).


    AWS access key ID [None]: <YOUR_AWS_ACCESS_KEY>
    AWS secret access key [None]: <YOUR_AWS_SECRET_KEY>
    Default region name [None]: us-west-1
    Default output format [None]: json

The AWS CLI organizes configuration and credentials into two separate files found in the home directory of your operating system. They are separated to isolate your credentials from the less sensitive configuration options of region and output.

    ~/.aws/config
    [default]
    region = us-west-1
    output = json
    Additional Information

    ~/.aws/credentials
    [default]
    aws_access_key_id = <YOUR_AWS_ACCESS_KEY>
    aws_secret_access_key = <YOUR_AWS_SECRET_KEY>

As you can see, the CLI has created these two files and identified them with [default]. Now we will be using the CLI’s ability to assume a role as per named profiles of all member accounts. 

Reference doc : [Configuration and Credential File Settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html)

Setup -

-- Assuming a user created in above step in executer account is  ec2-ebs-modify-user, make note of its arn that will be used in next steps.

-- Now create an IAM role in all member accounts with a trust policy to allow  sts:AssumeRole action to be assumed by our user ec2-ebs-modify-user from executer account. 

-- This IAM role must have the following IAM policy to allow certain permissions in order to achieve the volume modification. 

IAM Role : cross-account-modify-vol

    {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "ec2:ModifyVolume",
                "ec2:DescribeVolumes",
                "ec2:DescribeVolumesModifications",
                "ec2:DescribeVolumeStatus",
                "ec2:DescribeTags",
                "ec2:CreateTags",
                "ec2:DescribeRegions",
                "ec2:CreateVolume",
                "ec2:DescribeSnapshots",
                "ec2:CreateSnapshot"
            ],
            "Resource": "*"
        }
    ]
    }


IAM policy : ec2-ebs-modify-policy

      {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::999999999999:user/ec2-ebs-modify-user"
            },
            "Action": "sts:AssumeRole"
        }
    ]
    }
Trust relationship : Pls update the highlighted placeholder as per you user arn from mgmt account.

-- Make note of IAM roles created in member's accounts.

-- Now add/update the IAM policy of IAM user "ec2-ebs-modify-user" in executer account where we will allow our user to assume the roles created in memeber's accounts.

    {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": "sts:AssumeRole",
            "Resource": [
                "arn:aws:iam::333333333333:role/cross-account-modify-vol",
                "arn:aws:iam::444444444444:role/cross-account-modify-vol",
                "arn:aws:iam::555555555555:role/cross-account-modify-vol",
            ]
        }
    ]
    }

-- Setup profile for all your member accounts -

    aws configure set profile.333333333333.role_arn arn:aws:iam::333333333333role/cross-account-modify-vol

    aws configure set profile.333333333333.source_profile default

    aws configure set profile.444444444444.role_arn arn:aws:iam::444444444444:role/cross-account-modify-vol

    aws configure set profile.444444444444.source_profile default

    aws configure set profile.555555555555.role_arn arn:aws:iam::5555555555555:role/cross-account-modify-vol

    aws configure set profile.555555555555.source_profile default

--  Create a list of account numbers to be used in our script, assuming all account numbers are listed in account_list
    
--  Now call the script as per you use case with for loop to execute on all accounts listed in our account_list based on the profile setup.

Sample commands :

       for a in `cat account_list`; do /home/gp2_gp3_migration.sh --region all discover no-snapshot --profile $a;  done 
       for a in `cat account_list`; do /home/gp2_gp3_migration.sh --region all migrate snapshot --profile $a;  done 
       bash gp2_gp3_migration.sh --region all migrate no-snapshot --profile <account no / profile name as per your setup>
     


As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more information, see Grant Least Privilege in the AWS Identity and Access Management (IAM) User Guide.
    This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see Service Endpoints and Quotas in the AWS General Reference Guide.
    Running this code can result in charges to your AWS account. It's your responsibility to ensure that any resources created by this script are removed when you are done with them.
