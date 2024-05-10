// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the get started scenario.

package scenarios

import (
	"encoding/json"
	"fmt"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/redshift/stubs"
	"io"
	"math/rand"
	"os"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunGetStartedScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunBasicsScenario(t *testing.T) {
	scenarioTest := BasicsScenarioTest{
		Helper: ScenarioHelper{
			Prefix: "basics_test_",
			Random: rand.New(rand.NewSource(time.Now().UnixNano())),
		},
		File: MockFile{
			ReturnData: []byte(`[{"year": 2013, "title": "Rush"}]`),
		},
	}
	testtools.RunScenarioTests(&scenarioTest, t)
}

// httpErr is used to mock an HTTP error. This is required by the download manager,
// which calls GetObject until it receives a 415 status code.
type httpErr struct {
	statusCode int
}

type MockFile struct {
	ReturnData json.RawMessage
}

func (f MockFile) Read(p []byte) (n int, err error) {
	n = copy(p, f.ReturnData[0:])
	return n, nil
}
func (f MockFile) Write(p []byte) (n int, err error) {
	return 0, nil
}
func (f MockFile) Close() error {
	return nil
}

func (responseErr httpErr) HTTPStatusCode() int { return responseErr.statusCode }
func (responseErr httpErr) Error() string {
	return fmt.Sprintf("HTTP error: %v", responseErr.statusCode)
}

// GetStartedScenarioTest encapsulates data for a scenario test.
type BasicsScenarioTest struct {
	Config      aws.Config
	File        io.ReadWriteCloser
	Helper      IScenarioHelper
	Answers     []string
	OutFilename string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenarioTest *BasicsScenarioTest) SetupDataAndStubs() []testtools.Stub {
	// set up variables
	clusterId := "test-cluster-1"
	userName, userPassword := "awsuser", "AwsUser1000"
	databaseName := "dev"
	nodeType := "test.node"
	clusterType := "single-node"
	publiclyAccessible := true
	testId := "test-result-id"
	sql := "test sql statement"
	sqls := []string{sql}
	scenarioTest.OutFilename = "test.out"

	scenarioTest.Answers = []string{
		"enter", "enter", "enter", "2013", "enter", "y", "y", //"3", "4", "5", "6", "7", "8", "9", "10",
	}

	// set up stubs
	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubCreateCluster(clusterId, userPassword, userName, nodeType, clusterType, publiclyAccessible, nil))
	stubList = append(stubList, stubs.StubDescribeClusters(clusterId, nil))
	stubList = append(stubList, stubs.StubListDatabases(clusterId, databaseName, userName, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(clusterId, databaseName, userName, sql, testId, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(clusterId, databaseName, userName, sqls, testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil)) // This is where Execute is getting called instead of Describe. Comment this line out to see the second Describe work.
	stubList = append(stubList, stubs.StubExecuteStatement(clusterId, databaseName, userName, sql, testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil))
	stubList = append(stubList, stubs.StubGetStatementResult(nil))
	stubList = append(stubList, stubs.StubModifyCluster(nil))
	stubList = append(stubList, stubs.StubDeleteCluster(clusterId, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenarioTest *BasicsScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenarioTest.Answers}
	scenario := RedshiftBasics(*stubber.SdkConfig, &mockQuestioner, demotools.Pauser{}, demotools.NewMockFileSystem(scenarioTest.File), scenarioTest.Helper)

	scenario.Run()
}

// Cleanup deletes the output file created by the download test.
func (scenarioTest *BasicsScenarioTest) Cleanup() {
	_ = os.Remove(scenarioTest.OutFilename)
}
