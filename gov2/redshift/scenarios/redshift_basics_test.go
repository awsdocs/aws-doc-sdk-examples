// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the get started scenario.

package scenarios

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"math/rand"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/redshift/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/redshift/stubs"

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
// which calls GetObject until it receives a 415 status code. IDEs may indicate it's
// unused, but it is required for side effects.
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
	clusterId := "demo-cluster-1"
	secretId := "s3express/basics/secrets"
	user := User{
		Username: "testUser",
		Password: "testPassword",
	}
	databaseName := "dev"
	nodeType := "ra3.xlplus"
	clusterType := "single-node"
	publiclyAccessible := true
	testId := "test-result-id"
	sql := "test sql statement"
	sqls := []string{sql}
	scenarioTest.OutFilename = "test.out"

	scenarioTest.Answers = []string{
		"enter", "enter", "10", "2013", "y", "y", "y",
	}

	// set up stubs
	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubGetSecretValue(secretId, user.Username, user.Password, nil))
	stubList = append(stubList, stubs.StubCreateCluster(clusterId, user.Username, user.Password, nodeType, clusterType, publiclyAccessible, nil))
	stubList = append(stubList, stubs.StubDescribeClusters(clusterId, nil))
	stubList = append(stubList, stubs.StubDescribeClusters(clusterId, nil))
	stubList = append(stubList, stubs.StubListDatabases(clusterId, databaseName, user.Username, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(clusterId, databaseName, user.Username, sql, testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(clusterId, databaseName, user.Username, sqls, testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(clusterId, databaseName, user.Username, sql, testId, nil))
	stubList = append(stubList, stubs.StubDescribeStatement(testId, nil))
	stubList = append(stubList, stubs.StubGetStatementResult(nil))
	stubList = append(stubList, stubs.StubModifyCluster(nil))
	stubList = append(stubList, stubs.StubDeleteCluster(clusterId, nil))
	stubList = append(stubList, stubs.StubDescribeClusters(clusterId, &testtools.StubError{Err: &types.ClusterNotFoundFault{}}))

	return stubList
}

type TestPauser struct{}

func (tp TestPauser) Pause(secs int) {}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenarioTest *BasicsScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenarioTest.Answers}
	scenario := RedshiftBasics(*stubber.SdkConfig, &mockQuestioner, TestPauser{}, demotools.NewMockFileSystem(scenarioTest.File), scenarioTest.Helper)

	scenario.Run(context.Background())
}

func (scenarioTest *BasicsScenarioTest) Cleanup() {
}
