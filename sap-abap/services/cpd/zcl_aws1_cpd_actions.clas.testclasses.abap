" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_zcl_aws1_cpd_actions DEFINITION FOR TESTING
  DURATION SHORT
  RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    CONSTANTS cv_languagecode VALUE |EN|.

    METHODS: detectsentiment FOR TESTING.
ENDCLASS.       "ltc_Zcl_Aws1_Cpd_Actions


CLASS ltc_zcl_aws1_cpd_actions IMPLEMENTATION.

  METHOD detectsentiment.
    DATA lo_output TYPE REF TO /aws1/cl_cpddetectsentimentrsp.
    DATA(lv_expected_output) = |POSITIVE|.

    ao_cpd_actions->detectsentiment(
      IMPORTING
        oo_result = lo_output ).

    DATA(lv_found) = abap_true.
    IF lo_output->has_sentiment() = abap_true.
      IF lo_output->get_sentiment() = lv_expected_output.
          lv_found = abap_true.
      ENDIF.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Sentiment detection failed| ).
  ENDMETHOD.

ENDCLASS.
