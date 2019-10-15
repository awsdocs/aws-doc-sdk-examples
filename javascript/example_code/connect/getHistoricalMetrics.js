const aws = require('aws-sdk');
const connect = new aws.Connect();

let params = {
        InstanceId: "<provide your connect instance id>",
        EndTime: 1570845600,  //change the end time
        StartTime:1570780800, //change the start time
        Filters:{
            Queues:["<provide your connect instances queue id>"],
            Channels:["VOICE"]
        },
        HistoricalMetrics:[{
            Name: "CONTACTS_ABANDONED", //add more metrics in this section. View https://docs.aws.amazon.com/connect/latest/APIReference/API_GetMetricData.html
            Unit: "COUNT",
            Statistic:"SUM"
        },
        {
        Name: "CONTACTS_HOLD_ABANDONS",
        Unit: "COUNT",
        Statistic: "SUM"
        }]
    };


exports.handler = async (event) => {

    let result = await connect.getMetricData(params, function(err, data) {
        if (err){
                 console.log(err, err.stack);
        }
        else{   
             console.log(data);        
        }
    }).promise();
    
    var metrics = result["MetricResults"]
        console.log(metrics[0].Collections);
    
    
};
