# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'csv'
s3 = Aws::SNS::Resource.new(region: 'us-west-2')
CSV.read("s3://peccy-bucket/s3-select-object-content-file.csv")

describe Client do
let(:client) { Client.new }
  before(:each) do
    Aws.config[:s3] = {
        region: 'us-west-2',
        credentials: Credentials.new('peccy-bucket', 's3://peccy-bucket/s3-select-object-content-file.csv'),
        retry_backoff: ->(*args) {}
              }
              end
  after(:each) do
        Aws.config = {}
        S3::BUCKET_REGIONS.clear
  end

      it 'raises an error when a region is missing' do
        expect do
          Client.new(region: nil)
        end.to raise_error(Aws::Errors::MissingRegionError)
      end

describe '#list_objects' do
  it 'raises an error of the bucket name ends with a dash' do
    client = Client.new(stub_responses: true)
    expect do
      client.list_objects(bucket: 'bucket-name/key-prefix')
    end.to raise_error(
               ArgumentError, 'bucket name must not end with a dash(-)'
           )
  end


  describe '#create-bucket' do
    let(:client) { S3::Client.new(stub_responses: true) }
    let(:createbucket) do
      createbucket.new('bucket', 'key', 'id', client: client)
    end

    it 'calls complete with the given part list' do
      expect(client).to receive(:createbucket).with(
          bucket: 'bucket',
          key: 'key',
          multipart_upload: {
              parts: [
                  { part_number: 1, etag: 'etag-1' },
                  { part_number: 2, etag: 'etag-2' },
                  { part_number: 3, etag: 'etag-3' }
              ]
          }
      )
      expect(obj).to be_kind_of(S3::Object)
      expect(obj.bucket_name).to eq('bucket')
      expect(obj.key).to eq('key')
      end



    let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
    let(:select_object_content_client) { Aws::SelectObjectContent::Client.new(stub_responses: true) }

    let(:select_object_content) do
      SelectObjectContent.new
       s3_client: s3_client
       select_object_content: select_object_content
      )
    end

    describe '#select_object_content' do
      it 'raises an error if the correct data is not selected' do
        client = Client.new(stub_responses: true)
        expect do
          client.select_object_content(S3: '6', 'teri', 'evergreen', '2')
        end.to raise_error(
          ArgumentError, 'ParseExpectedKeyword: Did not find the expected keyword in the SQL expression.'
          )
          end
        end
      end
    end
  end










