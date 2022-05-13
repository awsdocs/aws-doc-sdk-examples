// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for table_basics.go.

package actions

import (
	"errors"
	"fmt"
	"reflect"
	"strconv"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func enterTest() (*testtools.AwsmStubber, *TableBasics) {
	stubber := testtools.NewStubber()
	basics := &TableBasics{TableName: "test-table", DynamoDbClient: dynamodb.NewFromConfig(*stubber.SdkConfig)}
	return stubber, basics
}

func TestTableBasics_TableExists(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { TableExists(nil, t) })
	t.Run("ResourceNotFoundException", func(t *testing.T) {
		TableExists(&testtools.StubError{Err: &types.ResourceNotFoundException{Message: aws.String("TestError")}}, t)
	})
	t.Run("TestError", func(t *testing.T) {
		TableExists(&testtools.StubError{Err: &testtools.StubError{Err: errors.New("TestError")}}, t)
	})
}

func TableExists(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()
	stubber.Add(stubs.StubDescribeTable(basics.TableName, raiseErr))

	exists, err := basics.TableExists()

	testtools.VerifyError(err, raiseErr, t, &types.ResourceNotFoundException{})
	var nfEx *types.ResourceNotFoundException
	if raiseErr == nil && !exists {
		t.Errorf("Expected to exist.\n")
	} else if errors.As(raiseErr, &nfEx) && exists {
		t.Errorf("Expected not exists, got %v.", exists)
	}

	testtools.ExitTest(stubber, t)
}

func TestTableBasics_CreateMovieTable(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { CreateMovieTable(nil, t) })
	t.Run("TestError", func(t *testing.T) { CreateMovieTable(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func CreateMovieTable(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()
	stubber.Add(stubs.StubCreateTable(basics.TableName, raiseErr))
	stubber.Add(stubs.StubDescribeTable(basics.TableName, raiseErr))

	tableDesc, err := basics.CreateMovieTable()

	testtools.VerifyError(err, raiseErr, t)
	if raiseErr == nil {
		if *tableDesc.TableName != basics.TableName {
			t.Errorf("Got table named %v, expected %v.\n", *tableDesc.TableName, basics.TableName)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestTableBasics_AddMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { AddMovie(nil, t) })
	t.Run("TestError", func(t *testing.T) { AddMovie(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func AddMovie(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	movie := Movie{Title: "Test movie", Year: 2001}
	item, marshErr := attributevalue.MarshalMap(movie)
	if marshErr != nil {
		panic(marshErr)
	}

	stubber.Add(stubs.StubAddMovie(basics.TableName, item, raiseErr))

	err := basics.AddMovie(movie)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestTableBasics_UpdateMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { UpdateMovie(nil, t) })
	t.Run("TestError", func(t *testing.T) { UpdateMovie(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func UpdateMovie(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	ratingS := "3.5"
	plot := "Test plot."
	movie := Movie{
		Title: "Test movie",
		Year:  2001,
		Info:  map[string]interface{}{"rating": 3.5, "plot": plot},
	}

	stubber.Add(stubs.StubUpdateMovie(basics.TableName, movie.GetKey(), ratingS, plot, raiseErr))

	attribs, err := basics.UpdateMovie(movie)

	testtools.VerifyError(err, raiseErr, t)
	if raiseErr == nil {
		if attribs["info"]["rating"] != 3.5 || attribs["info"]["plot"] != "Test plot." {
			t.Errorf("got %s but expected %s", attribs, movie)
		}
	}
	testtools.ExitTest(stubber, t)
}

func TestTableBasics_AddMovieBatch(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { AddMovieBatch(nil, t) })
	t.Run("TestError", func(t *testing.T) {
		AddMovieBatch(&testtools.StubError{Err: errors.New("TestError"), ContinueAfter: true}, t)
	})
}

func AddMovieBatch(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	var testData []Movie
	var inputRequests []types.WriteRequest
	for index := 0; index < 30; index++ {
		movie := Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  1920 + index,
		}
		testData = append(testData, movie)
		inputRequests = append(inputRequests, types.WriteRequest{
			PutRequest: &types.PutRequest{
				Item: map[string]types.AttributeValue{
					"title": &types.AttributeValueMemberS{Value: movie.Title},
					"year":  &types.AttributeValueMemberN{Value: strconv.Itoa(movie.Year)},
					"info":  &types.AttributeValueMemberNULL{Value: true},
				},
			},
		})
	}

	stubber.Add(stubs.StubAddMovieBatch(basics.TableName, inputRequests[0:25], raiseErr))
	stubber.Add(stubs.StubAddMovieBatch(basics.TableName, inputRequests[25:30], raiseErr))

	count, err := basics.AddMovieBatch(testData, 200)

	testtools.VerifyError(err, raiseErr, t)
	if raiseErr == nil {
		if count != len(testData) {
			t.Errorf("Got %v items written, expected %v.", count, len(testData))
		}
	}
	testtools.ExitTest(stubber, t)
}

func TestTableBasics_GetMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { GetMovie(nil, t) })
	t.Run("TestError", func(t *testing.T) { GetMovie(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func GetMovie(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	rating := 3.5
	ratingS := "3.5"
	plot := "Test plot"
	movie := Movie{Title: "Test movie", Year: 2001, Info: map[string]interface{}{"rating": rating, "plot": plot}}

	stubber.Add(stubs.StubGetMovie(basics.TableName, movie.GetKey(), movie.Title, strconv.Itoa(movie.Year),
		ratingS, plot, raiseErr))

	gotMovie, err := basics.GetMovie(movie.Title, movie.Year)

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if gotMovie.Title != movie.Title || gotMovie.Year != movie.Year {
			t.Errorf("got %s but expected %s", gotMovie, movie)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestTableBasics_Query(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { Query(nil, t) })
	t.Run("TestError", func(t *testing.T) { Query(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func Query(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	title := "Test movie"
	year := 2001
	yearS := "2001"

	stubber.Add(stubs.StubQuery(basics.TableName, title, yearS, raiseErr))

	movies, err := basics.Query(year)

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if len(movies) != 1 {
			t.Errorf("got %v movies but expected 1", len(movies))
		} else if movies[0].Title != title || movies[0].Year != year {
			t.Errorf("got %v, %v but expected %v, %v", movies[0].Title, movies[0].Year, title, year)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestTableBasics_Scan(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { Scan(nil, t) })
	t.Run("TestError", func(t *testing.T) { Scan(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func Scan(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	title := "Test movie"
	startYear := 2001
	startYearS := "2001"
	endYear := 2006
	endYearS := "2006"

	stubber.Add(stubs.StubScan(basics.TableName, title, startYearS, endYearS, raiseErr))

	movies, err := basics.Scan(startYear, endYear)

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if len(movies) != 1 {
			t.Errorf("got %v movies but expected 1", len(movies))
		} else if movies[0].Title != title || movies[0].Year != startYear {
			t.Errorf("got %v, %v but expected %v, %v", movies[0].Title, movies[0].Year, title, startYear)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestTableBasics_DeleteMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { DeleteMovie(nil, t) })
	t.Run("TestError", func(t *testing.T) { DeleteMovie(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func DeleteMovie(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	movie := Movie{Title: "Test title", Year: 2001}

	stubber.Add(stubs.StubDeleteItem(basics.TableName, movie.GetKey(), raiseErr))

	err := basics.DeleteMovie(movie)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestTableBasics_DeleteTable(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { DeleteTable(nil, t) })
	t.Run("TestError", func(t *testing.T) { DeleteTable(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func DeleteTable(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	stubber.Add(stubs.StubDeleteTable(basics.TableName, raiseErr))

	err := basics.DeleteTable()

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestTableBasics_ListTables(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { ListTables(nil, t) })
	t.Run("TestError", func(t *testing.T) { ListTables(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func ListTables(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()

	tableNames := []string{"Table 1", "Table 2", "Table 3"}

	stubber.Add(stubs.StubListTables(tableNames, raiseErr))

	tables, err := basics.ListTables()

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if !reflect.DeepEqual(tables, tableNames) {
			t.Errorf("got %v, expected %v", tables, tableNames)
		}
	}

	testtools.ExitTest(stubber, t)
}
