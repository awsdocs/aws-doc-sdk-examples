/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef RDS_EXAMPLES_AURORA_SAMPLES_H
#define RDS_EXAMPLES_AURORA_SAMPLES_H

namespace AwsDoc {
    namespace Aurora {
        //! Routine which creates an Amazon Aurora DB cluster and demonstrates several
        //! operations on that cluster. Aurora is part of Amazon Relational Database
        //! Service (Amazon RDS).
        /*!
         \sa gettingStartedWithDBClusters()
         \param clientConfiguration: AWS client configuration.
         \return bool: Successful completion.
         */
        bool gettingStartedWithDBClusters(
                const Aws::Client::ClientConfiguration &clientConfig);

    } // Aurora
} // AwsDoc

#endif //RDS_EXAMPLES_AURORA_SAMPLES_H
