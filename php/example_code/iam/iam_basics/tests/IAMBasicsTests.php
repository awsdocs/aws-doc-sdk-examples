<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithIAM.php.
#

namespace Iam\Basics;

use Iam\IamService;
use PHPUnit\Framework\TestCase;

class IAMBasicsTests extends TestCase
{
    protected static $uuid;
    protected static $service;
    protected static $policy = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Deny\",
                    \"Action\": \"*\",
                    \"Resource\": \"*\"
            }]}";

    public static function setUpBeforeClass(): void
    {
        self::$uuid = uniqid();
        self::$service = new IamService();
    }

    public function testItRunsWithoutThrowingAnException()
    {
        include "iam/iam_basics/GettingStartedWithIAM.php";
        self::assertTrue(true); // Asserts that we made it to this line with no exceptions.
    }

    public function testCreateUser()
    {
        $user = self::$service->createUser("iam_test_user_" . self::$uuid);
        self::assertEquals("iam_test_user_" . self::$uuid, $user['UserName']);
        sleep(10);
        return $user;
    }

    /**
     * @depends testCreateUser
     */
    public function testCreateRole($user)
    {
        $role = self::$service->createRole("iam_test_role_" . self::$uuid, "{
            \"Version\": \"2012-10-17\",
            \"Statement\": [{
                \"Effect\": \"Allow\",
                \"Principal\": {\"AWS\": \"{$user['Arn']}\"},
                \"Action\": \"sts:AssumeRole\"
            }]}");
        self::assertEquals("iam_test_role_" . self::$uuid, $role['RoleName']);
        return ["user" => $user, "role" => $role];
    }

    /**
     * @param $values // Contains user and role.
     * @depends testCreateRole
     * @return string
     */
    public function testDeleteRole($values)
    {
        $deleteRole = self::$service->deleteRole($values['role']['RoleName']);
        self::assertNotFalse($deleteRole);
        return $values['user'];
    }

    /**
     * @param $user
     * @depends testDeleteRole
     */
    public function testDeleteUser($user)
    {
        $deleteUser = self::$service->deleteUser($user['UserName']);
        self::assertNotFalse($deleteUser);
    }

    public function testCreateUserPolicy()
    {
        $user = self::$service->createUser("iam_test_user_" . self::$uuid);
        $userPolicy = self::$service->createUserPolicy(
            "iam_test_inline_policy_" . self::$uuid,
            self::$policy,
            $user['UserName']
        );

        self::assertTrue(true); //$userPolicy will be null, but this line only runs when no exception is thrown.

        self::$service->deleteUser($user['UserName']);
    }

    public function testCreatePolicy()
    {
        $policy = self::$service->createPolicy("iam_test_policy_" . self::$uuid, self::$policy);
        self::assertEquals("iam_test_policy_" . self::$uuid, $policy['PolicyName']);

        return $policy;
    }

    public function testAttachUserPolicy()
    {
        $user = self::$service->createUser("test_policy_user_" . self::$uuid);
        $policy = self::$service->createPolicy("test_policy_policy_" . self::$uuid, self::$policy);
        $attachPolicy = self::$service->attachUserPolicy($user['UserName'], $policy['Arn']);

        self::assertIsObject($attachPolicy);

        return ['user' => $user, 'policy' => $policy];
    }

    /**
     * @depends testAttachUserPolicy
     */
    public function testDetachUserPolicy($values)
    {
        self::$service->detachUserPolicy($values['user']['UserName'], $values['policy']['Arn']);
        self::assertTrue(true);
    }

    /**
     * @depends testCreatePolicy
     */
    public function testDeletePolicy($policy)
    {
        $deletedPolicy = self::$service->deletePolicy($policy['Arn']);

        self::assertIsObject($deletedPolicy);
    }

    public function testAttachRolePolicy()
    {
        $role = self::$service->createRole("iam_test_role_" . self::$uuid, "{
            \"Version\": \"2012-10-17\",
            \"Statement\": [{
                \"Effect\": \"Allow\",
                \"Principal\": {\"AWS\": \"*\"},
                \"Action\": \"sts:AssumeRole\"
            }]}");
        $policy = self::$service->createPolicy("iam_test_policy_" . self::$uuid, self::$policy);
        $attachRolePolicy = self::$service->attachRolePolicy($role['RoleName'], $policy['Arn']);

        self::assertNull($attachRolePolicy);
        return ['role' => $role, "policy" => $policy];
    }

    /**
     * @depends testAttachRolePolicy
     */
    public function testDetachRolePolicy($values)
    {
        $detached = self::$service->detachRolePolicy($values['role']['RoleName'], $values['policy']["Arn"]);

        self::assertIsObject($detached);

        self::$service->deleteRole($values['role']['RoleName']);
        self::$service->deletePolicy($values['policy']["Arn"]);
    }

    public function testCreateAccessKey()
    {
        $user = self::$service->createUser("test_key_user_" . self::$uuid);
        $key = self::$service->createAccessKey($user['UserName']);

        self::assertEquals("test_key_user_" . self::$uuid, $key['UserName']);
        self::assertArrayHasKey("AccessKeyId", $key);

        return ['user' => $user, 'key' => $key];
    }

    /**
     * @depends testCreateAccessKey
     */
    public function testDeleteAccessKey($values)
    {
        self::$service->deleteAccessKey($values['key']['AccessKeyId']);
        self::$service->deleteUser($values['user']['UserName']);

        self::assertTrue(true); // No exceptions thrown.
    }
}
