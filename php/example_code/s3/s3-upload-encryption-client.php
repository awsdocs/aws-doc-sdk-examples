<?php

    /*
    * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
    * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/s3-encryption-client.html
    */

    require 'vendor/autoload.php';
    use Aws\Credentials\CredentialProvider;
    use Aws\S3\S3Client;
    use Aws\S3\Exception\S3Exception;
    use Aws\S3\Crypto\S3EncryptionClientV2;
    use Aws\Kms\KmsClient;
    use Aws\Crypto\KmsMaterialsProviderV2;

    try {
     
        $bucket     = '*** Your Bucket Name ***';
        $keyName    = '*** Your FileName On S3 ***';
        $filePath   = '*** Your FilePath ***';

        /* - @CipherOptions: (array) Cipher options for encrypting data. Only the
        *   Cipher option is required. Accepts the following:
        *       - Cipher: (string) gcm
        *            See also: AbstractCryptoClientV2::$supportedCiphers
        *       - KeySize: (int) 128|256
        *            See also: MaterialsProvider::$supportedKeySizes
        *       - Aad: (string) Additional authentication data. This option is
        *            passed directly to OpenSSL when using gcm. Note if you pass in
        *            Aad, the PHP SDK will be able to decrypt the resulting object,
        *            but other AWS SDKs may not be able to do so.
        */

        $cipherOptions = [
            'Cipher'    => 'gcm',
            'KeySize'   => 256,
            'Aad'       => 'key-encryption'
        ];
        $credentialsPath = 'path/credentials';
        $kmsKeyId = '';
 
        $encryptionClient = new S3EncryptionClientV2(
            new S3Client([
               'profile'      => 'default',
               'region'       => 'ap-southeast-1',
                'version'     => 'latest',
                'credentials' => CredentialProvider::ini('default', $credentialsPath)
            ])
        );

        $materialsProvider = new KmsMaterialsProviderV2(
            new KmsClient([
               'profile'     => 'default',
               'region'      => 'ap-southeast-1',
               'version'     => 'latest',
               'credentials' => CredentialProvider::ini('default', $credentialsPath)
            ]),
            $kmsKeyId
        );

        $result = $encryptionClient->putObject([
            '@MaterialsProvider'    => $materialsProvider,
            '@CipherOptions'        => $cipherOptions,
            '@SecurityProfile'      => 'V2',
            '@KmsEncryptionContext' => ['context-key' => 'context-value'],
            'Bucket'                => $bucket,
            'Key'                   => $keyName,
            'Body'                  => fopen($filePath, 'r'),
        ]);

    }catch (S3Exception $e){
        echo $e->getMessage() . PHP_EOL;
        die('Error S3Exception:' . $e->getMessage());
    } catch (Exception $e) {
        die('Error Exception:' . $e->getMessage());
    }