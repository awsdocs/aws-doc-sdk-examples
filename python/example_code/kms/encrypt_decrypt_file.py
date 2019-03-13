# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[encrypt_decrypt_file.py demonstrates how to encrypt and decrypt a file using the AWS Key Management Service]
# snippet-service:[kms]
# snippet-keyword:[AWS Key Management Service (KMS)]
# snippet-keyword:[Python]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-03-12]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import base64
import logging
import boto3
from botocore.exceptions import ClientError

# To perform the optional file encryption/decryption operations, the Python
# cryptography package must be installed.
#       pip install cryptography
from cryptography.fernet import Fernet


def retrieve_cmk(desc):
    """Retrieve an existing KMS CMK based on its description

    :param desc: Description of CMK specified when the CMK was created
    :return Tuple of (KeyId, KeyArn) where KeyId is the CMK ID and KeyArn is
    its Amazon Resource Name
    :return Tuple of (None, None) if a CMK with the specified description was
    not found
    """

    # Retrieve a list of existing CMKs
    # If more than 100 keys exist, retrieve and process them in batches
    kms_client = boto3.client('kms')
    try:
        response = kms_client.list_keys()
    except ClientError as e:
        logging.error(e)
        return None, None

    done = False
    while not done:
        for cmk in response['Keys']:
            # Get info about the key, including its description
            try:
                key_info = kms_client.describe_key(KeyId=cmk['KeyArn'])
            except ClientError as e:
                logging.error(e)
                return None, None

            # Is this the key we're looking for?
            if key_info['KeyMetadata']['Description'] == desc:
                return cmk['KeyId'], cmk['KeyArn']

        # Are there more keys to retrieve?
        if not response['Truncated']:
            # No, the CMK was not found
            done = True
        else:
            # Yes, retrieve another batch
            try:
                response = kms_client.list_keys(Marker=response['NextMarker'])
            except ClientError as e:
                logging.error(e)
                return None, None

    # All existing CMKs were checked and the desired key was not found
    return None, None


def create_cmk(desc='Customer Master Key'):
    """Create a KMS Customer Master Key

    The created CMK is a Customer-managed key stored in AWS KMS.

    :param desc: key description
    :return Tuple of (KeyId, KeyArn) where KeyId is an AWS globally-unique
    string ID and KeyArn is the Amazon Resource Name of the CMK
    :return Tuple of (None, None) if error
    """

    # Create CMK
    kms_client = boto3.client('kms')
    try:
        response = kms_client.create_key(Description=desc)
    except ClientError as e:
        logging.error(e)
        return None, None

    # Return the key ID and ARN
    return response['KeyMetadata']['KeyId'], response['KeyMetadata']['Arn']


def create_data_key(cmk_id, key_spec='AES_256'):
    """Generate a data key to use when encrypting and decrypting data

    :param cmk_id: KMS CMK ID or ARN under which to generate and encrypt the
    data key.
    :param key_spec: Length of the data encryption key. Supported values:
        'AES_128': Generate a 128-bit symmetric key
        'AES_256': Generate a 256-bit symmetric key
    :return Binary string of encrypted CiphertextBlob data key
    :return None if error
    """

    # Create data key
    kms_client = boto3.client('kms')
    try:
        response = kms_client.generate_data_key_without_plaintext(KeyId=cmk_id,
                                                                  KeySpec=key_spec)
    except ClientError as e:
        logging.error(e)
        return None

    # Return the encrypted binary data key
    return response['CiphertextBlob']


def decrypt_data_key(data_key_encrypted):
    """Decrypt an encrypted data key

    :param data_key_encrypted: Encrypted ciphertext data key.
    :return Plaintext base64-encoded binary data key string
    :return None if error
    """

    # Decrypt the data key
    kms_client = boto3.client('kms')
    try:
        response = kms_client.decrypt(CiphertextBlob=data_key_encrypted)
    except ClientError as e:
        logging.error(e)
        return None

    # Return plaintext base64-encoded binary data key
    return response['Plaintext']


# Number of bytes in which to store an integer value
# Used by encrypt_file() and decrypt_file() to store the length of the
# encrypted data key
NUM_BYTES_INT = 4


def encrypt_file(filename, data_key_encrypted):
    """Encrypt filename using the specified encrypted data key

    The encrypted data key is saved with the encrypted file.
    The encrypted file is saved as <filename>.encrypted
    Limitation: The contents of filename must fit in memory.

    :param filename: File to encrypt
    :param data_key_encrypted: AWS KMS encrypted data key
    :return: True if file was encrypted. Otherwise, False.
    """

    # Read the entire file into memory
    try:
        with open(filename, 'rb') as file:
            file_contents = file.read()
    except IOError as e:
        logging.error(e)
        return False

    # Decrypt the data key before using it
    data_key_plaintext = decrypt_data_key(data_key_encrypted)
    if data_key_plaintext is None:
        return False
    data_key_64encoded = base64.b64encode(data_key_plaintext)

    # Encrypt the file
    f = Fernet(data_key_64encoded)
    file_contents_encrypted = f.encrypt(file_contents)

    # Write the encrypted data key and encrypted file contents together
    try:
        with open(filename + '.encrypted', 'wb') as file_encrypted:
            file_encrypted.write(len(data_key_encrypted).to_bytes(NUM_BYTES_INT,
                                                                  byteorder='big'))
            file_encrypted.write(data_key_encrypted)
            file_encrypted.write(file_contents_encrypted)
    except IOError as e:
        logging.error(e)
        return False

    # For the highest security, the data_key_plaintext/data_key_64encoded
    # values should be wiped from memory. Unfortunately, this is not possible
    # in Python. However, making them local variables enables them to be
    # garbage collected.
    return True


def decrypt_file(filename):
    """Decrypt file previously encrypted by encrypt_file()

    The encrypted file is read from <filename>.encrypted
    The decrypted file is written to <filename>.decrypted

    :param filename: File to decrypt
    :return: True if file was decrypted. Otherwise, False.
    """

    # Read the encrypted file into memory
    try:
        with open(filename + '.encrypted', 'rb') as file:
            file_contents = file.read()
    except IOError as e:
        logging.error(e)
        return False

    # The first NUM_BYTES_INT bytes contain the integer length of the
    # encrypted data key.
    # Add NUM_BYTES_INT to get index of end of encrypted data key/start
    # of encrypted data.
    data_key_encrypted_len = int.from_bytes(file_contents[:NUM_BYTES_INT],
                                            byteorder='big') \
                             + NUM_BYTES_INT
    data_key_encrypted = file_contents[NUM_BYTES_INT:data_key_encrypted_len]

    # Decrypt the data key before using it
    data_key_plaintext = decrypt_data_key(data_key_encrypted)
    if data_key_plaintext is None:
        logging.error("Cannot decrypt data key")
        return False
    data_key_64encoded = base64.b64encode(data_key_plaintext)

    # Decrypt the rest of the file
    f = Fernet(data_key_64encoded)
    file_contents_decrypted = f.decrypt(file_contents[data_key_encrypted_len:])

    # Write the decrypted file contents
    try:
        with open(filename + '.decrypted', 'wb') as file_decrypted:
            file_decrypted.write(file_contents_decrypted)
    except IOError as e:
        logging.error(e)
        return False

    # The same security issue described at the end of encrypt_file() exists
    # here, too (i.e., the wish to wipe the data_key_plaintext and
    # data_key_64encoded values from memory).
    return True


def main():
    """Exercise AWS KMS operations retrieve_cmk(), create_cmk(),
    create_data_key(), and decrypt_data_key().

    Optionally, exercise file encryption and decryption operations
    encrypt_file() and decrypt_file().
    """

    # Define the CMK description. If an existing CMK with this description
    # is not found, a new CMK is created.
    cmk_description = 'My sample CMK'

    # Optional: To use the CMK and data_key to encrypt and decrypt a file,
    # specify a filename. Otherwise, specify an empty string.
    file_to_encrypt = ''

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Does the desired CMK already exist?
    cmk_id, cmk_arn = retrieve_cmk(cmk_description)
    if cmk_id is None:
        # No, create it
        cmk_id, cmk_arn = create_cmk(cmk_description)
        if cmk_id is None:
            exit(1)
        logging.info('Created new CMK')
    else:
        logging.info('Retrieved existing CMK')

    # Generate a data key
    # The data key is used to encrypt data
    # Pass either the CMK ID or ARN to the function
    data_key_encrypted = create_data_key(cmk_arn)
    if data_key_encrypted is None:
        exit(2)
    logging.info('Created new data key')

    # Optional: Use the keys to encrypt and decrypt a file
    if file_to_encrypt:
        # Encrypted file contents are written to <file_to_encrypt>.encrypted
        # Encrypted data key is also written to the output file.
        # The encrypted file can be decrypted at any time and by any program
        # that has the credentials to decrypt the data key.
        if encrypt_file(file_to_encrypt, data_key_encrypted):
            logging.info(f'{file_to_encrypt} encrypted to '
                         f'{file_to_encrypt}.encrypted')

            # Decrypt the file
            if decrypt_file(file_to_encrypt):
                # Decrypted file contents are written to <file_to_encrypt>.decrypted
                # Contents of the original file_to_encrypt == contents of
                # file_to_encrypt.decrypted
                logging.info(f'{file_to_encrypt}.encrypted decrypted to '
                             f'{file_to_encrypt}.decrypted')


if __name__ == '__main__':
    main()
