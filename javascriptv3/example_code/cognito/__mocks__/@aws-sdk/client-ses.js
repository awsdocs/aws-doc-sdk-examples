const sendMock = jest.fn(async () => {});

class SES {
  send = sendMock;
}

class SendEmailCommand {}

export { SES, SendEmailCommand, sendMock };
