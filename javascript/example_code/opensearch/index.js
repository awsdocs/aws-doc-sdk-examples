const { HttpRequest} = require("@aws-sdk/protocol-http");
const { defaultProvider } = require("@aws-sdk/credential-provider-node");
const { SignatureV4 } = require("@aws-sdk/signature-v4");
const { NodeHttpHandler } = require("@aws-sdk/node-http-handler");
const { Sha256 } = require("@aws-crypto/sha256-browser");

var region = 'us-east-1'; // e.g. us-west-1
var host = 'search-search-sample-test-zqlr2gewgzc7xwzdtdeeydxg6q.us-east-1.es.amazonaws.com'; // The OpenSearch domain endpoint (e.g. search-domain.region.es.amazonaws.com)
var index = 'movies';

async function lambda_handler () {

    // Put the user query into the query DSL for more accurate search results.
    // Note that certain fields are boosted (^).
    var query = {
        "size": 25,
        "query": {
            "multi_match": {
                "query": "thor",
                "fields": ["title^4", "plot^2", "actors", "directors"]
            }
        }
    }; 

    // Create the HTTP request
    var request = new HttpRequest({
        body: JSON.stringify(query),
        headers: {
            'Content-Type': 'application/json', // Elasticsearch 6.x requires an explicit Content-Type header
            'host': host
        },
        hostname: host,
        method: 'POST', // comment about get not working
        path: index + '/_search'
    }); 

    // Sign the request
    var signer = new SignatureV4({
        credentials: defaultProvider(),
        region: region,
        service: 'es',
        sha256: Sha256
    });
    
    var signedRequest = await signer.sign(request);
    
    // Send the request
    var client = new NodeHttpHandler();
    var { response } =  await client.handle(signedRequest)

    // Create the response and add some extra content to support CORS
    var r = {
        "statusCode": 200,
        "headers": {
            "Access-Control-Allow-Origin": '*'
        },
        "isBase64Encoded": "False"
    };

    // Add the search results to the response
    var responseBody = '';
    await new Promise(() => {
      response.body.on('data', (chunk) => {
        responseBody += chunk;
      });
      response.body.on('end', () => {
        r['body'] = responseBody;
        console.log(r)
        return r;
      })
    });
};
