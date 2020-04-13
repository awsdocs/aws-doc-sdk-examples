/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package main

import (
    "encoding/json"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

    "github.com/google/uuid"
)

type Config struct {
    TableName   string `json:"TableName"`
    MovieName   string `json:"MovieName"`
    MovieYear   string `json:"MovieYear"`
    MovieRating string `json:"MovieRating"`
}

type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    if globalConfig.MovieYear == "" {
        globalConfig.MovieYear = "2000"
    } else {

        year, err := strconv.Atoi(globalConfig.MovieYear)
        if err != nil {
            return err
        }
        if year < 1900 {
            year = 1900
            globalConfig.MovieYear = strconv.Itoa(year)
        }
        if year > 2020 {
            year = 2020
            globalConfig.MovieYear = strconv.Itoa(year)
        }
    }

    if globalConfig.MovieRating == "" {
        globalConfig.MovieRating = "0.5"
    } else {

        rating, err := strconv.ParseFloat(globalConfig.MovieRating, 64)
        if err != nil {
            return err
        }
        if rating < 0.0 {
            rating = 0.0
            globalConfig.MovieRating = strconv.FormatFloat(rating, 'E', 1, 64)
        }
        if rating > 1.0 {
            rating = 1.0
            globalConfig.MovieRating = strconv.FormatFloat(rating, 'E', 1, 64)
        }
    }

    t.Log("TableName:   " + globalConfig.TableName)
    t.Log("MovieName:   " + globalConfig.MovieName)
    t.Log("MovieYear:   " + globalConfig.MovieYear)
    t.Log("MovieRating: " + globalConfig.MovieRating)

    return nil
}

func createTable(sess *session.Session, tableName *string) error {
    svc := dynamodb.New(sess)

    input := &dynamodb.CreateTableInput{
        AttributeDefinitions: []*dynamodb.AttributeDefinition{
            {
                AttributeName: aws.String("Year"),
                AttributeType: aws.String("N"),
            },
            {
                AttributeName: aws.String("Title"),
                AttributeType: aws.String("S"),
            },
        },
        KeySchema: []*dynamodb.KeySchemaElement{
            {
                AttributeName: aws.String("Year"),
                KeyType:       aws.String("HASH"),
            },
            {
                AttributeName: aws.String("Title"),
                KeyType:       aws.String("RANGE"),
            },
        },
        ProvisionedThroughput: &dynamodb.ProvisionedThroughput{
            ReadCapacityUnits:  aws.Int64(10),
            WriteCapacityUnits: aws.Int64(10),
        },
        TableName: tableName,
    }

    _, err := svc.CreateTable(input)
    if err != nil {
        return err
    }

    err = svc.WaitUntilTableExists(&dynamodb.DescribeTableInput{
        TableName: tableName,
    })
    if err != nil {
        return err
    }

    return nil
}

func addItem(sess *session.Session, tableName, movieName, movieYear, movieRating *string) error {
    svc := dynamodb.New(sess)

    year, err := strconv.Atoi(*movieYear)
    if err != nil {
        return err
    }

    rating, err := strconv.ParseFloat(*movieRating, 64)
    if err != nil {
        return err
    }

    item := Item{
        Year:   year,
        Title:  *movieName,
        Plot:   "Nothing happens at all.",
        Rating: rating,
    }

    av, err := dynamodbattribute.MarshalMap(item)
    if err != nil {
        return err
    }

    input := &dynamodb.PutItemInput{
        Item:      av,
        TableName: tableName,
    }

    _, err = svc.PutItem(input)
    if err != nil {
        return err
    }

    return nil
}

func deleteItem(sess *session.Session, tableName, movieName, movieYear *string) error {
    svc := dynamodb.New(sess)

    input := &dynamodb.DeleteItemInput{
        Key: map[string]*dynamodb.AttributeValue{
            "Year": {
                N: movieYear,
            },
            "Title": {
                S: movieName,
            },
        },
        TableName: tableName,
    }

    _, err := svc.DeleteItem(input)
    if err != nil {
        return err
    }

    return nil
}

func deleteTable(sess *session.Session, tableName *string) error {
    svc := dynamodb.New(sess)

    input := &dynamodb.DeleteTableInput{
        TableName: tableName,
    }

    _, err := svc.DeleteTable(input)
    if err != nil {
        return err
    }

    err = svc.WaitUntilTableNotExists(&dynamodb.DescribeTableInput{
        TableName: tableName,
    })
    if err != nil {
        return err
    }

    return nil
}

func TestUpdateItem(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    tableCreated := false

    if globalConfig.MovieName == "" || globalConfig.MovieRating == "" || globalConfig.MovieYear == "" || globalConfig.TableName == "" {
        id := uuid.New()
        globalConfig.TableName = "test-movies-" + id.String()
        globalConfig.MovieName = "The Big New Movie"
        globalConfig.MovieYear = "2014"
        globalConfig.MovieRating = "1.0"

        err := createTable(sess, &globalConfig.TableName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created table " + globalConfig.TableName)

        err = addItem(sess, &globalConfig.TableName, &globalConfig.MovieName, &globalConfig.MovieYear, &globalConfig.MovieRating)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.TableName + " yourself")
            t.Fatal(err)
        }

        t.Log("Added item with name: " + globalConfig.MovieName + ", year: " + globalConfig.MovieYear + " and rating: " + globalConfig.MovieRating)

        tableCreated = true
    }

    globalConfig.MovieYear = "2015"
    globalConfig.MovieRating = "0.5"

    err = UpdateMovie(sess, &globalConfig.TableName, &globalConfig.MovieName, &globalConfig.MovieYear, &globalConfig.MovieRating)
    if err != nil {
        t.Log("You'll have to delete " + globalConfig.TableName + " yourself")
        t.Fatal(err)
    }

    t.Log("Updated item to name: " + globalConfig.MovieName + ", year: " + globalConfig.MovieYear + " and rating: " + globalConfig.MovieRating)

    if tableCreated {
        err := deleteItem(sess, &globalConfig.TableName, &globalConfig.MovieName, &globalConfig.MovieYear)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.TableName + " yourself")
            t.Fatal(err)
        }

        err = deleteTable(sess, &globalConfig.TableName)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.TableName + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted table " + globalConfig.TableName)
    }
}
