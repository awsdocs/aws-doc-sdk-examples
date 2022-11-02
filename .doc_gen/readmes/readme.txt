# {{ short_service_name }} code examples for the {{ short_sdk_name }}
## Overview
These examples show how to {{ code_examples_actions }} using the {{ short_sdk_name }}.

{{ short_service_name }} {{ service_blurb_from_website }}

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
{% for action in actions %}
* {{ action }}
{% endfor %}

{% if scenarios %}
### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
{% for scenario in scenarios %}
* {{ scenario }}
{% endfor %}
{% endif %}

{% if cross_service_examples %}
### Cross-service examples
Sample applications that work across multiple AWS services.
{% for cross_service_example in cross_service_examples %}
* {{ cross_service_example }}
{% endfor %}
{% endif %}

## Run the examples

### Prerequisites

See the [Ruby README.md](../../../ruby/README.md) for prerequisites.

### Instructions
{{ code_instructions }}

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

{{ test_instructions }}

## Additional resources
* [Service Developer Guide]({{service_developer_guide}})
* [Service API Reference]({{service_api_reference_guide}})
* [SDK API reference guide]({{language_sdk_reference_guide}})

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

