<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace DynamoDb;

class DynamoDBAttribute
{
    public string $KeyType;
    public string $AttributeType;
    public string $AttributeName;
    public string $Value;

    public function __construct(string $AttributeName, string $AttributeType, string $KeyType = '', string $Value = '')
    {
        $this->AttributeName = $AttributeName;
        $this->AttributeType = $AttributeType;
        $this->KeyType = $KeyType;
        $this->Value = $Value;
    }
}
