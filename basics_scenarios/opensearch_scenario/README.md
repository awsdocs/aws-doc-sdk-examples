# Amazon OpenSearch Service Basic Scenario

## Overview

 This Amazon OpenSearch Service basic scenario demonstrates how to interact with the Amazon OpenSearch service using an AWS SDK.  The scenario covers various operations such as creating an OpenSearch domain, modifying a domain, waiting for changes to the domain to enter a complete state, and so on. 
 
 Here are the top five service operations this scenario covers. 

**Create an Amazon OpenSearch Domain**:
   - Description: This operation creates a new Amazon OpenSearch domain, which is a managed instance of the OpenSearch engine.

2. **Describe the Amazon OpenSearch Domain**:
   - Description: This operation retrieves information about the specified Amazon OpenSearch domain.
   - The method `describeDomain(domainName)` is called to obtain the Amazon Resource Name (ARN) of the specified OpenSearch domain.

3. **List the Domains in Your Account**:
   - Description: This operation lists all the Amazon OpenSearch domains in the current AWS account.
   - The method `listAllDomains()` is called to retrieve a list of all the OpenSearch domains available in the account.

4. **Wait until the Domain's Change Status Reaches a Completed State**:
   - Description: This operation waits until the change status of the specified Amazon OpenSearch domain reaches a completed state.
   - When making changes to an OpenSearch domain, such as scaling the number of data nodes or updating the OpenSearch version, the domain goes through a change process. This method, `domainChangeProgress(domainName)`, waits until the change status of the specified domain reaches a completed state, which can take several minutes to several hours, depending on the complexity of the change and the current load on the OpenSearch service.
   Note this operation may take up to 20 minutes. 

5. **Modify the Domain**:
   - Description: This operation modifies the cluster configuration of the specified Amazon OpenSearch domain, such as the instance count.
   - The flexibility to modify the OpenSearch domain's configuration is particularly useful when the data or usage patterns change over time, as you can easily scale the domain to meet the new requirements without having to recreate the entire domain.
   - The method `updateSpecificDomain(domainName)` is called to update the configuration of the specified OpenSearch domain.

Note: These steps are not the complete program, but summarizes the 5 high-level steps. See the Eng Spec for a complete listing of operations. 

### Resources

This scenario does not require any additional AWS resources to run.


## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- JavaScript

## Additional reading

- [Amazon OpenSearch](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/what-is.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
