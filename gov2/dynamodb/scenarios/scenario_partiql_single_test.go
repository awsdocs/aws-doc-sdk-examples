// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the PartiQL single action scenario.

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

// TestRunPartiQLSingleScenario runs the scenario multiple times. The first time, it
// runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error, and it verifies the results.
func TestRunPartiQLSingleScenario(t *testing.T) {
	scenTest := PartiQLSingleScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// PartiQLSingleScenarioTest encapsulates data for a scenario test.
type PartiQLSingleScenarioTest struct {
	TableName string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used
// to return mocked data.
func (scenTest *PartiQLSingleScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.TableName = "doc-example-test-partiql-single-table"
	currentYear, _, _ := time.Now().Date()
	movie := actions.Movie{
		Title: "24 Hour PartiQL People",
		Year:  currentYear,
		Info: map[string]interface{}{
			"plot":   "A group of data developers discover a new query language they can't stop using.",
			"rating": 9.9,
		},
	}
	newRating := 6.6

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubDescribeTable(
		scenTest.TableName, &testtools.StubError{Err: &types.ResourceNotFoundException{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubDescribeTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}", scenTest.TableName),
		[]interface{}{movie.Title, movie.Year, movie.Info}, nil, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("SELECT * FROM \"%v\" WHERE title=? AND year=?", scenTest.TableName),
		[]interface{}{movie.Title, movie.Year}, movie, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?", scenTest.TableName),
		[]interface{}{newRating, movie.Title, movie.Year}, movie, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("SELECT * FROM \"%v\" WHERE title=? AND year=?", scenTest.TableName),
		[]interface{}{movie.Title, movie.Year}, movie, nil))
	stubList = append(stubList, stubs.StubExecuteStatement(
		fmt.Sprintf("DELETE FROM \"%v\" WHERE title=? AND year=?", scenTest.TableName),
		[]interface{}{movie.Title, movie.Year}, movie, nil))
	stubList = append(stubList, stubs.StubDeleteTable(scenTest.TableName, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs that are set up to run with
// or without errors.
func (scenTest *PartiQLSingleScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	RunPartiQLSingleScenario(*stubber.SdkConfig, scenTest.TableName)
}
