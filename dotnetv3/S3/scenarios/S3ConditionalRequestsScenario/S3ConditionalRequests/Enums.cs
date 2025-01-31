// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace S3ConditionalRequestsScenario;

public enum S3ConditionType
{
    IfMatch,
    IfNoneMatch,
    IfModifiedSince,
    IfUnmodifiedSince
}