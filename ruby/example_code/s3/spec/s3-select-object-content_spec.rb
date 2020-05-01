/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    specific language governing permissions and limitations under the License.
 */
require 'fakefs/spec_helpers'

require_relative 'spec_helper'
require 'csv'
s3 = Aws::SNS::Resource.new(region: 'us-west-2')
CSV.read("s3://peccy-bucket/s3-select-object-content-file.csv")

module Aws
    module S3
# confirming the desired bucket was created, returned, returned with the correct configuration, and passes additional
# options to the base client
      context '#create' do
        it 'should call create_bucket with the bucket name' do
          client.should_receive(create_bucket).with(bucket_name => 'peccy-bucket')
          buckets.create('peccy-bucket')
        end

        it 'should return an S3 bucket' do
          buckets.create('peccy-bucket').should be_an(S3::Bucket)
        end

        it 'should return a bucket with the correct config' do
          buckets.create('peccy-bucket').config.should == buckets.config
        end

        it 'passes additional options to base client' do
          client.should_receive(create_bucket).
              with(bucket_name: 'peccy-bucket', key:'s3://peccy-bucket/s3-select-object-content-file.csv',
                   body: ‘Hello, Bucket!’)
          buckets.create(bucket_name: ‘peccy-bucket’, key: 's3://peccy-bucket/s3-select-object-content-file.csv',
                         body: ‘Hello, Bucket!’)
        end
      end

# making sure the SQL expression returns the targeted object content
      describe 'select_object_content' do
      it 'selects the correct object content according to the SQL expression'
      select_object_content = {
          S3 = ["6", "teri", "evergreen", "72"]
          post '/select-object-content-file.csv', CSV.generate(S3)
      }
    end

# testing to make sure the parameters are received by the client
      describe 'client' do
        it 'receives the correct parameters' do
      @client.should_receive(params)
      and_return(params.new)
      @S3.params.should be_true
      end

# testing to see whether the correct content is parsed and retrieved from the CSV file
      describe 'select-object-content' do
        it 'returns the correct content from the csv file ' do
          expected_result = ["6", "teri", "evergreen", "72"]
          assert.equal(expected_result, parse_csv(csv_file).select_object_content)
        end
        def csv_file
          File.read('s3://peccy-bucket/s3-select-object-content-file.csv')
        end

# testing to make sure an incomplete csv file entry throws an error
        context 'when csv file entry fails validation' do
          let(:object) {{'some' => 'data'}}
          before do
            allow(client).to receive(:records)
                .with(object)
                .and_return(RecordResult.new(false, 48, 'Entry incomplete'))
          end
          it 'returns an error message' do
            post '/object', CSV.generate(object)
            parsed = CSV.parse(last_response.body)
            expect(parsed).to include('error' => 'Entry incomplete')
          end
          it 'responds with a 422 (Unprocessable entity)' do
            post '/object', CSV.generate(object)
            expect(last_response.status).to eq(422)
          end
          end

          end

# testing to make sure a sampling of the correct key/value pairs are returned
        client.should_receive(:params)
            with(hash_including('id' => '1', 'name' => 'tom', 'favorite_plant' =>
                'fir', 'number_of_national_parks_visited_in_lifetime' => '15')) &
            with(hash_including('id' => '2', 'name' => 'ann', 'favorite_plant' =>
                'cactus', 'number_of_national_parks_visited_in_lifetime' => '22')) &
            with(hash_including('id' => '3', 'name' => 'john', 'favorite_plant' =>
                'grass', 'number_of_national_parks_visited_in_lifetime' => '6')) &
            with(hash_including('id' => '4', 'name' => 'bob', 'favorite_plant' =>
                'rose', 'number_of_national_parks_visited_in_lifetime' => '37')) &
            with(hash_including('id' => '5', 'name' => 'natalia', 'favorite_plant' =>
                'daisy', 'number_of_national_parks_visited_in_lifetime' => '18'))
        end

# testing to confirm the successful retrieval of an object from our bucket
      describe '#select_object_content' do
      it "can write and retrieve an object" do
        client.put_object(bucket: "peccy-bucket", key: "s3://peccy-bucket/s3-select-object-content-file.csv",
                          body: "Hello, Bucket!")
        obj = client.get_object(bucket: "peccy-bucket", key: "s3://peccy-bucket/s3-select-object-content-file.csv")
        expect(obj.body.read).to eq("Hello, Bucket!") &
        expect(client.api_requests.size).to eq(3) &
        expect(client.api_requests.last[:params]).to eq(
              bucket: "peccy-bucket",
              key: "s3://peccy-bucket/s3-select-object-content-file.csv"
        )
      end

# testing to make sure an the NoSuchBucket error is thrown when we attempt to search for a non-existent bucket
      it "raises the appropriate exception when a bucket doesn't exist" do
        expect {
          client.put_object(
              bucket: "unknown-peccy-bucket",
              key: "s3://peccy-bucket/s3-select-object-content-file.csv",
              body: "Bye!"
          )
        }.to raise_error(Aws::S3::Errors::NoSuchBucket)
        expect(client.api_requests.size).to eq(2)
      end

# testing to confirm an appropriate error message is returned when an object/key doesn't exist
      it "raises the appropriate exception when an object doesn't exist" do
        expect {
          client.get_object(bucket: "peccy-bucket", key: "hiddenfile.csv")
        }.to raise_error(Aws::S3::Errors::NoSuchKey)
        expect(client.api_requests.size).to eq(2)
      end
      end


