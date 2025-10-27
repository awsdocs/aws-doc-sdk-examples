" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_cpd_actions DEFINITION FOR TESTING
  DURATION SHORT
  RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    DATA ao_cpd_actions TYPE REF TO /awsex/cl_cpd_actions.
    METHODS: detectsentiment FOR TESTING RAISING /aws1/cx_rt_generic.
ENDCLASS.       "ltc_awsex_cl_cpd_actions


CLASS ltc_awsex_cl_cpd_actions IMPLEMENTATION.

  METHOD detectsentiment.
    ao_cpd_actions = NEW /awsex/cl_cpd_actions( ).
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
