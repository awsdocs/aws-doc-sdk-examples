const mockPutObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
    S3: function S3() {
        this.PutObjectCommand = mockPutObject;
    },
}));
const { run } = require("../../../cross-services/transcription-app/src/index");

test("has to mock db#PutObjectCommand", async (done) => {
    await run();
    expect(mockPutObject).toHaveBeenCalled;
    done();
});

const mockListObjects = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
    S3: function S3() {
        this.ListObjectsCommand = mockListObjects;
    },
}));
const { run } = require("../../../cross-services/transcription-app/src/index");

test("has to mock db#ListObjectsCommand", async (done) => {
    await run();
    expect(mockListObjects).toHaveBeenCalled;
    done();
});

const mockGetObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/GetObjectCommand", () => ({
    S3: function S3() {
        this.GetObjectCommand = mockGetObject;
    },
}));
const { run } = require("../../../cross-services/transcription-app/src/index");

test("has to mock db#GetObjectCommand", async (done) => {
    await run();
    expect(mockGetObject).toHaveBeenCalled;
    done();
});

const mockStartTranscriptionJobCommand = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/StartTranscriptionJobCommand", () => ({
    Transcribe: function Transcribe() {
        this.StartTranscriptionJobCommand = mockStartTranscriptionJobCommand;
    },
}));
const { run } = require("../../../cross-services/transcription-app/src/index");

test("has to mock db#mockStartTranscriptionJobCommand", async (done) => {
    await run();
    expect(mockStartTranscriptionJobCommand).toHaveBeenCalled;
    done();
});
