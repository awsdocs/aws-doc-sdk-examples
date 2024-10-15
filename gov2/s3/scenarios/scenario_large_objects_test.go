// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the get started scenario.

package scenarios

import (
	"context"
	"fmt"
	"io"
	"net/http"
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

// TestRunLargeObjectScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunLargeObjectScenario(t *testing.T) {
	scenTest := LargeObjectScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// httpErr is used to mock an HTTP error. This is required by the download manager,
// which calls GetObject until it receives a 415 status code.
type httpErr struct {
	statusCode int
}

func (responseErr httpErr) HTTPStatusCode() int { return responseErr.statusCode }
func (responseErr httpErr) Error() string {
	return fmt.Sprintf("HTTP error: %v", responseErr.statusCode)
}

// LargeObjectScenarioTest encapsulates data for a scenario test.
type LargeObjectScenarioTest struct {
	Answers     []string
	OutFilename string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *LargeObjectScenarioTest) SetupDataAndStubs() []testtools.Stub {
	bucketName := "amzn-s3-demo-bucket-1"
	largeKey := "doc-example-large"
	testConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic(err)
	}
	uploadId := "upload-id"
	testBody := io.NopCloser(strings.NewReader("Test data!"))
	dnRanges := []int{0, 10 * 1024 * 1024, 20 * 1024 * 1024, 30 * 1024 * 1024, 40 * 1024 * 1024}
	listKeys := []string{largeKey}
	scenTest.Answers = []string{
		bucketName, "", "", "y",
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubHeadBucket(
		bucketName, &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateBucket(bucketName, testConfig.Region, nil))
	stubList = append(stubList, stubs.StubHeadBucket(bucketName, nil))
	stubList = append(stubList, stubs.StubCreateMultipartUpload(bucketName, largeKey, uploadId, nil))
	stubList = append(stubList, stubs.StubUploadPart(bucketName, largeKey, uploadId, nil))
	stubList = append(stubList, stubs.StubUploadPart(bucketName, largeKey, uploadId, nil))
	stubList = append(stubList, stubs.StubUploadPart(bucketName, largeKey, uploadId, nil))
	stubList = append(stubList, stubs.StubCompleteMultipartUpload(bucketName, largeKey, uploadId, []int32{1, 2, 3}, nil))
	stubList = append(stubList, stubs.StubHeadObject(bucketName, largeKey, nil))
	for i := 0; i < len(dnRanges)-2; i++ {
		stubList = append(stubList, stubs.StubGetObject(bucketName, largeKey,
			aws.String(fmt.Sprintf("bytes=%v-%v", dnRanges[i], dnRanges[i+1]-1)), testBody, nil))
	}
	// The S3 downloader calls GetObject until it receives a 416 HTTP status code.
	respErr := httpErr{statusCode: http.StatusRequestedRangeNotSatisfiable}
	stubList = append(stubList, stubs.StubGetObject(bucketName, largeKey,
		aws.String(fmt.Sprintf("bytes=%v-%v", dnRanges[3], dnRanges[4]-1)), testBody,
		&testtools.StubError{Err: respErr, ContinueAfter: true}))
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
func (scenTest *LargeObjectScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	RunLargeObjectScenario(context.Background(), *stubber.SdkConfig, &mockQuestioner)
}

// Cleanup deletes the output file created by the download test.
func (scenTest *LargeObjectScenarioTest) Cleanup() {
	_ = os.Remove(scenTest.OutFilename)
}
