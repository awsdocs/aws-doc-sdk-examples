package secretsmanager

import (
	"context"
	"fmt"

	"example.aws/go/secretsmanager/common"
	"github.com/aws/aws-sdk-go-v2/config"
)

func main() {

	var secretArn string
	var value string

	cfg, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		panic("Couldn't load config!")
	}

	if secretArn, err = common.CreateSecret(cfg); err != nil {
		panic("Couldn't create secret!")
	}
	fmt.Printf("Created the arn %v\n", secretArn)

	if value, err = common.GetSecret(cfg, secretArn); err != nil {
		panic("Couldn't get secret value!")
	}
	fmt.Printf(`it has the value "%v"\n`, value)

	if err = common.UpdateSecret(cfg, secretArn, "correct horse battery staple"); err != nil {
		panic("Couldn't update secret!")
	}
	fmt.Println("The secret has been updated.")

	if value, err = common.GetSecret(cfg, secretArn); err != nil {
		panic("Couldn't get secret value!")
	}
	fmt.Printf(`it has the value "%v"\n`, value)

	var secretIds []string

	if secretIds, err = common.ListSecrets(cfg); err != nil {
		panic("Couldn't list secrets!")
	}

	fmt.Printf("There are %v secrets -- here's their IDs: \n", len(secretIds))
	for _, id := range secretIds {
		fmt.Println(id)
	}

	if err = common.DeleteSecret(cfg, secretArn); err != nil {
		panic("Couldn't delete secret!")
	}
}
