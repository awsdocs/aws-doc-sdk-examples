<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.kms.service.KmsService]

namespace Kms;

use Aws\Kms\Exception\KmsException;
use Aws\Kms\KmsClient;
use Aws\Result;
use Aws\ResultPaginator;
use AwsUtilities\AWSServiceClass;

class KmsService extends AWSServiceClass
{

    protected KmsClient $client;
    protected bool $verbose;

    /***
     * @param KmsClient|null $client
     * @param bool $verbose
     */
    public function __construct(KmsClient $client = null, bool $verbose = false)
    {
        $this->verbose = $verbose;
        if($client){
            $this->client = $client;
            return;
        }
        $this->client = new KmsClient([]);
    }

    // snippet-start:[php.example_code.kms.service.createKey]

    /***
     * @param string $keySpec
     * @param string $keyUsage
     * @param string $description
     * @return array
     */
    public function createKey(string $keySpec = "", string $keyUsage = "", string $description = "Created by the SDK for PHP")
    {
        $parameters = ['Description' => $description];
        if($keySpec && $keyUsage){
            $parameters['KeySpec'] = $keySpec;
            $parameters['KeyUsage'] = $keyUsage;
        }
        try {
            $result = $this->client->createKey($parameters);
            return $result['KeyMetadata'];
        }catch(KmsException $caught){
            // Check for error specific to createKey operations
            if ($caught->getAwsErrorMessage() == "LimitExceededException"){
                echo "The request was rejected because a quota was exceeded. For more information, see Quotas in the Key Management Service Developer Guide.";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.createKey]

    // snippet-start:[php.example_code.kms.service.decrypt]

    /***
     * @param string $keyId
     * @param string $ciphertext
     * @param string $algorithm
     * @return Result
     */
    public function decrypt(string $keyId, string $ciphertext, string $algorithm = "SYMMETRIC_DEFAULT")
    {
        try{
            return $this->client->decrypt([
                'CiphertextBlob' => $ciphertext,
                'EncryptionAlgorithm' => $algorithm,
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem decrypting the data: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.decrypt]

    // snippet-start:[php.example_code.kms.service.encrypt]

    /***
     * @param string $keyId
     * @param string $text
     * @return Result
     */
    public function encrypt(string $keyId, string $text)
    {
        try {
            return $this->client->encrypt([
                'KeyId' => $keyId,
                'Plaintext' => $text,
            ]);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "DisabledException"){
                echo "The request was rejected because the specified KMS key is not enabled.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.encrypt]

    // snippet-start:[php.example_code.kms.service.listAliases]

    /***
     * @param string $keyId
     * @param int $limit
     * @return ResultPaginator
     */
    public function listAliases(string $keyId = "", int $limit = 0)
    {
        $args = [];
        if($keyId){
            $args['KeyId'] = $keyId;
        }
        if($limit){
            $args['Limit'] = $limit;
        }
        try{
            return $this->client->getPaginator("ListAliases", $args);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "InvalidMarkerException"){
                echo "The request was rejected because the marker that specifies where pagination should next begin is not valid.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.listAliases]

    // snippet-start:[php.example_code.kms.service.createAlias]

    /***
     * @param string $keyId
     * @param string $alias
     * @return void
     */
    public function createAlias(string $keyId, string $alias)
    {
        try{
            $this->client->createAlias([
                'TargetKeyId' => $keyId,
                'AliasName' => $alias,
            ]);
        }catch (KmsException $caught){
            if($caught->getAwsErrorMessage() == "InvalidAliasNameException"){
                echo "The request was rejected because the specified alias name is not valid.";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.createAlias]

    // snippet-start:[php.example_code.kms.service.createGrant]

    /***
     * @param string $keyId
     * @param string $granteePrincipal
     * @param array $operations
     * @param array $grantTokens
     * @return Result
     */
    public function createGrant(string $keyId, string $granteePrincipal, array $operations, array $grantTokens = [])
    {
        $args = [
            'KeyId' => $keyId,
            'GranteePrincipal' => $granteePrincipal,
            'Operations' => $operations,
        ];
        if($grantTokens){
            $args['GrantTokens'] = $grantTokens;
        }
        try{
            return $this->client->createGrant($args);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "InvalidGrantTokenException"){
                echo "The request was rejected because the specified grant token is not valid.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.createGrant]

    // snippet-start:[php.example_code.kms.service.describeKey]

    /***
     * @param string $keyId
     * @return array
     */
    public function describeKey(string $keyId)
    {
        try {
            $result = $this->client->describeKey([
                "KeyId" => $keyId,
            ]);
            return $result['KeyMetadata'];
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "NotFoundException"){
                echo "The request was rejected because the specified entity or resource could not be found.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.describeKey]

    // snippet-start:[php.example_code.kms.service.disableKey]

    /***
     * @param string $keyId
     * @return void
     */
    public function disableKey(string $keyId)
    {
        try {
            $this->client->disableKey([
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem disabling the key: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.disableKey]

    // snippet-start:[php.example_code.kms.service.enableKey]

    /***
     * @param string $keyId
     * @return void
     */
    public function enableKey(string $keyId)
    {
        try {
            $this->client->enableKey([
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "NotFoundException"){
                echo "The request was rejected because the specified entity or resource could not be found.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.enableKey]

    // snippet-start:[php.example_code.kms.service.listKeys]

    /***
     * @return array
     */
    public function listKeys()
    {
        try {
            $contents = [];
            $paginator = $this->client->getPaginator("ListKeys");
            foreach($paginator as $result){
                foreach ($result['Content'] as $object) {
                    $contents[] = $object;
                }
            }
            return $contents;
        }catch(KmsException $caught){
            echo "There was a problem listing the keys: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.listKeys]

    // snippet-start:[php.example_code.kms.service.listGrants]

    /***
     * @param string $keyId
     * @return Result
     */
    public function listGrants(string $keyId)
    {
        try{
            return $this->client->listGrants([
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "NotFoundException"){
                echo "    The request was rejected because the specified entity or resource could not be found.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.listGrants]

    // snippet-start:[php.example_code.kms.service.getKeyPolicy]
    /***
     * @param string $keyId
     * @return Result
     */
    public function getKeyPolicy(string $keyId)
    {
        try {
            return $this->client->getKeyPolicy([
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem getting the key policy: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.getKeyPolicy]

    // snippet-start:[php.example_code.kms.service.revokeGrant]
    /***
     * @param string $grantId
     * @param string $keyId
     * @return void
     */
    public function revokeGrant(string $grantId, string $keyId)
    {
        try{
            $this->client->revokeGrant([
                'GrantId' => $grantId,
                'KeyId' => $keyId,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem with revoking the grant: {$caught->getAwsErrorMessage()}.\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.revokeGrant]

    // snippet-start:[php.example_code.kms.service.scheduleKeyDeletion]

    /***
     * @param string $keyId
     * @param int $pendingWindowInDays
     * @return void
     */
    public function scheduleKeyDeletion(string $keyId, int $pendingWindowInDays = 7)
    {
        try {
            $this->client->scheduleKeyDeletion([
                'KeyId' => $keyId,
                'PendingWindowInDays' => $pendingWindowInDays,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem scheduling the key deletion: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.scheduleKeyDeletion]

    // snippet-start:[php.example_code.kms.service.tagResource]

    /***
     * @param string $keyId
     * @param array $tags
     * @return void
     */
    public function tagResource(string $keyId, array $tags)
    {
        try {
            $this->client->tagResource([
                'KeyId' => $keyId,
                'Tags' => $tags,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem applying the tag(s): {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.tagResource]

    // snippet-start:[php.example_code.kms.service.sign]

    /***
     * @param string $keyId
     * @param string $message
     * @param string $algorithm
     * @return Result
     */
    public function sign(string $keyId, string $message, string $algorithm)
    {
        try {
            return $this->client->sign([
                'KeyId' => $keyId,
                'Message' => $message,
                'SigningAlgorithm' => $algorithm,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem signing the data: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.sign]

    // snippet-start:[php.example_code.kms.service.enableKeyRotation]

    /***
     * @param string $keyId
     * @param int $rotationPeriodInDays
     * @return void
     */
    public function enableKeyRotation(string $keyId, int $rotationPeriodInDays = 365)
    {
        try{
            $this->client->enableKeyRotation([
                'KeyId' => $keyId,
                'RotationPeriodInDays' => $rotationPeriodInDays,
            ]);
        }catch(KmsException $caught){
            if($caught->getAwsErrorMessage() == "NotFoundException"){
                echo "The request was rejected because the specified entity or resource could not be found.\n";
            }
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.enableKeyRotation]

    // snippet-start:[php.example_code.kms.service.putKeyPolicy]

    /***
     * @param string $keyId
     * @param string $policy
     * @return void
     */
    public function putKeyPolicy(string $keyId, string $policy)
    {
        try {
            $this->client->putKeyPolicy([
                'KeyId' => $keyId,
                'Policy' => $policy,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem replacing the key policy: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.putKeyPolicy]

    // snippet-start:[php.example_code.kms.service.deleteAlias]

    /***
     * @param string $aliasName
     * @return void
     */
    public function deleteAlias(string $aliasName)
    {
        try {
            $this->client->deleteAlias([
                'AliasName' => $aliasName,
            ]);
        }catch(KmsException $caught){
            echo "There was a problem deleting the alias: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.deleteAlias]

    // snippet-start:[php.example_code.kms.service.verify]

    /***
     * @param string $keyId
     * @param string $message
     * @param string $signature
     * @param string $signingAlgorithm
     * @return bool
     */
    public function verify(string $keyId, string $message, string $signature, string $signingAlgorithm)
    {
        try {
            $result = $this->client->verify([
                'KeyId' => $keyId,
                'Message' => $message,
                'Signature' => $signature,
                'SigningAlgorithm' => $signingAlgorithm,
            ]);
            return $result['SignatureValid'];
        }catch(KmsException $caught){
            echo "There was a problem verifying the signature: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
    }

    // snippet-end:[php.example_code.kms.service.verify]

}

// snippet-end:[php.example_code.kms.service.KmsService]
