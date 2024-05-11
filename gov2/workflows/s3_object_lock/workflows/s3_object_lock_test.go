// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

import (
	"context"
	"fmt"
	"s3_object_lock/stubs"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func TestRunObjectLockScenario(t *testing.T) {
	scenTest := ObjectLockScenarioTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

type ObjectLockScenarioTest struct {
	Answers []string
}

func (scenTest *ObjectLockScenarioTest) SetupDataAndStubs() []testtools.Stub {
	cfg, err := config.LoadDefaultConfig(context.Background())
	if err != nil {
		panic(err)
	}
	bucketPrefix := "test-bucket"
	standardBucket := fmt.Sprintf("%s.%s", bucketPrefix, createInfo[0].name)
	lockBucket := fmt.Sprintf("%s.%s", bucketPrefix, createInfo[1].name)
	retentionBucket := fmt.Sprintf("%s.%s", bucketPrefix, createInfo[2].name)

	objVersions := []types.ObjectVersion{
		{Key: aws.String("example-0"), VersionId: aws.String("version-0")},
		{Key: aws.String("example-1"), VersionId: aws.String("version-1")},
	}

	checksum := types.ChecksumAlgorithmSha256

	scenTest.Answers = []string{
		bucketPrefix,       // CreateBuckets
		"",                 // EnableLockOnBucket
		"30",               // SetDefaultRetentionPolicy
		"",                 // UploadTestObjects
		"y", "y", "y", "y", // SetObjectLockConfigurations
		"1", "2", "1", "3", "1", "4", "1", "5", "1", "6", "1", "7", // InteractWithObjects
		"y", // Cleanup
	}

	var stubList []testtools.Stub

	// CreateBuckets
	stubList = append(stubList, stubs.StubCreateBucket(standardBucket, cfg.Region, false, nil))
	stubList = append(stubList, stubs.StubHeadBucket(standardBucket, nil))
	stubList = append(stubList, stubs.StubCreateBucket(lockBucket, cfg.Region, true, nil))
	stubList = append(stubList, stubs.StubHeadBucket(lockBucket, nil))
	stubList = append(stubList, stubs.StubCreateBucket(retentionBucket, cfg.Region, false, nil))
	stubList = append(stubList, stubs.StubHeadBucket(retentionBucket, nil))

	// EnableLockOnBucket
	stubList = append(stubList, stubs.StubPutBucketVersioning(retentionBucket, nil))
	stubList = append(stubList, stubs.StubPutObjectLockConfiguration(retentionBucket, types.ObjectLockEnabledEnabled, 0, types.ObjectLockRetentionModeGovernance, nil))

	// SetDefaultRetentionPolicy
	stubList = append(stubList, stubs.StubPutObjectLockConfiguration(retentionBucket, types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance, nil))

	// UploadTestObjects
	stubList = append(stubList, stubs.StubPutObject(standardBucket, "example-0", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(standardBucket, "example-0", nil))
	stubList = append(stubList, stubs.StubPutObject(standardBucket, "example-1", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(standardBucket, "example-1", nil))
	stubList = append(stubList, stubs.StubPutObject(lockBucket, "example-0", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(lockBucket, "example-0", nil))
	stubList = append(stubList, stubs.StubPutObject(lockBucket, "example-1", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(lockBucket, "example-1", nil))
	stubList = append(stubList, stubs.StubPutObject(retentionBucket, "example-0", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(retentionBucket, "example-0", nil))
	stubList = append(stubList, stubs.StubPutObject(retentionBucket, "example-1", &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(retentionBucket, "example-1", nil))

	// SetObjectLockConfigurations
	stubList = append(stubList, stubs.StubPutObjectLegalHold(lockBucket, "example-0", "", types.ObjectLockLegalHoldStatusOn, nil))
	stubList = append(stubList, stubs.StubPutObjectRetention(lockBucket, "example-1", nil))
	stubList = append(stubList, stubs.StubPutObjectLegalHold(retentionBucket, "example-0", "", types.ObjectLockLegalHoldStatusOn, nil))
	stubList = append(stubList, stubs.StubPutObjectRetention(retentionBucket, "example-1", nil))

	// InteractWithObjects
	var stubListAll = func() []testtools.Stub {
		return []testtools.Stub{
			stubs.StubListObjectVersions(standardBucket, objVersions, nil),
			stubs.StubListObjectVersions(lockBucket, objVersions, nil),
			stubs.StubListObjectVersions(retentionBucket, objVersions, nil),
		}
	}

	// ListAll
	stubList = append(stubList, stubListAll()...)
	// DeleteObject
	stubList = append(stubList, stubListAll()...)
	stubList = append(stubList, stubs.StubDeleteObject(standardBucket, *objVersions[0].Key, *objVersions[0].VersionId, false, nil))
	// DeleteRetentionObject
	stubList = append(stubList, stubListAll()...)
	stubList = append(stubList, stubs.StubDeleteObject(standardBucket, *objVersions[0].Key, *objVersions[0].VersionId, true, nil))
	// OverwriteObject
	stubList = append(stubList, stubListAll()...)
	stubList = append(stubList, stubs.StubPutObject(standardBucket, *objVersions[0].Key, &checksum, nil))
	stubList = append(stubList, stubs.StubHeadObject(standardBucket, *objVersions[0].Key, nil))
	// ViewRetention
	stubList = append(stubList, stubListAll()...)
	stubList = append(stubList, stubs.StubGetObjectRetention(standardBucket, *objVersions[0].Key, types.ObjectLockRetentionModeGovernance, time.Now(), nil))
	// ViewLegalHold
	stubList = append(stubList, stubListAll()...)
	stubList = append(stubList, stubs.StubGetObjectLegalHold(standardBucket, *objVersions[0].Key, *objVersions[0].VersionId, types.ObjectLockLegalHoldStatusOn, nil))
	// Finish
	stubList = append(stubList, stubListAll()...)

	// Cleanup
	for _, info := range createInfo {
		bucket := fmt.Sprintf("%s.%s", bucketPrefix, info.name)
		stubList = append(stubList, stubs.StubGetObjectLockConfiguration(bucket, types.ObjectLockEnabledEnabled, nil))
		stubList = append(stubList, stubs.StubListObjectVersions(bucket, objVersions, nil))
		for _, version := range objVersions {
			stubList = append(stubList, stubs.StubGetObjectLegalHold(bucket, *version.Key, *version.VersionId, types.ObjectLockLegalHoldStatusOn, nil))
			stubList = append(stubList, stubs.StubPutObjectLegalHold(bucket, *version.Key, *version.VersionId, types.ObjectLockLegalHoldStatusOff, nil))
		}
		stubList = append(stubList, stubs.StubDeleteObjects(bucket, objVersions, info.name != "standard-bucket", nil))
		stubList = append(stubList, stubs.StubDeleteBucket(bucket, nil))
	}

	return stubList
}

func (scenTest *ObjectLockScenarioTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	scenario := NewObjectLockScenario(*stubber.SdkConfig, &mockQuestioner)
	scenario.Run(context.Background())
}

func (scenTest *ObjectLockScenarioTest) Cleanup() {}
