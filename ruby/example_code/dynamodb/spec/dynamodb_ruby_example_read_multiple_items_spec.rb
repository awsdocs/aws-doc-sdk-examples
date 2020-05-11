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

require_relative 'spec_helper'
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

     RSpec.describe ReadMultipleItems do
       let(:get_item, item: {'id': 'value', })
       let(:policy_json) do
         {
             request_items: {
                 'Movies': {
                     keys: [
                         {
                             'Year': '2005',
                             'MovieTitle': 'The Big New Movie',
                             'Plot': 'Action',
                             'Rating':'Excellent'
                         },
                         'Year': '2009',
                         'MovieTitle': 'A Nature Documentary',
                         'Plot': 'Documentary Short',
                         'Rating': 'Fair'
                 },
                 {
                     'Year': '1983',
                     'MovieTitle': 'An Animated Fairy Tale',
                     'Plot': 'Children Fiction',
                     'Rating': 'Good'
                 },
                 ]
                "Plays": {
                    keys: [
                 {
                     'Year': '2019',
                     'PlayTitle': 'King Sear',
                     'Plot': 'Tragedy',
                     'Rating': 'Poor'
                 },
                 {
                     'Year': '1913',
                     'PlayTitle': 'A Midautumn Night's Dream',
                      'Plot': 'Comedy',
                      'Rating': 'Excellent'
            },
        ],
      }.to_json
      end

      let(:dynamodb_client) { Aws::DynamoDB::Client.new(stub_responses: true) }
      let(:getbatchitem_client) { Aws::GetBatchItem::Client.new(stub_responses: true) }

      let(:read_multiple_items) do
        ReadMultipleItems.new(
          dynamodb_client: dynamodb_client
          getbatchitem_client: getbatchitem_client
         )
       end

      describe '#read_multiple_items' do
         it 'allows for an AWS DynamoDB Read Multiple Items function' do_
             expect_any_instance_of(Aws::DynamoDB::Client)
                 .to receive(:read_multiple_items).with(
                     name: table_name, keys: key_name
         )
         dynamodb.read_multiple_items(table_name, key_name)
       end

       context 'reading and parsing the data from the table as indicated by the projection expression is true' do
         it 'reads and parses data from the tables' do
           expect_any_instance_of(Aws::DynamoDB::Client)
               .to receive(:get_batch_item).with(
                   name: table_name, keys: key_name
               )

           expect_any_instance_of(Aws::GetBatchItem::Client)
               .to receive(:put_table_policy).with(
                   table: table_name
                   policy: policy_json
               )
         end
       end

       dynamodb.read_multiple_itesm(table_name, key_name, true)
     end
 end
end
end


# making sure that the projection_expression parameter returns the expected attributes from the Movies table
      describe 'projection_expression' do
        it 'returns the film projection expression as expected' do
          expect(projection_expression('FilmDirector')).to eq('John Doe', 'Jane Doe', 'Mr. Director')
          expect(projection_expression('MonthsInTheatre')).to eq('9', '4', '2')
          expect(projection_expression('AcademyAwardWinnerStatus')).to eq('Yes', 'No', 'Yes')
        end
# confirming that the projection_expression parameter returns the correct attributes from the Plays table
        it 'returns the play projection expression as expected' do
          expect(projection_expression('PlayDirector')).to eq('Willy Shakes', 'W.E. Shakingspear')
          expect(projection_expression('WeeksInTheatre')).to eq('3', '23')
          expect(projection_expression('TonyAwardWinnerStatus')).to eq ('No', 'Yes')
        end
# testing to see if we raise the correct exception and error message when the client request is correctly
# transmitted to DynamoDB, but DynamoDB cannot process the request
        it 'raises ServiceException when DynamoDB cannot process the request' do
          expect(projection_expression('TVDirector')).to raise_error(AmazonServiceException,
               /Could not complete operation/)
        end

# confirming whether the simple attribute option is the default option as specified
       describe ':simple_attributes' do
        it 'simple attributes mode is enabled by default' do
          expect(Client.new(opts).config.simple_attributes).to be(true)
        end
# testing to make sure the simple attribute option can be disabled, which would require that all attribute values have
# their types specified
        it 'can be disabled' do
          ddb = Client.new(opts.merge(simple_attributes: false))
          expect(ddb.config.simple_attributes).to be(false)
        end

# testing whether stub responses can successfully be provided and returned by the client
        describe '#stub_responses' do
          it 'accepts the simplified attribute format' do
            client = Client.new(stub_responses: true)
            client.stub_responses(:get_item, item: {'id' => 'value', })
            resp = client.get_item(table_name:'table', key: {'id' => 'value' })
            expect(resp.item).to eq('id' => 'value')
          end

# testing whether stub data, the top level of the specified operation’s response topology, can successfully be provided
# and returned by the client
          describe '#stub_data' do
            it 'accepts and returns simple attributes' do
              client = Client.new(stub_responses: true)
              data = client.stub_data(:get_item, item: { 'id' => 'value' })
              expect(data.item).to eq({ 'id' => 'value' })
            end

# when Aws::DynamoDB::Client is constructed with simple_attributes: false, values should be passed in hash notation, so
# that full control is given over the serialization; testing if an appropriate error is thrown otherwise
            it 'observes the :simple_attributes configuration option' do
              client = Client.new(stub_responses: true, simple_attributes: false)
              expect {
                client.stub_data(:get_item, item: { 'id' => 'value' })
              }.to raise_error(ArgumentError)
           # no errors are thrown if values are passed in hash notation
              data = client.stub_data(:get_item, item: { 'id' => { s: 'value' }})
              expect(data.to_h[:item]).to eq({ 'id' => { s: 'value' }})
            end

# tells the client to save the Movies' keys and then read the input from the database to make sure the keys were
# actually saved
            context 'with a valid key' do
              it 'successfully saves the movie keys in the DB' do
                result = client.record(keys)
                expect(result).to be_success &
                expect(DB[:Movies].all).match[a_hash_including({
                  "Year"  => "2015",
                  "MovieTitle" => "The Big New Movie",
                  "Plot" => "Action",
                  "Rating" => "Excellent",
                  },
                  {
                  "Year" => "2009",
                  "MovieTitle" => "A Nature Documentary",
                  "Plot" => "Documentary Short",
                  "Rating" => "Fair",
                  },
                   {
                  "Year" => "1983",
                  "Movie Title" => "An Animated Fairy Tale",
                  "Plot" => "Children's Fiction",
                  "Rating" => "Good",
                  })
              end

# tells the client to save the Plays' keys and then read the input from the database to make sure the keys were
# actually saved
              context 'with a valid key' do
                it 'successfully saves the play keys in the DB' do
                  result = client.record(keys)
                  expect(result).to be_success &
                  expect(DB[:Plays].all).match[a_hash_including({
                     "Year" => "2019",
                     "PlayTitle" => "King Sear",
                     "Plot" => "Tragedy",
                     "Rating" => "Poor",
                  },
                  {
                      "Year" => "1913",
                      "PlayTitle" => "A Midautumn Night's Dream",
                      "Plot" => "Comedy",
                      "Rating" => "Excellent",
                   })
            end
