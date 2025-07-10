// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
)

// DocumentWrapper encapsulates AWS Systems Manager document operations.
// This wrapper simplifies calling the SSM service from your applications.
type DocumentWrapper struct {
	SSMClient *ssm.Client
}

// snippet-start:[ssm.go.ListDocuments]
// ListDocuments lists SSM documents in your account.
func (wrapper DocumentWrapper) ListDocuments(ctx context.Context, maxResults int32) ([]types.DocumentIdentifier, error) {
	var allDocuments []types.DocumentIdentifier
	input := &ssm.ListDocumentsInput{
		MaxResults: aws.Int32(maxResults),
	}

	paginator := ssm.NewListDocumentsPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't list documents: %w", err)
		}
		allDocuments = append(allDocuments, result.DocumentIdentifiers...)
	}

	return allDocuments, nil
}

// snippet-end:[ssm.go.ListDocuments]

// snippet-start:[ssm.go.DescribeDocument]
// DescribeDocument gets detailed information about an SSM document.
func (wrapper DocumentWrapper) DescribeDocument(ctx context.Context, name string) (*types.DocumentDescription, error) {
	result, err := wrapper.SSMClient.DescribeDocument(ctx, &ssm.DescribeDocumentInput{
		Name: aws.String(name),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't describe document %s: %w", name, err)
	}

	return result.Document, nil
}

// snippet-end:[ssm.go.DescribeDocument]

// snippet-start:[ssm.go.GetDocument]
// GetDocument retrieves the content of an SSM document.
func (wrapper DocumentWrapper) GetDocument(ctx context.Context, name string) (*ssm.GetDocumentOutput, error) {
	result, err := wrapper.SSMClient.GetDocument(ctx, &ssm.GetDocumentInput{
		Name: aws.String(name),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't get document %s: %w", name, err)
	}

	return result, nil
}

// snippet-end:[ssm.go.GetDocument]

// snippet-start:[ssm.go.CreateDocument]
// CreateDocument creates a new SSM document.
func (wrapper DocumentWrapper) CreateDocument(ctx context.Context, name, content string, docType types.DocumentType, docFormat types.DocumentFormat) (*types.DocumentDescription, error) {
	result, err := wrapper.SSMClient.CreateDocument(ctx, &ssm.CreateDocumentInput{
		Name:           aws.String(name),
		Content:        aws.String(content),
		DocumentType:   docType,
		DocumentFormat: docFormat,
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't create document %s: %w", name, err)
	}

	log.Printf("Successfully created document: %s", name)
	return result.DocumentDescription, nil
}

// snippet-end:[ssm.go.CreateDocument]

// snippet-start:[ssm.go.UpdateDocument]
// UpdateDocument updates an existing SSM document.
func (wrapper DocumentWrapper) UpdateDocument(ctx context.Context, name, content, documentVersion string) (*types.DocumentDescription, error) {
	result, err := wrapper.SSMClient.UpdateDocument(ctx, &ssm.UpdateDocumentInput{
		Name:            aws.String(name),
		Content:         aws.String(content),
		DocumentVersion: aws.String(documentVersion),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't update document %s: %w", name, err)
	}

	log.Printf("Successfully updated document: %s", name)
	return result.DocumentDescription, nil
}

// snippet-end:[ssm.go.UpdateDocument]

// snippet-start:[ssm.go.DeleteDocument]
// DeleteDocument deletes an SSM document.
func (wrapper DocumentWrapper) DeleteDocument(ctx context.Context, name string) error {
	_, err := wrapper.SSMClient.DeleteDocument(ctx, &ssm.DeleteDocumentInput{
		Name: aws.String(name),
	})
	if err != nil {
		return fmt.Errorf("couldn't delete document %s: %w", name, err)
	}

	log.Printf("Successfully deleted document: %s", name)
	return nil
}

// snippet-end:[ssm.go.DeleteDocument]

// snippet-start:[ssm.go.ListDocumentVersions]
// ListDocumentVersions lists all versions of an SSM document.
func (wrapper DocumentWrapper) ListDocumentVersions(ctx context.Context, name string) ([]types.DocumentVersionInfo, error) {
	var allVersions []types.DocumentVersionInfo
	input := &ssm.ListDocumentVersionsInput{
		Name:       aws.String(name),
		MaxResults: aws.Int32(10),
	}

	paginator := ssm.NewListDocumentVersionsPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't list document versions for %s: %w", name, err)
		}
		allVersions = append(allVersions, result.DocumentVersions...)
	}

	return allVersions, nil
}

// snippet-end:[ssm.go.ListDocumentVersions]

// snippet-start:[ssm.go.DescribeDocumentPermission]
// DescribeDocumentPermission gets the permissions for an SSM document.
func (wrapper DocumentWrapper) DescribeDocumentPermission(ctx context.Context, name string, permissionType types.DocumentPermissionType) ([]types.AccountSharingInfo, error) {
	result, err := wrapper.SSMClient.DescribeDocumentPermission(ctx, &ssm.DescribeDocumentPermissionInput{
		Name:           aws.String(name),
		PermissionType: permissionType,
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't describe document permission for %s: %w", name, err)
	}

	return result.AccountSharingInfoList, nil
}

// snippet-end:[ssm.go.DescribeDocumentPermission]
