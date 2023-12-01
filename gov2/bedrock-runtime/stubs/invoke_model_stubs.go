// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the Bedrock Runtime actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

type StubInvokeModelParams struct {
    Request  []byte
    Response []byte
    ModelId  string
    RaiseErr *testtools.StubError
}

func StubInvokeModel(params StubInvokeModelParams) testtools.Stub {
    return testtools.Stub{
    		OperationName: "InvokeModel",
    		Input:	&bedrockruntime.InvokeModelInput{
    			Body:        params.Request,
    			ModelId:     aws.String(params.ModelId),
    			ContentType: aws.String("application/json"),
    		},
    		Output:	&bedrockruntime.InvokeModelOutput{ Body: params.Response, },
    		Error:	params.RaiseErr,
    	}
}

// func StubInvokeTitanImage(requestBytes []byte, raiseErr *testtools.StubError) testtools.Stub {
//     fakeTitanImageResponse := actions.TitanImageResponse{
//         Images: []string {
//             "FakeBase64String==",
//         },
//     }
//
//     responseBytes, err := json.Marshal(fakeTitanImageResponse)
//     if err != nil {
//         panic(err)
//     }
//
//     return testtools.Stub{
//         OperationName: "InvokeModel",
//         Input:	&bedrockruntime.InvokeModelInput{
//             Body:        requestBytes,
//             ModelId:     aws.String("amazon.titan-image-generator-v1"),
//             ContentType: aws.String("application/json"),
//         },
//         Output:	&bedrockruntime.InvokeModelOutput{
//             Body:        responseBytes,
//         },
//         Error:	raiseErr,
//     }
// }