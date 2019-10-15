const aws = require('aws-sdk');
const connect = new aws.Connect();

let params = {
        InstanceId: "1e8f045a-d7f1-4405-96c1-66701c25fd32",
        EndTime: 1571184000,
        StartTime:1571153400,
        Filters:{
            Queues:["943031b0-62ce-49a7-8688-9e84accd94fc"],
            Channels:["VOICE"]
        },
        HistoricalMetrics : [
      {
         "Name" : "AFTER_CONTACT_WORK_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "CONTACTS_QUEUED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_HANDLED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "HANDLE_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "CONTACTS_TRANSFERRED_OUT",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_MISSED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "OCCUPANCY",
         "Unit" : "PERCENT",
         "Statistic" : "AVG"
      },
      {
         "Name" : "QUEUED_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "MAX"
      },
      {
         "Name" : "HOLD_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "SERVICE_LEVEL",
         "Threshold" : {
            "Comparison" : "LT",
            "ThresholdValue" : 60.0
         },
         "Unit" : "PERCENT",
         "Statistic" : "AVG"
      },
      {
         "Name" : "SERVICE_LEVEL",
         "Threshold" : {
            "Comparison" : "LT",
            "ThresholdValue" : 120.0
         },
         "Unit" : "PERCENT",
         "Statistic" : "AVG"
      },
      {
         "Name" : "SERVICE_LEVEL",
         "Threshold" : {
            "Comparison" : "LT",
            "ThresholdValue" : 30.0
         },
         "Unit" : "PERCENT",
         "Statistic" : "AVG"
      },
      {
         "Name" : "CONTACTS_ABANDONED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_CONSULTED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_AGENT_HUNG_UP_FIRST",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_HANDLED_INCOMING",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_HANDLED_OUTBOUND",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_HOLD_ABANDONS",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_TRANSFERRED_IN",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_TRANSFERRED_IN_FROM_QUEUE",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CONTACTS_TRANSFERRED_OUT_FROM_QUEUE",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "CALLBACK_CONTACTS_HANDLED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "API_CONTACTS_HANDLED",
         "Unit" : "COUNT",
         "Statistic" : "SUM"
      },
      {
         "Name" : "ABANDON_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "QUEUE_ANSWER_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "INTERACTION_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      },
      {
         "Name" : "INTERACTION_AND_HOLD_TIME",
         "Unit" : "SECONDS",
         "Statistic" : "AVG"
      }
   ]
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
