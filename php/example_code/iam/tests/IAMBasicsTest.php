<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithIAM.php.
#

namespace Iam\Basics;

use Aws\Iam\Exception\IamException;
use Iam\IAMService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class IAMBasicsTest extends TestCase
{
    protected static string $uuid;
    protected static IAMService $service;
    protected static string $policy = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Deny\",
                    \"Action\": \"*\",
                    \"Resource\": \"*\"
            }]}";
    protected static array $roles = [];
    protected static array $users = [];
    protected static array $policies = [];

    public static function setUpBeforeClass(): void
    {
        self::$uuid = uniqid();
        self::$service = new IAMService();
    }

    public static function tearDownAfterClass(): void
    {
        //Delete all created users.
        foreach (self::$users as $user) {
            // Keep trying to delete the user until the service errors out, which should mean nothing to delete.
            do {
                try {
                    $userPolicies = self::$service->listUserPolicies($user['UserName']);
                    foreach ($userPolicies['PolicyNames'] as $userPolicyName) {
                        self::$service->deleteUserPolicy($user['UserName'], $userPolicyName);
                    }
                    $accessKeys = self::$service->listAccessKeys($user['UserName']);
                    foreach ($accessKeys['AccessKeyMetadata'] as $accessKey) {
                        self::$service->deleteAccessKey($accessKey['AccessKeyId'], $user['UserName']);
                    }
                    self::$service->deleteUser($user['UserName']);
                } catch (IamException $exception) {
                    break;
                }
            } while (true);
        }
        //Delete all created policies.
        foreach (self::$policies as $policy) {
            // Keep trying to delete the policy until the service errors out, which should mean nothing to delete.
            do {
                try {
                    self::$service->deletePolicy($policy['Arn']);
                } catch (IamException $exception) {
                    break;
                }
            } while (true);
        }
        //Delete all created roles.
        foreach (self::$roles as $role) {
            // Keep trying to delete the role until the service errors out, which should mean nothing to delete.
            do {
                try {
                    $rolePolicies = self::$service->listRolePolicies($role['RoleName']);
                    if ($rolePolicies === false) {
                        $rolePolicies = ['PolicyNames' => []];
                    }
                    foreach ($rolePolicies['PolicyNames'] as $policyName) {
                        self::$service->deleteRolePolicy($policyName, $role['RoleName']);
                    }
                    $attachedRolePolicies = self::$service->listAttachedRolePolicies($role['RoleName']);
                    foreach ($attachedRolePolicies['AttachedPolicies'] as $attachedRolePolicy) {
                        self::$service->detachRolePolicy($role['RoleName'], $attachedRolePolicy['PolicyArn']);
                    }
                    self::$service->deleteRole($role['RoleName']);
                } catch (IamException $exception) {
                    break;
                }
            } while (true);
        }
    }

    /** @group integ */
    public function testItRunsWithoutThrowingAnException()
    {
        include __DIR__ . "/../GettingStartedWithIAM.php";
        self::assertTrue(true); // Asserts that we made it to this line with no exceptions.
    }

    /** @group unit */
    public function testCreateUser()
    {
        $user = self::$service->createUser("iam_test_user_" . self::$uuid);
        self::$users[] = $user;
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
        self::$roles[] = $role;
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
        self::$users[] = $user;
        $userPolicy = self::$service->createUserPolicy(
            "iam_test_inline_policy_" . self::$uuid,
            self::$policy,
            $user['UserName']
        );

        self::assertTrue(true); //$userPolicy will be null, but this line only runs when no exception is thrown.
    }

    public function testCreatePolicy()
    {
        $policy = self::$service->createPolicy("iam_test_policy_" . self::$uuid, self::$policy);
        self::$policies[] = $policy;
        self::assertEquals("iam_test_policy_" . self::$uuid, $policy['PolicyName']);

        return $policy;
    }

    public function testAttachUserPolicy()
    {
        $user = self::$service->createUser("test_policy_user_" . self::$uuid);
        self::$users[] = $user;
        $policy = self::$service->createPolicy("test_policy_policy_" . self::$uuid, self::$policy);
        self::$policies[] = $policy;
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
        $rolePolicyUser = self::$service->createUser("iam_test_role_policy_user_" . self::$uuid);
        self::$users[] = $rolePolicyUser;
        $role = self::$service->createRole("iam_test_role_" . self::$uuid, "{
            \"Version\": \"2012-10-17\",
            \"Statement\": [{
                \"Effect\": \"Allow\",
                \"Principal\": {\"AWS\": \"{$rolePolicyUser['Arn']}\"},
                \"Action\": \"sts:AssumeRole\"
            }]}");
        $policy = self::$service->createPolicy("iam_test_policy_" . self::$uuid, self::$policy);
        self::$policies[] = $policy;
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
        self::$users[] = $user;
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
        self::$service->deleteAccessKey($values['key']['AccessKeyId'], $values['user']['UserName']);
        self::$service->deleteUser($values['user']['UserName']);

        self::assertTrue(true); // No exceptions thrown.
    }
}
