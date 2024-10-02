// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the foundation model actions.

package actions

import (
	"context"
	"log"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrock"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func CallFoundationModelActions(sdkConfig aws.Config) {
	defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	bedrockClient := bedrock.NewFromConfig(sdkConfig)
	foundationModelWrapper := FoundationModelWrapper{bedrockClient}

	ctx := context.Background()
	models, err := foundationModelWrapper.ListFoundationModels(ctx)
	if err != nil {
		panic(err)
	}
	for _, model := range models {
		log.Println(*model.ModelId)
	}

	log.Printf("Thanks for watching!")
}

func TestCallFoundationModelActions(t *testing.T) {
	scenTest := FoundationModelActionsTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

type FoundationModelActionsTest struct{}

func (scenTest *FoundationModelActionsTest) SetupDataAndStubs() []testtools.Stub {
	var stubList []testtools.Stub
	stubList = append(stubList, stubs.StubListFoundationModels(nil))
	return stubList
}

func (scenTest *FoundationModelActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	CallFoundationModelActions(*stubber.SdkConfig)
}

func (scenTest *FoundationModelActionsTest) Cleanup() {}
