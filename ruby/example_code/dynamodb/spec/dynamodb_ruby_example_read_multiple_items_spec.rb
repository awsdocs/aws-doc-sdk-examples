# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

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
            'Plays': {
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

