#  Amazon DynamoDB work item tracker web application

## Overview

|                   |             |
| -----------       | ----------- |
| Synopsis          | Shows how to create a web application that tracks work items and sends email reports. |
| SDK               | AWS SDK for Python (Boto3) |
| AWS services      | Amazon DynamoDB, Amazon Simple Email Service (Amazon SES) |
| Audience          |  Developer |
| Updated           | 1/21/2022 |
| Required skills   | Python, Flask |

## Purpose

Stuff.

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
 
### Deploy resources

1. Run `setup.py` with the `deploy` flag to create and deploy a CloudFormation stack 
that manages creation of an Amazon DynamoDB table and an IAM role that grants limited 
permissions to read from and write to the table and to send email with Amazon SES.
   ```
   python setup.py deploy
   ```
   The setup script outputs the name of the table and the Amazon Resource Name (ARN)
of the role.
1. (Optional) If you want to run the app with restricted permissions instead of the
default user permissions, take the following steps:
    1. Use the [AWS Command Line Interface (AWS CLI)](https://docs.aws.amazon.com/cli/) 
    to create an assume role profile. In the following commands,
    replace the value of `role_arn` with the RoleArn from the setup script output, 
    replace the value of `source_profile` with the user profile you used to run the 
    setup script (the default is `default`), and replace the value of `region` with
    your AWS Region.
       ```
       aws configure set role_arn arn:aws:iam::111122223333:role/doc-example-work-item-tracker-role --profile item_tracker_role
       aws configure set source_profile default --profile item_tracker_role
       aws configure set region YOUR-AWS-REGION --profile item_tracker_role
       ``` 
    1. You can find the entry for this profile in your `~/.aws/config` file under the
    heading `[profile item_tracker_role]`. Or you can verify it with the AWS CLI.
       ```
       aws configure list --profile item_tracker_role
       ```
    1. Add an 'ITEM_TRACKER_PROFILE' value to the `config.py` file created by the setup
    script.
       ```
       ITEM_TRACKER_PROFILE = 'item_tracker_role'
       ```  
1. To email reports from the app, you must register at least one email address.
In a browser, navigate to the [Amazon SES console](https://console.aws.amazon.com/ses/).
1. If necessary, select your AWS Region.
1. Select **Verified identities**.
1. Select **Create identity**.
1. Select **Email address**.
1. Enter an email address you own.
1. Select **Create identity**.
1. You will receive an email from Amazon Web Services that contains instructions on how
to verify the email with Amazon SES. Follow the instructions in the email to complete
verification.

**Note:** The example can be run with default permissions or with restricted 
permissions. When you run the app, it checks the `ITEM_TRACKER_PROFILE` value in 
`config.py`. If this entry exists, Boto3 is configured to use that profile to assume the 
specified role. The result is that Boto3 is granted restricted permissions instead of
the default user permissions, and the app is only able to make AWS requests that are 
allowed by the role.

#### Manual deployment

If you prefer, you can deploy the resources for the application by using the 
[AWS Management Console](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/learn-whats-new.html).
To deploy with the console, take the following steps:

1. Open the console in your browser.
1. Navigate to Amazon DynamoDB and create a table named `doc-example-work-item-tracker`
with a partition key of String type named `item_id`.

### Run the app

This example uses [Flask](https://flask.palletsprojects.com/en/2.0.x/) to host a local 
web server. With the web server running you can browse to the app endpoint and use
the app to add and remove work items and send email reports.

1. Run the app at a command prompt to start the Flask web server.
   ```
   python app.py
   ```
   When the app starts, it logs whether it is using default credentials or 
   restricted credentials along with the URL where the app is hosted.
   ```
   INFO: Using credentials from restricted profile item_tracker_role.
   INFO:  * Running on http://127.0.0.1:5000/
   ```
1. Start a web browser and browse to the [app URL](http://127.0.0.1:5000/).
1. At first, the table is empty.
    ![Work item tracker](images/item-tracker-start.png)
1. Select **Add item**, fill in the values, and select **Add** to add an item.
    ![Add item](images/item-tracker-add-item.png)
1. After you've added items, they're displayed in the table.
    ![Work item tracker with items](images/item-tracker-all-items.png)
1. Select a filter, such as **Archived**, and select **Filter** to get and display
only items with the specified status.
    ![Work item tracker Archived items](images/item-tracker-archived-items.png)

### Destroy