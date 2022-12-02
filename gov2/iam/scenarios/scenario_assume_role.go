// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"context"
	"errors"
	"fmt"
	"log"
	"math/rand"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/aws/smithy-go"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/iam/actions"
)

// IScenarioHelper abstracts input and wait functions from a scenario so that they
// can be mocked for unit testing.
type IScenarioHelper interface {
	GetName() string
	Pause(secs int)
}

const rMax = 100000
type ScenarioHelper struct {
	Prefix string
	Random *rand.Rand
}

// GetName returns a unique name formed of a prefix and a random number.
func (helper *ScenarioHelper) GetName() string {
	return fmt.Sprintf("%v%v", helper.Prefix, helper.Random.Intn(rMax))
}

// Pause waits for the specified number of seconds.
func (helper ScenarioHelper) Pause(secs int) {
	time.Sleep(time.Duration(secs) * time.Second)
}

// snippet-start:[gov2.iam.Scenario_AssumeRole]

// AssumeRoleScenario shows you how to use AWS Identity and Access Management (IAM) to
// perform the following actions:
//
// 1. Create a user who has no permissions.
// 2. Create a role that grants permission to list S3 buckets for the account.
// 3. Add a policy to let the user assume the role.
// 4. Try and fail to list buckets without permissions.
// 5. Assume the role and list S3 buckets using temporary credentials.
// 6. Delete the policy, role, and user.
type AssumeRoleScenario struct {
	sdkConfig aws.Config
	accountWrapper actions.AccountWrapper
	policyWrapper actions.PolicyWrapper
	roleWrapper actions.RoleWrapper
	userWrapper actions.UserWrapper
	questioner demotools.IQuestioner
	helper IScenarioHelper
	isTestRun bool
}

// NewAssumeRoleScenario constructs an AssumeRoleScenario instance from a configuration.
// It uses the specified config to get an IAM client and create wrappers for the actions
// used in the scenario.
func NewAssumeRoleScenario(sdkConfig aws.Config, questioner demotools.IQuestioner,
		helper IScenarioHelper) AssumeRoleScenario {
	iamClient := iam.NewFromConfig(sdkConfig)
	return AssumeRoleScenario{
		sdkConfig: 		sdkConfig,
		accountWrapper: actions.AccountWrapper{IamClient: iamClient},
		policyWrapper:  actions.PolicyWrapper{IamClient: iamClient},
		roleWrapper:    actions.RoleWrapper{IamClient: iamClient},
		userWrapper:    actions.UserWrapper{IamClient: iamClient},
		questioner:     questioner,
		helper:         helper,
	}
}

// addTestOptions appends the API options specified in the original configuration to
// another configuration. This is used to attach the middleware stubber to clients
// that are constructed during the scenario, which is needed for unit testing.
func (scenario AssumeRoleScenario) addTestOptions(scenarioConfig *aws.Config) {
	if scenario.isTestRun {
		scenarioConfig.APIOptions = append(scenarioConfig.APIOptions, scenario.sdkConfig.APIOptions...)
	}
}

// Run runs the interactive scenario.
func (scenario AssumeRoleScenario) Run() {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong with the demo.\n")
			log.Println(r)
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the AWS Identity and Access Management (IAM) assume role demo.")
	log.Println(strings.Repeat("-", 88))

	user := scenario.CreateUser()
	accessKey := scenario.CreateAccessKey(user)
	role := scenario.CreateRoleAndPolicies(user)
	noPermsConfig := scenario.ListBucketsWithoutPermissions(accessKey)
	scenario.ListBucketsWithAssumedRole(noPermsConfig, role)
	scenario.Cleanup(user, role)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// CreateUser creates a new IAM user. This user has no permissions.
func (scenario AssumeRoleScenario) CreateUser() *types.User {
	log.Println("Let's create an example user with no permissions.")
	userName := scenario.questioner.Ask("Enter a name for the example user:", demotools.NotEmpty{})
	user, err := scenario.userWrapper.GetUser(userName)
	if err != nil {
		panic(err)
	}
	if user == nil {
		user, err = scenario.userWrapper.CreateUser(userName)
		if err != nil {
			panic(err)
		}
		log.Printf("Created user %v.\n", *user.UserName)
	} else {
		log.Printf("User %v already exists.\n", *user.UserName)
	}
	log.Println(strings.Repeat("-", 88))
	return user
}

// CreateAccessKey creates an access key for the user.
func (scenario AssumeRoleScenario) CreateAccessKey(user *types.User) *types.AccessKey {
	accessKey, err := scenario.userWrapper.CreateAccessKeyPair(*user.UserName)
	if err != nil {
		panic(err)
	}
	log.Printf("Created access key %v for your user.", *accessKey.AccessKeyId)
	log.Println("Waiting a few seconds for your user to be ready...")
	scenario.helper.Pause(10)
	log.Println(strings.Repeat("-", 88))
	return accessKey
}

// CreateRoleAndPolicies creates a policy that grants permission to list S3 buckets for
// the current account and attached it to a newly created role. It also adds an inline
// policy to the specified user that grants the user permission to assume the role.
func (scenario AssumeRoleScenario) CreateRoleAndPolicies(user *types.User) *types.Role {
	log.Println("Let's create a role and policy that grant permission to list S3 buckets.")
	scenario.questioner.Ask("Press Enter when you're ready.")
	listBucketsRole, err := scenario.roleWrapper.CreateRole(scenario.helper.GetName(), *user.Arn)
	if err != nil {panic(err)}
	log.Printf("Created role %v.\n", *listBucketsRole.RoleName)
	listBucketsPolicy, err := scenario.policyWrapper.CreatePolicy(
		scenario.helper.GetName(), []string{"s3:ListAllMyBuckets"}, "arn:aws:s3:::*")
	if err != nil {panic(err)}
	log.Printf("Created policy %v.\n", *listBucketsPolicy.PolicyName)
	err = scenario.roleWrapper.AttachRolePolicy(*listBucketsPolicy.Arn, *listBucketsRole.RoleName)
	if err != nil {panic(err)}
	log.Printf("Attached policy %v to role %v.\n", *listBucketsPolicy.PolicyName,
		*listBucketsRole.RoleName)
	err = scenario.userWrapper.CreateUserPolicy(*user.UserName, scenario.helper.GetName(),
		[]string{"sts:AssumeRole"}, *listBucketsRole.Arn)
	log.Printf("Created an inline policy for user %v that lets the user assume the role.\n",
		*user.UserName)
	log.Println("Let's give AWS a few seconds to propagate these new resources and connections...")
	scenario.helper.Pause(10)
	log.Println(strings.Repeat("-", 88))
	return listBucketsRole
}

// ListBucketsWithoutPermissions creates an S3 client from the user's access key credentials
// and tries to list buckets for the account. Because the user does not have permission
// to perform this action, the action fails.
func (scenario AssumeRoleScenario) ListBucketsWithoutPermissions(accessKey *types.AccessKey) *aws.Config {
 	log.Println("Let's try to list buckets without permissions. This should return an AccessDenied error.")
 	scenario.questioner.Ask("Press Enter when you're ready.")
 	noPermsConfig, err := config.LoadDefaultConfig(context.TODO(),
		config.WithCredentialsProvider(credentials.NewStaticCredentialsProvider(
			*accessKey.AccessKeyId, *accessKey.SecretAccessKey, ""),
	))
 	if err != nil {panic(err)}

 	// Add test options if this is a test run. This is needed only for testing purposes.
	scenario.addTestOptions(&noPermsConfig)

 	s3Client := s3.NewFromConfig(noPermsConfig)
 	_, err = s3Client.ListBuckets(context.TODO(), &s3.ListBucketsInput{})
 	if err != nil {
 		// The SDK for Go does not model the AccessDenied error, so check ErrorCode directly.
		var ae smithy.APIError
		if errors.As(err, &ae) {
			switch ae.ErrorCode() {
			case "AccessDenied":
				log.Println("Got AccessDenied error, which is the expected result because\n" +
					"the ListBuckets call was made without permissions.")
			default:
				log.Println("Expected AccessDenied, got something else.")
				panic(err)
			}
		}
 	} else {
 		log.Println("Expected AccessDenied error when calling ListBuckets without permissions,\n" +
 			"but the call succeeded. Continuing the example anyway...")
	}
	log.Println(strings.Repeat("-", 88))
 	return &noPermsConfig
}

// ListBucketsWithAssumedRole performs the following actions:
//
// 1. Creates an AWS Security Token Service (AWS STS) client from the config created from
//   the user's access key credentials.
// 2. Gets temporary credentials by assuming the role that grants permission to list the
//    buckets,
// 3. Creates an S3 client from the temporary credentials.
// 4. Lists buckets for the account. Because the temporary credentials are generated by
//    assuming the role that grants permission, the action succeeds.
func (scenario AssumeRoleScenario) ListBucketsWithAssumedRole(noPermsConfig *aws.Config, role *types.Role) {
	log.Println("Let's assume the role that grants permission to list buckets and try again.")
	scenario.questioner.Ask("Press Enter when you're ready.")
	stsClient := sts.NewFromConfig(*noPermsConfig)
	tempCredentials, err := stsClient.AssumeRole(context.TODO(), &sts.AssumeRoleInput{
		RoleArn:           role.Arn,
		RoleSessionName:   aws.String("AssumeRoleExampleSession"),
		DurationSeconds:   aws.Int32(900),
	})
	if err != nil {
		log.Printf("Couldn't assume role %v.\n", *role.RoleName)
		panic(err)
	}
	log.Printf("Assumed role %v, got temporary credentials.\n", *role.RoleName)
	assumeRoleConfig, err := config.LoadDefaultConfig(context.TODO(),
		config.WithCredentialsProvider(credentials.NewStaticCredentialsProvider(
			*tempCredentials.Credentials.AccessKeyId,
			*tempCredentials.Credentials.SecretAccessKey,
			*tempCredentials.Credentials.SessionToken),
		),
	)

	// Add test options if this is a test run. This is needed only for testing purposes.
	scenario.addTestOptions(&assumeRoleConfig)

	s3Client := s3.NewFromConfig(assumeRoleConfig)
	result, err := s3Client.ListBuckets(context.TODO(), &s3.ListBucketsInput{})
	if err != nil {
		log.Println("Couldn't list buckets with assumed role credentials.")
		panic(err)
	}
	log.Println("Successfully called ListBuckets with assumed role credentials, \n" +
		"here are some of them:")
	for i := 0; i < len(result.Buckets) && i < 5; i++ {
		log.Printf("\t%v\n", *result.Buckets[i].Name)
	}
	log.Println(strings.Repeat("-", 88))
}

// Cleanup deletes all resources created for the scenario.
func (scenario AssumeRoleScenario) Cleanup(user *types.User, role *types.Role) {
	if scenario.questioner.AskBool(
		"Do you want to delete the resources created for this example? (y/n)", "y",
	) {
		 policies, err := scenario.roleWrapper.ListAttachedRolePolicies(*role.RoleName)
		 if err != nil {panic(err)}
		 for _, policy := range policies {
			 err = scenario.roleWrapper.DetachRolePolicy(*role.RoleName, *policy.PolicyArn)
			 if err != nil {panic(err)}
			 err = scenario.policyWrapper.DeletePolicy(*policy.PolicyArn)
			 if err != nil {panic(err)}
			 log.Printf("Detached policy %v from role %v and deleted the policy.\n",
			 	*policy.PolicyName, *role.RoleName)
		 }
		 err = scenario.roleWrapper.DeleteRole(*role.RoleName)
		 if err != nil {panic(err)}
		 log.Printf("Deleted role %v.\n", *role.RoleName)

		 userPols, err := scenario.userWrapper.ListUserPolicies(*user.UserName)
		 if err != nil {panic(err)}
		 for _, userPol := range userPols {
		 	err = scenario.userWrapper.DeleteUserPolicy(*user.UserName, userPol)
		 	if err != nil {panic(err)}
		 	log.Printf("Deleted policy %v from user %v.\n", userPol, *user.UserName)
		 }
		 keys, err := scenario.userWrapper.ListAccessKeys(*user.UserName)
		 if err != nil {panic(err)}
		 for _, key := range keys {
		 	err = scenario.userWrapper.DeleteAccessKey(*user.UserName, *key.AccessKeyId)
		 	if err != nil {panic(err)}
		 	log.Printf("Deleted access key %v from user %v.\n", *key.AccessKeyId, *user.UserName)
		 }
		 err = scenario.userWrapper.DeleteUser(*user.UserName)
		 if err != nil {panic(err)}
		 log.Printf("Deleted user %v.\n", *user.UserName)
		 log.Println(strings.Repeat("-", 88))
	}

}

// snippet-end:[gov2.iam.Scenario_AssumeRole]
