/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
#pragma once

#ifndef AUTO_SCALING_EXAMPLES_AUTOSCALING_SAMPLES_H
#define AUTO_SCALING_EXAMPLES_AUTOSCALING_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace AutoScaling {
        //! Routine which demonstrates using an Amazon EC2 Auto Scaling group
        //! to manage Amazon EC2 instances.
        /*!
         \sa groupsAndInstancesScenario()
         \param clientConfig: AWS client configuration.
         \return bool: Successful completion.
         */
        bool groupsAndInstancesScenario(
                const Aws::Client::ClientConfiguration &clientConfig);
    } // AutoScaling
} // AwsDoc

#endif //AUTO_SCALING_EXAMPLES_AUTOSCALING_SAMPLES_H
