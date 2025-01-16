# Customize Amazon Cognito authentication behavior with Lambda functions Scenario - Technical specification

This document contains the technical specifications for 
_Customize Amazon Cognito authentication behavior with Lambda functions_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Metadata](#metadata)

## Reference implementation

The implementation in [gov2/workflows/user_pools_and_lambda_triggers](../../../gov2/workflows/user_pools_and_lambda_triggers)
can be used as a reference for writing this example in other languages. 

## Resources and User Input

### Create AWS resources

The `PoolsAndTriggersBase` CDK construct in the  
[reference implementation for Go](../../../gov2/workflows/user_pools_and_lambda_triggers/.cdk/lib/pools-and-triggers-base.ts) 
folder creates all the common AWS resources you need for this example:

* Amazon DynamoDB table with a `UserEmail` primary key.
* Amazon Cognito user pool and client app.
* AWS Identity and Access Management (IAM) role that grants permission to write to CloudWatch Logs
  and to the Amazon DynamoDB table.

This construct is intended as a base script that you copy to your implementation and include in a stack that 
deploys Lambda handlers in the way that works best for your language. 
For an example, see the [reference implementation for Go](../../../gov2/workflows/user_pools_and_lambda_triggers/.cdk/lib/pools-and-triggers-stack.ts).

Because most languages require a bootstrap environment that contains an S3 bucket for staging Lambda handlers,
it's recommended that you use `cdk deploy` to deploy resources, but if your language supports it you can use 
CloudFormation directly.

The outputs from the stack can be read by your implementation to get things like the name of the
Amazon DynamoDB table, the user pool ID, and the client ID. Start each run by getting the outputs from
the stack using `CloudFormation.GetOutputs`, similar to the 
[reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/actions/cloud_formation_actions.go).

### Lambda handlers

Each scenario uses a specific Lambda handler to accomplish its goal. Write these in your language and
deploy them along with common resources with a CDK script.

#### Handler: automatically confirm known users

This handler is triggered when a user signs up. It looks up the user by their email name 
in the known users table and returns event data to Amazon Cognito that indicates the user 
should be confirmed and verified.

Your handler must perform the following steps:

1. Verify the `event.TriggerSource` is `PreSignUp_SignUp`. Some variations of this trigger
  ignore the returned event values.
2. Get the `email` value from `event.Request.UserAttributes`.
3. Look up the user's email in the known user table with `DynamoDB.GetItem`.
4. If the user is found and the `UserName` in the table matches the `event.UserName`, return an
   event that confirms and verifies the user.
   ```
   event.Response.AutoConfirmUser = true
   event.Response.AutoVerifyEmail = true
   ```
5. Otherwise, return an empty event to indicate that Amazon Cognito should follow its default flow.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/handlers/auto_confirm/auto_confirm_handler.go).

#### Handler: automatically migrate known users

This handler is triggered when a user tries to sign and is not in the user pool. It looks up the 
user by `UserName` and returns event data that verifies the user's email and specifies the user 
must reset their password on next sign in.

Your handler must perform the following steps:

1. Verify the `event.TriggerSource` is `UserMigration_Authentication`.
2. Look up the user by `event.UserName` in the known user table by using `DynamoDB.Scan`.
3. If the user is found, return an event that verifies the email, requires a password reset, and
   suppresses a welcome email.
   ```
	event.CognitoEventUserPoolsMigrateUserResponse.UserAttributes = map[string]string{
		"email":          user.UserEmail,
		"email_verified": "true", // email_verified is required for the forgot password flow.
	}
	event.CognitoEventUserPoolsMigrateUserResponse.FinalUserStatus = "RESET_REQUIRED"
	event.CognitoEventUserPoolsMigrateUserResponse.MessageAction = "SUPPRESS"
   ```
5. Otherwise, return an empty event to indicate that Amazon Cognito should follow its default flow.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/handlers/migrate_user/migrate_user_handler.go).

#### Handler: Write custom activity after authentication

This handler is triggered when a user is authenticated. It writes custom data to CloudWatch Logs and to the 
Amazon DynamoDB table.

Your handler must perform the following steps:

1. Write custom data to CloudWatch Logs:
   ```
   user := UserInfo{
     UserName:  event.UserName,
     UserEmail: event.Request.UserAttributes["email"],
     LastLogin: LoginInfo{
       UserPoolId: event.UserPoolID,
       ClientId:   event.CallerContext.ClientID,
       Time:       time.Now().Format(time.UnixDate), 
     },
   }
   ```
2. Add custom Login Info (user pool ID, client ID, and current time) to the user's record in the
   known user table by using `DynamoDB.PutItem`.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/handlers/activity_log/activity_log_handler.go).

### Interactive runs

Each scenario is a short, interactive walkthrough that demonstrates a specific kind of Lambda trigger.

#### Common set up: populate known users table

The known users table created by the CDK does not contain any data. Start by filling it with a few users,
according to the schema:

```
UserName: string
UserEmail: string
```

For an example, see `PopulateTable` in the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/actions/dynamo_actions.go).

#### Automatically confirm known users

This scenario signs up with a user name and email that is known to be in the known users table.
Because of this, the user is automatically confirmed and the email is verified so the user can sign in
without any additional action.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/workflows/scenario_auto_confirm_trusted_accounts.go).

Start by updating the user pool to invoke the Lambda handler by using `Cognito.UpdateUserPool`.
Let the user choose a known user from the known users table and enter a password.

Example output:

```
Welcome
----------------------------------------------------------------------------------------
First, let's add some users to the DynamoDB doc-example-custom-users table we'll use for this example.
Let's add a Lambda function to handle the PreSignUp trigger from Cognito.
This trigger happens when a user signs up, and lets your function take action before the main Cognito
sign up processing occurs.
Lambda function arn:aws:lambda:us-west-2:123456789012:function:PoolsAndTriggersStackForG-autoConfirmHandlerA482D8-mUBrYP59ZYlG added to user pool us-west-2_VvM3LlJ3D to handle the PreSignUp trigger.
Let's sign up a user to your Cognito user pool. When the user's email matches an email in the
DynamoDB known users table, it is automatically verified and the user is confirmed.
Which user do you want to use?
        1. test_user_1
        2. test_user_2
        3. test_user_3
Enter a choice between 1 and 3:
2
Enter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.
(the password will not display as you type):
```

Sign the user up using `Cognito.SignUp`. Wait 10 seconds to let the flow process and CloudWatch Logs
to settle. Get the most recent 10 items from the log stream for the Lambda handler and display them.
   
Example output:

```
Signing up user 'test_user_2' with email 'test_email_2@example.com' to Cognito.
User test_user_2 signed up, confirmed = true.
----------------------------------------------------------------------------------------
Waiting a few seconds to let Lambda write to CloudWatch Logs...
Okay, let's check the logs to find what's happened recently with your Lambda function.
Getting some recent events from log stream 2024/04/23/[$LATEST]476d0d1c971149c4b55d9185bEXAMPLE
        INIT_START Runtime Version: provided:al2.v34    Runtime Version ARN: arn:aws:lambda:us-west-2::runtime:63360369c057cd35ff35212a9d6c8e4a1d1f073ffb62e7dc1fc04e025EXAMPLE
        START RequestId: 9f9dd892-6978-4daa-a833-78a34c5d5c60 Version: $LATEST
        2024/04/23 22:59:15 Received presignup from PreSignUp_SignUp for user 'test_user_2'
        2024/04/23 22:59:15 Looking up email test_email_2@example.com in table doc-example-custom-users.
        2024/04/23 22:59:16 UserEmail test_email_2@example.com found with matching UserName test_user_2. User is confirmed.
        END RequestId: 9f9dd892-6978-4daa-a833-78a34c5d5c60
        REPORT RequestId: 9f9dd892-6978-4daa-a833-78a34c5d5c60  Duration: 636.04 ms     Billed Duration: 715 ms Memory Size: 128 MB     Max Memory Used: 27 MB  Init Duration: 78.33 ms
----------------------------------------------------------------------------------------
Press Enter when you're ready to continue.
```

Sign in as the user to show that they are, in fact, confirmed and verified. Then clean up.

Example output:

```
Let's sign in as test_user_2...
Successfully signed in. Your access token starts with: abcdefghijk...
----------------------------------------------------------------------------------------
Do you want to remove all of the AWS resources that were created during this demo (y/n)?
y
Deleted user.
Removed Cognito triggers from user pool.
----------------------------------------------------------------------------------------
Thanks for watching!
----------------------------------------------------------------------------------------
```

#### Automatically migrate known users

This scenario migrates a user name and email that is known to be in the known users table.
Because of this, the email is automatically verified and the forgot password flow is started
to confirm the user.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/workflows/scenario_migrate_user.go).

Start by updating the user pool to invoke the Lambda handler by using `Cognito.UpdateUserPool`.
Let the user enter a user name and an email they own. Add this user to the known users table.
Sign in as the user by using `Cognito.InitiateAuth`.
Amazon Cognito raises a `PasswordResetRequiredException` exception. Catch this exception.

Example output:

```
----------------------------------------------------------------------------------------
Welcome
----------------------------------------------------------------------------------------
Let's add a Lambda function to handle the MigrateUser trigger from Cognito.
This trigger happens when an unknown user signs in, and lets your function take action before Cognito
rejects the user.

Lambda function arn:aws:lambda:us-west-2:123456789012:function:PoolsAndTriggersStackForG-migrateUserHandler78F744-pXAPGscCQ8wD added to user pool us-west-2_VvM3LlJ3D to handle the MigrateUser trigger.
----------------------------------------------------------------------------------------
Let's sign in a user to your Cognito user pool. When the username and email matches an entry in the
DynamoDB known users table, the email is automatically verified and the user is migrated to the Cognito user pool.

Enter a username:
chester_tester

Enter an email that you own. This email will be used to confirm user migration
during this example:
your_alias@amazon.com
Adding user 'chester_tester' with email 'your_alias@amazon.com' to the DynamoDB known users table...
Signing in to Cognito as user 'chester_tester'. The expected result is a PasswordResetRequiredException.

Password reset required for the user

User 'chester_tester' is not in the Cognito user pool but was found in the DynamoDB known users table.
User migration is started and a password reset is required.
```

Wait 10 seconds for CloudWatch Logs to settle, then get the 10 most recent events from the log stream
for the Lambda handler and display these.

Example output:

```
----------------------------------------------------------------------------------------
Waiting a few seconds to let Lambda write to CloudWatch Logs...
Okay, let's check the logs to find what's happened recently with your Lambda function.
Getting some recent events from log stream 2024/04/23/[$LATEST]65876bc69da348a491a5eac6e03554a5
        INIT_START Runtime Version: provided:al2.v34    Runtime Version ARN: arn:aws:lambda:us-west-2::runtime:63360369c057cd35ff35212a9d6c8e4a1d1f073ffb62e7dc1fc04e0254062a12
        START RequestId: 6197d722-9a3f-44f6-bf51-88212c9329b1 Version: $LATEST
        2024/04/23 23:08:49 Received migrate trigger from UserMigration_Authentication for user 'chester_tester'
        2024/04/23 23:08:49 Looking up user 'chester_tester' in table doc-example-custom-users.
        2024/04/23 23:08:50 UserName 'chester_tester' found with email your_alias@amazon.com. User is migrated and must reset password.
        END RequestId: 6197d722-9a3f-44f6-bf51-88212c9329b1
        REPORT RequestId: 6197d722-9a3f-44f6-bf51-88212c9329b1  Duration: 572.32 ms     Billed Duration: 654 ms Memory Size: 128 MB     Max Memory Used: 27 MB  Init Duration: 80.69 ms
```

Start the forgot password flow by using `Cognito.ForgotPassword`. This sends a confirmation code to the
specified email. Have the user enter the confirmation code and a password, then confirm by using
`Cognito.ConfirmForgotPassword`. Sign in with `Cognito.InitiateAuth` to show the user is
now able to sign in, and end by cleaning up resources.

```
In order to migrate the user to Cognito, you must be able to receive a confirmation
code by email at your_alias@amazon.com. Do you want to send a code (y/n)?
y

A confirmation code has been sent to y***@a***.
Check your email and enter it here:
123456

Enter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.
(the password will not display as you type):

Confirming password reset for user 'chester_tester'.
User 'chester_tester' successfully confirmed and migrated.
Signing in with your username and password...
Successfully signed in. Your access token starts with: abcdefghijk...
----------------------------------------------------------------------------------------
Do you want to remove all of the AWS resources that were created during this demo (y/n)?
y
Deleted user.
Removed Cognito triggers from user pool.
----------------------------------------------------------------------------------------
Thanks for watching!
----------------------------------------------------------------------------------------
```

#### Write custom activity after authentication

This scenario writes custom activity data to CloudWatch Logs and to the Amazon DynamoDB table.

See the [reference implementation](../../../gov2/workflows/user_pools_and_lambda_triggers/workflows/scenario_activity_log.go).

Start by updating the user pool to invoke the Lambda handler by using `Cognito.UpdateUserPool`.
Pick a user from the known users table and add that user to the user pool by using `Cognito.AdminCreateUser`. 
Get a password from the user and set it by using `Cognito.AdminSetUserPassword`. 

Example output:

```
----------------------------------------------------------------------------------------
Welcome
----------------------------------------------------------------------------------------
First, let's add some users to the DynamoDB doc-example-custom-users table we'll use for this example.
To facilitate this example, let's add a user to the user pool using administrator privileges.
Adding known user test_user_1 to the user pool.

Enter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.
(the password will not display as you type):

Setting password for user 'test_user_1'.
----------------------------------------------------------------------------------------
Let's add a Lambda function to handle the PostAuthentication trigger from Cognito.
This trigger happens after a user is authenticated, and lets your function take action, such as logging
the outcome.
Lambda function arn:aws:lambda:us-west-2:123456789012:function:PoolsAndTriggersStackForG-activityLogHandler873BF2-bJewBJKMbGZJ added to user pool us-west-2_VvM3LlJ3D to handle PostAuthentication Cognito trigger.
----------------------------------------------------------------------------------------
```

Sign the user in, wait 10 seconds, then get and display CloudWatch Logs events.

```
Now we'll sign in user test_user_1 and check the results in the logs and the DynamoDB table.
Press Enter when you're ready.

Sign in successful. The PostAuthentication Lambda handler writes custom information to CloudWatch Logs.
Waiting a few seconds to let Lambda write to CloudWatch Logs...
Okay, let's check the logs to find what's happened recently with your Lambda function.
Getting some recent events from log stream 2024/04/23/[$LATEST]ce8311ca5d684ac69d2471c065fd20d5
        INIT_START Runtime Version: provided:al2.v34    Runtime Version ARN: arn:aws:lambda:us-west-2::runtime:63360369c057cd35ff35212a9d6c8e4a1d1f073ffb62e7dc1fc04e0254062a12
        START RequestId: d7ccb502-1d70-4354-9149-b6e4410efa84 Version: $LATEST
        2024/04/23 23:23:30 Received post authentication trigger from PostAuthentication_Authentication for user 'test_user_1'
        main.UserInfo{UserName:"test_user_1", UserEmail:"test_email_1@example.com", LastLogin:main.LoginInfo{UserPoolId:"us-west-2_VvM3LlJ3D", ClientId:"28er3kak4842k2qvttqEXAMPLE", Time:"Tue Apr 23 23:23:30 UTC 2024"}}2024/04/23 23:23:30 Wrote user info to DynamoDB table doc-example-custom-users.
        END RequestId: d7ccb502-1d70-4354-9149-b6e4410efa84
        REPORT RequestId: d7ccb502-1d70-4354-9149-b6e4410efa84  Duration: 607.64 ms     Billed Duration: 691 ms Memory Size: 128 MB     Max Memory Used: 27 MB  Init Duration: 82.93 ms
```

Get the user from the Amazon DynamoDB table and display the last login info written by the handler.
End by cleaning up resources.

```
The PostAuthentication handler also writes login data to the DynamoDB table.
Press Enter when you're ready to continue.

The last login info for the user in the known users table is:
        {UserPoolId:us-west-2_VvM3LlJ3D ClientId:28er3kak4842k2qvttqEXAMPLE Time:Tue Apr 23 23:23:30 UTC 2024}
----------------------------------------------------------------------------------------
Do you want to remove all of the AWS resources that were created during this demo (y/n)?
y
Deleted user.
Removed Cognito triggers from user pool.
----------------------------------------------------------------------------------------
Thanks for watching!
----------------------------------------------------------------------------------------
```

---

## Metadata

| action / scenario             | metadata file                           | metadata key                                         |
|-------------------------------|-----------------------------------------|------------------------------------------------------|
| `DescribeUserPool`            | cognito-identity-provider_metadata.yaml | cognito-identity-provider_DescribeUserPool           |
| `UpdateUserPool`              | cognito-identity-provider_metadata.yaml | cognito-identity-provider_UpdateUserPool             |
| `SignUp`                      | cognito-identity-provider_metadata.yaml | cognito-identity-provider_SignUp                     |
| `InitiateAuth`                | cognito-identity-provider_metadata.yaml | cognito-identity-provider_InitiateAuth               |
| `ForgotPassword`              | cognito-identity-provider_metadata.yaml | cognito-identity-provider_ForgotPassword             |
| `ConfirmForgotPassword`       | cognito-identity-provider_metadata.yaml | cognito-identity-provider_ConfirmForgotPassword      |
| `AdminCreateUser`             | cognito-identity-provider_metadata.yaml | cognito-identity-provider_AdminCreateUser            |
| `AdminSetUserPassword`        | cognito-identity-provider_metadata.yaml | cognito-identity-provider_AdminSetUserPassword       |
| `DeleteUser`                  | cognito-identity-provider_metadata.yaml | cognito-identity-provider_DeleteUser                 |
| `Automatically confirm users` | cognito-identity-provider_metadata.yaml | cognito-identity-provider_Scenario_AutoConfirmUsers  |
| `Automatically migrate users` | cognito-identity-provider_metadata.yaml | cognito-identity-provider_Scenario_AutoMigrateUsers  |
| `Write custom activity log`   | cognito-identity-provider_metadata.yaml | cognito-identity-provider_Scenario_CustomActivityLog |

