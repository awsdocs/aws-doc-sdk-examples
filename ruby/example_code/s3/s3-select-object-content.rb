require ‘aws-sdk-s3’
require 'csv'
s3 = Aws::S3::Resource.new(region: 'us-west-2')
#Trying a SQL query against a CSV file in Amazon S3. Given that we have a CSV document
# named s3-select-object-content-file.csv
# stored in an S3 bucket named peccy-bucket in the AWS Regions us-west-w, with contents describing
# id, name, favorite plant, number_of_national_parks_visited_in_lifetime
CSV.read("s3://peccy-bucket/s3-select-object-content-file.csv")
profile_name = 'peccy'
region = 'us-west-2'
bucket = 'peccy-bucket'
client = Aws::S3::Client.new(profile: peccy, region: us-west-2)
# Some basic S3 client usage

# Display a List of Amazon S3 Buckets
resp = s3.list_buckets
resp.buckets.each do |b|
  puts b.name
end

# Create a S3 bucket from S3::client
s3.create_bucket(bucket: 'peccy-bucket')

# puts data into an object, replacing the current contents
resp = client.put_object(bucket: ‘peccy-bucket’, key: 's3://peccy-bucket/s3-select-object-content-file.csv',
                         body: ‘Hello, Bucket!’)
# check if the file exists
resp = s3.list_objects_v2(bucket: bucket)
resp.contents.each do |obj|
puts obj.key
end

# retrieves an object for an S3 bucket
resp = client.get_object({bucket: ‘peccy-bucket’, key: 's3://peccy-bucket/s3-select-object-content-file.csv'})

module Aws
    module S3
        class SelectObjectContent
            def self.client
                @client ||= Aws::S3::Client.new
            end

# Use a Ruby block statement to process events
            client.select_object_content(params) do |stream|
            # Callback for every event that arrives
            stream.on_event do |event|
            end
            puts event.event_type
            # => :records / :stats / :end / :cont etc
            # Do Something with event object
            end
# use a Ruby block statement to catch unmodeled error events in the stream
              client.select_object_content(params) do |stream|
                  stream.on_error_event do |event|
              raise event
                  #         # => Aws::Errors::EventError
                  #         # event.event_type => :error
                  #         # event.error_code => String
                  #         # event.error_message => String
                  end

# pass in an EventStream object as the :event_stream_handler
                def self.run_query(sql:, bucket:, key:)
                  data = ""
                  handler = Aws::S3::EventStreams::SelectObjectContentEventStream.new
                  handler.on_records_event do |event|
                    puts "----records payload:----"
                    payload = event.payload.read
                    data += payload
                  end

                  handler.on_stats_event do |event|
                    # get :stats event that contains progress information
                    puts event.details.inspect
                    # => Aws::S3::Types::Stats bytes_scanned=xx, bytes_processed=xx, bytes_returned=xx
                  end
                 # Add :event_stream_handler option
                  params[:event_stream_handler] = handler
                  client.select_object_content(params)
                end

# or,
# use a Ruby Proc object, also following the same pattern as the EventStream object
        handler = Proc.new do |stream|
            stream.on_records_event do |event|
             # Do Something with :records event
                end
            stream.on_stats_event do |event|
                # Do Something with :stats event
            end
            end
 # Add :event_stream_handler option
        params[:event_stream_handler] = handler
        client.select_object_content(params)
            end

# our request syntax and input values
  resp = client.select_object_content ({
        bucket: 'peccy-bucket', # the S3 bucket; required String
        key: 's3://peccy-bucket/s3-select-object-content-file.csv', # input file name; required String
        expression_type: 'SQL', # the type of the provided expression eg) SQL; required String
        # required expression:
        expression: 'SELECT * FROM S3Object WHERE number_of_national_parks_visited_in_lifetime > 60',
        request_progress: enabled: false # indicates whether periodic request progress information should be enabled '
        },
# describes the format of the data in the object that is being queried
    input_serialization = {
        csv: {
        file_header_info: "USE", # accepts USE, IGNORE, NONE
        comments: "Comments",
        quote_escape_character: "QuoteEscapeCharacter",
        record_delimiter: "RecordDelimiter",
        field_delimiter: "FieldDelimiter",
        quote_character: "QuoteCharacter",
        allow_quoted_record_delimiter: false,
        },
        compression_type: "NONE", # accepts NONE, GZIP, BZIP2
        json: {
        type: "DOCUMENT", # accepts DOCUMENT, LINES
        },
        parquet: {
        },
        },

        output_serialization = { # required
        csv: {
        quote_fields: "ALWAYS", # accepts ALWAYS, ASNEEDED
        quote_escape_character: "QuoteEscapeCharacter",
        record_delimiter: "RecordDelimiter",
        field_delimiter: "FieldDelimiter",
        quote_character: "QuoteCharacter",
        },
        json: {
        record_delimiter: "RecordDelimiter",
         },
        },
        scan_range: {
        start: 1,
        end: 1,
        },
        event_stream_handler: _
    }
client.select_object_content(params)
    data
    end
    end
    )

# sample response for a CSV object after tOutputSerialization element directs S3 to return results in CSV
# HTTP/1.1 200 OK
# x-amz-id-2: ZRihv3y6+kE7KG11GEkQhU7/2/cHR3Yb2fCb2S04nxI423Dqwg2XiQ0B/UZlzYQvPiBlZNRcovw=
# x-amz-request-id: 8V541CD3C4BA79E0
# Date: Thurs, 16 Apr 2020 20:42:02 PST
# ["6", "teri", "evergreen", "72"]

# @param [Hash{null->null}] params
# @param [Hash{null->null}] options
# @param [Proc] block
# example response structure
def select_object_content(params = {}, options = {}, &block)
  params = params.dup
  event_stream_handler = case handler = params.delete(:event_stream_handler)
                         when EventStreams::SelectObjectContentEventStream then handler
                         when Proc then EventStreams::SelectObjectContentEventStream.new.tap(&handler)
                         when nil then EventStreams::SelectObjectContentEventStream.new
                         else
   msg = "expected :event_stream_handler to be a block or "\
            "instance of Aws::S3::EventStreams::SelectObjectContentEventStream"\
            ", got `#{handler.inspect}` instead"
                                       raise ArgumentError, msg
                                   end

            yield(event_stream_handler) if block_given?
            req = build_request(:select_object_content, params)
            req.context[:event_stream_handler] = event_stream_handler
            req.handlers.add(Aws::Binary::DecodeHandler, priority: 87)
            req.send_request(options, &block)
end


