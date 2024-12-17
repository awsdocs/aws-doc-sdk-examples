// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.cognito-identity-provider.Resources.complete]

import (
	"context"
	"log"
	"user_pools_and_lambda_triggers/actions"

	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// Resources keeps track of AWS resources created during an example and handles
// cleanup when the example finishes.
type Resources struct {
	userPoolId       string
	userAccessTokens []string
	triggers         []actions.Trigger

	cognitoActor *actions.CognitoActions
	questioner   demotools.IQuestioner
}

func (resources *Resources) init(cognitoActor *actions.CognitoActions, questioner demotools.IQuestioner) {
	resources.userAccessTokens = []string{}
	resources.triggers = []actions.Trigger{}
	resources.cognitoActor = cognitoActor
	resources.questioner = questioner
}

// Cleanup deletes all AWS resources created during an example.
func (resources *Resources) Cleanup(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong during cleanup.\n%v\n", r)
			log.Println("Use the AWS Management Console to remove any remaining resources \n" +
				"that were created for this scenario.")
		}
	}()

	wantDelete := resources.questioner.AskBool("Do you want to remove all of the AWS resources that were created "+
		"during this demo (y/n)?", "y")
	if wantDelete {
		for _, accessToken := range resources.userAccessTokens {
			err := resources.cognitoActor.DeleteUser(ctx, accessToken)
			if err != nil {
				log.Println("Couldn't delete user during cleanup.")
				panic(err)
			}
			log.Println("Deleted user.")
		}
		triggerList := make([]actions.TriggerInfo, len(resources.triggers))
		for i := 0; i < len(resources.triggers); i++ {
			triggerList[i] = actions.TriggerInfo{Trigger: resources.triggers[i], HandlerArn: nil}
		}
		err := resources.cognitoActor.UpdateTriggers(ctx, resources.userPoolId, triggerList...)
		if err != nil {
			log.Println("Couldn't update Cognito triggers during cleanup.")
			panic(err)
		}
		log.Println("Removed Cognito triggers from user pool.")
	} else {
		log.Println("Be sure to remove resources when you're done with them to avoid unexpected charges!")
	}
}

// snippet-end:[gov2.cognito-identity-provider.Resources.complete]
