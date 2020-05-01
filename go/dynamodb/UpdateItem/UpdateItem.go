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
// snippet-start:[dynamodb.go.update_movie]
package main

// snippet-start:[dynamodb.go.update_movie.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
)
// snippet-end:[dynamodb.go.update_movie.imports]

// UpdateMovie updates the year and rating of a movie in a table
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     tableName is the name of the table
//     movieName is the name of the movie
//     movieYear is the year the movie was released
//     movieRating is the rating, from 0.0 to 1.0, of the movie
// Output:
//     If success, nil
//     Otherwise, an error from the call to UpdateItem
func UpdateMovie(sess *session.Session, tableName, movieName, movieYear, movieRating *string) error {
    // snippet-start:[dynamodb.go.update_movie.call]
    svc := dynamodb.New(sess)

    input := &dynamodb.UpdateItemInput{
        ExpressionAttributeValues: map[string]*dynamodb.AttributeValue{
            ":r": {
                N: movieRating,
            },
        },
        TableName: tableName,
        Key: map[string]*dynamodb.AttributeValue{
            "Year": {
                N: movieYear,
            },
            "Title": {
                S: movieName,
            },
        },
        ReturnValues:     aws.String("UPDATED_NEW"),
        UpdateExpression: aws.String("set Rating = :r"),
    }

    _, err := svc.UpdateItem(input)
    // snippet-end:[dynamodb.go.update_movie.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[dynamodb.go.update_movie.args]
    tableName := flag.String("t", "", "The name of the table")
    movieName := flag.String("m", "", "The name of the movie")
    movieYear := flag.String("y", "", "The year the movie was made")
    movieRating := flag.String("r", "", "The rating, from 0 (zero) to 1 (one)")
    flag.Parse()

    if *tableName == "" || *movieName == "" || *movieYear == "" || *movieRating == "" {
        fmt.Println("You must supply a table name (-t TABLE), movie name (-m MOVIE), movie year (-y YEAR), and rating (-r RATING)")
        return
    }
    // snippet-end:[dynamodb.go.update_movie.args]

    // snippet-start:[dynamodb.go.update_movie.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[dynamodb.go.update_movie.session]

    err := UpdateMovie(sess, tableName, movieName, movieYear, movieRating)
    if err != nil {
        fmt.Println(err)
        return
    }

    // snippet-start:[dynamodb.go.update_movie.print]
    fmt.Println("Successfully updated '" + *movieName + "' (" + *movieYear + ") rating to " + *movieRating)
    // snippet-end:[dynamodb.go.update_movie.print]
}
// snippet-end:[dynamodb.go.update_movie]
