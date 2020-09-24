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
        $kmsKeyId = '';
        $nameFile = "exsample.png";
        $credentialsPath = 'path/credentials';

        // Cipher method that the encryption client uses while encrypting.
        $cipherOptions = [
            'Cipher'    => 'gcm',
            'KeySize'   => 256,
            'Aad'       => 'key-decryption'
        ];
 
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
 
        $result = $encryptionClient->getObject([
            '@KmsAllowDecryptWithAnyCmk' => true,
            '@SecurityProfile'           => 'V2',
            '@MaterialsProvider'         => $materialsProvider,
            '@CipherOptions'             => $cipherOptions,
            'Bucket'                     => $bucket,
            'Key'                        => $keyName,
        ]);

        //save file to local
        header('Content-Disposition: attachment; filename=' . $nameFile);
        header('Content-Type: '.$resultGet['ContentType']);
        echo $result['Body'];

    }catch (S3Exception $e){
        echo $e->getMessage() . PHP_EOL;
        die('Error S3Exception:' . $e->getMessage());
    } catch (Exception $e) {
        die('Error Exception:' . $e->getMessage());
    }