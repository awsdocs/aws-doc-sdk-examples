" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS zcl_test DEFINITION PUBLIC FINAL CREATE PUBLIC.
  PUBLIC SECTION.
    METHODS test.
ENDCLASS.

CLASS zcl_test IMPLEMENTATION.
  METHOD test.
    DATA lv_test TYPE string.
    lv_test = 'test'.
  ENDMETHOD.
ENDCLASS.
