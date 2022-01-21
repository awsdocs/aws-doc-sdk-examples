## Deploy the example

1. Run `setup.py` with the `deploy` flag to create and deploy a CloudFormation stack 
that manages creation of a Amazon DynamoDB table and an IAM role that grants limited 
permissions to the table and to Amazon SES.
   ```
   python setup.py deploy
   ```
1. The setup script outputs the name of the table and the Amazon Resource Name (ARN)
of the role. If you want to run app with restricted permissions, take the following
steps:
    1. Use the AWS CLI to create an assume role profile. In the following commands,
    replace the value of `role_arn` with the RoleArn from the setup script output, 
    replace the value of `source_profile` with the user profile you use to connect to 
    AWS from Boto3 (the default is `default`), and replace the value of `region` with
    your AWS Region.
       ```
       aws configure set role_arn arn:aws:iam::111122223333:role/doc-example-work-item-tracker-role --profile item_tracker_role
       aws configure set source_profile default --profile item_tracker_role
       aws configure set region YOUR-AWS-REGION --profile item_tracker_role
       ``` 
    1. You can find the entry for this profile in your `~/.aws/config` file under the
    heading `[profile item_tracker_role]`. Or verify it with the AWS CLI.
       ```
       aws configure list --profile item_tracker_role
       ```
    1. Add an 'ITEM_TRACKER_PROFILE' value to the `config.py` file created by the setup
    script.
       ```
       ITEM_TRACKER_PROFILE = 'item_tracker_role'
       ```  

**Note:** The example can be run with default permissions or with restricted 
permissions. When you run the app, it checks the `ITEM_TRACKER_PROFILE` value in 
`config.py`. If this entry exists, Boto3 is configured to use that profile to assume the 
specified role. The result is that Boto3 is granted restricted permissions instead of
the default user permissions, and the app is only able to make AWS requests that are 
allowed by the role. 

## Run the example

This example uses Flask to host a local web server, so running the app requires a
browser as well as the command line.

1. Run the app at the a command prompt to start the Flask web server.
   ```
   python app.py
   ```
   Among other things, the app will log whether it is using default credentials or 
   restricted credentials and the URL where the app is hosted.
   ```
   INFO: Using credentials from restricted profile item_tracker_role.
   INFO:  * Running on http://127.0.0.1:5000/
   ```
1. Start a web browser and browse to the app URL. 