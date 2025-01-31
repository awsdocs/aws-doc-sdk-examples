// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.workflows.s3.ObjectLock.S3Actions.complete]
// snippet-start:[gov2.workflows.s3.ObjectLock.S3Actions.struct]

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"log"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/s3/manager"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/aws/smithy-go"
)

// S3Actions wraps S3 service actions.
type S3Actions struct {
	S3Client  *s3.Client
	S3Manager *manager.Uploader
}

// snippet-end:[gov2.workflows.s3.ObjectLock.S3Actions.struct]

// snippet-start:[gov2.workflows.s3.ObjectLock.CreateBucket]

// CreateBucketWithLock creates a new S3 bucket with optional object locking enabled
// and waits for the bucket to exist before returning.
func (actor S3Actions) CreateBucketWithLock(ctx context.Context, bucket string, region string, enableObjectLock bool) (string, error) {
	input := &s3.CreateBucketInput{
		Bucket: aws.String(bucket),
		CreateBucketConfiguration: &types.CreateBucketConfiguration{
			LocationConstraint: types.BucketLocationConstraint(region),
		},
	}

	if enableObjectLock {
		input.ObjectLockEnabledForBucket = aws.Bool(true)
	}

	_, err := actor.S3Client.CreateBucket(ctx, input)
	if err != nil {
		var owned *types.BucketAlreadyOwnedByYou
		var exists *types.BucketAlreadyExists
		if errors.As(err, &owned) {
			log.Printf("You already own bucket %s.\n", bucket)
			err = owned
		} else if errors.As(err, &exists) {
			log.Printf("Bucket %s already exists.\n", bucket)
			err = exists
		}
	} else {
		err = s3.NewBucketExistsWaiter(actor.S3Client).Wait(
			ctx, &s3.HeadBucketInput{Bucket: aws.String(bucket)}, time.Minute)
		if err != nil {
			log.Printf("Failed attempt to wait for bucket %s to exist.\n", bucket)
		}
	}

	return bucket, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.CreateBucket]

// snippet-start:[gov2.workflows.s3.ObjectLock.GetObjectLegalHold]

// GetObjectLegalHold retrieves the legal hold status for an S3 object.
func (actor S3Actions) GetObjectLegalHold(ctx context.Context, bucket string, key string, versionId string) (*types.ObjectLockLegalHoldStatus, error) {
	var status *types.ObjectLockLegalHoldStatus
	input := &s3.GetObjectLegalHoldInput{
		Bucket:    aws.String(bucket),
		Key:       aws.String(key),
		VersionId: aws.String(versionId),
	}

	output, err := actor.S3Client.GetObjectLegalHold(ctx, input)
	if err != nil {
		var noSuchKeyErr *types.NoSuchKey
		var apiErr *smithy.GenericAPIError
		if errors.As(err, &noSuchKeyErr) {
			log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
			err = noSuchKeyErr
		} else if errors.As(err, &apiErr) {
			switch apiErr.ErrorCode() {
			case "NoSuchObjectLockConfiguration":
				log.Printf("Object %s does not have an object lock configuration.\n", key)
				err = nil
			case "InvalidRequest":
				log.Printf("Bucket %s does not have an object lock configuration.\n", bucket)
				err = nil
			}
		}
	} else {
		status = &output.LegalHold.Status
	}

	return status, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.GetObjectLegalHold]

// snippet-start:[gov2.workflows.s3.ObjectLock.GetObjectLockConfiguration]

// GetObjectLockConfiguration retrieves the object lock configuration for an S3 bucket.
func (actor S3Actions) GetObjectLockConfiguration(ctx context.Context, bucket string) (*types.ObjectLockConfiguration, error) {
	var lockConfig *types.ObjectLockConfiguration
	input := &s3.GetObjectLockConfigurationInput{
		Bucket: aws.String(bucket),
	}

	output, err := actor.S3Client.GetObjectLockConfiguration(ctx, input)
	if err != nil {
		var noBucket *types.NoSuchBucket
		var apiErr *smithy.GenericAPIError
		if errors.As(err, &noBucket) {
			log.Printf("Bucket %s does not exist.\n", bucket)
			err = noBucket
		} else if errors.As(err, &apiErr) && apiErr.ErrorCode() == "ObjectLockConfigurationNotFoundError" {
			log.Printf("Bucket %s does not have an object lock configuration.\n", bucket)
			err = nil
		}
	} else {
		lockConfig = output.ObjectLockConfiguration
	}

	return lockConfig, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.GetObjectLockConfiguration]

// snippet-start:[gov2.workflows.s3.ObjectLock.GetObjectRetention]

// GetObjectRetention retrieves the object retention configuration for an S3 object.
func (actor S3Actions) GetObjectRetention(ctx context.Context, bucket string, key string) (*types.ObjectLockRetention, error) {
	var retention *types.ObjectLockRetention
	input := &s3.GetObjectRetentionInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
	}

	output, err := actor.S3Client.GetObjectRetention(ctx, input)
	if err != nil {
		var noKey *types.NoSuchKey
		var apiErr *smithy.GenericAPIError
		if errors.As(err, &noKey) {
			log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
			err = noKey
		} else if errors.As(err, &apiErr) {
			switch apiErr.ErrorCode() {
			case "NoSuchObjectLockConfiguration":
				err = nil
			case "InvalidRequest":
				log.Printf("Bucket %s does not have locking enabled.", bucket)
				err = nil
			}
		}
	} else {
		retention = output.Retention
	}

	return retention, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.GetObjectRetention]

// snippet-start:[gov2.workflows.s3.ObjectLock.PutObjectLegalHold]

// PutObjectLegalHold sets the legal hold configuration for an S3 object.
func (actor S3Actions) PutObjectLegalHold(ctx context.Context, bucket string, key string, versionId string, legalHoldStatus types.ObjectLockLegalHoldStatus) error {
	input := &s3.PutObjectLegalHoldInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
		LegalHold: &types.ObjectLockLegalHold{
			Status: legalHoldStatus,
		},
	}
	if versionId != "" {
		input.VersionId = aws.String(versionId)
	}

	_, err := actor.S3Client.PutObjectLegalHold(ctx, input)
	if err != nil {
		var noKey *types.NoSuchKey
		if errors.As(err, &noKey) {
			log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
			err = noKey
		}
	}

	return err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.PutObjectLegalHold]

// snippet-start:[gov2.workflows.s3.ObjectLock.PutObjectLockConfiguration.ModifyDefaultBucketRetention]

// ModifyDefaultBucketRetention modifies the default retention period of an existing bucket.
func (actor S3Actions) ModifyDefaultBucketRetention(
	ctx context.Context, bucket string, lockMode types.ObjectLockEnabled, retentionPeriod int32, retentionMode types.ObjectLockRetentionMode) error {

	input := &s3.PutObjectLockConfigurationInput{
		Bucket: aws.String(bucket),
		ObjectLockConfiguration: &types.ObjectLockConfiguration{
			ObjectLockEnabled: lockMode,
			Rule: &types.ObjectLockRule{
				DefaultRetention: &types.DefaultRetention{
					Days: aws.Int32(retentionPeriod),
					Mode: retentionMode,
				},
			},
		},
	}
	_, err := actor.S3Client.PutObjectLockConfiguration(ctx, input)
	if err != nil {
		var noBucket *types.NoSuchBucket
		if errors.As(err, &noBucket) {
			log.Printf("Bucket %s does not exist.\n", bucket)
			err = noBucket
		}
	}

	return err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.PutObjectLockConfiguration.ModifyDefaultBucketRetention]

// snippet-start:[gov2.workflows.s3.ObjectLock.PubObjectLockConfiguration.EnableObjectLockOnBucket]

// EnableObjectLockOnBucket enables object locking on an existing bucket.
func (actor S3Actions) EnableObjectLockOnBucket(ctx context.Context, bucket string) error {
	// Versioning must be enabled on the bucket before object locking is enabled.
	verInput := &s3.PutBucketVersioningInput{
		Bucket: aws.String(bucket),
		VersioningConfiguration: &types.VersioningConfiguration{
			MFADelete: types.MFADeleteDisabled,
			Status:    types.BucketVersioningStatusEnabled,
		},
	}
	_, err := actor.S3Client.PutBucketVersioning(ctx, verInput)
	if err != nil {
		var noBucket *types.NoSuchBucket
		if errors.As(err, &noBucket) {
			log.Printf("Bucket %s does not exist.\n", bucket)
			err = noBucket
		}
		return err
	}

	input := &s3.PutObjectLockConfigurationInput{
		Bucket: aws.String(bucket),
		ObjectLockConfiguration: &types.ObjectLockConfiguration{
			ObjectLockEnabled: types.ObjectLockEnabledEnabled,
		},
	}
	_, err = actor.S3Client.PutObjectLockConfiguration(ctx, input)
	if err != nil {
		var noBucket *types.NoSuchBucket
		if errors.As(err, &noBucket) {
			log.Printf("Bucket %s does not exist.\n", bucket)
			err = noBucket
		}
	}

	return err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.PubObjectLockConfiguration.EnableObjectLockOnBucket]

// snippet-start:[gov2.workflows.s3.ObjectLock.PutObjectRetention]

// PutObjectRetention sets the object retention configuration for an S3 object.
func (actor S3Actions) PutObjectRetention(ctx context.Context, bucket string, key string, retentionMode types.ObjectLockRetentionMode, retentionPeriodDays int32) error {
	input := &s3.PutObjectRetentionInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
		Retention: &types.ObjectLockRetention{
			Mode:            retentionMode,
			RetainUntilDate: aws.Time(time.Now().AddDate(0, 0, int(retentionPeriodDays))),
		},
		BypassGovernanceRetention: aws.Bool(true),
	}

	_, err := actor.S3Client.PutObjectRetention(ctx, input)
	if err != nil {
		var noKey *types.NoSuchKey
		if errors.As(err, &noKey) {
			log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
			err = noKey
		}
	}

	return err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.PutObjectRetention]

// snippet-start:[gov2.workflows.s3.ObjectLock.UploadObject]

// UploadObject uses the S3 upload manager to upload an object to a bucket.
func (actor S3Actions) UploadObject(ctx context.Context, bucket string, key string, contents string) (string, error) {
	var outKey string
	input := &s3.PutObjectInput{
		Bucket:            aws.String(bucket),
		Key:               aws.String(key),
		Body:              bytes.NewReader([]byte(contents)),
		ChecksumAlgorithm: types.ChecksumAlgorithmSha256,
	}
	output, err := actor.S3Manager.Upload(ctx, input)
	if err != nil {
		var noBucket *types.NoSuchBucket
		if errors.As(err, &noBucket) {
			log.Printf("Bucket %s does not exist.\n", bucket)
			err = noBucket
		}
	} else {
		err := s3.NewObjectExistsWaiter(actor.S3Client).Wait(ctx, &s3.HeadObjectInput{
			Bucket: aws.String(bucket),
			Key:    aws.String(key),
		}, time.Minute)
		if err != nil {
			log.Printf("Failed attempt to wait for object %s to exist in %s.\n", key, bucket)
		} else {
			outKey = *output.Key
		}
	}
	return outKey, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.UploadObject]

// snippet-start:[gov2.workflows.s3.ObjectLock.ListObjectVersions]

// ListObjectVersions lists all versions of all objects in a bucket.
func (actor S3Actions) ListObjectVersions(ctx context.Context, bucket string) ([]types.ObjectVersion, error) {
	var err error
	var output *s3.ListObjectVersionsOutput
	var versions []types.ObjectVersion
	input := &s3.ListObjectVersionsInput{Bucket: aws.String(bucket)}
	versionPaginator := s3.NewListObjectVersionsPaginator(actor.S3Client, input)
	for versionPaginator.HasMorePages() {
		output, err = versionPaginator.NextPage(ctx)
		if err != nil {
			var noBucket *types.NoSuchBucket
			if errors.As(err, &noBucket) {
				log.Printf("Bucket %s does not exist.\n", bucket)
				err = noBucket
			}
			break
		} else {
			versions = append(versions, output.Versions...)
		}
	}
	return versions, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.ListObjectVersions]

// snippet-start:[gov2.workflows.s3.ObjectLock.DeleteObject]

// DeleteObject deletes an object from a bucket.
func (actor S3Actions) DeleteObject(ctx context.Context, bucket string, key string, versionId string, bypassGovernance bool) (bool, error) {
	deleted := false
	input := &s3.DeleteObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
	}
	if versionId != "" {
		input.VersionId = aws.String(versionId)
	}
	if bypassGovernance {
		input.BypassGovernanceRetention = aws.Bool(true)
	}
	_, err := actor.S3Client.DeleteObject(ctx, input)
	if err != nil {
		var noKey *types.NoSuchKey
		var apiErr *smithy.GenericAPIError
		if errors.As(err, &noKey) {
			log.Printf("Object %s does not exist in %s.\n", key, bucket)
			err = noKey
		} else if errors.As(err, &apiErr) {
			switch apiErr.ErrorCode() {
			case "AccessDenied":
				log.Printf("Access denied: cannot delete object %s from %s.\n", key, bucket)
				err = nil
			case "InvalidArgument":
				if bypassGovernance {
					log.Printf("You cannot specify bypass governance on a bucket without lock enabled.")
					err = nil
				}
			}
		}
	} else {
		err = s3.NewObjectNotExistsWaiter(actor.S3Client).Wait(
			ctx, &s3.HeadObjectInput{Bucket: aws.String(bucket), Key: aws.String(key)}, time.Minute)
		if err != nil {
			log.Printf("Failed attempt to wait for object %s in bucket %s to be deleted.\n", key, bucket)
		} else {
			deleted = true
		}
	}
	return deleted, err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.DeleteObject]

// snippet-start:[gov2.workflows.s3.ObjectLock.DeleteObjects]

// DeleteObjects deletes a list of objects from a bucket.
func (actor S3Actions) DeleteObjects(ctx context.Context, bucket string, objects []types.ObjectIdentifier, bypassGovernance bool) error {
	if len(objects) == 0 {
		return nil
	}

	input := s3.DeleteObjectsInput{
		Bucket: aws.String(bucket),
		Delete: &types.Delete{
			Objects: objects,
			Quiet:   aws.Bool(true),
		},
	}
	if bypassGovernance {
		input.BypassGovernanceRetention = aws.Bool(true)
	}
	delOut, err := actor.S3Client.DeleteObjects(ctx, &input)
	if err != nil || len(delOut.Errors) > 0 {
		log.Printf("Error deleting objects from bucket %s.\n", bucket)
		if err != nil {
			var noBucket *types.NoSuchBucket
			if errors.As(err, &noBucket) {
				log.Printf("Bucket %s does not exist.\n", bucket)
				err = noBucket
			}
		} else if len(delOut.Errors) > 0 {
			for _, outErr := range delOut.Errors {
				log.Printf("%s: %s\n", *outErr.Key, *outErr.Message)
			}
			err = fmt.Errorf("%s", *delOut.Errors[0].Message)
		}
	} else {
		for _, delObjs := range delOut.Deleted {
			err = s3.NewObjectNotExistsWaiter(actor.S3Client).Wait(
				ctx, &s3.HeadObjectInput{Bucket: aws.String(bucket), Key: delObjs.Key}, time.Minute)
			if err != nil {
				log.Printf("Failed attempt to wait for object %s to be deleted.\n", *delObjs.Key)
			} else {
				log.Printf("Deleted %s.\n", *delObjs.Key)
			}
		}
	}
	return err
}

// snippet-end:[gov2.workflows.s3.ObjectLock.DeleteObjects]

// snippet-end:[gov2.workflows.s3.ObjectLock.S3Actions.complete]
