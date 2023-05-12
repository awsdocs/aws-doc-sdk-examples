<?php

namespace Iam\Tests;

use Exception;
use Iam\IAMService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class IAMServiceTest extends TestCase
{
    protected static $service;
    protected static $uuid;
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
        self::$service = new IAMService();
    }

    public function testConstructor()
    {
        self::assertInstanceOf(IAMService::class, self::$service);
    }

    public function testAttachRolePolicy()
    {
        $role = self::$service->createRole("iam_test_attach_role_" . self::$uuid, "{
            \"Version\": \"2012-10-17\",
            \"Statement\": [{
                \"Effect\": \"Deny\",
                \"Principal\": {\"AWS\": \"*\"},
                \"Action\": \"sts:AssumeRole\"
            }]}");
        $policy = self::$service->createPolicy("iam_test_attach_policy_" . self::$uuid, self::$policy);
        $attachRolePolicy = self::$service->attachRolePolicy($role['RoleName'], $policy['Arn']);

        self::assertNull($attachRolePolicy);

        return ['role' => $role, "policy" => $policy];
    }

    public function testAttachUserPolicy()
    {
        $user = self::$service->createUser("test_policy_user_" . self::$uuid);
        $policy = self::$service->createPolicy("test_policy_policy_" . self::$uuid, self::$policy);
        $attachPolicy = self::$service->attachUserPolicy($user['UserName'], $policy['Arn']);

        self::assertIsObject($attachPolicy);

        return ['user' => $user, 'policy' => $policy];
    }

    public function testCreateAccessKey()
    {
        $user = self::$service->createUser("test_key_user_" . self::$uuid);
        $key = self::$service->createAccessKey($user['UserName']);

        self::assertEquals("test_key_user_" . self::$uuid, $key['UserName']);
        self::assertArrayHasKey("AccessKeyId", $key);

        return ['user' => $user, 'key' => $key];
    }

    public function testCreatePolicy()
    {
        $policy = self::$service->createPolicy("iam_test_policy_" . self::$uuid, self::$policy);
        self::assertEquals("iam_test_policy_" . self::$uuid, $policy['PolicyName']);

        return $policy;
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
     * @depends testCreateServiceLinkedRole
     */
    public function testDeleteServiceLinkedRole($role)
    {
        self::$service->deleteServiceLinkedRole($role['Role']['RoleName']);
        self::assertTrue(true);
    }

    public function testCreateServiceLinkedRole()
    {
        $role = self::$service->createServiceLinkedRole("autoscaling.amazonaws.com", "test_" . self::$uuid);

        self::assertArrayHasKey('Role', $role);

        return $role;
    }

    public function testCreateUser()
    {
        $user = self::$service->createUser("iam_test_user_" . self::$uuid);
        self::assertEquals("iam_test_user_" . self::$uuid, $user['UserName']);
        sleep(10);
        return $user;
    }

    public function testCreateUserPolicy()
    {
        $user = self::$service->createUser("iam_test_policy_user_" . self::$uuid);
        $userPolicyName = "iam_test_inline_policy_" . self::$uuid;
        self::$service->createUserPolicy($userPolicyName, self::$policy, $user['UserName']);

        self::assertTrue(true); //$userPolicy will be null, but this line only runs when no exception is thrown

        self::$service->deleteUser($user['UserName']);

        return ['UserName' => $user['UserName'], 'PolicyName' => $userPolicyName];
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

    /**
     * @depends testGetPolicy
     */
    public function testDeletePolicy($policy)
    {
        $deletedPolicy = self::$service->deletePolicy($policy['Arn']);

        self::assertIsObject($deletedPolicy);
    }

    /**
     * @depends testGetRole
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

    /**
     * @depends testListAttachedRolePolicies
     */
    public function testDetachRolePolicy($values)
    {
        $detached = self::$service->detachRolePolicy($values['role']['RoleName'], $values['policy']["Arn"]);

        self::assertIsObject($detached);

        self::$service->deleteRole($values['role']['RoleName']);
        self::$service->deletePolicy($values['policy']["Arn"]);
    }

    /**
     * @depends testAttachUserPolicy
     */
    public function testDetachUserPolicy($values)
    {
        self::$service->detachUserPolicy($values['user']['UserName'], $values['policy']['Arn']);
        self::assertTrue(true);
    }

    public function testCreateAccountPasswordPolicy()
    {
        try {
            self::$service->updateAccountPasswordPolicy([
                'MinimumPasswordLength' => 8,
                'RequireNumbers' => true,
            ]);
            self::assertTrue(true);
        } catch (Exception $exception) {
            self::fail();
        }
    }

    /**
     * @depends testCreateAccountPasswordPolicy
     */
    public function testGetAccountPasswordPolicy()
    {
        $passwordPolicy = self::$service->getAccountPasswordPolicy();
        self::assertArrayHasKey('PasswordPolicy', $passwordPolicy);
    }

    /**
     * @depends testCreatePolicy
     */
    public function testGetPolicy($policy)
    {
        $getPolicy = self::$service->getPolicy($policy['Arn']);

        self::assertEquals($policy['PolicyName'], $getPolicy['Policy']['PolicyName']);

        return $policy;
    }

    /**
     * @depends testListRolePolicies
     */
    public function testGetRole($values)
    {
        $role = self::$service->getRole($values['role']['RoleName']);
        self::assertEquals($values['role']['RoleName'], $role['Role']['RoleName']);

        return $values;
    }

    /**
     * @depends testAttachRolePolicy
     */
    public function testListAttachedRolePolicies($rolePolicy)
    {
        $list = self::$service->listAttachedRolePolicies($rolePolicy['role']['RoleName']);
        self::assertEquals($rolePolicy['policy']['PolicyName'], $list['AttachedPolicies'][0]['PolicyName']);

        return $rolePolicy;
    }

    public function testListGroups()
    {
        $list = self::$service->listGroups();
        self::assertArrayHasKey('Groups', $list);
    }

    public function testListPolicies()
    {
        $list = self::$service->listPolicies();
        self::assertArrayHasKey('Policies', $list);
    }

    /**
     * @depends testCreateRole
     */
    public function testListRolePolicies($values)
    {
        $list = self::$service->listRolePolicies($values['role']['RoleName']);
        self::assertArrayHasKey('PolicyNames', $list);

        return $values;
    }

    public function testListRoles()
    {
        $roles = self::$service->listRoles();
        self::assertArrayHasKey('Roles', $roles);
    }

    public function testListSAMLProviders()
    {
        $SAMLProviders = self::$service->listSAMLProviders();
        self::assertArrayHasKey('SAMLProviderList', $SAMLProviders);
    }

    public function testListUsers()
    {
        $list = self::$service->listUsers();
        self::assertArrayHasKey('Users', $list);
    }
}
