// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the get started scenario.

package scenarios

import (
	"context"
	"fmt"
	"io"
	"os"
	"strings"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunGetStartedScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunGetStartedScenario(t *testing.T) {
	scenTest := GetStartedScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// GetStartedScenarioTest encapsulates data for a scenario test.
type GetStartedScenarioTest struct {
	Answers     []string
	OutFilename string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *GetStartedScenarioTest) SetupDataAndStubs() []testtools.Stub {
	bucketName := "amzn-s3-demo-bucket-1"
	objectKey := "doc-example-key"
	bucketList := []types.Bucket{{Name: aws.String(bucketName)}, {Name: aws.String("amzn-s3-demo-bucket-2")}}
	testConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic(err)
	}
	testBody := io.NopCloser(strings.NewReader("Test data!"))
	scenTest.OutFilename = "test.out"
	copyFolder := "copy_folder"
	listKeys := []string{"object-1", "object-2", "object-3"}
	scenTest.Answers = []string{
		bucketName, "../README.md", scenTest.OutFilename, copyFolder, "", "y",
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubListBuckets(bucketList, nil))
	stubList = append(stubList, stubs.StubHeadBucket(
		bucketName, &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateBucket(bucketName, testConfig.Region, nil))
	stubList = append(stubList, stubs.StubHeadBucket(bucketName, nil))
	stubList = append(stubList, stubs.StubPutObject(bucketName, objectKey, nil))
	stubList = append(stubList, stubs.StubHeadObject(bucketName, objectKey, nil))
	stubList = append(stubList, stubs.StubGetObject(bucketName, objectKey, nil, testBody, nil))
	stubList = append(stubList, stubs.StubCopyObject(
		bucketName, objectKey, bucketName, fmt.Sprintf("%v/%v", copyFolder, objectKey), nil))
	stubList = append(stubList, stubs.StubHeadObject(bucketName, fmt.Sprintf("%v/%v", copyFolder, objectKey), nil))
	stubList = append(stubList, stubs.StubListObjectsV2(bucketName, listKeys, nil))
	stubList = append(stubList, stubs.StubDeleteObjects(bucketName, listKeys, nil))
	for _, key := range listKeys {
		stubList = append(stubList, stubs.StubHeadObject(bucketName, key,
			&testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
	}
	stubList = append(stubList, stubs.StubDeleteBucket(bucketName, nil))
	stubList = append(stubList, stubs.StubHeadBucket(bucketName, &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *GetStartedScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	RunGetStartedScenario(context.Background(), *stubber.SdkConfig, &mockQuestioner)
}

// Cleanup deletes the output file created by the download test.
func (scenTest *GetStartedScenarioTest) Cleanup() {
	_ = os.Remove(scenTest.OutFilename)
}
