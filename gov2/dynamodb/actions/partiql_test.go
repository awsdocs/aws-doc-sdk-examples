// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for partiql.go.

package actions

import (
	"errors"
	"fmt"
	"testing"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func enterPartiQLTest() (*testtools.AwsmStubber, *PartiQLRunner) {
	stubber := testtools.NewStubber()
	runner := &PartiQLRunner{TableName: "test-table", DynamoDbClient: dynamodb.NewFromConfig(*stubber.SdkConfig)}
	return stubber, runner
}

func TestPartiQL_AddMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { AddMoviePartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { AddMoviePartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func AddMoviePartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movie := Movie{Title: "Test movie", Year: 2001, Info: map[string]interface{}{
		"rating": 3.5, "plot": "Not bad."}}

	stubber.Add(stubs.StubExecuteStatement(
		fmt.Sprintf("INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}", runner.TableName),
		[]interface{}{movie.Title, movie.Year, movie.Info}, nil, raiseErr))

	err := runner.AddMovie(movie)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestPartiQL_GetMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { GetMoviePartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { GetMoviePartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func GetMoviePartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movie := Movie{Title: "Test movie", Year: 2001, Info: map[string]interface{}{
		"rating": 3.5, "plot": "Not bad."}}

	stubber.Add(stubs.StubExecuteStatement(
		fmt.Sprintf("SELECT * FROM \"%v\" WHERE title=? AND year=?", runner.TableName),
		[]interface{}{movie.Title, movie.Year}, movie, raiseErr))

	gotMovie, err := runner.GetMovie(movie.Title, movie.Year)

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if gotMovie.Title != movie.Title || gotMovie.Year != movie.Year {
			t.Errorf("got %s but expected %s", gotMovie, movie)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestPartiQL_GetAllMovies(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { GetAllMoviesPartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { GetAllMoviesPartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func GetAllMoviesPartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	outProjection := map[string]interface{}{"title": "Test movie", "rating": 3.5}

	stubber.Add(stubs.StubExecuteStatement(
		fmt.Sprintf("SELECT title, info.rating FROM \"%v\"", runner.TableName),
		nil, outProjection, raiseErr))

	gotProjections, err := runner.GetAllMovies()

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		if len(gotProjections) > 1 ||
			gotProjections[0]["title"] != outProjection["title"] ||
			gotProjections[0]["year"] != outProjection["year"] {
			t.Errorf("got %s but expected %s", gotProjections, outProjection)
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestPartiQL_UpdateMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { UpdateMoviePartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { UpdateMoviePartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func UpdateMoviePartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movie := Movie{Title: "Test movie", Year: 2001, Info: map[string]interface{}{
		"rating": 3.5, "plot": "Not bad."}}
	newRating := 5.6

	stubber.Add(stubs.StubExecuteStatement(
		fmt.Sprintf("UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?", runner.TableName),
		[]interface{}{newRating, movie.Title, movie.Year}, movie, raiseErr))

	err := runner.UpdateMovie(movie, newRating)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestPartiQL_DeleteMovie(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { DeleteMoviePartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { DeleteMoviePartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func DeleteMoviePartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movie := Movie{Title: "Test movie", Year: 2001, Info: map[string]interface{}{
		"rating": 3.5, "plot": "Not bad."}}

	stubber.Add(stubs.StubExecuteStatement(
		fmt.Sprintf("DELETE FROM \"%v\" WHERE title=? AND year=?", runner.TableName),
		[]interface{}{movie.Title, movie.Year}, movie, raiseErr))

	err := runner.DeleteMovie(movie)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestPartiQL_AddMovieBatch(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { AddMovieBatchPartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { AddMovieBatchPartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func AddMovieBatchPartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movies := make([]Movie, 3)
	statements := make([]string, len(movies))
	paramList := make([][]interface{}, len(movies))
	for index := range movies {
		movies[index] = Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  2000 + index,
			Info: map[string]interface{}{
				"rating": float64(index) + 0.5, "plot": "Not great."},
		}
		statements[index] = fmt.Sprintf(
			"INSERT INTO \"%v\" VALUE {'title': ?, 'year': ?, 'info': ?}", runner.TableName)
		paramList[index] = []interface{}{movies[index].Title, movies[index].Year, movies[index].Info}
	}

	stubber.Add(stubs.StubBatchExecuteStatement(statements, paramList, nil, raiseErr))

	err := runner.AddMovieBatch(movies)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestPartiQL_GetMovieBatch(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { GetMovieBatchPartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { GetMovieBatchPartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func GetMovieBatchPartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movies := make([]Movie, 3)
	statements := make([]string, len(movies))
	paramList := make([][]interface{}, len(movies))
	for index := range movies {
		movies[index] = Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  2000 + index,
			Info: map[string]interface{}{
				"rating": float64(index) + 0.5, "plot": "Not great."},
		}
		statements[index] = fmt.Sprintf(
			"SELECT * FROM \"%v\" WHERE title=? AND year=?", runner.TableName)
		paramList[index] = []interface{}{movies[index].Title, movies[index].Year}
	}

	intMovies := make([]interface{}, len(movies))
	for index := range movies {
		intMovies[index] = movies[index]
	}
	stubber.Add(stubs.StubBatchExecuteStatement(statements, paramList, intMovies, raiseErr))

	outMovies, err := runner.GetMovieBatch(movies)

	testtools.VerifyError(err, raiseErr, t)
	if err == nil {
		for index := range movies {
			inMovie := movies[index]
			outMovie := outMovies[index]
			if outMovie.Title != inMovie.Title || outMovie.Year != inMovie.Year {
				t.Errorf("got %s but expected %s", outMovie, inMovie)
			}
		}
	}

	testtools.ExitTest(stubber, t)
}

func TestPartiQL_UpdateMovieBatch(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { UpdateMovieBatchPartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { UpdateMovieBatchPartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func UpdateMovieBatchPartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movies := make([]Movie, 3)
	newRatings := make([]float64, len(movies))
	statements := make([]string, len(movies))
	paramList := make([][]interface{}, len(movies))
	for index := range movies {
		movies[index] = Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  2000 + index,
		}
		newRatings[index] = float64(index) + 0.5
		statements[index] = fmt.Sprintf(
			"UPDATE \"%v\" SET info.rating=? WHERE title=? AND year=?", runner.TableName)
		paramList[index] = []interface{}{newRatings[index], movies[index].Title, movies[index].Year}
	}

	stubber.Add(stubs.StubBatchExecuteStatement(statements, paramList, nil, raiseErr))

	err := runner.UpdateMovieBatch(movies, newRatings)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}

func TestPartiQL_DeleteMovieBatch(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { DeleteMovieBatchPartiQL(nil, t) })
	t.Run("TestError", func(t *testing.T) { DeleteMovieBatchPartiQL(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func DeleteMovieBatchPartiQL(raiseErr *testtools.StubError, t *testing.T) {
	stubber, runner := enterPartiQLTest()

	movies := make([]Movie, 3)
	statements := make([]string, len(movies))
	paramList := make([][]interface{}, len(movies))
	for index := range movies {
		movies[index] = Movie{
			Title: fmt.Sprintf("Test movie %v", index),
			Year:  2000 + index,
		}
		statements[index] = fmt.Sprintf(
			"DELETE FROM \"%v\" WHERE title=? AND year=?", runner.TableName)
		paramList[index] = []interface{}{movies[index].Title, movies[index].Year}
	}

	stubber.Add(stubs.StubBatchExecuteStatement(statements, paramList, nil, raiseErr))

	err := runner.DeleteMovieBatch(movies)

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}
