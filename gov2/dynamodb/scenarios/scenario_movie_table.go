// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"fmt"
	"log"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/actions"
)

// snippet-start:[gov2.dynamodb.Scenario_GettingStartedMovies]

// RunMovieScenario is an interactive example that shows you how to use the AWS SDK for Go
// to create and use an Amazon DynamoDB table that stores data about movies.
//
//   1. Create a table that can hold movie data.
//   2. Put, get, and update a single movie in the table.
//   3. Write movie data to the table from a sample JSON file.
//   4. Query for movies that were released in a given year.
//   5. Scan for movies that were released in a range of years.
//   6. Delete a movie from the table.
//   7. Delete the table.
//
// This example creates a DynamoDB service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// It uses a questioner from the `demotools` package to get input during the example.
// This package can be found in the ..\..\demotools folder of this repo.
//
// The specified movie sampler is used to get sample data from a URL that is loaded
// into the named table.
func RunMovieScenario(
	sdkConfig aws.Config, questioner demotools.IQuestioner, tableName string,
	movieSampler actions.IMovieSampler) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Printf("Something went wrong with the demo.")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon DynamoDB getting started demo.")
	log.Println(strings.Repeat("-", 88))

	tableBasics := actions.TableBasics{TableName: tableName,
		DynamoDbClient: dynamodb.NewFromConfig(sdkConfig)}

	exists, err := tableBasics.TableExists()
	if err != nil {
		panic(err)
	}
	if !exists {
		log.Printf("Creating table %v...\n", tableName)
		_, err = tableBasics.CreateMovieTable()
		if err != nil {
			panic(err)
		} else {
			log.Printf("Created table %v.\n", tableName)
		}
	} else {
		log.Printf("Table %v already exists.\n", tableName)
	}

	var customMovie actions.Movie
	customMovie.Title = questioner.Ask("Enter a movie title to add to the table:",
		[]demotools.IAnswerValidator{demotools.NotEmpty{}})
	customMovie.Year = questioner.AskInt("What year was it released?",
		[]demotools.IAnswerValidator{demotools.NotEmpty{}, demotools.InIntRange{
			Lower: 1900, Upper: 2030}})
	customMovie.Info = map[string]interface{}{}
	customMovie.Info["rating"] = questioner.AskFloat64(
		"Enter a rating between 1 and 10:", []demotools.IAnswerValidator{
			demotools.NotEmpty{}, demotools.InFloatRange{Lower: 1, Upper: 10}})
	customMovie.Info["plot"] = questioner.Ask("What's the plot? ",
		[]demotools.IAnswerValidator{demotools.NotEmpty{}})
	err = tableBasics.AddMovie(customMovie)
	if err == nil {
		log.Printf("Added %v to the movie table.\n", customMovie.Title)
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Let's update your movie. You previously rated it %v.\n", customMovie.Info["rating"])
	customMovie.Info["rating"] = questioner.AskFloat64(
		"What new rating would you give it?", []demotools.IAnswerValidator{
			demotools.NotEmpty{}, demotools.InFloatRange{Lower: 1, Upper: 10}})
	log.Printf("You summarized the plot as '%v'.\n", customMovie.Info["plot"])
	customMovie.Info["plot"] = questioner.Ask("What would you say now?",
		[]demotools.IAnswerValidator{demotools.NotEmpty{}})
	attributes, err := tableBasics.UpdateMovie(customMovie)
	if err == nil {
		log.Printf("Updated %v with new values.\n", customMovie.Title)
		for _, attVal := range attributes {
			for valKey, val := range attVal {
				log.Printf("\t%v: %v\n", valKey, val)
			}
		}
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Getting movie data from %v and adding 250 movies to the table...\n",
		movieSampler.GetURL())
	movies := movieSampler.GetSampleMovies()
	written, err := tableBasics.AddMovieBatch(movies, 250)
	if err != nil {
		panic(err)
	} else {
		log.Printf("Added %v movies to the table.\n", written)
	}

	show := 10
	if show > written {
		show = written
	}
	log.Printf("The first %v movies in the table are:", show)
	for index, movie := range movies[:show] {
		log.Printf("\t%v. %v\n", index+1, movie.Title)
	}
	movieIndex := questioner.AskInt(
		"Enter the number of a movie to get info about it: ", []demotools.IAnswerValidator{
			demotools.InIntRange{Lower: 1, Upper: show}},
	)
	movie, err := tableBasics.GetMovie(movies[movieIndex-1].Title, movies[movieIndex-1].Year)
	if err == nil {
		log.Println(movie)
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Let's get a list of movies released in a given year.")
	releaseYear := questioner.AskInt("Enter a year between 1972 and 2018: ",
		[]demotools.IAnswerValidator{demotools.InIntRange{Lower: 1972, Upper: 2018}},
	)
	releases, err := tableBasics.Query(releaseYear)
	if err == nil {
		if len(releases) == 0 {
			log.Printf("I couldn't find any movies released in %v!\n", releaseYear)
		} else {
			for _, movie = range releases {
				log.Println(movie)
			}
		}
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Now let's scan for movies released in a range of years.")
	startYear := questioner.AskInt("Enter a year: ", []demotools.IAnswerValidator{
		demotools.InIntRange{Lower: 1972, Upper: 2018}})
	endYear := questioner.AskInt("Enter another year: ", []demotools.IAnswerValidator{
		demotools.InIntRange{Lower: 1972, Upper: 2018}})
	releases, err = tableBasics.Scan(startYear, endYear)
	if err == nil {
		if len(releases) == 0 {
			log.Printf("I couldn't find any movies released between %v and %v!\n", startYear, endYear)
		} else {
			log.Printf("Found %v movies. In this list, the plot is <nil> because "+
				"we used a projection expression when scanning for items to return only "+
				"the title, year, and rating.\n", len(releases))
			for _, movie = range releases {
				log.Println(movie)
			}
		}
	}
	log.Println(strings.Repeat("-", 88))

	var tables []string
	if questioner.AskBool("Do you want to list all of your tables? (y/n) ", "y") {
		tables, err = tableBasics.ListTables()
		if err == nil {
			log.Printf("Found %v tables:", len(tables))
			for _, table := range tables {
				log.Printf("\t%v", table)
			}
		}
	}
	log.Println(strings.Repeat("-", 88))

	log.Printf("Let's remove your movie '%v'.\n", customMovie.Title)
	if questioner.AskBool("Do you want to delete it from the table? (y/n) ", "y") {
		err = tableBasics.DeleteMovie(customMovie)
	}
	if err == nil {
		log.Printf("Deleted %v.\n", customMovie.Title)
	}

	if questioner.AskBool("Delete the table, too? (y/n)", "y") {
		err = tableBasics.DeleteTable()
	} else {
		log.Println("Don't forget to delete the table when you're done or you might " +
			"incur charges on your account.")
	}
	if err == nil {
		log.Printf("Deleted table %v.\n", tableBasics.TableName)
	}

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.dynamodb.Scenario_GettingStartedMovies]
