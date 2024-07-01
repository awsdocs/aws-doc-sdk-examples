// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

import (
	"context"
	"log"
	"s3_object_lock/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// snippet-start:[gov2.workflows.s3.ObjectLock.Resources.complete]

// DemoBucket contains metadata for buckets used in this example.
type DemoBucket struct {
	name             string
	legalHold        bool
	retentionEnabled bool
	objectKeys       []string
}

// Resources keeps track of AWS resources created during the ObjectLockScenario and handles
// cleanup when the scenario finishes.
type Resources struct {
	demoBuckets map[string]*DemoBucket

	s3Actions  *actions.S3Actions
	questioner demotools.IQuestioner
}

// init initializes objects in the Resources struct.
func (resources *Resources) init(s3Actions *actions.S3Actions, questioner demotools.IQuestioner) {
	resources.s3Actions = s3Actions
	resources.questioner = questioner
	resources.demoBuckets = map[string]*DemoBucket{}
}

// Cleanup deletes all AWS resources created during the ObjectLockScenario.
func (resources *Resources) Cleanup(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong during cleanup.\n%v\n", r)
			log.Println("Use the AWS Management Console to remove any remaining resources " +
				"that were created for this scenario.")
		}
	}()

	wantDelete := resources.questioner.AskBool("Do you want to remove all of the AWS resources that were created "+
		"during this demo (y/n)?", "y")
	if !wantDelete {
		log.Println("Be sure to remove resources when you're done with them to avoid unexpected charges!")
		return
	}

	log.Println("Removing objects from S3 buckets and deleting buckets...")
	resources.deleteBuckets(ctx)
	//resources.deleteRetentionObjects(resources.retentionBucket, resources.retentionObjects)

	log.Println("Cleanup complete.")
}

// deleteBuckets empties and then deletes all buckets created during the ObjectLockScenario.
func (resources *Resources) deleteBuckets(ctx context.Context) {
	for _, info := range createInfo {
		bucket := resources.demoBuckets[info.name]
		resources.deleteObjects(ctx, bucket)
		_, err := resources.s3Actions.S3Client.DeleteBucket(ctx, &s3.DeleteBucketInput{
			Bucket: aws.String(bucket.name),
		})
		if err != nil {
			panic(err)
		}
	}
	resources.demoBuckets = map[string]*DemoBucket{}
}

// deleteObjects deletes all objects in the specified bucket.
func (resources *Resources) deleteObjects(ctx context.Context, bucket *DemoBucket) {
	lockConfig, err := resources.s3Actions.GetObjectLockConfiguration(ctx, bucket.name)
	if err != nil {
		panic(err)
	}
	versions, err := resources.s3Actions.ListObjectVersions(ctx, bucket.name)
	if err != nil {
		switch err.(type) {
		case *types.NoSuchBucket:
			log.Printf("No objects to get from %s.\n", bucket.name)
		default:
			panic(err)
		}
	}
	delObjects := make([]types.ObjectIdentifier, len(versions))
	for i, version := range versions {
		if lockConfig != nil && lockConfig.ObjectLockEnabled == types.ObjectLockEnabledEnabled {
			status, err := resources.s3Actions.GetObjectLegalHold(ctx, bucket.name, *version.Key, *version.VersionId)
			if err != nil {
				switch err.(type) {
				case *types.NoSuchKey:
					log.Printf("Couldn't determine legal hold status for %s in %s.\n", *version.Key, bucket.name)
				default:
					panic(err)
				}
			} else if status != nil && *status == types.ObjectLockLegalHoldStatusOn {
				err = resources.s3Actions.PutObjectLegalHold(ctx, bucket.name, *version.Key, *version.VersionId, types.ObjectLockLegalHoldStatusOff)
				if err != nil {
					switch err.(type) {
					case *types.NoSuchKey:
						log.Printf("Couldn't turn off legal hold for %s in %s.\n", *version.Key, bucket.name)
					default:
						panic(err)
					}
				}
			}
		}
		delObjects[i] = types.ObjectIdentifier{Key: version.Key, VersionId: version.VersionId}
	}
	err = resources.s3Actions.DeleteObjects(ctx, bucket.name, delObjects, bucket.retentionEnabled)
	if err != nil {
		switch err.(type) {
		case *types.NoSuchBucket:
			log.Println("Nothing to delete.")
		default:
			panic(err)
		}
	}
}

// snippet-end:[gov2.workflows.s3.ObjectLock.Resources.complete]
