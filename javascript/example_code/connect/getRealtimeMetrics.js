const aws = require('aws-sdk');
const connect = new aws.Connect({apiVersion: '2017-08-08'});
 
let params = {
        CurrentMetrics: [{
            Name: "AGENTS_AVAILABLE",
            Unit: "COUNT"
        }],
        Filters: {
            Channels: ["VOICE"],
            Queues: ["yourQueueID"] //replace 'yourQueueID' with your Queue ID
        },
        InstanceId: "yourInstanceID" //replace 'yourInstanceID' with your Instance ID
    };
 
exports.handler = async (event) => {
        let data = await connect.getCurrentMetricData(params).promise();
        console.log(data);
        var str = JSON.stringify(data, null, 2);
        console.log(str);
}
