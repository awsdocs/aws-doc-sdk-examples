/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "rds_gtests.h"
#include "rds_samples.h"

namespace AwsDocTest {

    extern const std::vector<std::string> RESPONSES;

    void addHttpResponses(MockHTTP &mockHttp);

#if 0
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(RDS_GTests, delete_bucket) {
        AddCommandLineResponses(RESPONSES);

        bool result = AwsDoc::RDS::gettingStartedWithDBInstances(*s_clientConfig);
        ASSERT_TRUE(result);
    }
#endif

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(RDS_GTests, delete_bucket_m_) {
        AddCommandLineResponses(RESPONSES);

        MockHTTP mockHttp;
        addHttpResponses(mockHttp);

        bool result = AwsDoc::RDS::gettingStartedWithDBInstances(*s_clientConfig);
        ASSERT_TRUE(result);
    }

    const std::vector<std::string> RESPONSES = {"1", // Which family do you want to use?
                                                "3", // Enter a new value in the range 1-65535:
                                                "2", // Enter a new value in the range 1-65535:
                                                "foo", // Enter an administrator username for the database:
                                                "foo_Foo8", // Enter a password for the administrator (at least 8 characters):
                                                "1", // The available engines for your parameter group are:
                                                "1", // Which micro DB instance class do you want to use?
                                                "y", // Do you want to create a snapshot of your DB instance (y/n)?
                                                "y"}; // Do you want to delete the DB instance and parameter group (y/n)?

    void addHttpResponses(MockHTTP &mockHttp) {
// DescribeDBParameterGroups
        mockHttp.addResponseWithBody(R"(<ErrorResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <Error>
     <Type>Sender</Type>
     <Code>DBParameterGroupNotFound</Code>
     <Message>DBParameterGroup not found: doc-example-parameter-group</Message>
   </Error>
   <RequestId>ad9db88d-df22-4c3b-b3a3-e05dbf775491</RequestId>
 </ErrorResponse>

)", Aws::Http::HttpResponseCode::NOT_FOUND);

// DescribeDBEngineVersions
        mockHttp.addResponseWithBody(R"(<DescribeDBEngineVersionsResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <DescribeDBEngineVersionsResult>
     <DBEngineVersions>
       <DBEngineVersion>
         <SupportsBabelfish>false</SupportsBabelfish>
         <MajorEngineVersion>5.7</MajorEngineVersion>
         <DBEngineVersionDescription>MySQL 5.7.33</DBEngineVersionDescription>
         <SupportsCertificateRotationWithoutRestart>false</SupportsCertificateRotationWithoutRestart>
         <SupportedCACertificateIdentifiers>
           <member>rds-ca-2019</member>
           <member>rds-ca-ecc384-g1</member>
           <member>rds-ca-rsa4096-g1</member>
           <member>rds-ca-rsa2048-g1</member>
         </SupportedCACertificateIdentifiers>
         <SupportsGlobalDatabases>false</SupportsGlobalDatabases>
         <DBParameterGroupFamily>mysql5.7</DBParameterGroupFamily>
         <SupportsParallelQuery>false</SupportsParallelQuery>
         <MinorEngineVersion>33</MinorEngineVersion>
         <Engine>mysql</Engine>
         <EngineVersion>5.7.33</EngineVersion>
         <SupportsReadReplica>true</SupportsReadReplica>
         <SupportsCluster>false</SupportsCluster>
         <ValidUpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.34</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.34</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>34</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.36</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.36</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>36</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.37</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.37</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>37</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.38</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>true</AutoUpgrade>
             <Description>MySQL 5.7.38</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>38</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.39</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.39</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>39</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.40</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.40</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>40</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.41</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.41</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>41</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.23</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.23</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>23</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.27</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.27</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>27</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.28</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.28</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>28</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.30</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.30</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>30</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.31</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.31</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>31</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.32</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.32</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>32</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
         </ValidUpgradeTarget>
         <ExportableLogTypes>
           <member>audit</member>
           <member>error</member>
           <member>general</member>
           <member>slowquery</member>
         </ExportableLogTypes>
         <SupportedFeatureNames/>
         <SupportsLocalWriteForwarding>false</SupportsLocalWriteForwarding>
         <SupportsLogExportsToCloudwatchLogs>true</SupportsLogExportsToCloudwatchLogs>
         <DBEngineDescription>MySQL Community Edition</DBEngineDescription>
         <Status>available</Status>
       </DBEngineVersion>
     </DBEngineVersions>
    </DescribeDBEngineVersionsResult>
</DescribeDBEngineVersionsResponse>
)");
        // CreateDBParameterGroup
        mockHttp.addResponseWithBody(R"(<CreateDBParameterGroupResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <CreateDBParameterGroupResult>
     <DBParameterGroup>
       <DBParameterGroupFamily>mysql5.7</DBParameterGroupFamily>
       <DBParameterGroupName>doc-example-parameter-group</DBParameterGroupName>
       <DBParameterGroupArn>arn:aws:rds:us-east-1:123502194722:pg:doc-example-parameter-group</DBParameterGroupArn>
       <Description>Example parameter group.</Description>
     </DBParameterGroup>
   </CreateDBParameterGroupResult>
   <ResponseMetadata>
     <RequestId>7bd5552a-0a90-4be8-8ed1-c7af78b07815</RequestId>
   </ResponseMetadata>
 </CreateDBParameterGroupResponse>

)");

// DescribeDBParameters
        mockHttp.addResponseWithBody(R"(<DescribeDBParametersResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <DescribeDBParametersResult>
     <Parameters>
       <Parameter>
         <AllowedValues>1-65535</AllowedValues>
         <ApplyType>dynamic</ApplyType>
         <DataType>integer</DataType>
         <Description>Intended for use with master-to-master replication, and can be used to control the operation of AUTO_INCREMENT columns</Description>
         <ApplyMethod>pending-reboot</ApplyMethod>
         <ParameterName>auto_increment_increment</ParameterName>
         <Source>user</Source>
         <IsModifiable>true</IsModifiable>
         <ParameterValue>3</ParameterValue>
       </Parameter>
       <Parameter>
         <AllowedValues>1-65535</AllowedValues>
         <ApplyType>dynamic</ApplyType>
         <DataType>integer</DataType>
         <Description>Determines the starting point for the AUTO_INCREMENT column value</Description>
         <ApplyMethod>pending-reboot</ApplyMethod>
         <ParameterName>auto_increment_offset</ParameterName>
         <Source>user</Source>
         <IsModifiable>true</IsModifiable>
         <ParameterValue>2</ParameterValue>
       </Parameter>
     </Parameters>
   </DescribeDBParametersResult>
   <ResponseMetadata>
     <RequestId>7d540796-3a43-4a38-90df-42c7b2e0f171</RequestId>
   </ResponseMetadata>
 </DescribeDBParametersResponse>

)");


// ModifyDBParameterGroup
        mockHttp.addResponseWithBody(R"(<ModifyDBParameterGroupResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <ModifyDBParameterGroupResult>
     <DBParameterGroupName>doc-example-parameter-group</DBParameterGroupName>
   </ModifyDBParameterGroupResult>
   <ResponseMetadata>
     <RequestId>8cbc41e9-e0d1-42f0-af64-3e0ad2be4b62</RequestId>
   </ResponseMetadata>
 </ModifyDBParameterGroupResponse>

)");

// DescribeDBParameters
        mockHttp.addResponseWithBody(R"(<DescribeDBParametersResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <DescribeDBParametersResult>
     <Parameters>
       <Parameter>
         <AllowedValues>1-65535</AllowedValues>
         <ApplyType>dynamic</ApplyType>
         <DataType>integer</DataType>
         <Description>Intended for use with master-to-master replication, and can be used to control the operation of AUTO_INCREMENT columns</Description>
         <ApplyMethod>pending-reboot</ApplyMethod>
         <ParameterName>auto_increment_increment</ParameterName>
         <Source>user</Source>
         <IsModifiable>true</IsModifiable>
         <ParameterValue>3</ParameterValue>
       </Parameter>
       <Parameter>
         <AllowedValues>1-65535</AllowedValues>
         <ApplyType>dynamic</ApplyType>
         <DataType>integer</DataType>
         <Description>Determines the starting point for the AUTO_INCREMENT column value</Description>
         <ApplyMethod>pending-reboot</ApplyMethod>
         <ParameterName>auto_increment_offset</ParameterName>
         <Source>user</Source>
         <IsModifiable>true</IsModifiable>
         <ParameterValue>2</ParameterValue>
       </Parameter>
     </Parameters>
   </DescribeDBParametersResult>
   <ResponseMetadata>
     <RequestId>7d540796-3a43-4a38-90df-42c7b2e0f171</RequestId>
   </ResponseMetadata>
 </DescribeDBParametersResponse>

)");

        // DescribeDBInstances
        mockHttp.addResponseWithBody(R"(<ErrorResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <Error>
     <Type>Sender</Type>
     <Code>DBInstanceNotFound</Code>
     <Message>DBInstance doc-example-instance not found.</Message>
   </Error>
   <RequestId>67279f88-7a84-4ad9-8bfe-be4f131dc17f</RequestId>
 </ErrorResponse>

)", Aws::Http::HttpResponseCode::NOT_FOUND);

// DescribeDBEngineVersions
        mockHttp.addResponseWithBody(R"(<DescribeDBEngineVersionsResponse xmlns="http://rds.amazonaws.com/doc/2014-10-31/">   <DescribeDBEngineVersionsResult>
     <DBEngineVersions>
       <DBEngineVersion>
         <SupportsBabelfish>false</SupportsBabelfish>
         <MajorEngineVersion>5.7</MajorEngineVersion>
         <DBEngineVersionDescription>MySQL 5.7.33</DBEngineVersionDescription>
         <SupportsCertificateRotationWithoutRestart>false</SupportsCertificateRotationWithoutRestart>
         <SupportedCACertificateIdentifiers>
           <member>rds-ca-2019</member>
           <member>rds-ca-ecc384-g1</member>
           <member>rds-ca-rsa4096-g1</member>
           <member>rds-ca-rsa2048-g1</member>
         </SupportedCACertificateIdentifiers>
         <SupportsGlobalDatabases>false</SupportsGlobalDatabases>
         <DBParameterGroupFamily>mysql5.7</DBParameterGroupFamily>
         <SupportsParallelQuery>false</SupportsParallelQuery>
         <MinorEngineVersion>33</MinorEngineVersion>
         <Engine>mysql</Engine>
         <EngineVersion>5.7.33</EngineVersion>
         <SupportsReadReplica>true</SupportsReadReplica>
         <SupportsCluster>false</SupportsCluster>
         <ValidUpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.34</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.34</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>34</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.36</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.36</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>36</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.37</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.37</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>37</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.38</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>true</AutoUpgrade>
             <Description>MySQL 5.7.38</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>38</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.39</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.39</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>39</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.40</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.40</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>40</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>5.7.41</EngineVersion>
             <MajorEngineVersion>5.7</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 5.7.41</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>41</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>false</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.23</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.23</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>23</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.27</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.27</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>27</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.28</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.28</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>28</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.30</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.30</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>30</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.31</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.31</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>31</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
           <UpgradeTarget>
             <EngineVersion>8.0.32</EngineVersion>
             <MajorEngineVersion>8.0</MajorEngineVersion>
             <AutoUpgrade>false</AutoUpgrade>
             <Description>MySQL 8.0.32</Description>
             <SupportsSnapshotUpgrade>false</SupportsSnapshotUpgrade>
             <MinorEngineVersion>32</MinorEngineVersion>
             <Engine>mysql</Engine>
             <IsMajorVersionUpgrade>true</IsMajorVersionUpgrade>
           </UpgradeTarget>
         </ValidUpgradeTarget>
         <ExportableLogTypes>
           <member>audit</member>
           <member>error</member>
           <member>general</member>
           <member>slowquery</member>
         </ExportableLogTypes>
         <SupportedFeatureNames/>
         <SupportsLocalWriteForwarding>false</SupportsLocalWriteForwarding>
         <SupportsLogExportsToCloudwatchLogs>true</SupportsLogExportsToCloudwatchLogs>
         <DBEngineDescription>MySQL Community Edition</DBEngineDescription>
         <Status>available</Status>
       </DBEngineVersion>
     </DBEngineVersions>
</DescribeDBEngineVersionsResult>
</DescribeDBEngineVersionsResponse>
)");
    }
} // namespace AwsDocTest
