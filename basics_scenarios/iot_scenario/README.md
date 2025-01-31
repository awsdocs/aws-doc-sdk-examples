# AWS IoT Basics Scenario

## Introduction
The AWS IoT basics scenario demonstrates how to interact with the AWS IoT Core service. The program guides you through a series of steps, including creating an IoT Thing, generating a device certificate, updating the Thing with attributes, and performing various other operations. It utilizes the AWS SDK and showcases the functionality for managing IoT Things, certificates, rules, shadows, and performing searches. Furthermore, this example covers a wide range of functionality for new users of the AWS SDK and AWS IoT Core service.

## User Interaction
The program prompts the user for input at various stages, allowing them to customize the scenario to their specific needs. The user is asked to provide the following information:

1. **Thing Name**: The name of the IoT Thing to be created.
2. **Certificate Creation**: The user can choose to create a device certificate for the IoT Thing.
3. **Thing Attribute Update**: The user can update the IoT Thing with new attributes.
4. **Rule Creation**: The user is prompted to provide a rule name and an SNS action ARN for creating a new IoT rule.
5. **Certificate Deletion**: The user can choose to detach and delete the previously created device certificate.
6. **Thing Deletion**: The user can choose to delete the IoT Thing.

Throughout the workflow, the program provides detailed explanations and prompts the user to press Enter to continue, ensuring a user-friendly experience.

## Implementations

This scenario example is implemented in the following languages:

- Java
- C++
- Kotlin

## Additional reading

- [AWS IoT](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)






