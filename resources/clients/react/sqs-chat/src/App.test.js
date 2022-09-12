import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders Amazon Simple Queue Service (Amazon SQS)", async () => {
  render(<App />);
  const textElement = await screen.findByText(
    "Amazon Simple Queue Service (Amazon SQS)"
  );
  expect(textElement).toBeInTheDocument();
});
