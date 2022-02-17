// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ssm.go.put_parameter]
package main

// snippet-start:[ssm.go.put_parameter.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ssm"
	"github.com/aws/aws-sdk-go/service/ssm/ssmiface"
)

// snippet-end:[ssm.go.put_parameter.imports]

// PutParameter creates a SSM parameter
// Inputs:
//     svc is an Amazon SSM service client
//     name is the name of the parameter
// 	   value is the value of the parameter
//     paramType is type of the parameter
// Output:
//     If success, information about the saved parameter and nil
//     Otherwise, nil and an error from the call to PutParameter
func PutParameter(svc ssmiface.SSMAPI, name *string, value *string, paramType *string) (*ssm.PutParameterOutput, error) {
	// snippet-start:[ssm.go.put_parameter.call]
	results, err := svc.PutParameter(&ssm.PutParameterInput{
		Name:  name,
		Value: value,
		Type:  paramType,
	})
	// snippet-end:[ssm.go.put_parameter.call]

	return results, err
}

func main() {
	// snippet-start:[ssm.go.put_parameter.args]
	parameterName := flag.String("n", "", "The name of the parameter")
	parameterValue := flag.String("v", "", "The value of the parameter")
	parameterType := flag.String("t", "", "The type of the parameter")
	flag.Parse()

	if *parameterName == "" {
		fmt.Println("You must supply the name of the parameter")
		fmt.Println("-n NAME")
		return
	}

	if *parameterValue == "" {
		fmt.Println("You must supply the value of the parameter")
		fmt.Println("-v VALUE")
		return
	}

	if *parameterType == "" || (*parameterType != ssm.ParameterTypeString && *parameterType != ssm.ParameterTypeStringList && *parameterType != ssm.ParameterTypeSecureString) {
		fmt.Println("You must supply one of the listed value for Type (String,StringList,SecureString)")
		fmt.Println("-t TYPE")
		return
	}


	// snippet-end:[ssm.go.put_parameter.args]

	// snippet-start:[ssm.go.put_parameter.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ssm.New(sess)
	// snippet-end:[ssm.go.put_parameter.session]

	results, err := PutParameter(svc, parameterName, parameterValue, parameterType)
	if err != nil {
		fmt.Println(err.Error())
		return
	}

	// snippet-start:[ssm.go.put_parameter.display]
	fmt.Println(*results.Version)
	// snippet-end:[ssm.go.put_parameter.display]
}

// snippet-end:[ssm.go.put_parameter]
