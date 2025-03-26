## Overview
This AWS IoT SiteWise Service basic scenario demonstrates how to interact with the AWS IoT SiteWise service using an AWS SDK. The scenario covers various operations such as creating an Asset Model, creating assets, sending data to assets, and retrieving data.

## Key Operations

1. **Create an AWS SiteWise Asset Model**:
   - This step creates an AWS SiteWise Asset Model by invoking the `createAssetModel` method.

2. **Create an AWS IoT SiteWise Asset**:
   - This operation creates an AWS SiteWise asset.

3. **Retrieve the property ID values**:
   - To send data to an asset, we need to get the property ID values for the model properties. This scenario uses temperature and humidity properties.

4. **Send data to an AWS IoT SiteWise Asset**:
   - This operation sends data to an IoT SiteWise Asset.

5. **Retrieve the value of the IoT SiteWise Asset property**:
   - This operation gets data from the asset.

**Note** See the Eng spec for a full listing of operations. 

## Resources

This Basics scenario requires an IAM role that has permissions to work with the AWS IoT SiteWise service. The scenario creates this resource using a CloudFormation template.

## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- JavaScript

## Additional Reading

- [AWS IoT SiteWise Documentation](https://docs.aws.amazon.com/iot-sitewise/latest/userguide/what-is-sitewise.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
