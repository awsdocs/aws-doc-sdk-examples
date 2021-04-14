# AttachUserPolicyv2.go

This example attaches an Amazon DynamoDB full-access policy to an IAM role.

`go run AttachUserPolicyv2.go -n ROLE-NAME`

- _ROLE-NAME_ is the name of the role to which the policy is attached.

The unit test accepts a similar value in _config.json_.

## Running the unit tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
