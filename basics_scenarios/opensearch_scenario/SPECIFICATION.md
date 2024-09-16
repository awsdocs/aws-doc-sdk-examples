# Amazon OpenSearch Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with Amazon OpenSearch using the AWS SDK. It demonstrates various tasks such as creating a domain, modifying a domain, getting the status of a change request, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with Amazon OpenSearch and the AWS SDK.

## Resources
This Basics scenario does not have a depedency on any AWS resources.

## Hello Amazon OpenSearch
This program is intended for users not familiar with the Amazon OpenSearch SDK to easily get up and running. The logic is to show use of `
`listVersions()`.

## Scenario Program Flow
The Amazon OpenSearch Basics scenario executes the following operations.

1. **Create an Amazon OpenSearch Domain**:
   - Description: This operation creates a new Amazon OpenSearch domain, which is a managed instance of the OpenSearch engine. Invoke the `createDomain` method. 
   - Exception Handling: Check to see if a `OpenSearchException` is thrown. We tested as group and `ResourceExistsException` is not thrown. Display the message and end the program.

2. **Describe the Amazon OpenSearch Domain**:
   - Description: This operation retrieves information about the specified Amazon OpenSearch domain.
   - The method `describeDomain(domainName)` is called to obtain the Amazon Resource Name (ARN) of the specified OpenSearch domain.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

3. **List the Domains in Your Account**:
   - Description: This operation lists all the Amazon OpenSearch domains in the current AWS account.
   - The method `listAllDomains()` is called to retrieve a list of all the OpenSearch domains available in the account.
   - Exception Handling: Check to see if a `OpenSearchException` is thrown. There are not many other useful exceptions for this specific call. If so, display the message and end the program. 

4. **Wait until the Domain's Change Status Reaches a Completed State**:
   - Description: This operation waits until the change status of the specified Amazon OpenSearch domain reaches a completed state.
   - When making changes to an OpenSearch domain, such as scaling the number of data nodes or updating the OpenSearch version, the domain goes through a change process. This method, `domainChangeProgress(domainName)`, waits until the change status of the specified domain reaches a completed state, which can take several minutes to several hours, depending on the complexity of the change and the current load on the OpenSearch service.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 
   Note this operation may take up to 20 minutes (I am observering that uses the Java SDK). 

5. **Modify the Domain**:
   - Description: This operation modifies the cluster configuration of the specified Amazon OpenSearch domain, such as the instance count.
   - The flexibility to modify the OpenSearch domain's configuration is particularly useful when the data or usage patterns change over time, as you can easily scale the domain to meet the new requirements without having to recreate the entire domain.
   - The method `updateSpecificDomain(domainName)` is called to update the configuration of the specified OpenSearch domain.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program.  

6. **Wait until the Domain's Change Status Reaches a Completed State (Again)**:
   - Description: This operation is similar to the previous "Wait until the Domain's Change Status Reaches a Completed State" operation, but it is called after the domain modification to ensure the changes have been fully applied.
   - The method `domainChangeProgress(domainName)` is called again to wait until the change status of the specified domain reaches a completed state.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

7. **Tag the Domain**:
   - Description: This operation adds tags to the specified Amazon OpenSearch domain.
   - Tags are key-value pairs that can be used to categorize and filter OpenSearch domains. They can be useful for tracking costs by grouping expenses for similarly tagged resources.
   - The method `addDomainTags(arn)` is called to add tags to the OpenSearch domain identified by the provided ARN.
   - Exception Handling: Check to see if a `OpenSearchException` is thrown. There are not many other useful exceptions for this specific call. If so, display the message and end the program. 

8. **List Domain Tags**:
   - Description: This operation lists the tags associated with the specified Amazon OpenSearch domain.
   - The method `listDomainTags(arn)` is called to retrieve the tags for the OpenSearch domain identified by the provided ARN.
   - Exception Handling: Check to see if a `OpenSearchException` is thrown. There are not many other useful exceptions for this specific call. If so, display the message and end the program. 

9. **Delete the Amazon OpenSearch Domain**:
   - Description: This operation deletes the specified Amazon OpenSearch domain.
   - The method `deleteSpecificDomain(domainName)` is called to delete the OpenSearch domain with the specified name."
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program.


### Program execution
The following shows the output of the Amazon OpenSearch program in the console. 

```
These operations exposed by the OpenSearch Service client is focused on managing the OpenSearch Service domains
and their configurations, not the data within the domains (such as indexing or querying documents).
For document management, you typically interact directly with the OpenSearch REST API or use other libraries,
such as the OpenSearch Java client.

Lets get started...


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
1. Create an Amazon OpenSearch domain
An Amazon OpenSearch domain is a managed instance of the OpenSearch engine,
which is an open-source search and analytics engine derived from Elasticsearch.
An OpenSearch domain is essentially a cluster of compute resources and storage that hosts
one or more OpenSearch indexes, enabling you to perform full-text searches, data analysis, and
visualizations.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Sending domain creation request...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Domain status is DomainStatus(DomainId=814548047983/test-domain-1725641905678, DomainName=test-domain-1725641905678, AnonymousAuthEnabled=false), AutoTuneOptions=AutoTuneOptionsOutput(State=DISABLED, UseOffPeakWindow=false), ChangeProgressDetails=ChangeProgressDetails(ChangeId=9ccf4ee9-b924-4615-82b3-3cc5564abb6a), OffPeakWindowOptions=OffPeakWindowOptions(Enabled=true, OffPeakWindow=OffPeakWindow(WindowStartTime=WindowStartTime(Hours=2, Minutes=0))), SoftwareUpdateOptions=SoftwareUpdateOptions(AutoSoftwareUpdateEnabled=false))
Domain Id is 814548047983/test-domain-1725641905678
The Id of the domain is 814548047983/test-domain-1725641905678

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Describe the Amazon OpenSearch domain 

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Domain endpoint is: null
ARN: arn:aws:es:us-east-1:814548047983:domain/test-domain-1725641905678
Engine version OpenSearch_1.0

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. List the domains in your account 

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Domain name is test-domain-1725493654227
Domain name is test-domain-1725565012971
Domain name is test-domain-1725493860109
Domain name is test-domain-1725641905678
Domain name is test-domain-1725493726160

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Wait until the domain's change status reaches a completed state
In the following method call, the clock counts up until the domain's change status reaches a completed state.

Roughly, the time it takes for an OpenSearch domain's change status to reach a completed state can range
from a few minutes to several hours, depending on the complexity of the change and the current load on
the OpenSearch service. In general, simple changes, such as scaling the number of data nodes or
updating the OpenSearch version, may take 10-30 minutes,


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

OpenSearch domain state: PROCESSING | Time Elapsed: 19:29
OpenSearch domain status: Completed

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Modify the domain 
You can modify the cluster configuration, such as the instance count, without having to manually
manage the domain's settings. This flexibility is particularly useful when your data or usage patterns
change over time, as you can easily scale the OpenSearch domain to meet the new requirements without
having to recreate the entire domain.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Sending domain update request...
Domain update response from Amazon OpenSearch Service:
UpdateDomainConfigResponse(DomainConfig=DomainConfig(EngineVersion=VersionStatus(Options=OpenSearch_1.0, Status=OptionStatus(CreationDate=2024-09-06T16:58:34.731Z, UpdateDate=2024-09-06T17:19:11.915Z, UpdateVersion=10, State=Active, PendingDeletion=false)), ClusterConfig=ClusterConfigStatus(Options=ClusterConfig(InstanceType=t2.small.search, InstanceCount=3, DedicatedMasterEnabled=true, ZoneAwarenessEnabled=false, DedicatedMasterType=t2.small.search, DedicatedMasterCount=3, WarmEnabled=false, ColdStorageOptions=ColdStorageOptions(Enabled=false), MultiAZWithStandbyEnabled=false), Status=OptionStatus(CreationDate=2024-09-06T16:58:34.743Z, UpdateDate=2024-09-06T17:33:27.638Z, UpdateVersion=11, State=Processing, PendingDeletion=false)), EBSOptions=EBSOptionsStatus(Options=EBSOptions(EBSEnabled=true, VolumeType=gp2, VolumeSize=10), Status=OptionStatus(CreationDate=2024-09-06T16:58:34.733Z, UpdateDate=2024-09-06T17:19:11.919Z, UpdateVersion=10, State=Active, PendingDeletion=false)), AccessPolicies=AccessPoliciesStatus(Options=, Status=OptionStatus(CreationDate=2024-09-06T17:33:27.876Z, UpdateDate=2024-09-06T17:33:27.876Z, UpdateVersion=11, State=Active, PendingDeletion=false)), IPAddressType=IPAddressTypeStatus(Options=ipv4, Status=OptionStatus(CreationDate=2024-09-06T16:58:34.727Z, UpdateDate=2024-09-06T17:19:11.909Z, UpdateVersion=10, State=Active, PendingDeletion=false)), SnapshotOptions=SnapshotOptionsStatus(Options=SnapshotOptions(AutomatedSnapshotStartHour=0), Status=OptionStatus(CreationDate=2024-09-06T16:58:34.734Z, UpdateDate=2024-09-06T17:19:11.919Z, UpdateVersion=10, State=Active, PendingDeletion=false)), VPCOptions=VPCDerivedInfoStatus(Options=VPCDerivedInfo(), Status=OptionStatus(CreationDate=2024-09-06T17:33:27.876Z, UpdateDate=2024-09-06T17:33:27.876Z, UpdateVersion=11, State=Active, PendingDeletion=false))))

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Wait until the domain's change status reaches a completed state
In the following method call, the clock counts up until the domain's change status reaches a completed state.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

OpenSearch domain state: PROCESSING | Time Elapsed: 10:42
OpenSearch domain status: Completed

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Tag the Domain 
Tags let you assign arbitrary information to an Amazon OpenSearch Service domain so you can
categorize and filter on that information. A tag is a key-value pair that you define and
associate with an OpenSearch Service domain. You can use these tags to track costs by grouping
expenses for similarly tagged resources. 


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Successfully added tags to the domain

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. List Domain tags 

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Tag key is instances
Tag value is m3.2xlarge
Tag key is service
Tag value is OpenSearch

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Delete the Amazon OpenSearch 

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

test-domain-1725641905678 was successfully deleted.
test-domain-1725641905678 has been deleted.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the AWS OpenSearch Scenario
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                         | metadata file                     | metadata key                            |
|--------------------------------|-----------------------------------|---------------------------------------- |
| `createDomain`                 | opensearch_metadata.yaml          | opensearch_CreateDomain                 |
| `deleteDomain            `     | opensearch_metadata.yaml          | opensearch_DeleteDomain                 |
| `listDomainNames  `            | opensearch_metadata.yaml          | opensearch_ListDomainNames              |
| `updateDomainConfig`           | opensearch_metadata.yaml          | opensearch_UpdateDomainConfig           |
| `describeDomain`               | opensearch_metadata.yaml          | opensearch_DescribeDomain               |
| `describeDomainChangeProgress` | opensearch_metadata.yaml          | opensearch_ChangeProgress               |
| `listDomainTags`               | opensearch_metadata.yaml          | opensearch_ListTags                     |
| `scenario`                     | opensearch_metadata.yaml          | opensearch_Scenario                     |

