# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'openssl'

# Creates a public and private key file pair.
#
# @param public_key_file [File] An instance of the public key file.
# @param private_key_file [File] An instance of the private key file.
# @param passphrase [String] A passphrase for the private key file.
# @return [Boolean] true if the public and private key files were created;
#   otherwise, false.
# @example
#   public_key_file = File.new('public_key.pem', 'w')
#   private_key_file = File.new('private_key.pem', 'w')
#   exit 1 unless public_and_private_key_created?(
#     public_key_file,
#     private_key_file,
#     'my-passphrase'
#   )
def public_and_private_key_created?(
  public_key_file,
  private_key_file,
  passphrase
)

  key = OpenSSL::PKey::RSA.new(2048)

  public_key_file.write(key.public_key.to_pem)
  public_key_file.close

  cipher = OpenSSL::Cipher.new('AES-128-CBC')
  key_secure = key.export(cipher, passphrase)

  private_key_file.write(key_secure)
  private_key_file.close

  return true
rescue StandardError => e
  puts 'Could not create the public key file, the private key file, ' \
    "or both: #{e.message}"
end

# Full example call:
=begin
public_key_file_name = 'public_key.pem'
public_key_file = File.new(public_key_file_name, 'w')
private_key_file_name = 'private_key.pem'
private_key_file = File.new(private_key_file_name, 'w')
passphrase = 'my-passphrase'

puts "Creating public key file at '#{public_key_file_name}', and " \
  "creating private key file at '#{private_key_file_name}' with passphrase " \
  "'#{passphrase}'..."

if public_and_private_key_created?(
  public_key_file,
  private_key_file,
  passphrase)
  puts 'Public and private key file pair created.'
else
  exit 1
end
=end
