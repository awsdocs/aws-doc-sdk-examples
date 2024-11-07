// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package workflows

// snippet-start:[gov2.workflows.s3.ObjectLock.scenario.complete]

import (
	"context"
	"fmt"
	"log"
	"strings"

	"s3_object_lock/actions"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/s3/manager"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// ObjectLockScenario contains the steps to run the S3 Object Lock workflow.
type ObjectLockScenario struct {
	questioner demotools.IQuestioner
	resources  Resources
	s3Actions  *actions.S3Actions
	sdkConfig  aws.Config
}

// NewObjectLockScenario constructs a new ObjectLockScenario instance.
func NewObjectLockScenario(sdkConfig aws.Config, questioner demotools.IQuestioner) ObjectLockScenario {
	scenario := ObjectLockScenario{
		questioner: questioner,
		resources:  Resources{},
		s3Actions:  &actions.S3Actions{S3Client: s3.NewFromConfig(sdkConfig)},
		sdkConfig:  sdkConfig,
	}
	scenario.s3Actions.S3Manager = manager.NewUploader(scenario.s3Actions.S3Client)
	scenario.resources.init(scenario.s3Actions, questioner)
	return scenario
}

type nameLocked struct {
	name   string
	locked bool
}

var createInfo = []nameLocked{
	{"standard-bucket", false},
	{"lock-bucket", true},
	{"retention-bucket", false},
}

// CreateBuckets creates the S3 buckets required for the workflow.
func (scenario *ObjectLockScenario) CreateBuckets(ctx context.Context) {
	log.Println("Let's create some S3 buckets to use for this workflow.")
	success := false
	for !success {
		prefix := scenario.questioner.Ask(
			"This example creates three buckets. Enter a prefix to name your buckets (remember bucket names must be globally unique):")

		for _, info := range createInfo {
			log.Println(fmt.Sprintf("%s.%s", prefix, info.name))
			bucketName, err := scenario.s3Actions.CreateBucketWithLock(ctx, fmt.Sprintf("%s.%s", prefix, info.name), scenario.sdkConfig.Region, info.locked)
			if err != nil {
				switch err.(type) {
				case *types.BucketAlreadyExists, *types.BucketAlreadyOwnedByYou:
					log.Printf("Couldn't create bucket %s.\n", bucketName)
				default:
					panic(err)
				}
				break
			}
			scenario.resources.demoBuckets[info.name] = &DemoBucket{
				name:       bucketName,
				objectKeys: []string{},
			}
			log.Printf("Created bucket %s.\n", bucketName)
		}

		if len(scenario.resources.demoBuckets) < len(createInfo) {
			scenario.resources.deleteBuckets(ctx)
		} else {
			success = true
		}
	}

	log.Println("S3 buckets created.")
	log.Println(strings.Repeat("-", 88))
}

// EnableLockOnBucket enables object locking on an existing bucket.
func (scenario *ObjectLockScenario) EnableLockOnBucket(ctx context.Context) {
	log.Println("\nA bucket can be configured to use object locking.")
	scenario.questioner.Ask("Press Enter to continue.")

	var err error
	bucket := scenario.resources.demoBuckets["retention-bucket"]
	err = scenario.s3Actions.EnableObjectLockOnBucket(ctx, bucket.name)
	if err != nil {
		switch err.(type) {
		case *types.NoSuchBucket:
			log.Printf("Couldn't enable object locking on bucket %s.\n", bucket.name)
		default:
			panic(err)
		}
	} else {
		log.Printf("Object locking enabled on bucket %s.", bucket.name)
	}

	log.Println(strings.Repeat("-", 88))
}

// SetDefaultRetentionPolicy sets a default retention governance policy on a bucket.
func (scenario *ObjectLockScenario) SetDefaultRetentionPolicy(ctx context.Context) {
	log.Println("\nA bucket can be configured to use object locking with a default retention period.")

	bucket := scenario.resources.demoBuckets["retention-bucket"]
	retentionPeriod := scenario.questioner.AskInt("Enter the default retention period in days: ")
	err := scenario.s3Actions.ModifyDefaultBucketRetention(ctx, bucket.name, types.ObjectLockEnabledEnabled, int32(retentionPeriod), types.ObjectLockRetentionModeGovernance)
	if err != nil {
		switch err.(type) {
		case *types.NoSuchBucket:
			log.Printf("Couldn't configure a default retention period on bucket %s.\n", bucket.name)
		default:
			panic(err)
		}
	} else {
		log.Printf("Default retention policy set on bucket %s with %d day retention period.", bucket.name, retentionPeriod)
		bucket.retentionEnabled = true
	}

	log.Println(strings.Repeat("-", 88))
}

// UploadTestObjects uploads test objects to the S3 buckets.
func (scenario *ObjectLockScenario) UploadTestObjects(ctx context.Context) {
	log.Println("Uploading test objects to S3 buckets.")

	for _, info := range createInfo {
		bucket := scenario.resources.demoBuckets[info.name]
		for i := 0; i < 2; i++ {
			key, err := scenario.s3Actions.UploadObject(ctx, bucket.name, fmt.Sprintf("example-%d", i),
				fmt.Sprintf("Example object content #%d in bucket %s.", i, bucket.name))
			if err != nil {
				switch err.(type) {
				case *types.NoSuchBucket:
					log.Printf("Couldn't upload %s to bucket %s.\n", key, bucket.name)
				default:
					panic(err)
				}
			} else {
				log.Printf("Uploaded %s to bucket %s.\n", key, bucket.name)
				bucket.objectKeys = append(bucket.objectKeys, key)
			}
		}
	}

	scenario.questioner.Ask("Test objects uploaded. Press Enter to continue.")
	log.Println(strings.Repeat("-", 88))
}

// SetObjectLockConfigurations sets object lock configurations on the test objects.
func (scenario *ObjectLockScenario) SetObjectLockConfigurations(ctx context.Context) {
	log.Println("Now let's set object lock configurations on individual objects.")

	buckets := []*DemoBucket{scenario.resources.demoBuckets["lock-bucket"], scenario.resources.demoBuckets["retention-bucket"]}
	for _, bucket := range buckets {
		for index, objKey := range bucket.objectKeys {
			switch index {
			case 0:
				if scenario.questioner.AskBool(fmt.Sprintf("\nDo you want to add a legal hold to %s in %s (y/n)? ", objKey, bucket.name), "y") {
					err := scenario.s3Actions.PutObjectLegalHold(ctx, bucket.name, objKey, "", types.ObjectLockLegalHoldStatusOn)
					if err != nil {
						switch err.(type) {
						case *types.NoSuchKey:
							log.Printf("Couldn't set legal hold on %s.\n", objKey)
						default:
							panic(err)
						}
					} else {
						log.Printf("Legal hold set on %s.\n", objKey)
					}
				}
			case 1:
				q := fmt.Sprintf("\nDo you want to add a 1 day Governance retention period to %s in %s?\n"+
					"Reminder: Only a user with the s3:BypassGovernanceRetention permission is able to delete this object\n"+
					"or its bucket until the retention period has expired. (y/n) ", objKey, bucket.name)
				if scenario.questioner.AskBool(q, "y") {
					err := scenario.s3Actions.PutObjectRetention(ctx, bucket.name, objKey, types.ObjectLockRetentionModeGovernance, 1)
					if err != nil {
						switch err.(type) {
						case *types.NoSuchKey:
							log.Printf("Couldn't set retention period on %s in %s.\n", objKey, bucket.name)
						default:
							panic(err)
						}
					} else {
						log.Printf("Retention period set to 1 for %s.", objKey)
						bucket.retentionEnabled = true
					}
				}
			}
		}
	}
	log.Println(strings.Repeat("-", 88))
}

const (
	ListAll = iota
	DeleteObject
	DeleteRetentionObject
	OverwriteObject
	ViewRetention
	ViewLegalHold
	Finish
)

// InteractWithObjects allows the user to interact with the objects and test the object lock configurations.
func (scenario *ObjectLockScenario) InteractWithObjects(ctx context.Context) {
	log.Println("Now you can interact with the objects to explore the object lock configurations.")
	interactiveChoices := []string{
		"List all objects and buckets.",
		"Attempt to delete an object.",
		"Attempt to delete an object with retention period bypass.",
		"Attempt to overwrite a file.",
		"View the retention settings for an object.",
		"View the legal hold settings for an object.",
		"Finish the workflow."}

	choice := ListAll
	for choice != Finish {
		objList := scenario.GetAllObjects(ctx)
		objChoices := scenario.makeObjectChoiceList(objList)
		choice = scenario.questioner.AskChoice("Choose an action from the menu:\n", interactiveChoices)
		switch choice {
		case ListAll:
			log.Println("The current objects in the example buckets are:")
			for _, objChoice := range objChoices {
				log.Println("\t", objChoice)
			}
		case DeleteObject, DeleteRetentionObject:
			objChoice := scenario.questioner.AskChoice("Enter the number of the object to delete:\n", objChoices)
			obj := objList[objChoice]
			deleted, err := scenario.s3Actions.DeleteObject(ctx, obj.bucket, obj.key, obj.versionId, choice == DeleteRetentionObject)
			if err != nil {
				switch err.(type) {
				case *types.NoSuchKey:
					log.Println("Nothing to delete.")
				default:
					panic(err)
				}
			} else if deleted {
				log.Printf("Object %s deleted.\n", obj.key)
			}
		case OverwriteObject:
			objChoice := scenario.questioner.AskChoice("Enter the number of the object to overwrite:\n", objChoices)
			obj := objList[objChoice]
			_, err := scenario.s3Actions.UploadObject(ctx, obj.bucket, obj.key, fmt.Sprintf("New content in object %s.", obj.key))
			if err != nil {
				switch err.(type) {
				case *types.NoSuchBucket:
					log.Println("Couldn't upload to nonexistent bucket.")
				default:
					panic(err)
				}
			} else {
				log.Printf("Uploaded new content to object %s.\n", obj.key)
			}
		case ViewRetention:
			objChoice := scenario.questioner.AskChoice("Enter the number of the object to view:\n", objChoices)
			obj := objList[objChoice]
			retention, err := scenario.s3Actions.GetObjectRetention(ctx, obj.bucket, obj.key)
			if err != nil {
				switch err.(type) {
				case *types.NoSuchKey:
					log.Printf("Can't get retention configuration for %s.\n", obj.key)
				default:
					panic(err)
				}
			} else if retention != nil {
				log.Printf("Object %s has retention mode %s until %v.\n", obj.key, retention.Mode, retention.RetainUntilDate)
			} else {
				log.Printf("Object %s does not have object retention configured.\n", obj.key)
			}
		case ViewLegalHold:
			objChoice := scenario.questioner.AskChoice("Enter the number of the object to view:\n", objChoices)
			obj := objList[objChoice]
			legalHold, err := scenario.s3Actions.GetObjectLegalHold(ctx, obj.bucket, obj.key, obj.versionId)
			if err != nil {
				switch err.(type) {
				case *types.NoSuchKey:
					log.Printf("Can't get legal hold configuration for %s.\n", obj.key)
				default:
					panic(err)
				}
			} else if legalHold != nil {
				log.Printf("Object %s has legal hold %v.", obj.key, *legalHold)
			} else {
				log.Printf("Object %s does not have legal hold configured.", obj.key)
			}
		case Finish:
			log.Println("Let's clean up.")
		}
		log.Println(strings.Repeat("-", 88))
	}
}

type BucketKeyVersionId struct {
	bucket    string
	key       string
	versionId string
}

// GetAllObjects gets the object versions in the example S3 buckets and returns them in a flattened list.
func (scenario *ObjectLockScenario) GetAllObjects(ctx context.Context) []BucketKeyVersionId {
	var objectList []BucketKeyVersionId
	for _, info := range createInfo {
		bucket := scenario.resources.demoBuckets[info.name]
		versions, err := scenario.s3Actions.ListObjectVersions(ctx, bucket.name)
		if err != nil {
			switch err.(type) {
			case *types.NoSuchBucket:
				log.Printf("Couldn't get object versions for %s.\n", bucket.name)
			default:
				panic(err)
			}
		} else {
			for _, version := range versions {
				objectList = append(objectList,
					BucketKeyVersionId{bucket: bucket.name, key: *version.Key, versionId: *version.VersionId})
			}
		}
	}
	return objectList
}

// makeObjectChoiceList makes the object version list into a list of strings that are displayed
// as choices.
func (scenario *ObjectLockScenario) makeObjectChoiceList(bucketObjects []BucketKeyVersionId) []string {
	choices := make([]string, len(bucketObjects))
	for i := 0; i < len(bucketObjects); i++ {
		choices[i] = fmt.Sprintf("%s in %s with VersionId %s.",
			bucketObjects[i].key, bucketObjects[i].bucket, bucketObjects[i].versionId)
	}
	return choices
}

// Run runs the S3 Object Lock workflow scenario.
func (scenario *ObjectLockScenario) Run(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
			_, isMock := scenario.questioner.(*demotools.MockQuestioner)
			if isMock || scenario.questioner.AskBool("Do you want to see the full error message (y/n)?", "y") {
				log.Println(r)
			}
			scenario.resources.Cleanup(ctx)
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon S3 Object Lock Workflow Scenario.")
	log.Println(strings.Repeat("-", 88))

	scenario.CreateBuckets(ctx)
	scenario.EnableLockOnBucket(ctx)
	scenario.SetDefaultRetentionPolicy(ctx)
	scenario.UploadTestObjects(ctx)
	scenario.SetObjectLockConfigurations(ctx)
	scenario.InteractWithObjects(ctx)

	scenario.resources.Cleanup(ctx)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.workflows.s3.ObjectLock.scenario.complete]
