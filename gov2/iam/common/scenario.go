//snippet-start:[iam.go-v2.iam_basics]
package main

import (
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/credentials/stscreds"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/aws/smithy-go"
)

/*
This is a basic scenario for using AWS Identity and Access Management (IAM) and AWS Security Token Service (STS).

This example will do the following:

 *   Create a user that has no permissions.
 *   Create a role and policy that grant s3:ListAllMyBuckets permission.
 *   Grant the user permission to assume the role.
 *   Create an S3 client object as the user and try to list buckets (this should fail).
 *   Get temporary credentials by assuming the role.
 *   Create an S3 client object with the temporary credentials and list the buckets (this should succeed).
 *   Delete all the resources.

 */

const (
	scenario_UserName               = "ExampleUser123"
	scenario_BucketListerRoleName   = "BucketListerRole"
	scenario_BucketListerPolicyName = "BucketListerListMyBucketsPolicy"
)

func scenario() {

	cfg, err := config.LoadDefaultConfig(context.Background())

	if err != nil {
		panic("Couldn't load a configuration")
	}

	iamSvc := iam.NewFromConfig(cfg)

	fmt.Println("⏺️ Create user")

	var userInfo types.User

	user, err := iamSvc.CreateUser(context.Background(), &iam.CreateUserInput{
		UserName: aws.String(scenario_UserName),
	})

	if err != nil {

		var existsAlready *types.EntityAlreadyExistsException
		if errors.As(err, &existsAlready) {
			// The user possibly exists.
			// Check if the user actually exists within IAM.
			uuser, err := iamSvc.GetUser(context.Background(), &iam.GetUserInput{UserName: aws.String(scenario_UserName)})
			if err != nil {
				panic("Can't get user: " + err.Error())
			} else {
				// Make sure the user info is set for later.
				userInfo = *uuser.User
				fmt.Println("User already existed...")
			}
		} else {
			fmt.Println("Couldn't create user! " + err.Error())
		}
	} else {
		userInfo = *user.User
	}

	fmt.Printf("User %s has id %s\n", scenario_UserName, *userInfo.Arn)

	fmt.Println("⏺️ Create access key")

	creds, err := iamSvc.CreateAccessKey(context.Background(), &iam.CreateAccessKeyInput{
		UserName: aws.String(scenario_UserName),
	})

	if err != nil {
		panic("Couldn't create credentials for a user! " + err.Error())
	}

	akId := *creds.AccessKey.AccessKeyId
	sakId := *creds.AccessKey.SecretAccessKey

	fmt.Printf("🗝️ CREDS: accessKeyId(%s) Secretkey(%s)\n", akId, sakId)

	// Grant the user the ability to assume the role.

	fmt.Println("💤 waiting for a few moments for keys to become available")

	time.Sleep(10 * time.Second)

	fmt.Println("⏺️ Create the bucket lister role")
	bucketListerRole, err := iamSvc.CreateRole(context.Background(), &iam.CreateRoleInput{
		RoleName: aws.String(scenario_BucketListerRoleName),
		AssumeRolePolicyDocument: aws.String(`{
			"Version": "2012-10-17",
			"Statement": [
			  {
				"Effect": "Allow",
				"Principal": {
				  "AWS":"` + (*userInfo.Arn) + `"
				},
				"Action": "sts:AssumeRole"
			  }
			]
		  }`),
		Description: aws.String("Role to let users list their buckets."),
	})
	var bucketListerRoleArn string
	if err != nil {
		// Check to see if the role exists.
		var existsException *types.EntityAlreadyExistsException
		if errors.As(err, &existsException) {
			// Check if we can look up the role as it stands already
			tRole, err := iamSvc.GetRole(context.Background(), &iam.GetRoleInput{
				RoleName: aws.String(scenario_BucketListerRoleName),
			})
			if err != nil {
				// Told it already exists, but now it's gone.
				panic("Couldn't find seemingly extant role: " + err.Error())
			} else {
				bucketListerRoleArn = *tRole.Role.Arn
			}
		} else {
			panic("Couldn't create role! " + err.Error())
		}
	} else {
		bucketListerRoleArn = *bucketListerRole.Role.Arn
	}

	fmt.Printf("✔️ The ARN for the bucket lister role is %s", bucketListerRoleArn)

	fmt.Println("⏺️ Create policy to allow bucket listing")
	bucketAllowPolicy := aws.String(`{
		"Version": "2012-10-17",
		"Statement": [
		  {
			"Sid": "Stmt1646154730759",
			"Action": [
			  "s3:ListAllMyBuckets"
			],
			"Effect": "Allow",
			"Resource": "*"}]}`)

	var bucketListerPolicyArn string
	bucketListerPolicy, err := iamSvc.CreatePolicy(context.Background(), &iam.CreatePolicyInput{
		PolicyName:     aws.String(scenario_BucketListerPolicyName),
		PolicyDocument: bucketAllowPolicy,
		Description:    aws.String("Allow user to list their own buckets"),
	})

	if err != nil {
		var existsException *types.EntityAlreadyExistsException
		if errors.As(err, &existsException) {

			stsClient := sts.NewFromConfig(cfg)
			mCallerId, _ := stsClient.GetCallerIdentity(context.Background(), &sts.GetCallerIdentityInput{})

			mpolicyArn := fmt.Sprintf("arn:aws:iam::%s:policy/%s", *mCallerId.Account, scenario_BucketListerPolicyName)

			_, err := iamSvc.GetPolicy(context.Background(), &iam.GetPolicyInput{
				PolicyArn: &mpolicyArn,
			})
			if err != nil {
				panic("Failed to find policy by arn(" + mpolicyArn + ") -> " + err.Error())
			}
			bucketListerPolicyArn = mpolicyArn

		} else {
			panic("Couldn't create policy! " + err.Error())
		}
	} else {
		bucketListerPolicyArn = *bucketListerPolicy.Policy.Arn
	}

	fmt.Println("⏺️ Attach role policy")

	_, err = iamSvc.AttachRolePolicy(context.Background(), &iam.AttachRolePolicyInput{
		PolicyArn: &bucketListerPolicyArn,
		RoleName:  aws.String(scenario_BucketListerRoleName),
	})

	if err != nil {
		fmt.Println("Couldn't attach policy to role! " + err.Error())
	}

	fmt.Printf("⭕  attached role policy ")

	fmt.Println("💤 waiting for a few moments for keys to become available")

	time.Sleep(10 * time.Second)

	// Create an S3 client that acts as the user.
	userConfig, err := config.LoadDefaultConfig(context.TODO(),
		// Use credentials created earlier.
		config.WithCredentialsProvider(credentials.StaticCredentialsProvider{
			Value: aws.Credentials{
				AccessKeyID: akId, SecretAccessKey: sakId,
				Source: "Example user creds",
			},
		}))
	if err != nil {
		panic("Couldn't create config for new credentials! " + err.Error())
	} else {
		creds, err := userConfig.Credentials.Retrieve(context.Background())
		if err != nil {
			panic("Couldn't get credentials for our new config, something is wrong: " + err.Error())
		}
		fmt.Printf("-> config creds are %s %s\n", creds.AccessKeyID, creds.SecretAccessKey)
	}

	s3Client := s3.NewFromConfig(userConfig)
	// Attempt to list buckets.
	_, err = s3Client.ListBuckets(context.Background(), &s3.ListBucketsInput{})

	if err == nil {
		fmt.Println("Call to s3:ListBuckets was not denied (unexpected!)")
	} else {
		var oe smithy.APIError
		if errors.As(err, &oe) && (oe.ErrorCode() == "AccessDenied") {
			fmt.Println("Couldn't list buckets (expected!)")
		} else {
			panic("unexpected error: " + err.Error())
		}
	}

	stsClient := sts.NewFromConfig(userConfig)
	roleCreds := stscreds.NewAssumeRoleProvider(stsClient, bucketListerRoleArn)

	tmpCreds, err := roleCreds.Retrieve(context.Background())
	if err != nil {
		fmt.Println("couldn't get role creds: " + err.Error())
	} else {
		fmt.Printf("role creds: %s %v\n\n", tmpCreds.AccessKeyID, tmpCreds.Expires)
	}

	roleConfig, err := config.LoadDefaultConfig(context.Background(),
		config.WithCredentialsProvider(roleCreds),
	)
	if err != nil {
		panic("Couldn't create config with assumed role! " + err.Error())
	}

	roleS3client := s3.NewFromConfig(roleConfig)

	mybuckets, err := roleS3client.ListBuckets(context.Background(), &s3.ListBucketsInput{})

	if err != nil {
		panic("Couldn't list buckets while assuming role that allows this: " + err.Error())
	} else {
		fmt.Println("Buckets owned by the user....")
		for _, bucket := range mybuckets.Buckets {
			fmt.Printf("%s -> %s\n", *bucket.Name, bucket.CreationDate)
		}
	}

	// ---- Clean up ----

	// Delete the user's access keys.

	fmt.Println("cleanup: Delete created access key")
	_, _ = iamSvc.DeleteAccessKey(context.Background(), &iam.DeleteAccessKeyInput{
		AccessKeyId: &akId,
		UserName:    aws.String(scenario_UserName),
	})

	// Delete the user.
	fmt.Println("cleanup: delete the user we created")
	_, err = iamSvc.DeleteUser(context.Background(), &iam.DeleteUserInput{UserName: aws.String(scenario_UserName)})
	if err != nil {
		fmt.Println("Couldn't delete user! " + err.Error())
	}

	// Detach the role policy.

	fmt.Println("cleanup: Detach the policy from the role")
	_, err = iamSvc.DetachRolePolicy(context.Background(), &iam.DetachRolePolicyInput{
		RoleName:  aws.String(scenario_BucketListerRoleName),
		PolicyArn: &bucketListerPolicyArn,
	})

	if err != nil {
		fmt.Println("Couldn't detach role policy from role " + err.Error())
	}

	// Delete the role.
	fmt.Println("cleanup: Remove the role")
	_, err = iamSvc.DeleteRole(context.Background(), &iam.DeleteRoleInput{
		RoleName: aws.String(scenario_BucketListerRoleName),
	})
	if err != nil {
		fmt.Println("Couldn't delete role! " + err.Error())
	}

	// Delete the policy.
	fmt.Println("cleanup: delete the policy")
	_, err = iamSvc.DeletePolicy(context.Background(), &iam.DeletePolicyInput{
		PolicyArn: &bucketListerPolicyArn,
	})
	if err != nil {
		fmt.Println("couldn't delete policy!")
	}

	fmt.Println("done!")
}

//snippet-end:[iam.go-v2.iam_basics]
