// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"
    
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/SERVICE"
    "github.com/aws/aws-sdk-go/service/SERVIVE/SERVICEiface"
)

// Define a mock struct to use in unit tests
//type mockSERVICEClient struct {
//    SERVICEiface.SERVICEAPI
//}

// Then for every *real* call you make in the example:
// func (m *mockSERVICEClient) API(input *SERVICE.APIInput) (*SERVIVE.APIOutput, error) {
       // Check that required inputs exist
       
//     resp := SERVICE.APIOutput{}
//     return &resp, nil
// }

func Test???(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    RESOURCE := "test-RESOURCE"

    mockSvc := &mockSERVICEClient{}

    err := EXAMPLE(mockSvc, &RESOURCE)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("VERB RESOURCE " + RESOURCE)
}