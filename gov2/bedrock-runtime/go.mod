module github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime

go 1.21

require (
	github.com/aws/aws-sdk-go-v2 v1.22.2
	github.com/aws/aws-sdk-go-v2/config v1.23.0
	github.com/aws/aws-sdk-go-v2/service/bedrockruntime v1.3.1
	github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/scenarios v0.0.0-00010101000000-000000000000
	github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs v0.0.0-00010101000000-000000000000
	github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools v0.0.0-20231116013656-9f08f276537a
	github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools v0.0.0-20231116013656-9f08f276537a
)

require (
	github.com/aws/aws-sdk-go-v2/aws/protocol/eventstream v1.5.0 // indirect
	github.com/aws/aws-sdk-go-v2/credentials v1.15.2 // indirect
	github.com/aws/aws-sdk-go-v2/feature/ec2/imds v1.14.3 // indirect
	github.com/aws/aws-sdk-go-v2/internal/configsources v1.2.2 // indirect
	github.com/aws/aws-sdk-go-v2/internal/endpoints/v2 v2.5.2 // indirect
	github.com/aws/aws-sdk-go-v2/internal/ini v1.6.0 // indirect
	github.com/aws/aws-sdk-go-v2/service/internal/presigned-url v1.10.2 // indirect
	github.com/aws/aws-sdk-go-v2/service/sso v1.17.1 // indirect
	github.com/aws/aws-sdk-go-v2/service/ssooidc v1.19.1 // indirect
	github.com/aws/aws-sdk-go-v2/service/sts v1.25.1 // indirect
	github.com/aws/smithy-go v1.16.0 // indirect
	golang.org/x/sys v0.9.0 // indirect
	golang.org/x/term v0.9.0 // indirect
)

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs => ./stubs

replace github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/scenarios => ./scenarios
