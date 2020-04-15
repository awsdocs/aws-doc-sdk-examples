/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[s3.go.put_bucket_acl]
package main

// snippet-start:[s3.go.put_bucket_acl.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.put_bucket_acl.imports]

// SetBucketACL gives a user access to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func SetBucketACL(sess *session.Session, bucket, address, permission *string) error {
	// snippet-start:[s3.go.put_bucket_acl.service_acl]
	svc := s3.New(sess)

	result, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
		Bucket: bucket,
	})
       	// snippet-end:[s3.go.put_bucket_acl.service_acl]
	if err != nil {
		return err
	}

	// snippet-start:[s3.go.put_bucket_acl.add_grant]
	owner := *result.Owner.DisplayName
	ownerID := *result.Owner.ID
	grants := result.Grants

	var newGrantee = s3.Grantee{
		EmailAddress: address,
		Type:         aws.String("AmazonCustomerByEmail"),
	}
	var newGrant = s3.Grant{
		Grantee:    &newGrantee,
		Permission: permission,
	}

	grants = append(grants, &newGrant)
	// snippet-end:[s3.go.put_bucket_acl.add_grant]

	// snippet-start:[s3.go.put_bucket_acl.call]
	_, err = svc.PutBucketAcl(&s3.PutBucketAclInput{
		Bucket: bucket,
		AccessControlPolicy: &s3.AccessControlPolicy{
			Grants: grants,
			Owner: &s3.Owner{
				DisplayName: &owner,
				ID:          &ownerID,
			},
		},
	})
	// snippet-end:[s3.go.put_bucket_acl.call]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	// snippet-start:[s3.go.put_bucket_acl.args]
	bucket := flag.String("b", "", "The bucket to read")
	address := flag.String("e", "", "The email address of the reader")
	permission := flag.String("p", "READ", "The type of permission (FULL_CONTROL, WRITE, WRITE_ACP, READ, or READ_ACP)")
	flag.Parse()

	if *bucket == "" || *address == "" {
		fmt.Println("You must supply a bucket (-b BUCKET) and email address (-e ADDRESS)")
		return
	}

	if !(*permission == "FULL_CONTROL" || *permission == "WRITE" || *permission == "WRITE_ACP" || *permission == "READ" || *permission == "READ_ACP") {
		fmt.Println("Illegal permission value. It must be one of:")
		fmt.Println("FULL_CONTROL, WRITE, WRITE_ACP, READ, or READ_ACP")
		return
	}
	// snippet-end:[s3.go.put_bucket_acl.args]

        // snippet-start:[s3.go.put_bucket_acl.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
        // snippet-end:[s3.go.put_bucket_acl.session]
        
	err := SetBucketACL(sess, bucket, address, permission)
	if err != nil {
		fmt.Println("Got an error setting bucket ACL:")
		fmt.Println(err)
		return
	}

	// snippet-start:[s3.go.put_bucket_acl.print]
	fmt.Println("Congratulations. You gave user with email address", address, permission, "permission to bucket", bucket)
	// snippet-end:[s3.go.put_bucket_acl.print]
}
// snippet-end:[s3.go.put_bucket_acl]
