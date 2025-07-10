// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.go.SSMBasics]
package scenarios

import (
	"context"
	"log"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/ssm/actions"
)

// SSMBasics contains the AWS Systems Manager operations used in the examples.
// It contains ParameterWrapper, DocumentWrapper, and CommandWrapper objects that wrap
// Systems Manager actions.
type SSMBasics struct {
	ParameterWrapper actions.ParameterWrapper
	DocumentWrapper  actions.DocumentWrapper
	CommandWrapper   actions.CommandWrapper
	questioner       demotools.IQuestioner
}

// NewSSMBasics constructs an SSMBasics instance from a configuration.
func NewSSMBasics(parameterWrapper actions.ParameterWrapper, documentWrapper actions.DocumentWrapper, commandWrapper actions.CommandWrapper, questioner demotools.IQuestioner) *SSMBasics {
	return &SSMBasics{
		ParameterWrapper: parameterWrapper,
		DocumentWrapper:  documentWrapper,
		CommandWrapper:   commandWrapper,
		questioner:       questioner,
	}
}

// Run runs the SSM basics scenario.
func (basics *SSMBasics) Run(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong with the demo: %v\n", r)
		}
	}()

	log.Println(strings.Repeat("=", 88))
	log.Println("Welcome to the AWS Systems Manager (SSM) basics demo!")
	log.Println(strings.Repeat("=", 88))

	basics.ParameterStoreDemo(ctx)
	basics.DocumentDemo(ctx)
	basics.RunCommandDemo(ctx)

	log.Println(strings.Repeat("=", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("=", 88))
}

// ParameterStoreDemo demonstrates Parameter Store operations.
func (basics *SSMBasics) ParameterStoreDemo(ctx context.Context) {
	log.Println(strings.Repeat("-", 88))
	log.Println("Parameter Store Demo")
	log.Println(strings.Repeat("-", 88))

	// Put a parameter
	paramName := "/demo/myapp/database-url"
	paramValue := "jdbc:mysql://localhost:3306/mydb"

	log.Printf("Putting parameter: %s", paramName)
	err := basics.ParameterWrapper.PutParameter(ctx, paramName, paramValue, types.ParameterTypeString)
	if err != nil {
		log.Printf("Error putting parameter: %v", err)
		return
	}

	// Get the parameter
	log.Printf("Getting parameter: %s", paramName)
	param, err := basics.ParameterWrapper.GetParameter(ctx, paramName, false)
	if err != nil {
		log.Printf("Error getting parameter: %v", err)
		return
	}
	log.Printf("Parameter value: %s", *param.Value)

	// Put a secure parameter
	secureParamName := "/demo/myapp/database-password"
	secureParamValue := "super-secret-password"

	log.Printf("Putting secure parameter: %s", secureParamName)
	err = basics.ParameterWrapper.PutParameter(ctx, secureParamName, secureParamValue, types.ParameterTypeSecureString)
	if err != nil {
		log.Printf("Error putting secure parameter: %v", err)
		return
	}

	// Get parameters by path
	log.Println("Getting parameters by path: /demo/myapp/")
	params, err := basics.ParameterWrapper.GetParametersByPath(ctx, "/demo/myapp/", false, true)
	if err != nil {
		log.Printf("Error getting parameters by path: %v", err)
		return
	}

	for _, p := range params {
		log.Printf("  %s = %s (Type: %s)", *p.Name, *p.Value, p.Type)
	}

	// Clean up parameters
	if basics.questioner.AskBool("Do you want to delete the demo parameters? (y/n)", "y") {
		log.Println("Deleting demo parameters...")
		err = basics.ParameterWrapper.DeleteParameter(ctx, paramName)
		if err != nil {
			log.Printf("Error deleting parameter %s: %v", paramName, err)
		}

		err = basics.ParameterWrapper.DeleteParameter(ctx, secureParamName)
		if err != nil {
			log.Printf("Error deleting parameter %s: %v", secureParamName, err)
		}
	}
}

// DocumentDemo demonstrates SSM document operations.
func (basics *SSMBasics) DocumentDemo(ctx context.Context) {
	log.Println(strings.Repeat("-", 88))
	log.Println("Document Demo")
	log.Println(strings.Repeat("-", 88))

	// List documents
	log.Println("Listing first 5 SSM documents:")
	docs, err := basics.DocumentWrapper.ListDocuments(ctx, 5)
	if err != nil {
		log.Printf("Error listing documents: %v", err)
		return
	}

	for i, doc := range docs {
		log.Printf("  %d. %s (%s)", i+1, *doc.Name, doc.DocumentFormat)
	}

	// Describe a common AWS document
	documentName := "AWS-RunShellScript"
	log.Printf("Describing document: %s", documentName)
	docDesc, err := basics.DocumentWrapper.DescribeDocument(ctx, documentName)
	if err != nil {
		log.Printf("Error describing document: %v", err)
		return
	}

	log.Printf("  Document: %s", *docDesc.Name)
	log.Printf("  Description: %s", *docDesc.Description)
	log.Printf("  Document Type: %s", docDesc.DocumentType)
	log.Printf("  Platform Types: %v", docDesc.PlatformTypes)
}

// RunCommandDemo demonstrates Run Command operations.
func (basics *SSMBasics) RunCommandDemo(ctx context.Context) {
	log.Println(strings.Repeat("-", 88))
	log.Println("Run Command Demo")
	log.Println(strings.Repeat("-", 88))

	instanceId := basics.questioner.Ask("Enter an EC2 instance ID to run commands on (or press Enter to skip):")
	if instanceId == "" {
		log.Println("Skipping Run Command demo - no instance ID provided")
		return
	}

	// Run a simple shell command
	commands := []string{"echo 'Hello from SSM Run Command!'", "date", "whoami"}
	log.Printf("Running shell commands on instance: %s", instanceId)

	command, err := basics.CommandWrapper.RunShellCommand(ctx, []string{instanceId}, commands)
	if err != nil {
		log.Printf("Error running shell command: %v", err)
		return
	}

	log.Printf("Command sent successfully. Command ID: %s", *command.CommandId)
	log.Printf("Command Status: %s", command.Status)

	// Wait for command completion
	if basics.questioner.AskBool("Do you want to wait for the command to complete? (y/n)", "y") {
		log.Println("Waiting for command to complete...")
		err = basics.CommandWrapper.WaitForCommandCompletion(ctx, *command.CommandId, []string{instanceId}, 2*time.Minute)
		if err != nil {
			log.Printf("Error waiting for command completion: %v", err)
			return
		}

		// Get command output
		log.Println("Getting command output...")
		invocation, err := basics.CommandWrapper.GetCommandInvocation(ctx, *command.CommandId, instanceId)
		if err != nil {
			log.Printf("Error getting command invocation: %v", err)
			return
		}

		log.Printf("Command Status: %s", invocation.Status)
		if invocation.StandardOutputContent != nil {
			log.Printf("Standard Output:\n%s", *invocation.StandardOutputContent)
		}
		if invocation.StandardErrorContent != nil && *invocation.StandardErrorContent != "" {
			log.Printf("Standard Error:\n%s", *invocation.StandardErrorContent)
		}
	}

	// List recent commands
	log.Println("Listing recent commands for this instance:")
	recentCommands, err := basics.CommandWrapper.ListCommands(ctx, instanceId, 5)
	if err != nil {
		log.Printf("Error listing commands: %v", err)
		return
	}

	for i, cmd := range recentCommands {
		log.Printf("  %d. %s - %s (%s)", i+1, *cmd.CommandId, *cmd.DocumentName, cmd.Status)
	}
}

// snippet-end:[ssm.go.SSMBasics]
