const mockGetQueueUrlCommand = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/GetQueueUrlCommand", () => ({
  SQS: function SQS() {
    this.GetQueueUrlCommand = mockDGetQueueUrlCommand;
  },
}));
import { run } from "../../../cross-services/message-app/js/index";
test("has to mock sqs#getqueueurlcommand", async (done) => {
  await run();
  expect(mockGetQueueUrlCommand).toHaveBeenCalled;
  done();
});

const mockSendMessageCommand = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/SendMessageCommand", () => ({
  SQS: function SQS() {
    this.SendMessageCommand = mockSendMessageCommand;
  },
}));
import { run } from "../../../cross-services/message-app/js/index";
test("has to mock sqs#sendmessagecommand", async (done) => {
  await run();
  expect(mockSendMessageCommand).toHaveBeenCalled;
  done();
});

const mockReceiveMessageCommand = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/ReceiveMessageCommand", () => ({
  SQS: function SQS() {
    this.ReceiveMessageCommand = mockReceiveMessageCommand;
  },
}));
import { run } from "../../../cross-services/message-app/js/index";
test("has to mock sqs#receivemessagecommand", async (done) => {
  await run();
  expect(mockReceiveMessageCommand).toHaveBeenCalled;
  done();
});

const mockPurgeQueueCommand = jest.fn();
jest.mock("@aws-sdk/client-sqs/commands/PurgeQueueCommand", () => ({
  SQS: function SQS() {
    this.PurgeQueueCommand = mockPurgeQueueCommand;
  },
}));
import { run } from "../../../cross-services/message-app/js/index";
test("has to mock sqs#purgequeuecommand", async (done) => {
  await run();
  expect(mockPurgeQueueCommand).toHaveBeenCalled;
  done();
});
