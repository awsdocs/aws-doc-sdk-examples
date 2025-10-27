class /AWSEX/CL_CPD_ACTIONS definition
  public
  final
  create public .

public section.

  methods DETECTSENTIMENT
    exporting
      value(OO_RESULT) type ref to /AWS1/CL_CPDDETECTSENTIMENTRSP
    raising
      /AWS1/CX_RT_GENERIC .
protected section.
private section.

ENDCLASS.



CLASS /AWSEX/CL_CPD_ACTIONS IMPLEMENTATION.


  METHOD DETECTSENTIMENT.
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
