import { fireEvent, render, screen } from "@testing-library/react";
import { MockWorkItemService } from "./testing/MockWorkItemService";
import { WorkItemControls } from "./WorkItemListComponent";
import * as service from "./WorkItemService";

jest.mock("./WorkItemService");
const mocked = jest.mocked(service);

const FAKE_EMAIL = "ses-recipient@example.com";

test("AddWorkItemComponent sends report", async () => {
  mocked.workItemService = new MockWorkItemService();
  await render(<WorkItemControls />);

  await fireEvent.change(
    screen.getByPlaceholderText(/Recipient's email/),
    FAKE_EMAIL
  );
  await fireEvent.click(screen.getByTestId("send-report"));

  expect(mocked.workItemService.mailItem).toHaveBeenCalledWith(FAKE_EMAIL);
});
