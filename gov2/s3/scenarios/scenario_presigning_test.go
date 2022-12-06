// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the presigning scenario.

package scenarios

import (
	"context"
	"io"
	"net/http"
	"strings"
	"testing"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// MockHttpRequester mocks HTTP requests called by the presigning scenario.
type MockHttpRequester struct {
	GetBody io.ReadCloser
}

func (httpReq MockHttpRequester) Get(url string) (resp *http.Response, err error) {
	return &http.Response{Status: "Testing", StatusCode: 200, Body: httpReq.GetBody}, nil
}
func (httpReq MockHttpRequester) Put(url string, contentLength int64, body io.Reader) (resp *http.Response, err error) {
	return &http.Response{Status: "Testing", StatusCode: 200}, nil
}
func (httpReq MockHttpRequester) Delete(url string) (resp *http.Response, err error) {
	return &http.Response{Status: "Testing", StatusCode: 204}, nil
}

// TestRunPresigningScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunPresigningScenario(t *testing.T) {
	scenTest := PresigningScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// PresigningScenarioTest encapsulates data for a scenario test.
type PresigningScenarioTest struct {
	Answers  []string
	TestBody io.ReadCloser
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *PresigningScenarioTest) SetupDataAndStubs() []testtools.Stub {
	bucketName := "test-bucket-1"
	testConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {panic(err)}
	objectKey := "doc-example-key"
	scenTest.TestBody = io.NopCloser(strings.NewReader("Test data!"))
	scenTest.Answers = []string{
		bucketName, "../README.md", objectKey, "", "",
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubHeadBucket(
		bucketName, &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateBucket(bucketName, testConfig.Region, nil))
	stubList = append(stubList, stubs.StubPresignedRequest("PUT", bucketName, objectKey, nil))
	stubList = append(stubList, stubs.StubPresignedRequest("GET", bucketName, objectKey, nil))
	stubList = append(stubList, stubs.StubPresignedRequest("DELETE", bucketName, objectKey, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *PresigningScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	RunPresigningScenario(*stubber.SdkConfig, &mockQuestioner, MockHttpRequester{GetBody: scenTest.TestBody})
}

func (scenTest *PresigningScenarioTest) Cleanup() {}
