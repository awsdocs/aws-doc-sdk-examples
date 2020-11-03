

const { TranscribeClient, StartTranscriptionJobCommand } = require("@aws-sdk/client-transcribe");

const client = new TranscribeClient({region: 'eu-west-1'});
const params = {
    TranscriptionJobName: "HelloWorld-brmur",
    LanguageCode: "en-US",
    MediaFormat: "mp3",
    Media: {
        MediaFileUri: "https://transcribe-demo-brmur.s3-eu-west-1.amazonaws.com/hello_world.mp3"
    }
};

const run = async () => {
    try {
        const data = await client.send(new StartTranscriptionJobCommand(params));
        console.log('Success - put', data)}
    catch(err){
        console.log('Error', err)}
}
run();
