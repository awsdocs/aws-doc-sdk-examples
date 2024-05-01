// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once

#ifndef CODEBUILD_EXAMPLES_CODEBUILD_SAMPLES_H
#define CODEBUILD_EXAMPLES_CODEBUILD_SAMPLES_H

#include <aws/core/Aws.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/SortOrderType.h>

namespace AwsDoc {
    namespace CodeBuild {
        //! Start an AWS CodeBuild project build.
        /*!
          \param projectName: A CodeBuild project name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool startBuild(const Aws::String &projectName,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the CodeBuild builds.
        /*!
          \param sortType: 'SortOrderType' type.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listBuilds(Aws::CodeBuild::Model::SortOrderType sortType,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the CodeBuild projects.
        /*!
          \param sortType: 'SortOrderType' type.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listProjects(Aws::CodeBuild::Model::SortOrderType sortType,
                          const Aws::Client::ClientConfiguration &clientConfiguration);
    } // CodeBuild
} // AwsDoc

#endif //CODEBUILD_EXAMPLES_CODEBUILD_SAMPLES_H

