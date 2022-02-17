// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ssm.go.get_parameter]
package main

// snippet-start:[ssm.go.get_parameter.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ssm"
	"github.com/aws/aws-sdk-go/service/ssm/ssmiface"
)

// snippet-end:[ssm.go.get_parameter.imports]

// GetParameter fetches details of a parameter in SSM
// Inputs:
//     svc is an Amazon SSM service client
//     name is the name of the parameter
// Output:
//     If success, information about the parameter and nil
//     Otherwise, nil and an error from the call to GetParameter
func GetParameter(svc ssmiface.SSMAPI, name *string) (*ssm.GetParameterOutput, error) {
	// snippet-start:[ssm.go.get_parameter.call]
	results, err := svc.GetParameter(&ssm.GetParameterInput{
		Name:  name,
	})
	// snippet-end:[ssm.go.get_parameter.call]

	return results, err
}

func main() {
	// snippet-start:[ssm.go.get_parameter.args]
	parameterName := flag.String("n", "", "The name of the parameter")
	flag.Parse()

	if *parameterName == "" {
		fmt.Println("You must supply the name of the parameter")
		fmt.Println("-n NAME")
		return
	}

	// snippet-end:[ssm.go.get_parameter.args]

	// snippet-start:[ssm.go.get_parameter.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ssm.New(sess)
	// snippet-end:[ssm.go.get_parameter.session]

	results, err := GetParameter(svc, parameterName)
	if err != nil {
		fmt.Println(err.Error())
		return
	}

	// snippet-start:[ssm.go.get_parameter.display]
	fmt.Println(*results.Parameter.Value)
	// snippet-end:[ssm.go.get_parameter.display]
}

// snippet-end:[ssm.go.get_parameter]
