// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"encoding/json"
	"log"
)

// PolicyDocument defines an AWS Identity and Access Management (IAM) policy document
// that can be serialized to JSON.
type PolicyDocument struct {
	Version   string
	Statement []PolicyStatement
}

// PolicyStatement defines a statement in a policy document.
type PolicyStatement struct {
	Effect    string
	Action    []string
	Principal map[string]string `json:",omitempty"`
	Resource  *string           `json:",omitempty"`
}

// String serializes the policy document to a JSON string.
func (doc *PolicyDocument) String() string {
	docBytes, err := json.Marshal(doc)
	if err != nil {
		log.Printf("Couldn't marshal policy document. Here's why: %v", err)
		panic(err)
	}
	return string(docBytes)
}
