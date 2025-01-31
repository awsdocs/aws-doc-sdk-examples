// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo

class LabelCount {
    private var key: String? = null
    private var name: String? = null
    private var count = 0

    fun setKey(key: String?) {
        this.key = key
    }

    fun getKey(): String? = key

    fun setName(name: String?) {
        this.name = name
    }

    fun getName(): String? = name

    fun getCount(): Int = count

    fun setCount(count: Int) {
        this.count = count
    }
}
