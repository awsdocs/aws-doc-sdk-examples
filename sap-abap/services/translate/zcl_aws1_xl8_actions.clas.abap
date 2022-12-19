" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_XL8_ACTIONS definition
  public
  final
  create public .

public section.

  methods DESCRIBE_TEXT_TRANSLATION_JOB
    importing
      !IV_JOBID type /AWS1/XL8JOBID
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8DSCTEXTXLATJOBRSP .
  methods LIST_TEXT_TRANSLATION_JOBS
    importing
      !IV_JOBNAME type /AWS1/XL8JOBNAME
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8LSTTEXTXLATJOBSRSP .
  methods START_TEXT_TRANSLATION_JOB
    importing
      !IV_SOURCELANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_TARGETLANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_JOBNAME type /AWS1/XL8JOBNAME
      !IV_INPUT_DATA_S3URI type /AWS1/XL8S3URI
      !IV_INPUT_DATA_CONTENTTYPE type /AWS1/XL8CONTENTTYPE
      !IV_OUTPUT_DATA_S3URI type /AWS1/XL8S3URI
      !IV_DATAACCESSROLEARN type /AWS1/XL8IAMROLEARN
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8STRTTEXTXLATJOBRSP .
  methods STOP_TEXT_TRANSLATION_JOB
    importing
      !IV_JOBID type /AWS1/XL8JOBID
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8STOPTEXTXLATJOBRSP .
  methods TRANSLATE_TEXT
    importing
      !IV_SOURCELANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_TARGETLANGUAGECODE type /AWS1/XL8LANGUAGECODESTRING optional
      !IV_TEXT type /AWS1/XL8BOUNDEDLENGTHSTRING
    exporting
      !OO_RESULT type ref to /AWS1/CL_XL8TRANSLATETEXTRSP .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_XL8_ACTIONS IMPLEMENTATION.


  METHOD describe_text_translation_job.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.describe_text_translation_job]

    "Gets the properties associated with an asynchronous batch translation job."
    "Includes properties such as name, ID, status, source and target languages, and input/output Amazon Simple Storage Service (Amazon S3) buckets."
    TRY.
        oo_result = lo_xl8->describetexttranslationjob(      "oo_result is returned for testing purposes."
          EXPORTING
            iv_jobid        = iv_jobid
          ).
        MESSAGE 'Job description retrieved.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.describe_text_translation_job]
  ENDMETHOD.


  METHOD list_text_translation_jobs.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.list_text_translation_jobs]
    "Gets a list of the batch translation jobs that you have submitted."

    DATA lo_filter TYPE REF TO /aws1/cl_xl8textxlationjobfilt.

    "Create an ABAP object for filtering using jobname."
    CREATE OBJECT lo_filter
      EXPORTING
        iv_jobname = iv_jobname.

    TRY.
        oo_result = lo_xl8->listtexttranslationjobs(      "oo_result is returned for testing purposes."
          EXPORTING
            io_filter        = lo_filter
          ).
        MESSAGE 'Jobs retrieved.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidfilterex .
        MESSAGE 'The filter specified for the operation is not valid. Specify a different filter.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex .
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.list_text_translation_jobs]
  ENDMETHOD.


  METHOD start_text_translation_job.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) =     /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.start_text_translation_job]
    "Starts an asynchronous batch translation job."
    "Use batch translation jobs to translate large volumes of text across multiple documents at once."

    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.

    "Create an ABAP object for the input data config."
    CREATE OBJECT lo_inputdataconfig
      EXPORTING
        iv_s3uri       = iv_input_data_s3uri
        iv_contenttype = iv_input_data_contenttype.

    "Create an ABAP object for the output data config."
    CREATE OBJECT lo_outputdataconfig
      EXPORTING
        iv_s3uri = iv_output_data_s3uri.

    "Create an internal table for target languages."
    CREATE OBJECT lo_targetlanguagecodes
      EXPORTING
        iv_value = iv_targetlanguagecode.
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    TRY.
        oo_result = lo_xl8->starttexttranslationjob(      "oo_result is returned for testing purposes."
          EXPORTING
            io_inputdataconfig = lo_inputdataconfig
            io_outputdataconfig = lo_outputdataconfig
            it_targetlanguagecodes = lt_targetlanguagecodes
            iv_dataaccessrolearn = iv_dataaccessrolearn
            iv_jobname = iv_jobname
            iv_sourcelanguagecode = iv_sourcelanguagecode
          ).
        MESSAGE 'Translation job started.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8invparamvalueex .
        MESSAGE 'The value of the parameter is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex.
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
      CATCH /aws1/cx_xl8unsuppedlanguage00 .
        MESSAGE 'Amazon Translate does not support translation from the language of the source text into the requested target language.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.start_text_translation_job]
  ENDMETHOD.


  METHOD stop_text_translation_job.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) =     /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.stop_text_translation_job]
    "Stops an asynchronous batch translation job that is in progress."

    TRY.
        oo_result = lo_xl8->stoptexttranslationjob(      "oo_result is returned for testing purposes."
          EXPORTING
            iv_jobid        = iv_jobid
          ).
        MESSAGE 'Translation job stopped.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.stop_text_translation_job]
  ENDMETHOD.


  METHOD translate_text.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) =     /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.translate_text]
    "Translates input text from the source language to the target language."
    TRY.
        oo_result = lo_xl8->translatetext(      "oo_result is returned for testing purposes."
          EXPORTING
            iv_text        = iv_text
            iv_sourcelanguagecode = iv_sourcelanguagecode
            iv_targetlanguagecode = iv_targetlanguagecode
          ).
        MESSAGE 'Translation completed.' TYPE 'I'.
      CATCH /aws1/cx_xl8detectedlanguage00 .
        MESSAGE 'The confidence that Amazon Comprehend accurately detected the source language is low.' TYPE 'E'.
      CATCH /aws1/cx_xl8internalserverex .
        MESSAGE 'An internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex .
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex .
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8serviceunavailex .
        MESSAGE 'The Amazon Translate service is temporarily unavailable.' TYPE 'E'.
      CATCH /aws1/cx_xl8textsizelmtexcdex .
        MESSAGE 'The size of the text you submitted exceeds the size limit. ' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex .
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
      CATCH /aws1/cx_xl8unsuppedlanguage00 .
        MESSAGE 'Amazon Translate does not support translation from the language of the source text into the requested target language. ' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.translate_text]

  ENDMETHOD.
ENDCLASS.
