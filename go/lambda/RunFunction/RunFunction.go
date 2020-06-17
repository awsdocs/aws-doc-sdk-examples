// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[lambda.go.run_function]
package main

// snippet-start:[lambda.go.run_function.imports]
import (
    "encoding/json"
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)
// snippet-end:[lambda.go.run_function.imports]

// snippet-start:[lambda.go.run_function.structs]
type getItemsRequest struct {
    SortBy     string
    SortOrder  string
    ItemsToGet int
}

type getItemsResponseError struct {
    Message string `json:"message"`
}

type getItemsResponseData struct {
    Item string `json:"item"`
}

type getItemsResponseBody struct {
    Result string                 `json:"result"`
    Data   []getItemsResponseData `json:"data"`
    Error  getItemsResponseError  `json:"error"`
}

type getItemsResponseHeaders struct {
    ContentType string `json:"Content-Type"`
}

type getItemsResponse struct {
    StatusCode int                     `json:"statusCode"`
    Headers    getItemsResponseHeaders `json:"headers"`
    Body       getItemsResponseBody    `json:"body"`
}
// snippet-end:[lambda.go.run_function.structs]

// CallFunction calls an AWS Lambda function
// Inputs:
//     svc is an AWS Lambda service client
//     maxItems is the maximum number of items to retrieve
//     function is the name of the AWS Lambda function
// Output:
//     If success, the results of the function call and nil
//     Otherwise, nil and an error from the call to Invoke
func CallFunction(svc lambdaiface.LambdaAPI, maxItems *int, function *string) (*lambda.InvokeOutput, error) {
    // snippet-start:[lambda.go.run_function.marshall]
    request := getItemsRequest{"time", "descending", *maxItems}

    payload, err := json.Marshal(request)
    // snippet-end:[lambda.go.run_function.marshall]
    if err != nil {
        fmt.Println("Got an error marshalling the request")
        return nil, err
    }

    // snippet-start:[lambda.go.run_function.call]
    result, err := svc.Invoke(&lambda.InvokeInput{
        FunctionName: function,
        Payload:      payload,
    })
    // snippet-end:[lambda.go.run_function.call]
    return result, err
}

func main() {
    // snippet-start:[lambda.go.run_function.args]
    function := flag.String("f", "", "The name of the AWS Lambda function to invoke")
    maxItems := flag.Int("m", 10, "The maximum number of items to retrieve")
    flag.Parse()

    if *function == "" || *maxItems < 0 {
        fmt.Println("You must supply a function to call and maximum number of items to retrieve that is greater than zero")
        return
    }
    // snippet-end:[lambda.go.run_function.args]

    // snippet-start:[lambda.go.run_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)
    // snippet-end:[lambda.go.run_function.session]

    result, err := CallFunction(svc, maxItems, function)
    if err != nil {
        fmt.Println("Got drror calling " + *function + ":")
        fmt.Println(err)
        return
    }

    // snippet-start:[lambda.go.run_function.unmarshall]
    var resp getItemsResponse

    err = json.Unmarshal(result.Payload, &resp)
    // snippet-end:[lambda.go.run_function.unmarshall]
    if err != nil {
        fmt.Println("Got an error unmarshalling the response")
        fmt.Println(err)
        return
    }

    // If the status code is NOT 200, the call failed
    if resp.StatusCode != 200 {
        fmt.Println("Error getting items, StatusCode: " + strconv.Itoa(resp.StatusCode))
        return
    }

    // If the result is failure, we got an error
    if resp.Body.Result == "failure" {
        fmt.Println("Failed to get items")
        return
    }

    // snippet-start:[lambda.go.run_function.display]
    if len(resp.Body.Data) > 0 {
        for i := range resp.Body.Data {
            fmt.Println(resp.Body.Data[i].Item)
        }
    } else {
        fmt.Println("There were no items")
    }
    // snippet-end:[lambda.go.run_function.display]
}
// snippet-end:[lambda.go.run_function]
