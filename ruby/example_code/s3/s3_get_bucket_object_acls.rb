# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Gets the ACL for an S3 bucket item.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[get_object_acl method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[s3]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
require 'aws-sdk-s3'  # v2: require 'aws-sdk'
require 'os'

# Required on Windows
# See: https://github.com/aws/aws-sdk-core-ruby/issues/166
if OS.windows?
  Aws.use_bundled_cert!
end

# main
if ARGV.length < 2
  puts 'You must supply a bucket and object name'
  exit 1
end

bucket_name = ARGV[0]
object_name = ARGV[1]

client = Aws::S3::Client.new(region: 'us-west-2')

resp = client.get_object_acl({bucket: bucket_name, key: object_name})

puts
puts "Owner           #{resp.owner.display_name}"
puts

resp.grants.each do |g|
  if g.grantee.display_name == nil
    puts 'Grantee     EVERYONE'
  else
    puts 'Grantee     ' + g.grantee.display_name
  end

  if g.grantee.id == nil
    puts 'ID          ' + '-'
  else
    puts 'ID          ' + g.grantee.id
  end

  puts 'Permission  ' + g.permission
  puts
end
