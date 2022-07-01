<?php

namespace DynamoDb;

class DynamoDBAttribute
{
    public string $KeyType;
    public string $AttributeType;
    public string $AttributeName;

    public function __construct(string $AttributeName, string $AttributeType, string $KeyType = '')
    {
        $this->AttributeName = $AttributeName;
        $this->AttributeType = $AttributeType;
        $this->KeyType = $KeyType;
    }
}
