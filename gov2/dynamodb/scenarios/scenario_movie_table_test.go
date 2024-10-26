// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the movie table scenario.

package scenarios

import (
	"context"
	"fmt"
	"strconv"
	"testing"

	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// MockSampler mocks the movie sampler and returns a small set of predefined data.
type MockSampler struct {
}

func (sampler MockSampler) GetURL() string { return "http://example.com" }
func (sampler MockSampler) GetSampleMovies() []actions.Movie {
	var movies []actions.Movie
	for index := 1; index <= 5; index++ {
		movies = append(movies, actions.Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  2000 + index,
			Info: map[string]interface{}{
				"rating": 1.4 + float64(index),
				"plot":   fmt.Sprintf("Test plot %v", index),
			},
		})
	}
	return movies
}

// TestRunMovieScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunMovieScenario(t *testing.T) {
	scenTest := MovieScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// MovieScenarioTest encapsulates data for a scenario test.
type MovieScenarioTest struct {
	TableName string
	Answers   []string
	Sampler   MockSampler
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *MovieScenarioTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.TableName = "doc-example-test-movie-table"
	title := "Test movie"
	year := 2002
	addRating := 3.5
	addRatingS := "3.5"
	addPlot := "Add test plot."
	addMovie := actions.Movie{
		Title: title,
		Year:  year,
		Info:  map[string]interface{}{"rating": addRating, "plot": addPlot},
	}
	addMovieItem, marshErr := attributevalue.MarshalMap(addMovie)
	if marshErr != nil {
		panic(marshErr)
	}
	updateRating := 6.6
	updateRatingS := "6.6"
	updatePlot := "Update test plot."
	updateMovie := actions.Movie{
		Title: title,
		Year:  year,
		Info:  map[string]interface{}{"rating": updateRating, "plot": updatePlot},
	}
	getIndex := 2
	queryYear := "1985"
	scanStart := "2001"
	scanEnd := "2010"
	tableNames := []string{"Table 1", "Table 2", "Table 3"}
	scenTest.Answers = []string{
		addMovie.Title,
		strconv.Itoa(addMovie.Year),
		addRatingS,
		addPlot,
		updateRatingS,
		updatePlot,
		strconv.Itoa(getIndex + 1),
		queryYear,
		scanStart,
		scanEnd,
		"y",
		"y",
		"y",
	}

	scenTest.Sampler = MockSampler{}
	sampleMovies := scenTest.Sampler.GetSampleMovies()
	var writeReqs []types.WriteRequest
	for _, movie := range sampleMovies {
		item, marshErr := attributevalue.MarshalMap(movie)
		if marshErr != nil {
			panic(marshErr)
		}
		writeReqs = append(
			writeReqs,
			types.WriteRequest{PutRequest: &types.PutRequest{Item: item}})
	}

	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubDescribeTable(
		scenTest.TableName, &testtools.StubError{Err: &types.ResourceNotFoundException{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubCreateTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubDescribeTable(scenTest.TableName, nil))
	stubList = append(stubList, stubs.StubAddMovie(scenTest.TableName, addMovieItem, nil))
	stubList = append(stubList, stubs.StubUpdateMovie(scenTest.TableName, updateMovie.GetKey(), updateRatingS, updatePlot, nil))
	stubList = append(stubList, stubs.StubAddMovieBatch(scenTest.TableName, writeReqs, nil))
	stubList = append(stubList, stubs.StubGetMovie(
		scenTest.TableName, sampleMovies[getIndex].GetKey(), sampleMovies[getIndex].Title, strconv.Itoa(sampleMovies[getIndex].Year),
		strconv.FormatFloat(sampleMovies[getIndex].Info["rating"].(float64), 'f', 1, 64),
		sampleMovies[getIndex].Info["plot"].(string), nil))
	stubList = append(stubList, stubs.StubQuery(scenTest.TableName, title, queryYear, nil))
	stubList = append(stubList, stubs.StubScan(scenTest.TableName, title, scanStart, scanEnd, nil))
	stubList = append(stubList, stubs.StubListTables(tableNames, nil))
	stubList = append(stubList, stubs.StubDeleteItem(scenTest.TableName, addMovie.GetKey(), nil))
	stubList = append(stubList, stubs.StubDeleteTable(scenTest.TableName, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *MovieScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	RunMovieScenario(context.Background(), *stubber.SdkConfig, &mockQuestioner, scenTest.TableName, scenTest.Sampler)
}

func (scenTest *MovieScenarioTest) Cleanup() {}
