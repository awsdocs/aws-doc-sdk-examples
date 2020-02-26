<?php
namespace Aws\LicenseManager;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS License Manager** service.
 * @method \Aws\Result createLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result deleteLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result getLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result getServiceSettings(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getServiceSettingsAsync(array $args = [])
 * @method \Aws\Result listAssociationsForLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listAssociationsForLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result listFailuresForLicenseConfigurationOperations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listFailuresForLicenseConfigurationOperationsAsync(array $args = [])
 * @method \Aws\Result listLicenseConfigurations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listLicenseConfigurationsAsync(array $args = [])
 * @method \Aws\Result listLicenseSpecificationsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listLicenseSpecificationsForResourceAsync(array $args = [])
 * @method \Aws\Result listResourceInventory(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listResourceInventoryAsync(array $args = [])
 * @method \Aws\Result listTagsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listTagsForResourceAsync(array $args = [])
 * @method \Aws\Result listUsageForLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listUsageForLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result tagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise tagResourceAsync(array $args = [])
 * @method \Aws\Result untagResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise untagResourceAsync(array $args = [])
 * @method \Aws\Result updateLicenseConfiguration(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateLicenseConfigurationAsync(array $args = [])
 * @method \Aws\Result updateLicenseSpecificationsForResource(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateLicenseSpecificationsForResourceAsync(array $args = [])
 * @method \Aws\Result updateServiceSettings(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateServiceSettingsAsync(array $args = [])
 */
class LicenseManagerClient extends AwsClient {}
