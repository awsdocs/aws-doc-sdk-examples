// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubCreateBucket(bucket string, region string, enableObjectLock bool, raiseErr *testtools.StubError) testtools.Stub {
	input := &s3.CreateBucketInput{
		Bucket: aws.String(bucket),
		CreateBucketConfiguration: &types.CreateBucketConfiguration{
			LocationConstraint: types.BucketLocationConstraint(region),
		},
	}
	if enableObjectLock {
		input.ObjectLockEnabledForBucket = aws.Bool(enableObjectLock)
	}

	return testtools.Stub{
		OperationName: "CreateBucket",
		Input:         input,
		Output:        &s3.CreateBucketOutput{},
		Error:         raiseErr,
	}
}

func StubHeadBucket(bucket string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "HeadBucket",
		Input:         &s3.HeadBucketInput{Bucket: aws.String(bucket)},
		Output:        &s3.HeadBucketOutput{},
		Error:         raiseErr,
		SkipErrorTest: true,
	}
}

func StubGetObjectLegalHold(bucket string, key string, versionId string, legalHoldStatus types.ObjectLockLegalHoldStatus, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetObjectLegalHold",
		Input:         &s3.GetObjectLegalHoldInput{Bucket: aws.String(bucket), Key: aws.String(key), VersionId: aws.String(versionId)},
		Output:        &s3.GetObjectLegalHoldOutput{LegalHold: &types.ObjectLockLegalHold{Status: legalHoldStatus}},
		Error:         raiseErr,
	}
}

func StubGetObjectLockConfiguration(bucket string, lockEnabled types.ObjectLockEnabled, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetObjectLockConfiguration",
		Input:         &s3.GetObjectLockConfigurationInput{Bucket: aws.String(bucket)},
		Output: &s3.GetObjectLockConfigurationOutput{
			ObjectLockConfiguration: &types.ObjectLockConfiguration{ObjectLockEnabled: lockEnabled},
		},
		Error: raiseErr,
	}
}

func StubGetObjectRetention(bucket string, key string, retention types.ObjectLockRetentionMode, until time.Time, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetObjectRetention",
		Input: &s3.GetObjectRetentionInput{
			Bucket: aws.String(bucket),
			Key:    aws.String(key),
		},
		Output: &s3.GetObjectRetentionOutput{Retention: &types.ObjectLockRetention{
			Mode:            retention,
			RetainUntilDate: aws.Time(until),
		}},
		Error: raiseErr,
	}
}

func StubPutBucketVersioning(bucket string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutBucketVersioning",
		Input: &s3.PutBucketVersioningInput{
			Bucket: aws.String(bucket),
			VersioningConfiguration: &types.VersioningConfiguration{
				MFADelete: types.MFADeleteDisabled,
				Status:    types.BucketVersioningStatusEnabled,
			},
		},
		Output: &s3.PutBucketVersioningOutput{},
		Error:  raiseErr,
	}
}

func StubPutObject(bucket string, key string, checksum *types.ChecksumAlgorithm, raiseErr *testtools.StubError) testtools.Stub {
	input := &s3.PutObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
	}
	if checksum != nil {
		input.ChecksumAlgorithm = *checksum
	}
	return testtools.Stub{
		OperationName: "PutObject",
		Input:         input,
		Output:        &s3.PutObjectOutput{},
		Error:         raiseErr,
		IgnoreFields:  []string{"Body"},
	}
}

func StubHeadObject(bucket string, key string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "HeadObject",
		Input:         &s3.HeadObjectInput{Bucket: aws.String(bucket), Key: aws.String(key)},
		Output:        &s3.HeadObjectOutput{},
		Error:         raiseErr,
		SkipErrorTest: true,
	}
}

func StubPutObjectLegalHold(bucket string, key string, versionId string, legalHoldStatus types.ObjectLockLegalHoldStatus, raiseErr *testtools.StubError) testtools.Stub {
	input := &s3.PutObjectLegalHoldInput{
		Bucket: aws.String(bucket), Key: aws.String(key), LegalHold: &types.ObjectLockLegalHold{Status: legalHoldStatus}}
	if versionId != "" {
		input.VersionId = aws.String(versionId)
	}
	return testtools.Stub{
		OperationName: "PutObjectLegalHold",
		Input:         input,
		Output:        &s3.PutObjectLegalHoldOutput{},
		Error:         raiseErr,
	}
}

func StubPutObjectLockConfiguration(bucket string, lockMode types.ObjectLockEnabled, retentionDays int32, retentionMode types.ObjectLockRetentionMode, raiseErr *testtools.StubError) testtools.Stub {
	input := &s3.PutObjectLockConfigurationInput{
		Bucket:                  aws.String(bucket),
		ObjectLockConfiguration: &types.ObjectLockConfiguration{ObjectLockEnabled: lockMode},
	}
	if retentionDays > 0 {
		input.ObjectLockConfiguration.Rule = &types.ObjectLockRule{DefaultRetention: &types.DefaultRetention{
			Days: aws.Int32(retentionDays),
			Mode: retentionMode,
		}}
	}

	return testtools.Stub{
		OperationName: "PutObjectLockConfiguration",
		Input:         input,
		Output:        &s3.PutObjectLockConfigurationOutput{},
		Error:         raiseErr,
	}
}

func StubPutObjectRetention(bucket string, key string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutObjectRetention",
		Input: &s3.PutObjectRetentionInput{
			Bucket:                    aws.String(bucket),
			Key:                       aws.String(key),
			BypassGovernanceRetention: aws.Bool(true),
		},
		Output:       &s3.PutObjectRetentionOutput{},
		IgnoreFields: []string{"Retention"},
		Error:        raiseErr,
	}
}

func StubListObjectVersions(bucket string, versions []types.ObjectVersion, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListObjectVersions",
		Input:         &s3.ListObjectVersionsInput{Bucket: aws.String(bucket)},
		Output:        &s3.ListObjectVersionsOutput{Versions: versions},
		Error:         raiseErr,
	}
}

func StubDeleteObject(bucket string, key string, versionId string, bypassGovernance bool, raiseErr *testtools.StubError) testtools.Stub {
	input := &s3.DeleteObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
	}
	if versionId != "" {
		input.VersionId = aws.String(versionId)
	}
	if bypassGovernance {
		input.BypassGovernanceRetention = aws.Bool(bypassGovernance)
	}
	return testtools.Stub{
		OperationName: "DeleteObject",
		Input:         input,
		Output:        &s3.DeleteObjectOutput{},
		Error:         raiseErr,
	}
}

func StubDeleteObjects(bucket string, objVersions []types.ObjectVersion, bypassGov bool, raiseErr *testtools.StubError) testtools.Stub {
	delObjs := make([]types.ObjectIdentifier, len(objVersions))
	delOuts := make([]types.DeletedObject, len(objVersions))
	for i := 0; i < len(objVersions); i++ {
		delObjs[i] = types.ObjectIdentifier{
			Key:       objVersions[i].Key,
			VersionId: objVersions[i].VersionId,
		}
		delOuts[i] = types.DeletedObject{Key: objVersions[i].Key}
	}
	input := &s3.DeleteObjectsInput{
		Bucket: aws.String(bucket),
		Delete: &types.Delete{
			Objects: delObjs,
			Quiet:   aws.Bool(true),
		},
	}
	if bypassGov {
		input.BypassGovernanceRetention = aws.Bool(true)
	}
	return testtools.Stub{
		OperationName: "DeleteObjects",
		Input:         input,
		Output:        &s3.DeleteObjectsOutput{Deleted: delOuts},
		Error:         raiseErr,
	}
}

func StubDeleteBucket(bucket string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteBucket",
		Input:         &s3.DeleteBucketInput{Bucket: aws.String(bucket)},
		Output:        &s3.DeleteBucketOutput{},
		Error:         raiseErr,
	}
}
