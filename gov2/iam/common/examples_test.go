package main

import (
	"context"
	"fmt"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/sts"
)

func TestExamples(t *testing.T) {

	cfg, err := config.LoadDefaultConfig(context.Background())

	if err != nil {
		panic("Couldn't load a configuration")
	}
	iamClient := iam.NewFromConfig(cfg)

	var created ExampleCreatedResources

	func() {
		defer func() {
			if err := recover(); err != nil {
				// Clean up after a failed test run.
				t.Logf("Failed to run tests: %v", err)

				// Clean up whatever possible.

				_, xerr := iamClient.DeleteUser(context.Background(), &iam.DeleteUserInput{UserName: aws.String(ExampleUserName)})
				if xerr != nil {
					t.Error("Couldn't clean up user")
				}
				_, xerr = iamClient.DetachRolePolicy(context.Background(), &iam.DetachRolePolicyInput{
					PolicyArn: aws.String(ExamplePolicyARN),
					RoleName:  aws.String(ExampleRoleName),
				})
				if xerr == nil {
					t.Log("Cleaned up role policy attach")
				} else {
					t.Log("Failed to detach role policy")
				}
				_, xerr = iamClient.DeleteRole(context.TODO(), &iam.DeleteRoleInput{RoleName: aws.String(ExampleRoleName)})

				if xerr == nil {
					t.Log("Cleaned up role")
				} else {
					t.Log("Failed to clean up role")
				}

				// Get the caller identity.
				stsClient := sts.NewFromConfig(cfg)
				identity, xerr := stsClient.GetCallerIdentity(context.Background(), &sts.GetCallerIdentityInput{})

				if xerr != nil {
					panic("Couldn't get my own identity: "+xerr.Error())
				}

				ourPolicyArn := fmt.Sprintf("arn:aws:iam::%s:policy/%v", *identity.Account, ExamplePolicyName)

				_, xerr = iamClient.DeletePolicy(context.Background(), &iam.DeletePolicyInput{PolicyArn: &ourPolicyArn})

				if xerr != nil {
					t.Log("Failed to clean up policy ARN " + ourPolicyArn+ " -> "+ xerr.Error())
				}
				t.FailNow()
			}
		}()

		func() {

			created = examples(cfg)

			t.Logf("created the user %v", created.User)
			t.Logf("created the role %v", created.Role)
			t.Logf("created the policy %v", created.Policy)

		}()
	}()
	// Clean up the resources.

	t.Log(" --- Clean up ")

	_, err = iamClient.DeleteRole(context.Background(), &iam.DeleteRoleInput{
		RoleName: aws.String(ExampleRoleName),
	})
	if err != nil {
		t.Fatalf("Couldn't delete role after create! %v", err)
	}

	_, err = iamClient.DeletePolicy(context.Background(), &iam.DeletePolicyInput{
		PolicyArn: &created.Policy,
	})
	if err != nil {
		t.Fatalf("Couldn't delete policy! %v", err)
	}

}
