import { expect, test, vi } from "vitest";
import { SendConversationtoBedrock } from "../models/amazonNovaText/converse-with-tool.js";
import {
  BedrockRuntimeClient,
  ConverseCommand,
} from "@aws-sdk/client-bedrock-runtime";

vi.mock("@aws-sdk/client-bedrock-runtime", () => ({
  BedrockRuntimeClient: vi.fn().mockImplementation(() => ({
    send: vi.fn().mockResolvedValue({
      stopReason: "end_turn",
      output: {
        message: {
          content: [
            {
              text: "The most popular song on WZPZ is Elemental Hotel by 8 Storey Hike.",
            },
          ],
        },
      },
    }),
  })),
  ConverseCommand: vi.fn(),
}));

vi.mock("./converse-with-tool.js", () => ({
  get_top_song: vi.fn().mockResolvedValue({
    song: "Elemental Hotel",
    artist: "8 Storey Hike",
  }),
}));

test("SendConversationtoBedrock", async () => {
  const modelId = "amazon.nova-lite-v1:0";
  const message = [
    {
      role: "user",
      content: [{ text: "What is the most popular song on WZPZ?" }],
    },
  ];
  const system_prompt = [
    {
      text: "You are a music expert that provides the most popular song played on a radio station, using only the top_song tool.",
    },
  ];
  const tool_config = {
    tools: [
      {
        toolSpec: {
          name: "top_song",
          description: "Get the most popular song played on a radio station.",
          inputSchema: {
            json: {
              type: "object",
              properties: {
                sign: {
                  type: "string",
                  description:
                    "The call sign for the radio station for which you want the most popular song. Example calls signs are WZPZ and WKRP.",
                },
              },
              required: ["sign"],
            },
          },
        },
      },
    ],
  };

  const result = await SendConversationtoBedrock(
    modelId,
    message,
    system_prompt,
    tool_config,
  );

  expect(result).toContain("The most popular song");
});
