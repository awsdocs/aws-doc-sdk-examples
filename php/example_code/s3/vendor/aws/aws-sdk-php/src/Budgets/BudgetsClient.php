<?php
namespace Aws\Budgets;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Budgets** service.
 * @method \Aws\Result createBudget(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createBudgetAsync(array $args = [])
 * @method \Aws\Result createNotification(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createNotificationAsync(array $args = [])
 * @method \Aws\Result createSubscriber(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createSubscriberAsync(array $args = [])
 * @method \Aws\Result deleteBudget(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteBudgetAsync(array $args = [])
 * @method \Aws\Result deleteNotification(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteNotificationAsync(array $args = [])
 * @method \Aws\Result deleteSubscriber(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteSubscriberAsync(array $args = [])
 * @method \Aws\Result describeBudget(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeBudgetAsync(array $args = [])
 * @method \Aws\Result describeBudgetPerformanceHistory(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeBudgetPerformanceHistoryAsync(array $args = [])
 * @method \Aws\Result describeBudgets(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeBudgetsAsync(array $args = [])
 * @method \Aws\Result describeNotificationsForBudget(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeNotificationsForBudgetAsync(array $args = [])
 * @method \Aws\Result describeSubscribersForNotification(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeSubscribersForNotificationAsync(array $args = [])
 * @method \Aws\Result updateBudget(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateBudgetAsync(array $args = [])
 * @method \Aws\Result updateNotification(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateNotificationAsync(array $args = [])
 * @method \Aws\Result updateSubscriber(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateSubscriberAsync(array $args = [])
 */
class BudgetsClient extends AwsClient {}
