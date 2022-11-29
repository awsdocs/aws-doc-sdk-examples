" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_XL8_SCENARIO definition
  public
  final
  create public .

public section.

  methods GETTING_STARTED_WITH_XL8
    importing
      !IV_SOURCELANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_TARGETLANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_JOBNAME type /AWS1/XL8JOBNAME
      !IV_INPUT_DATA_S3URI type /AWS1/XL8S3URI
      !IV_INPUT_DATA_CONTENTTYPE type /AWS1/XL8CONTENTTYPE
      !IV_OUTPUT_DATA_S3URI type /AWS1/XL8S3URI
      !IV_DATAACCESSROLEARN type /AWS1/XL8IAMROLEARN
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8DSCTEXTXLATJOBRSP .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_XL8_SCENARIO IMPLEMENTATION.


  METHOD getting_started_with_xl8.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) =     /aws1/cl_xl8_factory=>create( lo_session ).

    "1. Starts an asynchronous batch translation job."
    "2. Wait for the asynchronous job to complete"
    "3. Describe the asynchronous job"

    "snippet-start:[xl8.abapv1.getting_started_with_xl8]

    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.

    "Create an ABAP object for the input data config"
    CREATE OBJECT lo_inputdataconfig
      EXPORTING
        iv_s3uri       = iv_input_data_s3uri
        iv_contenttype = iv_input_data_contenttype.

    "Create an ABAP object for the output data config"
    CREATE OBJECT lo_outputdataconfig
      EXPORTING
        iv_s3uri = iv_output_data_s3uri.

    "Create an interal table for target languages"
    CREATE OBJECT lo_targetlanguagecodes
      EXPORTING
        iv_value = iv_targetlanguagecode.
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    TRY.
        DATA(lo_translationjob_result) = lo_xl8->starttexttranslationjob(
          EXPORTING
            io_inputdataconfig = lo_inputdataconfig
            io_outputdataconfig = lo_outputdataconfig
            it_targetlanguagecodes = lt_targetlanguagecodes
            iv_dataaccessrolearn = iv_dataaccessrolearn
            iv_jobname = iv_jobname
            iv_sourcelanguagecode = iv_sourcelanguagecode
          ).
        MESSAGE 'Translation job started' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8invparamvalueex .
        MESSAGE 'The value of the parameter is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex.
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time. ' TYPE 'E'.
      CATCH /aws1/cx_xl8unsuppedlanguage00 .
        MESSAGE 'Amazon Translate does not support translation from the language of the source text into the requested target language.' TYPE 'E'.
    ENDTRY.

    "Get the job ID"
    DATA(lv_jobid) = lo_translationjob_result->get_jobid( ).

    "Wait for translate job to complete"
    DATA(lo_des_translation_result) = lo_xl8->describetexttranslationjob( iv_jobid = lv_jobid ).
    WHILE lo_des_translation_result->get_textxlationjobproperties( )->get_jobstatus( ) <> 'COMPLETED'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds"
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_des_translation_result = lo_xl8->describetexttranslationjob( iv_jobid = lv_jobid ).
    ENDWHILE.

    TRY.
        oo_result = lo_xl8->describetexttranslationjob(      "oo_result is returned for testing purpose"
          EXPORTING
            iv_jobid        = lv_jobid
          ).
        MESSAGE 'Job description retrieved' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.

    "snippet-end:[xl8.abapv1.getting_started_with_xl8]
  ENDMETHOD.
ENDCLASS.
