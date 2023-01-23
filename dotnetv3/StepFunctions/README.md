# Amazon Step Functions code examples for the SDK for .NET

## Overview
The examples in this folder perform Step Functions actions using the AWS SDK for .NET.

AWS Step Functions is a visual workflow service that helps developers use AWS
services to build distributed applications, automate processes, orchestrate
microservices, and create data and machine learning (ML) pipelines.  

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Step Functions](Actions/HelloStepFunctions.cs)

### Single actions
Code excerpts that show you how to call individual service functions.
* [Create an activity](Actions/StepFunctionsWrapper.cs) (`CreateActionAsync`)
* [Create a state machine](Actions/StepFunctionsWrapper.cs) (`CreateStateMachineAsync`)
* [Delete an activity](Actions/StepFunctionsWrapper.cs) (`DeleteActivityAsync`)
* [Delete a state machine](Actions/StepFunctionsWrapper.cs) (`DeleteStateMachineAsync`)
* [List actions](Actions/StepFunctionsWrapper.cs) (`ListActivitiessAsync`)
* [List state machines](Actions/StepFunctionsWrapper.cs) (`ListStateMachinesAsync`)
* [List state machine executions](Actions/StepFunctionsWrapper.cs) (`ListExecutionsAsync`)
* [Start state machine executions](Actions/StepFunctionsWrapper.cs) (`StartExecutionAsync`)
* [Stop state machine exeucitons](Actions/StepFunctionsWrapper.cs) (`StopExecutionAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Get started with activities and step functions](Scenarios/StepFunctionsBasics.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

### Instructions
The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS Region. The following
example shows how to supply the AWS Region to match your own as a
parameter to the client constructor:

```
var client = new AmazonKinesisClient(Amazon.RegionEndpoint.USWest2);
```

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test Runner to run the tests.

## Additional resources
* [Step Functions Developer Guide](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html)
* [Step Functions API Reference](https://docs.aws.amazon.com/step-functions/latest/apireference/Welcome.html)
* [Step Functions .NET SDK Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/StepFunctions/NStepFunctions.html)
* [Amazon States Language Definition](https://states-language.net/spec.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
