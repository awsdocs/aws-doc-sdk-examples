/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { Box, Cards } from "@cloudscape-design/components";

export const ChatItems = ({ chatItems = [] }) => (
  <Cards
    cardsPerRow={[{ cards: 1 }]}
    cardDefinition={{
      header: (e) => <Box variant="strong">{e.name}</Box>,
      sections: [{ id: "message", content: (e) => e.body }],
    }}
    items={chatItems}
    empty={<Box textAlign="center">No messages</Box>}
  ></Cards>
);
