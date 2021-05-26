const mockPutObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
  S3: function S3() {
    this.PutObjectCommand = mockPutObject;
  },
}));
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#PutObjectCommand", async (done) => {
  await run();
  expect(mockPutObject).toHaveBeenCalled();
  done();
});

const mockListObjects = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
  S3: function S3() {
    this.ListObjectsCommand = mockListObjects;
  },
}));
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#ListObjectsCommand", async (done) => {
  await run();
  expect(mockListObjects).toHaveBeenCalled();
  done();
});

const mockGetObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/GetObjectCommand", () => ({
  S3: function S3() {
    this.GetObjectCommand = mockGetObject;
  },
}));
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#GetObjectCommand", async (done) => {
  await run();
  expect(mockGetObject).toHaveBeenCalled();
  done();
});

const mockGetObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/DeleteObjectCommand", () => ({
  S3: function S3() {
    this.DeleteObjectCommand = mockGetObject;
  },
}));
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#DeleteObjectCommand", async (done) => {
  await run();
  expect(mockGetObject).toHaveBeenCalled();
  done();
});

const mockSendEmailCommand = jest.fn();
jest.mock(
  "@aws-sdk/client-ses/commands/SendEmailCommand",
  () => ({
    SES: function SES() {
      this.SendEmailCommand = mockSendEmailCommand;
    },
  })
);
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#mockmockSendEmailCommand", async (done) => {
  await run();
  expect(mockSendEmailCommand).toHaveBeenCalled();
  done();
});

const mockStartFaceDetectionCommand = jest.fn();
jest.mock(
    "@aws-sdk/client-rekognition/commands/StartFaceDetectionCommand",
    () => ({
        Rekognition: function Rekognition() {
            this.StartFaceDetection = mockStartFaceDetectionCommand;
        },
    })
);
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#mockmockStartFaceDetection", async (done) => {
    await run();
    expect(mockStartFaceDetectionCommand).toHaveBeenCalled();
    done();
});


const mockGetFaceDetectionCommand = jest.fn();
jest.mock(
    "@aws-sdk/client-rekognition/commands/GetFaceDetectionCommand",
    () => ({
        Rekognition: function Rekognition() {
            this.GetFaceDetection = mockGetFaceDetectionCommand;
        },
    })
);
import { run } from "../../../cross-services/video-analyzer/src/js/index";

test("has to mock db#mockmockGetFaceDetection", async (done) => {
    await run();
    expect(mockGetFaceDetectionCommand).toHaveBeenCalled();
    done();
});
