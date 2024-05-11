---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  Write a resources struct that tracks any AWS resources created during the ObjectLockScenario run and deletes them at the end.
  <example>
  // Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    // SPDX-License-Identifier: Apache-2.0
    
      package workflows
    
      import (
      "errors"
      "log"
      "strings"
      "user_pools_and_lambda_triggers/actions"
    
      "github.com/aws/aws-sdk-go-v2/aws"
      "github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
      "github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
      "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
      )
    
      // snippet-start:[gov2.workflows.PoolsAndTriggers.ActivityLog]
    
      // ActivityLog separates the steps of this scenario into individual functions so that
      // they are simpler to read and understand.
      type ActivityLog struct {
      helper       IScenarioHelper
      questioner   demotools.IQuestioner
      resources    Resources
      cognitoActor *actions.CognitoActions
    }
    
      // NewActivityLog constructs a new activity log runner.
      func NewActivityLog(sdkConfig aws.Config, questioner demotools.IQuestioner, helper IScenarioHelper) ActivityLog {
      scenario := ActivityLog{
    helper:       helper,
    questioner:   questioner,
    resources:    Resources{},
    cognitoActor: &actions.CognitoActions{CognitoClient: cognitoidentityprovider.NewFromConfig(sdkConfig)},
    }
      scenario.resources.init(scenario.cognitoActor, questioner)
      return scenario
    }
    
      // AddUserToPool selects a user from the known users table and uses administrator credentials to add the user to the user pool.
      func (runner *ActivityLog) AddUserToPool(userPoolId string, tableName string) (string, string) {
      log.Println("To facilitate this example, let's add a user to the user pool using administrator privileges.")
      users, err := runner.helper.GetKnownUsers(tableName)
      if err != nil {
      panic(err)
    }
      user := users.Users[0]
      log.Printf("Adding known user %v to the user pool.\n", user.UserName)
      err = runner.cognitoActor.AdminCreateUser(userPoolId, user.UserName, user.UserEmail)
      if err != nil {
      panic(err)
    }
      pwSet := false
      password := runner.questioner.AskPassword("\nEnter a password that has at least eight characters, uppercase, lowercase, numbers and symbols.\n"+
      "(the password will not display as you type):", 8)
      for !pwSet {
      log.Printf("\nSetting password for user '%v'.\n", user.UserName)
      err = runner.cognitoActor.AdminSetUserPassword(userPoolId, user.UserName, password)
      if err != nil {
      var invalidPassword *types.InvalidPasswordException
      if errors.As(err, &invalidPassword) {
      password = runner.questioner.AskPassword("\nEnter another password:", 8)
    } else {
      panic(err)
    }
    } else {
      pwSet = true
    }
    }
    
      log.Println(strings.Repeat("-", 88))
    
      return user.UserName, password
    }
    
      // AddActivityLogTrigger adds a Lambda handler as an invocation target for the PostAuthentication trigger.
      func (runner *ActivityLog) AddActivityLogTrigger(userPoolId string, activityLogArn string) {
      log.Println("Let's add a Lambda function to handle the PostAuthentication trigger from Cognito.\n" +
      "This trigger happens after a user is authenticated, and lets your function take action, such as logging\n" +
      "the outcome.")
      err := runner.cognitoActor.UpdateTriggers(
      userPoolId,
    actions.TriggerInfo{Trigger: actions.PostAuthentication, HandlerArn: aws.String(activityLogArn)})
      if err != nil {
      panic(err)
    }
      runner.resources.triggers = append(runner.resources.triggers, actions.PostAuthentication)
      log.Printf("Lambda function %v added to user pool %v to handle PostAuthentication Cognito trigger.\n",
      activityLogArn, userPoolId)
    
      log.Println(strings.Repeat("-", 88))
    }
    
      // SignInUser signs in as the specified user.
      func (runner *ActivityLog) SignInUser(clientId string, userName string, password string) {
      log.Printf("Now we'll sign in user %v and check the results in the logs and the DynamoDB table.", userName)
      runner.questioner.Ask("Press Enter when you're ready.")
      authResult, err := runner.cognitoActor.SignIn(clientId, userName, password)
      if err != nil {
      panic(err)
    }
      log.Println("Sign in successful.",
      "The PostAuthentication Lambda handler writes custom information to CloudWatch Logs.")
    
      runner.resources.userAccessTokens = append(runner.resources.userAccessTokens, *authResult.AccessToken)
    }
    
      // GetKnownUserLastLogin gets the login info for a user from the Amazon DynamoDB table and displays it.
      func (runner *ActivityLog) GetKnownUserLastLogin(tableName string, userName string) {
      log.Println("The PostAuthentication handler also writes login data to the DynamoDB table.")
      runner.questioner.Ask("Press Enter when you're ready to continue.")
      users, err := runner.helper.GetKnownUsers(tableName)
      if err != nil {
      panic(err)
    }
      for _, user := range users.Users {
      if user.UserName == userName {
      log.Println("The last login info for the user in the known users table is:")
      log.Printf("\t%+v", *user.LastLogin)
    }
    }
      log.Println(strings.Repeat("-", 88))
    }
    
      // Run runs the scenario.
      func (runner *ActivityLog) Run(stackName string) {
      defer func() {
      if r := recover(); r != nil {
      log.Println("Something went wrong with the demo.")
      runner.resources.Cleanup()
    }
    }()
    
      log.Println(strings.Repeat("-", 88))
      log.Printf("Welcome\n")
    
      log.Println(strings.Repeat("-", 88))
    
      stackOutputs, err := runner.helper.GetStackOutputs(stackName)
      if err != nil {
      panic(err)
    }
      runner.resources.userPoolId = stackOutputs["UserPoolId"]
      runner.helper.PopulateUserTable(stackOutputs["TableName"])
      userName, password := runner.AddUserToPool(stackOutputs["UserPoolId"], stackOutputs["TableName"])
    
      runner.AddActivityLogTrigger(stackOutputs["UserPoolId"], stackOutputs["ActivityLogFunctionArn"])
      runner.SignInUser(stackOutputs["UserPoolClientId"], userName, password)
      runner.helper.ListRecentLogEvents(stackOutputs["ActivityLogFunction"])
      runner.GetKnownUserLastLogin(stackOutputs["TableName"], userName)
    
      runner.resources.Cleanup()
    
      log.Println(strings.Repeat("-", 88))
      log.Println("Thanks for watching!")
      log.Println(strings.Repeat("-", 88))
    }
    
      // snippet-end:[gov2.workflows.PoolsAndTriggers.ActivityLog]
  </example>
  <example>
      // snippet-start:[gov2.cognito-identity-provider.Resources.complete]
    
      // Resources keeps track of AWS resources created during an example and handles
      // cleanup when the example finishes.
      type Resources struct {
      userPoolId       string
      userAccessTokens []string
      triggers         []actions.Trigger
    
      cognitoActor *actions.CognitoActions
      questioner   demotools.IQuestioner
    }
    
      func (resources *Resources) init(cognitoActor *actions.CognitoActions, questioner demotools.IQuestioner) {
      resources.userAccessTokens = []string{}
      resources.triggers = []actions.Trigger{}
      resources.cognitoActor = cognitoActor
      resources.questioner = questioner
    }
    
      // Cleanup deletes all AWS resources created during an example.
      func (resources *Resources) Cleanup() {
      defer func() {
      if r := recover(); r != nil {
      log.Printf("Something went wrong during cleanup.\n%v\n", r)
      log.Println("Use the AWS Management Console to remove any remaining resources \n" +
      "that were created for this scenario.")
    }
    }()
    
      wantDelete := resources.questioner.AskBool("Do you want to remove all of the AWS resources that were created "+
      "during this demo (y/n)?", "y")
      if wantDelete {
      for _, accessToken := range resources.userAccessTokens {
      err := resources.cognitoActor.DeleteUser(accessToken)
      if err != nil {
      log.Println("Couldn't delete user during cleanup.")
      panic(err)
    }
      log.Println("Deleted user.")
    }
      triggerList := make([]actions.TriggerInfo, len(resources.triggers))
      for i := 0; i < len(resources.triggers); i++ {
    triggerList[i] = actions.TriggerInfo{Trigger: resources.triggers[i], HandlerArn: nil}
    }
      err := resources.cognitoActor.UpdateTriggers(resources.userPoolId, triggerList...)
      if err != nil {
      log.Println("Couldn't update Cognito triggers during cleanup.")
      panic(err)
    }
      log.Println("Removed Cognito triggers from user pool.")
    } else {
      log.Println("Be sure to remove resources when you're done with them to avoid unexpected charges!")
    }
    }
    
      // snippet-end:[gov2.cognito-identity-provider.Resources.complete]
  </example>
---
Here's an implementation of the `Resources` struct for the `ObjectLockScenario` workflow:

```go
package workflows

import (
    "context"
    "log"

    "github.com/aws/aws-sdk-go-v2/service/s3"
    "github.com/aws/aws-sdk-go-v2/service/s3/types"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/actions"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// Resources keeps track of AWS resources created during the ObjectLockScenario and handles
// cleanup when the scenario finishes.
type Resources struct {
    nolockBucket     string
    lockBucket       string
    retentionBucket  string
    nolockObjects    []s3.Object
    lockObjects      []s3.Object
    retentionObjects []s3.Object

    s3Actions  *actions.S3Actions
    questioner demotools.IQuestioner
}

func (resources *Resources) init(s3Actions *actions.S3Actions, questioner demotools.IQuestioner) {
    resources.s3Actions = s3Actions
    resources.questioner = questioner
}

// Cleanup deletes all AWS resources created during the ObjectLockScenario.
func (resources *Resources) Cleanup() {
    defer func() {
        if r := recover(); r != nil {
            log.Printf("Something went wrong during cleanup.\n%v\n", r)
            log.Println("Use the AWS Management Console to remove any remaining resources " +
                "that were created for this scenario.")
        }
    }()

    wantDelete := resources.questioner.AskBool("Do you want to remove all of the AWS resources that were created "+
        "during this demo (y/n)?", "y")
    if !wantDelete {
        log.Println("Be sure to remove resources when you're done with them to avoid unexpected charges!")
        return
    }

    log.Println("Removing objects from S3 buckets...")
    resources.deleteObjects(resources.nolockBucket, resources.nolockObjects)
    resources.deleteObjects(resources.lockBucket, resources.lockObjects)
    resources.deleteRetentionObjects(resources.retentionBucket, resources.retentionObjects)

    log.Println("Deleting S3 buckets...")
    _, err := resources.s3Actions.S3Client.DeleteBucket(context.TODO(), &s3.DeleteBucketInput{
        Bucket: &resources.nolockBucket,
    })
    if err != nil {
        log.Printf("Error deleting bucket %s: %v", resources.nolockBucket, err)
    }

    _, err = resources.s3Actions.S3Client.DeleteBucket(context.TODO(), &s3.DeleteBucketInput{
        Bucket: &resources.lockBucket,
    })
    if err != nil {
        log.Printf("Error deleting bucket %s: %v", resources.lockBucket, err)
    }

    _, err = resources.s3Actions.S3Client.DeleteBucket(context.TODO(), &s3.DeleteBucketInput{
        Bucket: &resources.retentionBucket,
    })
    if err != nil {
        log.Printf("Error deleting bucket %s: %v", resources.retentionBucket, err)
    }

    log.Println("Cleanup complete.")
}

func (resources *Resources) deleteObjects(bucket string, objects []s3.Object) {
    for _, obj := range objects {
        _, err := resources.s3Actions.S3Client.DeleteObject(context.TODO(), &s3.DeleteObjectInput{
            Bucket: &bucket,
            Key:    obj.Key,
        })
        if err != nil {
            log.Printf("Error deleting object %s from bucket %s: %v", *obj.Key, bucket, err)
        }
    }
}

func (resources *Resources) deleteRetentionObjects(bucket string, objects []s3.Object) {
    for _, obj := range objects {
        _, err := resources.s3Actions.S3Client.DeleteObject(context.TODO(), &s3.DeleteObjectInput{
            Bucket:                      &bucket,
            Key:                         obj.Key,
            BypassGovernanceRetention:   types.BypassGovernanceRetentionTrue,
        })
        if err != nil {
            log.Printf("Error deleting object %s from bucket %s: %v", *obj.Key, bucket, err)
        }
    }
}
```

This `Resources` struct keeps track of the S3 buckets and objects created during the `ObjectLockScenario` workflow. It has fields to store the bucket names and lists of objects in each bucket.

The `init` function initializes the `s3Actions` and `questioner` fields, which are used for making S3 service calls and interacting with the user, respectively.

The `Cleanup` function is responsible for deleting all the resources created during the scenario. It first prompts the user to confirm if they want to delete the resources. If confirmed, it deletes all the objects from the buckets and then deletes the buckets themselves. For objects in the retention bucket, it uses the `BypassGovernanceRetention` parameter to bypass the retention period and delete the objects.

The `deleteObjects` and `deleteRetentionObjects` helper functions handle the deletion of objects from regular buckets and the retention bucket, respectively.

Note that this implementation assumes that the `IScenarioHelper` interface provides a `CreateBucket` and `UploadTestFiles` method to create the buckets and upload test objects, respectively.