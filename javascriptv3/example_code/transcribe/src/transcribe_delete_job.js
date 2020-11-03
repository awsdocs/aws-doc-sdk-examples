
const { TranscribeClient, DeleteTranscriptionJobCommand } = require("@aws-sdk/client-transcribe");

const client = new TranscribeClient({region: 'eu-west-1'});
const params = {
    TranscriptionJobName: "HelloWorld-brmur"
};

const run = async () => {
    try {
        const data = await client.send(new DeleteTranscriptionJobCommand(params));
        console.log('Success - deleted')}
    catch(err){
        console.log('Error', err)}
}
run();
