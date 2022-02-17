// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ssm.go.delete_parameter]
package main

// snippet-start:[ssm.go.delete_parameter.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ssm"
	"github.com/aws/aws-sdk-go/service/ssm/ssmiface"
)

// snippet-end:[ssm.go.delete_parameter.imports]

// DeleteParameter deletes a parameter in SSM
// Inputs:
//     svc is an Amazon SSM service client
//     name is the name of the parameter
// Output:
//     If success, return nil
//     Otherwise, error from the call to DeleteParameter
func DeleteParameter(svc ssmiface.SSMAPI, name *string) error {
	// snippet-start:[ssm.go.delete_parameter.call]
	_, err := svc.DeleteParameter(&ssm.DeleteParameterInput{
		Name:  name,
	})
	// snippet-end:[ssm.go.delete_parameter.call]

	return err
}

func main() {
	// snippet-start:[ssm.go.delete_parameter.args]
	parameterName := flag.String("n", "", "The name of the parameter")
	flag.Parse()

	if *parameterName == "" {
		fmt.Println("You must supply the name of the parameter")
		fmt.Println("-n NAME")
		return
	}

	// snippet-end:[ssm.go.delete_parameter.args]

	// snippet-start:[ssm.go.delete_parameter.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ssm.New(sess)
	// snippet-end:[ssm.go.delete_parameter.session]

	err := DeleteParameter(svc, parameterName)
	if err != nil {
		fmt.Println(err.Error())
		return
	}
}

// snippet-end:[ssm.go.delete_parameter]
