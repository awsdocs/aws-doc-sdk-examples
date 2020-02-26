<?php
namespace Aws\FMS;

use Aws\AwsClient;

/**
 * This client is used to interact with the **Firewall Management Service** service.
 * @method \Aws\Result associateAdminAccount(array $args = [])
 * @method \GuzzleHttp\Promise\Promise associateAdminAccountAsync(array $args = [])
 * @method \Aws\Result deleteNotificationChannel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteNotificationChannelAsync(array $args = [])
 * @method \Aws\Result deletePolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deletePolicyAsync(array $args = [])
 * @method \Aws\Result disassociateAdminAccount(array $args = [])
 * @method \GuzzleHttp\Promise\Promise disassociateAdminAccountAsync(array $args = [])
 * @method \Aws\Result getAdminAccount(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getAdminAccountAsync(array $args = [])
 * @method \Aws\Result getComplianceDetail(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getComplianceDetailAsync(array $args = [])
 * @method \Aws\Result getNotificationChannel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getNotificationChannelAsync(array $args = [])
 * @method \Aws\Result getPolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getPolicyAsync(array $args = [])
 * @method \Aws\Result getProtectionStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getProtectionStatusAsync(array $args = [])
 * @method \Aws\Result listComplianceStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listComplianceStatusAsync(array $args = [])
 * @method \Aws\Result listMemberAccounts(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listMemberAccountsAsync(array $args = [])
 * @method \Aws\Result listPolicies(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listPoliciesAsync(array $args = [])
 * @method \Aws\Result listTagsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listTagsForResourceAsync(array $args = [])
 * @method \Aws\Result putNotificationChannel(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putNotificationChannelAsync(array $args = [])
 * @method \Aws\Result putPolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putPolicyAsync(array $args = [])
 * @method \Aws\Result tagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise tagResourceAsync(array $args = [])
 * @method \Aws\Result untagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise untagResourceAsync(array $args = [])
 */
class FMSClient extends AwsClient {}
