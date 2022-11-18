require 'net/smtp'
filename = "templates/report.txt"
file_content = File.read(filename)
encoded_content = [file_content].pack("m")   # base64
marker = "AUNIQUEMARKER"
part1 = <<END_OF_MESSAGE
From: YourRubyApp <fprior@amazon.com>
To: BestUserEver <yfprior@amazon.com>
Subject: Adding attachment to email
MIME-Version: 1.0
Content-Type: multipart/mixed; boundary = #{marker}
--#{marker}
END_OF_MESSAGE
part2 = <<END_OF_MESSAGE
Content-Type: text/html
Content-Transfer-Encoding:8bit
A bit of plain text.
<strong>The beginning of your HTML content.</strong>
<h1>And some headline, as well.</h1>
--#{marker}
END_OF_MESSAGE
part3 = <<END_OF_MESSAGE
Content-Type: multipart/mixed; name = "#{filename}"
Content-Transfer-Encoding:base64
Content-Disposition: attachment; filename = "#{filename}"
#{encoded_content}
--#{marker}--
END_OF_MESSAGE
message = part1 + part2 + part3

Net::SMTP.start('your.smtp.server', 25) do |smtp|
  smtp.send_message message,
                    'info@yourrubyapp.com',
                    'your@bestuserever.com'
end