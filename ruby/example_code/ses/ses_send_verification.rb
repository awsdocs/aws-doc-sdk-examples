require 'aws-sdk'

# We require one arg, the name of the file containing email addresses,
# one address per line.
if ARGV.length != 1
  puts 'You must supply the filename of the list of email addresses'
  exit(1)
end

file = ARGV[0]

# Replace sender@example.com with your "From" address.
# This address must be verified with Amazon SES.
sender = "sender@example.com"

# Create a new SES resource and specify a region
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
ses = Aws::SES::Client.new(region: 'us-west-2')

# Try to verify email address.
begin
  File.readlines(file).each do |email_address|
    ses.verify_email_identity({
      email_address: email_address,
    })

    puts 'Email sent to ' + email_address
  end

# If something goes wrong, display an error message.
rescue Aws::SES::Errors::ServiceError => error
  puts "Email not sent. Error message: #{error}"
end
