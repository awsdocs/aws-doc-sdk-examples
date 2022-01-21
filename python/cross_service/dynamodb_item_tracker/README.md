## Set up restricted profile

Best practice is to grant this application only the permissions it needs. To do this,
follow these steps:

1. Open AWS Identity and Access Management (IAM) in the 
[AWS Management Console](console.aws.amazon.com/iam). 
1. Select **Users**.
1. Search for the user created by the setup script. You can find the UserName in the 
outputs from the setup script. The default value is `doc-example-work-item-tracker-user`.
1. Select the user in the console.
1. Select the **Security credentials** tab.
1. Select **Create access key**.
1. A **Create access key** window appears. Select **Download .csv file**.
1. Edit the .csv file to add a "User Name" heading and value. Your .csv file will look
something like this:
   ```
   User Name,Access key ID,Secret access key
   item_tracker,AIDACKCEVSQ6C2EXAMPLE,wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   ```
1. Use the AWS CLI to import the access keys to your credentials file:
   ```
   aws configure import --csv file://doc-example-work-item-tracker-user_accessKeys.csv
   ```
1. This creates an `item_tracker` profile in your AWS credentials file that contains
the access keys for the user.
1. You can find the entry for this profile in your `~/.aws/credentials` file.
1. Use the AWS CLI to create an assume role profile. The `role_arn` is the RoleArn
value output from the setup script and the `source_profile` is the user profile created 
in the previous steps.
   ```
   aws configure set role_arn arn:aws:iam::111122223333:role/doc-example-work-item-tracker-role --profile item_tracker_role
   aws configure set source_profile item_tracker --profile item_tracker_role
   aws configure set region YOUR-AWS-REGION --profile item_tracker_role
   ``` 
1. You can find the entry for this profile in your `~/.aws/config` file.
1. Set an 'ITEM_TRACKER_PROFILE' environment variable with the name of the role profile.
   Windows
   ```
   set ITEM_TRACKER_PROFILE=item_tracker_role
   ```  
   Linux
   ```
   export ITEM_TRACKER_PROFILE=item_tracker_role
   ```
1. Now when you run the example, it configures Boto3 to use the `item_tracker_role` 
profile. This causes Boto3 to use the source `item_tracker` profile credentials to 
assume the role specified by the role ARN. Because the role has restricted permissions, 
the application is prevented from making AWS requests other than those granted by the 
role.
