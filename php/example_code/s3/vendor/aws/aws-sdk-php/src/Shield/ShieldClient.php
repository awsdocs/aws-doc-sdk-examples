<?php
namespace Aws\Shield;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Shield** service.
 * @method \Aws\Result associateDRTLogBucket(array $args = [])
 * @method \GuzzleHttp\Promise\Promise associateDRTLogBucketAsync(array $args = [])
 * @method \Aws\Result associateDRTRole(array $args = [])
 * @method \GuzzleHttp\Promise\Promise associateDRTRoleAsync(array $args = [])
 * @method \Aws\Result associateHealthCheck(array $args = [])
 * @method \GuzzleHttp\Promise\Promise associateHealthCheckAsync(array $args = [])
 * @method \Aws\Result createProtection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createProtectionAsync(array $args = [])
 * @method \Aws\Result createSubscription(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createSubscriptionAsync(array $args = [])
 * @method \Aws\Result deleteProtection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteProtectionAsync(array $args = [])
 * @method \Aws\Result deleteSubscription(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteSubscriptionAsync(array $args = [])
 * @method \Aws\Result describeAttack(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeAttackAsync(array $args = [])
 * @method \Aws\Result describeDRTAccess(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeDRTAccessAsync(array $args = [])
 * @method \Aws\Result describeEmergencyContactSettings(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeEmergencyContactSettingsAsync(array $args = [])
 * @method \Aws\Result describeProtection(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeProtectionAsync(array $args = [])
 * @method \Aws\Result describeSubscription(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeSubscriptionAsync(array $args = [])
 * @method \Aws\Result disassociateDRTLogBucket(array $args = [])
 * @method \GuzzleHttp\Promise\Promise disassociateDRTLogBucketAsync(array $args = [])
 * @method \Aws\Result disassociateDRTRole(array $args = [])
 * @method \GuzzleHttp\Promise\Promise disassociateDRTRoleAsync(array $args = [])
 * @method \Aws\Result disassociateHealthCheck(array $args = [])
 * @method \GuzzleHttp\Promise\Promise disassociateHealthCheckAsync(array $args = [])
 * @method \Aws\Result getSubscriptionState(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getSubscriptionStateAsync(array $args = [])
 * @method \Aws\Result listAttacks(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listAttacksAsync(array $args = [])
 * @method \Aws\Result listProtections(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listProtectionsAsync(array $args = [])
 * @method \Aws\Result updateEmergencyContactSettings(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateEmergencyContactSettingsAsync(array $args = [])
 * @method \Aws\Result updateSubscription(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateSubscriptionAsync(array $args = [])
 */
class ShieldClient extends AwsClient {}
