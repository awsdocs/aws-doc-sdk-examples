### AssumeRolev2.go

This example gets temporary security credentials to access resources.

`go run AssumeRolev2.go -r ROLE-ARN -s SESSION-NAME`

- _ROLE-ARN_ is the ARN of the role to assume.
- _SESSION-NAME_ is the name of the assumed role session.

The unit test accepts similar values in _config.json_.
