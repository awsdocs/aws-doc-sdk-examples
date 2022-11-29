/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from 'vitest';

describe('list-metrics', () => {
  it('should list metrics', async () => {

    const mod = await import('../actions/list-metrics.js');
    const { Metrics } = await mod.default;
    
    expect(Metrics.length).toBeGreaterThan(0)
  })
})