/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { vi } from "vitest";

const mockPrompter = (returnValue) => ({
  prompt: vi.fn(async () => returnValue),
});

export { mockPrompter };
