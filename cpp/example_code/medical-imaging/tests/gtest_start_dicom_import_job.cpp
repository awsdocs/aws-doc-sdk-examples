// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "medical-imaging_samples.h"
#include "medical-imaging_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MedicalImaging_GTests, start_dicom_import_job_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody(
                "mock_input/startDICOMImportJob.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String jobID;
        result = AwsDoc::Medical_Imaging::startDICOMImportJob(
                "12345678901234567890123456789012",
                "source_bucket",
                "source_folder",
                "destination_bucket",
                "destination_folder",
                "arn:aws:iam::123456789012:role/dicom_import",
                jobID,
                *s_clientConfig);

        ASSERT_TRUE(result);
        ASSERT_FALSE(jobID.empty());
    }

} // namespace AwsDocTest
