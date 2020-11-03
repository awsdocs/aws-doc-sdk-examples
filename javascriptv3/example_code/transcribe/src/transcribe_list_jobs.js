
const { TranscribeClient, ListTranscriptionJobsCommand } = require("@aws-sdk/client-transcribe");

const client = new TranscribeClient({region: 'eu-west-1'});
const params = {
    JobNameContains: 'Hello'
};

const run = async () => {
    try {
        const data = await client.send(new ListTranscriptionJobsCommand(params));
        console.log('Success', data.TranscriptionJobSummaries)}
    catch(err){
        console.log('Error', err)}
}
run();
