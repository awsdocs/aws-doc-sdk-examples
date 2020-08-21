/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[index.js demonstrates how to query for information from Amazon Athena.]
// snippet-service:[athena]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Athena]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

// For more information about Amazon Athena, see the user guide and API reference at:
// https://docs.aws.amazon.com/athena

// snippet-start:[athena.JavaScript.index.complete]
const AWS = require('aws-sdk')
const Queue = require('async/queue')
const _ = require('lodash')

const ATHENA_DB = 'default'
const ATHENA_OUTPUT_LOCATION = 's3://my-bucket/athena-temp/'
const RESULT_SIZE = 1000
const POLL_INTERVAL = 1000

let creds = new AWS.SharedIniFileCredentials({filename:'/Users/username/.aws/credentials', profile: 'username'});
AWS.config.credentials = creds;

let client = new AWS.Athena({region: 'us-east-1'})

/* Create an async queue to handle polling for query results */
let q = Queue((id, cb) => {
    startPolling(id)
    .then((data) => { return cb(null, data) })
    .catch((err) => { console.log('Failed to poll query: ', err); return cb(err) })
}, 5);

/* Make a SQL query and display results */
makeQuery("SELECT * FROM cloudtrail_logs limit 10;")
.then((data) => {
    console.log('Row Count: ', data.length)
    console.log('DATA: ', data)
})
.catch((e) => { console.log('ERROR: ', e) })


function makeQuery(sql) {
    return new Promise((resolve, reject) => {
        let params = {
            QueryString: sql,
            ResultConfiguration: { OutputLocation: ATHENA_OUTPUT_LOCATION },
            QueryExecutionContext: { Database: ATHENA_DB }
        }

        /* Make API call to start the query execution */
        client.startQueryExecution(params, (err, results) => {
            if (err) return reject(err)
            /* If successful, get the query ID and queue it for polling */
            q.push(results.QueryExecutionId, (err, qid) => {
                if (err) return reject(err)
                /* Once query completed executing, get and process results */
                return buildResults(qid)
                .then((data) => { return resolve(data) })
                .catch((err) => { return reject(err) })
            })
        })
    })
}

function buildResults(query_id, max, page) {
    let max_num_results = max ? max : RESULT_SIZE
    let page_token = page ? page : undefined
    return new Promise((resolve, reject) => {
        let params = {
            QueryExecutionId: query_id,
            MaxResults: max_num_results,
            NextToken: page_token
        }

        let dataBlob = []
        go(params)

        /* Get results and iterate through all pages */
        function go(param) {
            getResults(param)
            .then((res) => {
                dataBlob = _.concat(dataBlob, res.list)
                if (res.next) {
                    param.NextToken = res.next
                    return go(param)
                } else return resolve(dataBlob)
            }).catch((err) => { return reject(err) })
        }

        /* Process results merging column names and values into a JS object */
        function getResults() {
            return new Promise((resolve, reject) => {
                client.getQueryResults(params, (err, data) => {
                    if (err) return reject(err)
                    var list = []
                    let header = buildHeader(data.ResultSet.ResultSetMetadata.ColumnInfo)
                    let top_row = _.map(_.head(data.ResultSet.Rows).Data, (n) => { return n.VarCharValue })
                    let resultSet = (_.difference(header, top_row).length > 0) ?
                        data.ResultSet.Rows :
                        _.drop(data.ResultSet.Rows)
                    resultSet.forEach((item) => {
                        list.push(_.zipObject(header, _.map(item.Data, (n) => {return n.VarCharValue })))
                    })
                    return resolve({next: ('NextToken' in data) ? data.NextToken : undefined, list: list})
                })
            })
        }
    })
}

function startPolling(id) {
    return new Promise((resolve, reject) => {
        function poll(id) {
            client.getQueryExecution({QueryExecutionId: id}, (err, data) => {
                if (err) return reject(err)
                if (data.QueryExecution.Status.State === 'SUCCEEDED') return resolve(id)
                else if (['FAILED', 'CANCELLED'].includes(data.QueryExecution.Status.State)) return reject(new Error(`Query ${data.QueryExecution.Status.State}`))
                else { setTimeout(poll, POLL_INTERVAL, id) }
            })
        }
        poll(id)
    })
}

function buildHeader(columns) {
    return _.map(columns, (i) => { return i.Name })
}
// snippet-end:[athena.JavaScript.index.complete]