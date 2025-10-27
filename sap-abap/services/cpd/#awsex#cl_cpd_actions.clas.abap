" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_cpd_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS detectsentiment
      EXPORTING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectsentimentrsp
      RAISING
        /aws1/cx_rt_generic .
  PROTECTED SECTION.
  PRIVATE SECTION.

ENDCLASS.



CLASS /AWSEX/CL_CPD_ACTIONS IMPLEMENTATION.


  METHOD detectsentiment.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    DATA(lv_text) = |I love unicorns!|  .
    DATA(lv_language_code) = |en| .


    " snippet-start:[cpd.abapv1.detect_sentiment]
    TRY.
        oo_result = lo_cpd->detectsentiment(
          iv_languagecode = lv_language_code
          iv_text = lv_text
        ).

        MESSAGE |Detected sentiment: { oo_result->get_sentiment( ) }| TYPE 'I'.

      CATCH /aws1/cx_cpdtextsizelmtexcdex INTO DATA(lo_cpdex) .
        MESSAGE 'The size of the input text exceeds the limit. Use a smaller document.' TYPE 'E'.

    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_sentiment]
  ENDMETHOD.
ENDCLASS.
