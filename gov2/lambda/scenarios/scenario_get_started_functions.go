// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"archive/zip"
	"bytes"
	"context"
	"encoding/base64"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"os"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	iamtypes "github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/aws-sdk-go-v2/service/lambda"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/lambda/actions"
)

// snippet-start:[gov2.lambda.GetStartedFunctions_Helper]

// IScenarioHelper abstracts I/O and wait functions from a scenario so that they
// can be mocked for unit testing.
type IScenarioHelper interface {
	Pause(secs int)
	CreateDeploymentPackage(sourceFile string, destinationFile string) *bytes.Buffer
}

// ScenarioHelper lets the caller specify the path to Lambda handler functions.
type ScenarioHelper struct {
	HandlerPath string
}

// Pause waits for the specified number of seconds.
func (helper *ScenarioHelper) Pause(secs int) {
	time.Sleep(time.Duration(secs) * time.Second)
}

// CreateDeploymentPackage creates an AWS Lambda deployment package from a source file. The
// deployment package is stored in .zip format in a bytes.Buffer. The buffer can be
// used to pass a []byte to Lambda when creating the function.
// The specified destinationFile is the name to give the file when it's deployed to Lambda.
func (helper *ScenarioHelper) CreateDeploymentPackage(sourceFile string, destinationFile string) *bytes.Buffer {
	var err error
	buffer := &bytes.Buffer{}
	writer := zip.NewWriter(buffer)
	zFile, err := writer.Create(destinationFile)
	if err != nil {
		log.Panicf("Couldn't create destination archive %v. Here's why: %v\n", destinationFile, err)
	}
	sourceBody, err := os.ReadFile(fmt.Sprintf("%v/%v", helper.HandlerPath, sourceFile))
	if err != nil {
		log.Panicf("Couldn't read handler source file %v. Here's why: %v\n",
			sourceFile, err)
	} else {
		_, err = zFile.Write(sourceBody)
		if err != nil {
			log.Panicf("Couldn't write handler %v to zip archive. Here's why: %v\n",
				sourceFile, err)
		}
	}
	err = writer.Close()
	if err != nil {
		log.Panicf("Couldn't close zip writer. Here's why: %v\n", err)
	}
	return buffer
}

// snippet-end:[gov2.lambda.GetStartedFunctions_Helper]

// snippet-start:[gov2.lambda.Scenario_GetStartedFunctions]

// GetStartedFunctionsScenario shows you how to use AWS Lambda to perform the following
// actions:
//
//  1. Create an AWS Identity and Access Management (IAM) role and Lambda function, then upload handler code.
//  2. Invoke the function with a single parameter and get results.
//  3. Update the function code and configure with an environment variable.
//  4. Invoke the function with new parameters and get results. Display the returned execution log.
//  5. List the functions for your account, then clean up resources.
type GetStartedFunctionsScenario struct {
	sdkConfig       aws.Config
	functionWrapper actions.FunctionWrapper
	questioner      demotools.IQuestioner
	helper          IScenarioHelper
	isTestRun       bool
}

// NewGetStartedFunctionsScenario constructs a GetStartedFunctionsScenario instance from a configuration.
// It uses the specified config to get a Lambda client and create wrappers for the actions
// used in the scenario.
func NewGetStartedFunctionsScenario(sdkConfig aws.Config, questioner demotools.IQuestioner,
	helper IScenarioHelper) GetStartedFunctionsScenario {
	lambdaClient := lambda.NewFromConfig(sdkConfig)
	return GetStartedFunctionsScenario{
		sdkConfig:       sdkConfig,
		functionWrapper: actions.FunctionWrapper{LambdaClient: lambdaClient},
		questioner:      questioner,
		helper:          helper,
	}
}

// Run runs the interactive scenario.
func (scenario GetStartedFunctionsScenario) Run() {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong with the demo.\n")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the AWS Lambda get started with functions demo.")
	log.Println(strings.Repeat("-", 88))

	role := scenario.GetOrCreateRole()
	funcName := scenario.CreateFunction(role)
	scenario.InvokeIncrement(funcName)
	scenario.UpdateFunction(funcName)
	scenario.InvokeCalculator(funcName)
	scenario.ListFunctions()
	scenario.Cleanup(role, funcName)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// GetOrCreateRole checks whether the specified role exists and returns it if it does.
// Otherwise, a role is created that specifies Lambda as a trusted principal.
// The AWSLambdaBasicExecutionRole managed policy is attached to the role and the role
// is returned.
func (scenario GetStartedFunctionsScenario) GetOrCreateRole() *iamtypes.Role {
	var role *iamtypes.Role
	iamClient := iam.NewFromConfig(scenario.sdkConfig)
	log.Println("First, we need an IAM role that Lambda can assume.")
	roleName := scenario.questioner.Ask("Enter a name for the role:", demotools.NotEmpty{})
	getOutput, err := iamClient.GetRole(context.TODO(), &iam.GetRoleInput{
		RoleName: aws.String(roleName)})
	if err != nil {
		var noSuch *iamtypes.NoSuchEntityException
		if errors.As(err, &noSuch) {
			log.Printf("Role %v doesn't exist. Creating it....\n", roleName)
		} else {
			log.Panicf("Couldn't check whether role %v exists. Here's why: %v\n",
				roleName, err)
		}
	} else {
		role = getOutput.Role
		log.Printf("Found role %v.\n", *role.RoleName)
	}
	if role == nil {
		trustPolicy := PolicyDocument{
			Version: "2012-10-17",
			Statement: []PolicyStatement{{
				Effect:    "Allow",
				Principal: map[string]string{"Service": "lambda.amazonaws.com"},
				Action:    []string{"sts:AssumeRole"},
			}},
		}
		policyArn := "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
		createOutput, err := iamClient.CreateRole(context.TODO(), &iam.CreateRoleInput{
			AssumeRolePolicyDocument: aws.String(trustPolicy.String()),
			RoleName:                 aws.String(roleName),
		})
		if err != nil {
			log.Panicf("Couldn't create role %v. Here's why: %v\n", roleName, err)
		}
		role = createOutput.Role
		_, err = iamClient.AttachRolePolicy(context.TODO(), &iam.AttachRolePolicyInput{
			PolicyArn: aws.String(policyArn),
			RoleName:  aws.String(roleName),
		})
		if err != nil {
			log.Panicf("Couldn't attach a policy to role %v. Here's why: %v\n", roleName, err)
		}
		log.Printf("Created role %v.\n", *role.RoleName)
		log.Println("Let's give AWS a few seconds to propagate resources...")
		scenario.helper.Pause(10)
	}
	log.Println(strings.Repeat("-", 88))
	return role
}

// CreateFunction creates a Lambda function and uploads a handler written in Python.
// The code for the Python handler is packaged as a []byte in .zip format.
func (scenario GetStartedFunctionsScenario) CreateFunction(role *iamtypes.Role) string {
	log.Println("Let's create a function that increments a number.\n" +
		"The function uses the 'lambda_handler_basic.py' script found in the \n" +
		"'handlers' directory of this project.")
	funcName := scenario.questioner.Ask("Enter a name for the Lambda function:", demotools.NotEmpty{})
	zipPackage := scenario.helper.CreateDeploymentPackage("lambda_handler_basic.py", fmt.Sprintf("%v.py", funcName))
	log.Printf("Creating function %v and waiting for it to be ready.", funcName)
	funcState := scenario.functionWrapper.CreateFunction(funcName, fmt.Sprintf("%v.lambda_handler", funcName),
		role.Arn, zipPackage)
	log.Printf("Your function is %v.", funcState)
	log.Println(strings.Repeat("-", 88))
	return funcName
}

// InvokeIncrement invokes a Lambda function that increments a number. The function
// parameters are contained in a Go struct that is used to serialize the parameters to
// a JSON payload that is passed to the function.
// The result payload is deserialized into a Go struct that contains an int value.
func (scenario GetStartedFunctionsScenario) InvokeIncrement(funcName string) {
	parameters := actions.IncrementParameters{Action: "increment"}
	log.Println("Let's invoke our function. This function increments a number.")
	parameters.Number = scenario.questioner.AskInt("Enter a number to increment:", demotools.NotEmpty{})
	log.Printf("Invoking %v with %v...\n", funcName, parameters.Number)
	invokeOutput := scenario.functionWrapper.Invoke(funcName, parameters, false)
	var payload actions.LambdaResultInt
	err := json.Unmarshal(invokeOutput.Payload, &payload)
	if err != nil {
		log.Panicf("Couldn't unmarshal payload from invoking %v. Here's why: %v\n",
			funcName, err)
	}
	log.Printf("Invoking %v with %v returned %v.\n", funcName, parameters.Number, payload)
	log.Println(strings.Repeat("-", 88))
}

// UpdateFunction updates the code for a Lambda function by uploading a simple arithmetic
// calculator written in Python. The code for the Python handler is packaged as a
// []byte in .zip format.
// After the code is updated, the configuration is also updated with a new log
// level that instructs the handler to log additional information.
func (scenario GetStartedFunctionsScenario) UpdateFunction(funcName string) {
	log.Println("Let's update the function to an arithmetic calculator.\n" +
		"The function uses the 'lambda_handler_calculator.py' script found in the \n" +
		"'handlers' directory of this project.")
	scenario.questioner.Ask("Press Enter when you're ready.")
	log.Println("Creating deployment package...")
	zipPackage := scenario.helper.CreateDeploymentPackage("lambda_handler_calculator.py",
		fmt.Sprintf("%v.py", funcName))
	log.Println("...and updating the Lambda function and waiting for it to be ready.")
	funcState := scenario.functionWrapper.UpdateFunctionCode(funcName, zipPackage)
	log.Printf("Updated function %v. Its current state is %v.", funcName, funcState)
	log.Println("This function uses an environment variable to control logging level.")
	log.Println("Let's set it to DEBUG to get the most logging.")
	scenario.functionWrapper.UpdateFunctionConfiguration(funcName,
		map[string]string{"LOG_LEVEL": "DEBUG"})
	log.Println(strings.Repeat("-", 88))
}

// InvokeCalculator invokes the Lambda calculator function. The parameters are stored in a
// Go struct that is used to serialize the parameters to a JSON payload. That payload is then passed
// to the function.
// The result payload is deserialized to a Go struct that stores the result as either an
// int or float32, depending on the kind of operation that was specified.
func (scenario GetStartedFunctionsScenario) InvokeCalculator(funcName string) {
	wantInvoke := true
	choices := []string{"plus", "minus", "times", "divided-by"}
	for wantInvoke {
		choice := scenario.questioner.AskChoice("Select an arithmetic operation:\n", choices)
		x := scenario.questioner.AskInt("Enter a value for x:", demotools.NotEmpty{})
		y := scenario.questioner.AskInt("Enter a value for y:", demotools.NotEmpty{})
		log.Printf("Invoking %v %v %v...", x, choices[choice], y)
		calcParameters := actions.CalculatorParameters{
			Action: choices[choice],
			X:      x,
			Y:      y,
		}
		invokeOutput := scenario.functionWrapper.Invoke(funcName, calcParameters, true)
		var payload any
		if choice == 3 { // divide-by results in a float.
			payload = actions.LambdaResultFloat{}
		} else {
			payload = actions.LambdaResultInt{}
		}
		err := json.Unmarshal(invokeOutput.Payload, &payload)
		if err != nil {
			log.Panicf("Couldn't unmarshal payload from invoking %v. Here's why: %v\n",
				funcName, err)
		}
		log.Printf("Invoking %v with %v %v %v returned %v.\n", funcName,
			calcParameters.X, calcParameters.Action, calcParameters.Y, payload)
		scenario.questioner.Ask("Press Enter to see the logs from the call.")
		logRes, err := base64.StdEncoding.DecodeString(*invokeOutput.LogResult)
		if err != nil {
			log.Panicf("Couldn't decode log result. Here's why: %v\n", err)
		}
		log.Println(string(logRes))
		wantInvoke = scenario.questioner.AskBool("Do you want to calculate again? (y/n)", "y")
	}
	log.Println(strings.Repeat("-", 88))
}

// ListFunctions lists up to the specified number of functions for your account.
func (scenario GetStartedFunctionsScenario) ListFunctions() {
	count := scenario.questioner.AskInt(
		"Let's list functions for your account. How many do you want to see?", demotools.NotEmpty{})
	functions := scenario.functionWrapper.ListFunctions(count)
	log.Printf("Found %v functions:", len(functions))
	for _, function := range functions {
		log.Printf("\t%v", *function.FunctionName)
	}
	log.Println(strings.Repeat("-", 88))
}

// Cleanup removes the IAM and Lambda resources created by the example.
func (scenario GetStartedFunctionsScenario) Cleanup(role *iamtypes.Role, funcName string) {
	if scenario.questioner.AskBool("Do you want to clean up resources created for this example? (y/n)",
		"y") {
		iamClient := iam.NewFromConfig(scenario.sdkConfig)
		policiesOutput, err := iamClient.ListAttachedRolePolicies(context.TODO(),
			&iam.ListAttachedRolePoliciesInput{RoleName: role.RoleName})
		if err != nil {
			log.Panicf("Couldn't get policies attached to role %v. Here's why: %v\n",
				*role.RoleName, err)
		}
		for _, policy := range policiesOutput.AttachedPolicies {
			_, err = iamClient.DetachRolePolicy(context.TODO(), &iam.DetachRolePolicyInput{
				PolicyArn: policy.PolicyArn, RoleName: role.RoleName,
			})
			if err != nil {
				log.Panicf("Couldn't detach policy %v from role %v. Here's why: %v\n",
					*policy.PolicyArn, *role.RoleName, err)
			}
		}
		_, err = iamClient.DeleteRole(context.TODO(), &iam.DeleteRoleInput{RoleName: role.RoleName})
		if err != nil {
			log.Panicf("Couldn't delete role %v. Here's why: %v\n", *role.RoleName, err)
		}
		log.Printf("Deleted role %v.\n", *role.RoleName)

		scenario.functionWrapper.DeleteFunction(funcName)
		log.Printf("Deleted function %v.\n", funcName)
	} else {
		log.Println("Okay. Don't forget to delete the resources when you're done with them.")
	}
}

// snippet-end:[gov2.lambda.Scenario_GetStartedFunctions]
