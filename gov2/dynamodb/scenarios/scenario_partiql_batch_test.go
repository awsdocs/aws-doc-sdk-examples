// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the PartiQL batch scenario.

package scenarios

import (
	"fmt"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/picante-io/aws-doc-sdk-examples/gov2/dynamodb/actions"
	"github.com/picante-io/aws-doc-sdk-examples/gov2/dynamodb/stubs"
	"github.com/picante-io/aws-doc-sdk-examples/gov2/testtools"
)

// PartiQLBatchScenarioTest encapsulates data for a scenario test.
type PartiQLBatchScenarioTest struct {
	TableName string
}

// TestRunPartiQLBatchScenario runs the scenario multiple times. The first time, it
// runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error, and it verifies the results.
func TestRunPartiQLBatchScenario(t *testing.T) {
	scenTest := PartiQLBatchScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// SetupDataAndStubs sets up test data and builds the stubs that are used
// to return mocked data.
func (scenTest *PartiQLBatchScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.TableName = "doc-example-test-partiql-batch-table"
	currentYear, _, _ := time.Now().Date()
	customMovies := []actions.Movie{{
		Title: "House PartiQL",
		Year:  currentYear - 5,
		Info: map[string]interface{}{
			"plot":   "Wacky high jinks result from querying a mysterious database.",
			"rating": 8.5}}, {
		Title: "House PartiQL 2",
		Year:  currentYear - 3,
		Info: map[string]interface{}{
			"plot":   "Moderate high jinks result from querying another mysterious database.",
			"rating": 6.5}}, {
		Title: "House PartiQL 3",
		Year:  currentYear - 1,
		Info: map[string]interface{}{
			"plot":   "Tepid high jinks result from querying yet another mysterious database.",
			"rating": 2.5},
	},
	}
	newRatings := []float64{7.7, 4.4, 1.1}

	insertStatements := make([]string, len(customMovies))
	updateStatements := make([]string, len(customMovies))
	getStatements := make([]string, len(customMovies))
	deleteStatements := make([]string, len(customMovies))
	insertParamList := make([][]interface{}, len(customMovies))
	updateParamList := make([][]interface{}, len(customMovies))
	getDelParamList := make([][]interface{}, len(customMovies))
	interMovies := make([]interface{}, len(customMovies))
	projectedMovies := make([]map[string]interface{}, len(customMovies))
	for index := range customMovies {
		insertStatements[index] = fmt.Sprintf(
			"INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}", scenTest.TableName)
		updateStatements[index] = fmt.Sprintf(
			"UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?", scenTest.TableName)
		getStatements[index] = fmt.Sprintf(
			"SELECT * FROM \"%v\" WHERE title=? AND year=?", scenTest.TableName)
		deleteStatements[index] = fmt.Sprintf(
			"DELETE FROM \"%v\" WHERE title=? AND year=?", scenTest.TableName)
		insertParamList[index] = []interface{}{customMovies[index].Title, customMovies[index].Year, customMovies[index].Info}
		updateParamList[index] = []interface{}{newRatings[index], customMovies[index].Title, customMovies[index].Year}
		getDelParamList[index] = []interface{}{customMovies[index].Title, customMovies[index].Year}
		interMovies[index] = customMovies[index]
		projectedMovies[index] = map[string]interface{}{"title": customMovies[index].Title, "rating": customMovies[index].Info["rating"]}
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubDescribeTable(
		scenTest.TableName, &testtools.StubError{Err: &types.ResourceNotFoundException{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubDescribeTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(insertStatements, insertParamList, nil, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(getStatements, getDelParamList, interMovies, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(updateStatements, updateParamList, nil, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("SELECT title, info.rating FROM \"%v\"", scenTest.TableName),
		nil, projectedMovies, nil))
	stubList = append(stubList, stubs.StubBatchExecuteStatement(deleteStatements, getDelParamList, nil, nil))
	stubList = append(stubList, stubs.StubDeleteTable(scenTest.TableName, nil))

	return stubList
}

// RunSubTest performs a batch test run with a set of stubs that are set up to run with
// or without errors.
func (scenTest *PartiQLBatchScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	RunPartiQLBatchScenario(*stubber.SdkConfig, scenTest.TableName)
}
