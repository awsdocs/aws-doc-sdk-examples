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
  Write unit tests for the ObjectLockScenario.
  Cover all of the options from the InteractWithObject function in the correct sequence and provide mocked input and output.
  Here is an example of how to write unit tests from another workflow.
  <example>
  // TestRunTopicsAndQueuesScenario runs the scenario multiple times. The first time, it runs with no
  // errors. In subsequent runs, it specifies that each stub in the sequence should
  // raise an error and verifies the results.
  func TestRunTopicsAndQueuesScenario(t *testing.T) {
      scenTest := TopicsAndQueuesScenarioTest{}
      testtools.RunScenarioTests(&scenTest, t)
  }

  // TopicsAndQueuesScenarioTest encapsulates data for a scenario test.
  type TopicsAndQueuesScenarioTest struct {
      Answers []string
  }

  // SetupDataAndStubs sets up test data and builds the stubs that are used to return
  // mocked data.
  func (scenTest *TopicsAndQueuesScenarioTest) SetupDataAndStubs() []testtools.Stub {
      baseTopicName := "test-topic-name"
      fifoTopicName := baseTopicName + ".fifo"
      topicArn := fmt.Sprintf("arn:%v", fifoTopicName)
      topicAttribs := map[string]string{"FifoTopic": "true"}
      queueCount := 2
      baseQueueName := "test-queue-"
      baseQueueNames := make([]string, queueCount)
      fifoQueueNames := make([]string, queueCount)
      queueUrls := make([]string, queueCount)
      queueArns := make([]string, queueCount)
      subscriptionArns := make([]string, queueCount)
      for q_idx := 0; q_idx < queueCount; q_idx++ {
          baseQueueNames[q_idx] = fmt.Sprintf("%v%v", baseQueueName, q_idx)
          fifoQueueNames[q_idx] = baseQueueNames[q_idx] + ".fifo"
          queueUrls[q_idx] = fmt.Sprintf("https://%v", fifoQueueNames[q_idx])
          queueArns[q_idx] = fmt.Sprintf("arn:sqs:test:%v", fifoQueueNames[q_idx])
          subscriptionArns[q_idx] = fmt.Sprintf("arn:sns:test:subscription/%v", fifoQueueNames[q_idx])
      }
      createQueueAttribs := map[string]string{"FifoQueue": "true"}
      getQueueArnAttribNames := []sqstypes.QueueAttributeName{sqstypes.QueueAttributeNameQueueArn}
      messages := []string{"Test message 1.", "Test message 2.", "Test message 3."}
      groupId := "test-group-id"
      dedupId := "test-dedup-id"
      toneChoice := 1
      filterAttributes := map[string]snstypes.MessageAttributeValue{TONE_KEY: {
          DataType:    aws.String("String"),
          StringValue: aws.String(ToneChoices[toneChoice]),
      }}
      receiptHandles := []string{"test-handle-1", "test-handle-2", "test-handle-3"}

      scenTest.Answers = []string{
          "y", "n", baseTopicName, // CreateTopic
          baseQueueNames[0], "y", "1", "n", baseQueueNames[1], "y", "1", "n", // CreateQueue and SubscribeQueueToTopic
          messages[0], groupId, dedupId, "y", "1", "y", messages[1], groupId, dedupId, "y", "1", "y", messages[2], groupId, dedupId, "y", "1", "n", // PublishMessages
          "y", // Cleanup
      }

      var stubList []testtools.Stub
      stubList = append(stubList, stubs.StubCreateTopic(fifoTopicName, topicAttribs, nil))
      for queueIndex := range fifoQueueNames {
          stubList = append(stubList, stubs.StubCreateQueue(fifoQueueNames[queueIndex], createQueueAttribs, nil))
          stubList = append(stubList, stubs.StubGetQueueAttributes(
              queueUrls[queueIndex], getQueueArnAttribNames,
              map[string]string{string(sqstypes.QueueAttributeNameQueueArn): queueArns[queueIndex]}, nil))
          stubList = append(stubList, stubs.StubSetQueueAttributes(queueUrls[queueIndex], nil, nil))
          stubList = append(stubList, stubs.StubSubscribe("sqs", topicArn, nil, queueArns[queueIndex],
              subscriptionArns[queueIndex], nil))
      }
      for _, message := range messages {
          stubList = append(stubList, stubs.StubPublish(topicArn, message, groupId, dedupId, filterAttributes, nil))
      }
      for _, queueUrl := range queueUrls {
          stubList = append(stubList, stubs.StubReceiveMessage(queueUrl, 10, 1, messages, receiptHandles, nil))
          stubList = append(stubList, stubs.StubReceiveMessage(queueUrl, 10, 1, nil, nil, nil))
          stubList = append(stubList, stubs.StubDeleteMessageBatch(queueUrl, receiptHandles, nil))
      }

      //cleanup
      stubList = append(stubList, stubs.StubDeleteTopic(topicArn, nil))
      for _, queueUrl := range queueUrls {
          stubList = append(stubList, stubs.StubDeleteQueue(queueUrl, nil))
      }

      return stubList
  }

  // RunSubTest performs a single test run with a set of stubs set up to run with
  // or without errors.
  func (scenTest *TopicsAndQueuesScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
      mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
      RunTopicsAndQueuesScenario(*stubber.SdkConfig, &mockQuestioner)
  }

  func (scenTest *TopicsAndQueuesScenarioTest) Cleanup() {}
  </example>
---
Sure, here's an example of how you could write unit tests for the `ObjectLockScenario` workflow:

```go
package workflows

import (
    "context"
    "testing"

    "github.com/aws/aws-sdk-go-v2/service/s3"
    "github.com/aws/aws-sdk-go-v2/service/s3/types"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/workflows/stubs"
)

func TestRunObjectLockScenario(t *testing.T) {
    scenTest := ObjectLockScenarioTest{}
    testtools.RunScenarioTests(&scenTest, t)
}

type ObjectLockScenarioTest struct {
    Answers []string
}

func (scenTest *ObjectLockScenarioTest) SetupDataAndStubs() []testtools.Stub {
    nolockBucket := "no-lock-bucket"
    lockBucket := "lock-enabled-bucket"
    retentionBucket := "retention-bucket"

    nolockObjects := []s3.Object{
        {Key: testtools.PtrStr("no-lock-obj1")},
        {Key: testtools.PtrStr("no-lock-obj2")},
    }
    lockObjects := []s3.Object{
        {Key: testtools.PtrStr("lock-obj1")},
        {Key: testtools.PtrStr("lock-obj2")},
    }
    retentionObjects := []s3.Object{
        {Key: testtools.PtrStr("retention-obj1")},
        {Key: testtools.PtrStr("retention-obj2")},
    }

    legalHold := types.ObjectLockLegalHold{
        Status: types.ObjectLockLegalHoldStatusOn,
    }
    lockConfig := types.ObjectLockConfiguration{
        ObjectLockEnabled: types.ObjectLockEnabledEnabled,
    }
    retention := types.ObjectLockRetention{
        Mode:            types.ObjectLockRetentionModeGovernance,
        RetainUntilDate: testtools.PtrTime(testtools.MustParseTime("2024-02-28T00:00:00Z")),
    }

    scenTest.Answers = []string{
        "y", "n", "n",                        // CreateBuckets
        "30",                                 // SetDefaultRetentionPolicy
        "y", "y", "10", "y", "y", "20",       // SetObjectLockConfigurations
        "1", "2", "1", "2", "1", "4", "5",    // InteractWithObjects
    }

    var stubList []testtools.Stub
    stubList = append(stubList, stubs.StubCreateBucket(nolockBucket, false, nil))
    stubList = append(stubList, stubs.StubCreateBucket(lockBucket, true, nil))
    stubList = append(stubList, stubs.StubCreateBucket(retentionBucket, false, nil))
    stubList = append(stubList, stubs.StubPutBucketObjectLockConfiguration(retentionBucket, types.ObjectLockEnabledEnabled, 30, nil))

    stubList = append(stubList, stubs.StubUploadTestFiles(nolockBucket, 2, nil))
    stubList = append(stubList, stubs.StubUploadTestFiles(lockBucket, 2, nil))
    stubList = append(stubList, stubs.StubUploadTestFiles(retentionBucket, 2, nil))

    stubList = append(stubList, stubs.StubPutObjectLegalHold(lockBucket, *lockObjects[0].Key, &legalHold, nil))
    stubList = append(stubList, stubs.StubPutObjectLockConfiguration(lockBucket, *lockObjects[0].Key, &lockConfig, 10, nil))
    stubList = append(stubList, stubs.StubPutObjectLegalHold(lockBucket, *lockObjects[1].Key, nil, nil))
    stubList = append(stubList, stubs.StubPutObjectLockConfiguration(lockBucket, *lockObjects[1].Key, &lockConfig, 20, nil))
    stubList = append(stubList, stubs.StubPutObjectRetention(retentionBucket, *retentionObjects[0].Key, &retention, nil))
    stubList = append(stubList, stubs.StubPutObjectRetention(retentionBucket, *retentionObjects[1].Key, &retention, nil))

    stubList = append(stubList, stubs.StubListObjects(nolockBucket, nolockObjects, nil))
    stubList = append(stubList, stubs.StubListObjects(lockBucket, lockObjects, nil))
    stubList = append(stubList, stubs.StubListObjects(retentionBucket, retentionObjects, nil))

    stubList = append(stubList, stubs.StubDeleteObject(nolockBucket, *nolockObjects[0].Key, nil))
    stubList = append(stubList, stubs.StubDeleteObject(lockBucket, *lockObjects[0].Key, &testtools.StubError{Err: &types.ObjectLockedException{}}))
    stubList = append(stubList, stubs.StubDeleteObject(nolockBucket, *nolockObjects[1].Key, nil))
    stubList = append(stubList, stubs.StubDeleteObject(lockBucket, *lockObjects[1].Key, &testtools.StubError{Err: &types.ObjectLockedException{}}))

    stubList = append(stubList, stubs.StubOverwriteObject(nolockBucket, *nolockObjects[0].Key, nil))
    stubList = append(stubList, stubs.StubOverwriteObject(lockBucket, *lockObjects[0].Key, &testtools.StubError{Err: &types.ObjectLockedException{}}))
    stubList = append(stubList, stubs.StubOverwriteObject(retentionBucket, *retentionObjects[0].Key, &testtools.StubError{Err: &types.ObjectLockedException{}}))

    stubList = append(stubList, stubs.StubGetObjectLegalHold(lockBucket, *lockObjects[0].Key, &legalHold, nil))
    stubList = append(stubList, stubs.StubGetObjectRetention(lockBucket, *lockObjects[0].Key, &retention, nil))
    stubList = append(stubList, stubs.StubGetObjectLockConfiguration(lockBucket, *lockObjects[0].Key, &lockConfig, nil))
    stubList = append(stubList, stubs.StubGetObjectLegalHold(retentionBucket, *retentionObjects[0].Key, nil, &testtools.StubError{Err: &types.NoSuchObjectLockConfiguration{}}))
    stubList = append(stubList, stubs.StubGetObjectRetention(retentionBucket, *retentionObjects[0].Key, &retention, nil))
    stubList = append(stubList, stubs.StubGetObjectLockConfiguration(retentionBucket, *retentionObjects[0].Key, nil, &testtools.StubError{Err: &types.NoSuchObjectLockConfiguration{}}))

    return stubList
}

func (scenTest *ObjectLockScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
    mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
    mockHelper := testtools.NewMockScenarioHelper()
    scenario := NewObjectLockScenario(*stubber.SdkConfig, &mockQuestioner, &mockHelper)
    scenario.Run()
}

func (scenTest *ObjectLockScenarioTest) Cleanup() {}
```

This test suite covers the different options in the `InteractWithObjects` function by providing appropriate mocked input and output.

Here's a breakdown of what's happening:

1. The `TestRunObjectLockScenario` function is the entry point for the test suite. It creates an instance of `ObjectLockScenarioTest` and runs the scenario tests using the `testtools.RunScenarioTests` function.

2. The `ObjectLockScenarioTest` struct encapsulates the test data, including the answers that will be provided by the `MockQuestioner`.

3. The `SetupDataAndStubs` method sets up the test data and builds the stubs that will be used to return mocked data. It creates test data for buckets, objects, and object lock configurations, and then builds stubs for various S3 operations, such as creating buckets, uploading objects, setting object lock configurations, listing objects, deleting objects, overwriting objects, and retrieving object lock configurations.

4. The `RunSubTest` method creates a `MockQuestioner` instance with the provided answers, creates a `MockScenarioHelper` instance, creates a new `ObjectLockScenario` instance with the mocked dependencies, and runs the scenario.

5. The `Cleanup` method is currently empty, as there's no cleanup required for this test suite.

This test suite covers the following scenarios:

- Creating buckets with and without object locking enabled
- Setting a default retention policy on a bucket
- Uploading test objects to the buckets
- Setting object lock configurations (legal hold and retention) on objects
- Listing objects in the buckets
- Attempting to delete and overwrite locked and unlocked objects
- Retrieving object lock configurations (legal hold, retention, and object lock enabled status) for objects

By providing appropriate mocked input and output in the stubs, the test suite can simulate various scenarios and verify the expected behavior of the `ObjectLockScenario` workflow.