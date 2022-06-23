module example.aws/go-v2/examples/cross_service/wordfreq/service

go 1.16

require (
	github.com/aws/aws-sdk-go-v2 v1.8.0
	github.com/aws/aws-sdk-go-v2/config v1.6.0
	github.com/aws/aws-sdk-go-v2/credentials v1.3.2
	github.com/aws/aws-sdk-go-v2/feature/ec2/imds v1.4.0
	github.com/aws/aws-sdk-go-v2/service/s3 v1.11.1
	github.com/aws/aws-sdk-go-v2/service/sqs v1.7.0
	github.com/aws/aws-sdk-go-v2/service/sts v1.6.1
	github.com/bwmarrin/snowflake v0.3.0
	github.com/google/uuid v1.3.0
	github.com/tidwall/gjson v1.9.3
	go.uber.org/zap v1.18.1
)

replace example.aws/go-v2/examples/cross_service/wordfreq/service/shared => ./shared
