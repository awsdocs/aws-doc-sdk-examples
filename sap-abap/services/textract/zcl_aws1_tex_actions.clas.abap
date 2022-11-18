" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_TEX_ACTIONS definition
  public
  final
  create public .

public section.

  methods ANALYZE_DOCUMENT
    importing
      !IV_S3OBJECT type /AWS1/TEXS3OBJECTNAME optional
      !IV_S3BUCKET type /AWS1/TEXS3BUCKET optional
    exporting
      !OO_RESULT type ref to /AWS1/CL_TEXANALYZEDOCRESPONSE .
  methods DETECT_DOCUMENT_TEXT
    importing
      !IV_S3OBJECT type /AWS1/TEXS3OBJECTNAME
      !IV_S3BUCKET type /AWS1/TEXS3BUCKET
    exporting
      !OO_RESULT type ref to /AWS1/CL_TEXDETECTDOCTEXTRSP .
  methods GET_DOCUMENT_ANALYSIS
    importing
      !IV_JOBID type /AWS1/TEXJOBID
    exporting
      !OO_RESULT type ref to /AWS1/CL_TEXGETDOCALYRESPONSE .
  methods START_DOCUMENT_ANALYSIS
    importing
      !IV_S3OBJECT type /AWS1/TEXS3OBJECTNAME
      !IV_S3BUCKET type /AWS1/TEXS3BUCKET
    exporting
      !OO_RESULT type ref to /AWS1/CL_TEXSTARTDOCALYRSP .
  methods START_DOCUMENT_TEXT_DETECTION
    importing
      !IV_S3OBJECT type /AWS1/TEXS3OBJECTNAME
      !IV_S3BUCKET type /AWS1/TEXS3BUCKET
    exporting
      !OO_RESULT type ref to /AWS1/CL_TEXSTARTDOCTEXTDETRSP .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_TEX_ACTIONS IMPLEMENTATION.


  METHOD analyze_document.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create( lo_session ).

    "snippet-start:[tex.abapv1.analyze_document]

    "Detects text and additional elements, such as forms or tables,"
    "in a local image file or from in-memory byte data."
    "The image must be in PNG or JPG format."

    DATA lo_document TYPE REF TO /aws1/cl_texdocument.
    DATA lo_s3object TYPE REF TO /aws1/cl_texs3object.
    DATA lo_featuretypes TYPE REF TO /aws1/cl_texfeaturetypes_w.
    DATA lt_featuretypes TYPE /aws1/cl_texfeaturetypes_w=>tt_featuretypes.

    "Create ABAP objects for feature type"
    "add TABLES to return information about the tables"
    "add FORMS to return detected form data."
    "To perform both types of analysis, add TABLES and FORMS to FeatureTypes"

    CREATE OBJECT lo_featuretypes EXPORTING iv_value = 'FORMS'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    CREATE OBJECT lo_featuretypes EXPORTING iv_value = 'TABLES'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    "Create an ABAP object for the S3 obejct."
    CREATE OBJECT lo_s3object
      EXPORTING
        iv_bucket = iv_s3bucket
        iv_name   = iv_s3object.

    "Create an ABAP object for the document."
    CREATE OBJECT lo_document EXPORTING io_s3object = lo_s3object.

    "Analyze document stored in S3"
    TRY.
        oo_result = lo_tex->analyzedocument(      "oo_result is returned for testing purpose"
      EXPORTING
        io_document        = lo_document
        it_featuretypes    = lt_featuretypes
      ).
        MESSAGE 'Analyze document completed' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex .
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex .
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex .
        MESSAGE 'The document is too large' TYPE 'E'.
      CATCH /aws1/cx_texhlquotaexceededex .
        MESSAGE 'Human loop quota has been exceeded' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr .
        MESSAGE 'Internal server error' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex .
        MESSAGE 'Request has invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex .
        MESSAGE 'S3 object is invalid' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex .
        MESSAGE 'Provisioned throughput exceeded limit' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex .
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex .
        MESSAGE 'The document is not supported' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.analyze_document]
  ENDMETHOD.


  METHOD detect_document_text.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create( lo_session ).

    "snippet-start:[tex.abapv1.detect_document_text]

    "Detects text in the input document."
    "Amazon Textract can detect lines of text and the words that make up a line of text."
    "The input document must be in one of the following image formats: JPEG, PNG, PDF, or TIFF."

    DATA lo_document TYPE REF TO /aws1/cl_texdocument.
    DATA lo_s3object TYPE REF TO /aws1/cl_texs3object.

    "Create an ABAP object for the S3 obejct."
    CREATE OBJECT lo_s3object
      EXPORTING
        iv_bucket = iv_s3bucket
        iv_name   = iv_s3object.

    "Create an ABAP object for the document."
    CREATE OBJECT lo_document EXPORTING io_s3object = lo_s3object.

    "Analyze document stored in S3"
    TRY.
        oo_result = lo_tex->detectdocumenttext( io_document = lo_document ).         "oo_result is returned for testing purpose"
        MESSAGE 'Detect document text completed' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex .
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex .
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex .
        MESSAGE 'The document is too large' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr .
        MESSAGE 'Internal server error' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex .
        MESSAGE 'Request has invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex .
        MESSAGE 'S3 object is invalid' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex .
        MESSAGE 'Provisioned throughput exceeded limit' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex .
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex .
        MESSAGE 'The document is not supported' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.detect_document_text]
  ENDMETHOD.


  METHOD get_document_analysis.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create( lo_session ).

    "snippet-start:[tex.abapv1.get_document_analysis]

    "Gets the results for an Amazon Textract"
    "asynchronous operation that analyzes text in a document."
    TRY.
        oo_result = lo_tex->getdocumentanalysis( iv_jobid = iv_jobid ).    "oo_result is returned for testing purpose"
        MESSAGE 'Document analysis retrieved' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex .
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr .
        MESSAGE 'Internal server error' TYPE 'E'.
      CATCH /aws1/cx_texinvalidjobidex .
        MESSAGE 'Job ID is invalid' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex .
        MESSAGE 'KMS key is invalid' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex .
        MESSAGE 'Request has invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex .
        MESSAGE 'S3 object is invalid' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex .
        MESSAGE 'Provisioned throughput exceeded limit' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex .
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.get_document_analysis]

  ENDMETHOD.


  METHOD start_document_analysis.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create( lo_session ).

    "snippet-start:[tex.abapv1.start_document_analysis]

    "Starts the asynchronous analysis of an input document for relationships"
    "between detected items such as key-value pairs, tables, and selection elements."

    DATA lo_documentlocation TYPE REF TO /aws1/cl_texdocumentlocation.
    DATA lo_s3object TYPE REF TO /aws1/cl_texs3object.
    DATA lo_featuretypes TYPE REF TO /aws1/cl_texfeaturetypes_w.
    DATA lt_featuretypes TYPE /aws1/cl_texfeaturetypes_w=>tt_featuretypes.

    "Create ABAP objects for feature type"
    "add TABLES to return information about the tables"
    "add FORMS to return detected form data."
    "To perform both types of analysis, add TABLES and FORMS to FeatureTypes"

    CREATE OBJECT lo_featuretypes EXPORTING iv_value = 'FORMS'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    CREATE OBJECT lo_featuretypes EXPORTING iv_value = 'TABLES'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    "Create an ABAP object for the S3 obejct."
    CREATE OBJECT lo_s3object
      EXPORTING
        iv_bucket = iv_s3bucket
        iv_name   = iv_s3object.

    "Create an ABAP object for the document."
    CREATE OBJECT lo_documentlocation EXPORTING io_s3object = lo_s3object.

    "Start async document analysis."
    TRY.
        oo_result = lo_tex->startdocumentanalysis(      "oo_result is returned for testing purpose"
      EXPORTING
        io_documentlocation     = lo_documentlocation
        it_featuretypes         = lt_featuretypes
      ).
        MESSAGE 'Document analysis started' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex .
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex .
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex .
        MESSAGE 'The document is too large' TYPE 'E'.
      CATCH /aws1/cx_texidempotentprmmis00 .
        MESSAGE 'Idempotent parameter mismatch exception' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr .
        MESSAGE 'Internal server error' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex .
        MESSAGE 'KMS key is invalid' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex .
        MESSAGE 'Request has invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex .
        MESSAGE 'S3 object is invalid' TYPE 'E'.
      CATCH /aws1/cx_texlimitexceededex .
        MESSAGE 'An Amazon Textract service limit was exceeded.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex .
        MESSAGE 'Provisioned throughput exceeded limit' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex .
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex .
        MESSAGE 'The document is not supported' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.start_document_analysis]
  ENDMETHOD.


  METHOD start_document_text_detection.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create( lo_session ).

    "snippet-start:[tex.abapv1.start_document_text_detection]

    "Starts the asynchronous detection of text in a document."
    "Amazon Textract can detect lines of text and the words that make up a line of text."

    DATA lo_documentlocation TYPE REF TO /aws1/cl_texdocumentlocation.
    DATA lo_s3object TYPE REF TO /aws1/cl_texs3object.

    "Create an ABAP object for the S3 obejct."
    CREATE OBJECT lo_s3object
      EXPORTING
        iv_bucket = iv_s3bucket
        iv_name   = iv_s3object.

    "Create an ABAP object for the document."
    CREATE OBJECT lo_documentlocation
      EXPORTING
        io_s3object = lo_s3object.

    "Start document analysis."
    TRY.
        oo_result = lo_tex->startdocumenttextdetection( io_documentlocation = lo_documentlocation ).                 "oo_result is returned for testing purpose"
        MESSAGE 'Document analysis started' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex .
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex .
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex .
        MESSAGE 'The document is too large' TYPE 'E'.
      CATCH /aws1/cx_texidempotentprmmis00 .
        MESSAGE 'Idempotent parameter mismatch exception' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr .
        MESSAGE 'Internal server error' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex .
        MESSAGE 'KMS key is invalid' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex .
        MESSAGE 'Request has invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex .
        MESSAGE 'S3 object is invalid' TYPE 'E'.
      CATCH /aws1/cx_texlimitexceededex .
        MESSAGE 'An Amazon Textract service limit was exceeded.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex .
        MESSAGE 'Provisioned throughput exceeded limit' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex .
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex .
        MESSAGE 'The document is not supported' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.start_document_text_detection]
  ENDMETHOD.
ENDCLASS.
