" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_zcl_aws1_cpd_actions DEFINITION FOR TESTING
  DURATION SHORT
  RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    DATA ao_cpd_actions TYPE REF TO zcl_aws1_cpd_actions.
    METHODS: detectsentiment FOR TESTING.
ENDCLASS.       "ltc_Zcl_Aws1_Cpd_Actions


CLASS ltc_zcl_aws1_cpd_actions IMPLEMENTATION.

  METHOD detectsentiment.
    ao_cpd_actions = NEW zcl_aws1_cpd_actions( ).
    DATA lo_output TYPE REF TO /aws1/cl_cpddetectsentimentrsp.
    DATA(lv_expected_output) = |POSITIVE|.

    ao_cpd_actions->detectsentiment(
      IMPORTING
        oo_result = lo_output ).

    DATA(lv_found) = abap_true.
    IF lo_output->has_sentiment( ) = abap_true.
      IF lo_output->ask_sentiment( ) = lv_expected_output.
          lv_found = abap_true.
      ENDIF.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Sentiment detection failed| ).
  ENDMETHOD.

ENDCLASS.
