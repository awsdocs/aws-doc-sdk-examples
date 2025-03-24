" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS zcl_aws1_xl8_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS describe_text_translation_job
      IMPORTING
      !iv_jobid TYPE /aws1/xl8jobid
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp .
    METHODS list_text_translation_jobs
      IMPORTING
      !iv_jobname TYPE /aws1/xl8jobname
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_xl8lsttextxlatjobsrsp .
    METHODS start_text_translation_job
      IMPORTING
      !iv_sourcelanguagecode TYPE /aws1/xl8languagecodestring OPTIONAL
      !iv_targetlanguagecode TYPE /aws1/xl8languagecodestring OPTIONAL
      !iv_jobname TYPE /aws1/xl8jobname
      !iv_input_data_s3uri TYPE /aws1/xl8s3uri
      !iv_input_data_contenttype TYPE /aws1/xl8contenttype
      !iv_output_data_s3uri TYPE /aws1/xl8s3uri
      !iv_dataaccessrolearn TYPE /aws1/xl8iamrolearn
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_xl8strttextxlatjobrsp .
    METHODS stop_text_translation_job
      IMPORTING
      !iv_jobid TYPE /aws1/xl8jobid
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_xl8stoptextxlatjobrsp .
    METHODS translate_text
      IMPORTING
      !iv_sourcelanguagecode TYPE /aws1/xl8languagecodestring OPTIONAL
      !iv_targetlanguagecode TYPE /aws1/xl8languagecodestring OPTIONAL
      !iv_text TYPE /aws1/xl8boundedlengthstring
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_xl8translatetextrsp .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_XL8_ACTIONS IMPLEMENTATION.


  METHOD describe_text_translation_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.describe_text_translation_job]

    "Gets the properties associated with an asynchronous batch translation job."
    "Includes properties such as name, ID, status, source and target languages, and input/output Amazon Simple Storage Service (Amazon S3) buckets."
    TRY.
        oo_result = lo_xl8->describetexttranslationjob(      "oo_result is returned for testing purposes."
          iv_jobid        = iv_jobid ).
        MESSAGE 'Job description retrieved.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex.
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex.
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.describe_text_translation_job]
  ENDMETHOD.


  METHOD list_text_translation_jobs.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.list_text_translation_jobs]
    "Gets a list of the batch translation jobs that you have submitted."

    DATA lo_filter TYPE REF TO /aws1/cl_xl8textxlationjobfilt.

    "Create an ABAP object for filtering using jobname."
    lo_filter = NEW #( iv_jobname = iv_jobname ).

    TRY.
        oo_result = lo_xl8->listtexttranslationjobs(      "oo_result is returned for testing purposes."
          io_filter        = lo_filter ).
        MESSAGE 'Jobs retrieved.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex.
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidfilterex.
        MESSAGE 'The filter specified for the operation is not valid. Specify a different filter.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex.
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.list_text_translation_jobs]
  ENDMETHOD.


  METHOD start_text_translation_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.start_text_translation_job]
    "Starts an asynchronous batch translation job."
    "Use batch translation jobs to translate large volumes of text across multiple documents at once."

    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.

    "Create an ABAP object for the input data config."
    lo_inputdataconfig = NEW #( iv_s3uri = iv_input_data_s3uri
                                iv_contenttype = iv_input_data_contenttype ).

    "Create an ABAP object for the output data config."
    lo_outputdataconfig = NEW #( iv_s3uri = iv_output_data_s3uri ).

    "Create an internal table for target languages."
    lo_targetlanguagecodes = NEW #( iv_value = iv_targetlanguagecode ).
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    TRY.
        oo_result = lo_xl8->starttexttranslationjob(      "oo_result is returned for testing purposes."
          io_inputdataconfig = lo_inputdataconfig
            io_outputdataconfig = lo_outputdataconfig
            it_targetlanguagecodes = lt_targetlanguagecodes
            iv_dataaccessrolearn = iv_dataaccessrolearn
            iv_jobname = iv_jobname
            iv_sourcelanguagecode = iv_sourcelanguagecode ).
        MESSAGE 'Translation job started.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex.
        MESSAGE 'An internal server error occurred. Retry your request.' TYPE 'E'.
      CATCH /aws1/cx_xl8invparamvalueex.
        MESSAGE 'The value of the parameter is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex.
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex.
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
      CATCH /aws1/cx_xl8unsuppedlanguage00.
        MESSAGE 'Amazon Translate does not support translation from the language of the source text into the requested target language.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.start_text_translation_job]
  ENDMETHOD.


  METHOD stop_text_translation_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.stop_text_translation_job]
    "Stops an asynchronous batch translation job that is in progress."

    TRY.
        oo_result = lo_xl8->stoptexttranslationjob(      "oo_result is returned for testing purposes."
          iv_jobid        = iv_jobid ).
        MESSAGE 'Translation job stopped.' TYPE 'I'.
      CATCH /aws1/cx_xl8internalserverex.
        MESSAGE 'An internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex.
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.stop_text_translation_job]
  ENDMETHOD.


  METHOD translate_text.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_xl8) = /aws1/cl_xl8_factory=>create( lo_session ).

    "snippet-start:[xl8.abapv1.translate_text]
    "Translates input text from the source language to the target language."
    TRY.
        oo_result = lo_xl8->translatetext(      "oo_result is returned for testing purposes."
          iv_text        = iv_text
            iv_sourcelanguagecode = iv_sourcelanguagecode
            iv_targetlanguagecode = iv_targetlanguagecode ).
        MESSAGE 'Translation completed.' TYPE 'I'.
      CATCH /aws1/cx_xl8detectedlanguage00.
        MESSAGE 'The confidence that Amazon Comprehend accurately detected the source language is low.' TYPE 'E'.
      CATCH /aws1/cx_xl8internalserverex.
        MESSAGE 'An internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_xl8invalidrequestex.
        MESSAGE 'The request that you made is not valid.' TYPE 'E'.
      CATCH /aws1/cx_xl8resourcenotfoundex.
        MESSAGE 'The resource you are looking for has not been found.' TYPE 'E'.
      CATCH /aws1/cx_xl8serviceunavailex.
        MESSAGE 'The Amazon Translate service is temporarily unavailable.' TYPE 'E'.
      CATCH /aws1/cx_xl8textsizelmtexcdex.
        MESSAGE 'The size of the text you submitted exceeds the size limit. ' TYPE 'E'.
      CATCH /aws1/cx_xl8toomanyrequestsex.
        MESSAGE 'You have made too many requests within a short period of time.' TYPE 'E'.
      CATCH /aws1/cx_xl8unsuppedlanguage00.
        MESSAGE 'Amazon Translate does not support translation from the language of the source text into the requested target language. ' TYPE 'E'.
    ENDTRY.
    "snippet-end:[xl8.abapv1.translate_text]

  ENDMETHOD.
ENDCLASS.
