<?php


// Replace sender@example.com with your "From" address.
// This address must be verified with Amazon SES.
define('SENDER', 'sender@example.com');

// Replace recipient@example.com with a "To" address. If your account
// is still in the sandbox, this address must be verified.
define('RECIPIENT', 'recipient@example.com');

// Specify a configuration set. If you do not want to use a configuration
// set, comment the following variable, and the
// 'ConfigurationSetName' => CONFIGSET argument below.
define('CONFIGSET', 'ConfigSet');

define('SUBJECT', 'Amazon SES test (AWS SDK for PHP)');

define('HTMLBODY', '<h1>AWS Amazon Simple Email Service Test Email</h1>' .
    '<p>This email was sent with <a href="https://aws.amazon.com/ses/">' .
    'Amazon SES</a> using the <a href="https://aws.amazon.com/sdk-for-php/">' .
    'AWS SDK for PHP</a>.</p>');
define('TEXTBODY', 'This email was send with Amazon SES using the AWS SDK for PHP.');

define('CHARSET', 'UTF-8');

require 'vendor/autoload.php';

use Aws\Ses\SesClient;
use Aws\Ses\Exception\SesException;

$client = new SesClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-12-01'
]);

try {
    $result = $client->sendEmail([
        'Destination' => [
            'ToAddresses' => [
                RECIPIENT,
            ],
        ],
        'Message' => [
            'Body' => [
                'Html' => [
                    'Charset' => CHARSET,
                    'Data' => HTMLBODY,
                ],
                'Text' => [
                    'Charset' => CHARSET,
                    'Data' => TEXTBODY,
                ],
            ],
            'Subject' => [
                'Charset' => CHARSET,
                'Data' => SUBJECT,
            ],
        ],
        'Source' => SENDER,
        // If you are not using a configuration set, comment or delete the
        // following line
        'ConfigurationSetName' => CONFIGSET,
    ]);
    $messageId = $result->get('MessageId');
    echo("Email sent! Message ID: $messageId" . "\n");
} catch (SesException $error) {
    echo("The email was not sent. Error message: " . $error->getAwsErrorMessage() . "\n");
}
