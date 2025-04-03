module github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime

go 1.22

toolchain go1.22.2

require (
	github.com/aws/aws-sdk-go-v2 v1.36.3
	github.com/aws/aws-sdk-go-v2/config v1.27.33
	github.com/aws/aws-sdk-go-v2/service/bedrockruntime v1.26.1
	github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0-20240907001412-a9375541143b
	github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0-20250401185104-e9ddacfbb4a0
)

require (
	github.com/aws/aws-sdk-go-v2/aws/protocol/eventstream v1.6.10 // indirect
	github.com/aws/aws-sdk-go-v2/credentials v1.17.32 // indirect
	github.com/aws/aws-sdk-go-v2/feature/ec2/imds v1.16.13 // indirect
	github.com/aws/aws-sdk-go-v2/internal/configsources v1.3.34 // indirect
	github.com/aws/aws-sdk-go-v2/internal/endpoints/v2 v2.6.34 // indirect
	github.com/aws/aws-sdk-go-v2/internal/ini v1.8.1 // indirect
	github.com/aws/aws-sdk-go-v2/service/internal/accept-encoding v1.11.4 // indirect
	github.com/aws/aws-sdk-go-v2/service/internal/presigned-url v1.11.19 // indirect
	github.com/aws/aws-sdk-go-v2/service/sso v1.22.7 // indirect
	github.com/aws/aws-sdk-go-v2/service/ssooidc v1.26.7 // indirect
	github.com/aws/aws-sdk-go-v2/service/sts v1.30.7 // indirect
	github.com/aws/smithy-go v1.22.3 // indirect
	golang.org/x/sys v0.25.0 // indirect
	golang.org/x/term v0.24.0 // indirect
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs => ./stubs

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/scenarios => ./scenarios
