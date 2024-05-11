---
combined: true
debug:
  engine: bedrock
  finish: max_tokens
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  Write a scenario struct that implements all of the Example steps in the SPECIFICATION.
  Write a separate function for each Example step.
  Write a run function that runs the steps in order.
  Write a resources struct that tracks any AWS resources created during the example and deletes them at the end.
  Here is an examples scenario and resources structs from a different workflow.
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
```go
package workflows

import (
    "context"
    "errors"
    "fmt"
    "log"
    "strings"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/s3"
    "github.com/aws/aws-sdk-go-v2/service/s3/types"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/actions"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// ObjectLockScenario contains the steps to run the S3 Object Lock workflow.
type ObjectLockScenario struct {
    helper     IScenarioHelper
    questioner demotools.IQuestioner
    resources  Resources
    s3Actions  *actions.S3Actions
}

// NewObjectLockScenario constructs a new ObjectLockScenario instance.
func NewObjectLockScenario(sdkConfig aws.Config, questioner demotools.IQuestioner, helper IScenarioHelper) ObjectLockScenario {
    scenario := ObjectLockScenario{
        helper:     helper,
        questioner: questioner,
        resources:  Resources{},
        s3Actions:  &actions.S3Actions{S3Client: s3.NewFromConfig(sdkConfig)},
    }
    scenario.resources.init(scenario.s3Actions, questioner)
    return scenario
}

// CreateBuckets creates the S3 buckets required for the workflow.
func (scenario *ObjectLockScenario) CreateBuckets() {
    log.Println("Creating S3 buckets...")

    scenario.resources.nolockBucket = scenario.helper.CreateBucket(scenario.s3Actions.S3Client)
    scenario.resources.lockBucket = scenario.helper.CreateBucket(scenario.s3Actions.S3Client, true)
    scenario.resources.retentionBucket = scenario.helper.CreateBucket(scenario.s3Actions.S3Client)

    log.Println("S3 buckets created.")
    log.Println(strings.Repeat("-", 88))
}

// SetDefaultRetentionPolicy sets a default retention policy on a bucket.
func (scenario *ObjectLockScenario) SetDefaultRetentionPolicy() {
    log.Println("Setting a default retention policy on a bucket...")

    retentionPeriod := scenario.questioner.AskNumber("Enter the default retention period in days: ", 1, 36500)
    _, err := scenario.s3Actions.PutBucketObjectLockConfiguration(context.TODO(), scenario.resources.retentionBucket, "Enabled", retentionPeriod)
    if err != nil {
        panic(err)
    }

    log.Printf("Default retention policy set on bucket %s with %d day retention period.", scenario.resources.retentionBucket, retentionPeriod)
    log.Println(strings.Repeat("-", 88))
}

// UploadTestObjects uploads test objects to the S3 buckets.
func (scenario *ObjectLockScenario) UploadTestObjects() {
    log.Println("Uploading test objects to S3 buckets...")

    scenario.resources.nolockObjects = scenario.helper.UploadTestFiles(scenario.s3Actions.S3Client, scenario.resources.nolockBucket, 2)
    scenario.resources.lockObjects = scenario.helper.UploadTestFiles(scenario.s3Actions.S3Client, scenario.resources.lockBucket, 2)
    scenario.resources.retentionObjects = scenario.helper.UploadTestFiles(scenario.s3Actions.S3Client, scenario.resources.retentionBucket, 2)

    log.Println("Test objects uploaded.")
    log.Println(strings.Repeat("-", 88))
}

// SetObjectLockConfigurations sets object lock configurations on the test objects.
func (scenario *ObjectLockScenario) SetObjectLockConfigurations() {
    log.Println("Setting object lock configurations...")

    for _, obj := range scenario.resources.lockObjects {
        if scenario.questioner.AskBool(fmt.Sprintf("Add legal hold to %s (y/n)? ", obj.Key), "n") {
            _, err := scenario.s3Actions.PutObjectLegalHold(context.TODO(), scenario.resources.lockBucket, obj.Key, "ON")
            if err != nil {
                panic(err)
            }
            log.Printf("Legal hold set on %s.", obj.Key)
        }

        retentionPeriod := scenario.questioner.AskNumber(fmt.Sprintf("Enter retention period (in days) for %s: ", obj.Key), 1, 36500)
        _, err := scenario.s3Actions.PutObjectLockConfiguration(context.TODO(), scenario.resources.lockBucket, obj.Key, "Enabled", retentionPeriod)
        if err != nil {
            panic(err)
        }
        log.Printf("Retention period set to %d days for %s.", retentionPeriod, obj.Key)
    }

    for _, obj := range scenario.resources.retentionObjects {
        retentionPeriod := scenario.questioner.AskNumber(fmt.Sprintf("Enter retention period (in days) for %s: ", obj.Key), 1, 36500)
        _, err := scenario.s3Actions.PutObjectRetention(context.TODO(), scenario.resources.retentionBucket, obj.Key, retentionPeriod)
        if err != nil {
            panic(err)
        }
        log.Printf("Retention period set to %d days for %s.", retentionPeriod, obj.Key)
    }

    log.Println("Object lock configurations set.")
    log.Println(strings.Repeat("-", 88))
}

// InteractWithObjects allows the user to interact with the objects and test the object lock configurations.
func (scenario *ObjectLockScenario) InteractWithObjects() {
    log.Println("Now you can interact with the objects to test the object lock configurations.")
    log.Println("Choose an action from the menu:")

    for {
        choice := scenario.questioner.AskNumber("1. List objects\n2. Delete object\n3. Overwrite object\n4. View object lock configuration\n5. Exit\n", 1, 5)

        switch choice {
        case 1:
            scenario.ListObjects()
        case 2:
            scenario.DeleteObject()
        case 3:
            scenario.OverwriteObject()
        case 4:
            scenario.ViewObjectLockConfiguration()
        case 5:
            return
        }
    }
}

// ListObjects lists the objects in the S3 buckets.
func (scenario *ObjectLockScenario) ListObjects() {
    log.Println("Objects in the no-lock bucket:")
    for _, obj := range scenario.resources.nolockObjects {
        log.Printf("\t%s\n", obj.Key)
    }

    log.Println("\nObjects in the lock-enabled bucket:")
    for _, obj := range scenario.resources.lockObjects {
        log.Printf("\t%s\n", obj.Key)
    }

    log.Println("\nObjects in the retention bucket:")
    for _, obj := range scenario.resources.retentionObjects {
        log.Printf("\t%s\n", obj.Key)
    }

    log.Println(strings.Repeat("-", 88))
}

// DeleteObject attempts to delete an object from an S3 bucket.
func (scenario *ObjectLockScenario) DeleteObject() {
    bucketChoice := scenario.questioner.AskNumber("Choose a bucket:\n1. No-lock bucket\n2. Lock-enabled bucket\n3. Retention bucket\n", 1, 3)
    var bucket string
    var objects []s3.Object

    switch bucketChoice {
    case 1:
        bucket = scenario.resources.nolockBucket
        objects = scenario.resources.nolockObjects
    case 2:
        bucket = scenario.resources.lockBucket
        objects = scenario.resources.lockObjects
    case 3:
        bucket = scenario.resources.retentionBucket
        objects = scenario.resources.retentionObjects
    }

    if len(objects) == 0 {
        log.Println("No objects in the selected bucket.")
        return
    }

    objectChoice := scenario.questioner.AskNumber("Choose an object to delete:\n", 1, len(objects))
    objectKey := objects[objectChoice-1].Key

    _, err := scenario.s3Actions.S3Client.DeleteObject(context.TODO(), &s3.DeleteObjectInput{
        Bucket: aws.String(bucket),
        Key:    aws.String(objectKey),
    })

    if err != nil {
        log.Printf("Unable to delete object %s: %v", objectKey, err)
    } else {
        log.Printf("Deleted object %s from bucket %s.", objectKey, bucket)
    }

    log.Println(strings.Repeat("-", 88))
}

// OverwriteObject attempts to overwrite an object in an S3 bucket.
func (scenario *ObjectLockScenario) OverwriteObject() {
    bucketChoice := scenario.questioner.AskNumber("Choose a bucket:\n1. No-lock bucket\n2. Lock-enabled bucket\n3. Retention bucket\n", 1, 3)
    var bucket string
    var objects []s3.Object

    switch bucketChoice {
    case 1:
        bucket = scenario.resources.nolockBucket
        objects = scenario.resources.nolockObjects
    case 2:
        bucket = scenario.resources.lockBucket
        objects = scenario.resources.lockObjects
    case 3:
        bucket = scenario.resources.retentionBucket
        objects = scenario.resources.retentionObjects
    }

    if len(objects) == 0 {
        log.Println("No objects in the selected bucket.")
        return
    }

    objectChoice := scenario.questioner.AskNumber("Choose an object to overwrite:\n", 1, len(objects))
    objectKey := objects[objectChoice-1].Key

    _, err := scenario.helper.UploadTestFile(scenario.s3Actions.S3Client, bucket, objectKey)
    if err != nil {
        log.Printf("Unable to overwrite object %s: %v", objectKey, err)
    } else {
        log.Printf("Overwrote object %s in bucket %s.", objectKey, bucket)
    }

    log.Println(strings.Repeat("-", 88))
}

// ViewObjectLockConfiguration displays the object lock configuration for an object.
func (scenario *ObjectLockScenario) ViewObjectLockConfiguration() {
    bucketChoice := scenario.questioner.AskNumber("Choose a bucket:\n1. No-lock bucket\n2. Lock-enabled bucket\n3. Retention bucket\n", 1, 3)
    var bucket string
    var objects []s3.Object

    switch bucketChoice {
    case 1:
        bucket = scenario.resources.nolockBucket
        objects = scenario.resources.nolockObjects
    case 2:
        bucket = scenario.resources.lockBucket
        objects = scenario.resources.lockObjects
    case 3:
        bucket = scenario.resources.retentionBucket
        objects = scenario.resources.retentionObjects
    }

    if len(objects) == 0 {
        log.Println("No objects in the selected bucket.")
        return
    }

    objectChoice := scenario.questioner.AskNumber("Choose an object to view lock configuration:\n", 1, len(objects))
    objectKey := objects[objectChoice-1].Key

    legalHold, err := scenario.s3Actions.GetObjectLegalHold(context.TODO(), bucket, objectKey)
    if err != nil {
        if errors.Is(err, &types.NoSuchObjectLockConfiguration{}) {
            log.Printf("Object %s does not have a legal hold configuration.", objectKey)
        } else {
            log.Printf("Error getting legal hold for %s: %v", objectKey, err)
        }
    } else {
        log.Printf("Legal hold for %s: %s", objectKey, legalHold.Status)
    }

    retention, err := scenario.s3Actions.GetObjectRetention(context.TODO(), bucket, objectKey)
    if err != nil {
        if errors.Is(err, &types.NoSuchObjectLockConfiguration{}) {
            log.Printf("Object %s does not have a retention configuration.", objectKey)
        } else {
            log.Printf("Error getting retention for %s: %v", objectKey, err)
        }
    } else {
        log.Printf("Retention for %s: %s until %v", objectKey, retention.Mode, retention.RetainUntilDate)
    }

    lockConfig, err := scenario.s3Actions.GetObjectLockConfiguration(context.TODO(), bucket, objectKey)
    if err != nil {
        if errors.Is(err, &types.NoSuchObjectLockConfiguration{}) {
            log.Printf("Object %s does not have an object lock configuration.", objectKey)
        } else {
            log.Printf("Error getting object lock configuration for %s: %v", objectKey, err)
        }
    } else {
        log.Printf("Object lock configuration for %s: %s", objectKey, lockConfig.ObjectLockEnabled)
    }

    log.Println(strings.Repeat("-", 88))
}

// Run runs the S3 Object Lock workflow scenario.
func (scenario *ObjectLockScenario) Run() {
    defer func() {
        if r := recover(); r != nil {
            log.Println("Something went wrong with the demo.")
            scenario.resources.Cleanup()
        }
    }()

    log.Println(strings.Repeat("-", 88))
    log.Println("Welcome to the Amazon S3 Object Lock Workflow Scenario.")
    log.Println(strings.Repeat("-", 88))

    scenario.CreateBuckets()
    scenario.SetDefaultRetentionPolicy()
    scenario.UploadTestObjects()
    scenario.SetObjectLockConfigurations()
    scenario.InteractWithObjects()

    scenario.resources.Cleanup()

    log.Println(strings.Repeat("-", 88))
    log.Println("Thanks for watching!")
    log.Println(strings.Repeat("-", 88))
}
```