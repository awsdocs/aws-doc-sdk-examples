//snippet-sourcedescription:[Common IAM actions with the AWS SDK for Go v2]
//snippet-keyword:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[02/22/2022]
//snippet-sourceauthor:[gangwere]
package main

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/config"
)

func main() {
	// Get the default AWS SDK configuration.
	cfg, err := config.LoadDefaultConfig(context.Background())

	if err != nil {
		panic("Couldn't load configuration: "+err.Error())
	}

	scenario()

	examples(cfg)

}
