CLASS /awsex/cl_tex_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS analyze_document
      IMPORTING
                !iv_s3object     TYPE /aws1/texs3objectname OPTIONAL
                !iv_s3bucket     TYPE /aws1/texs3bucket OPTIONAL
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_texanalyzedocresponse
      RAISING   /aws1/cx_rt_generic.
    METHODS detect_document_text
      IMPORTING
                !iv_s3object     TYPE /aws1/texs3objectname
                !iv_s3bucket     TYPE /aws1/texs3bucket
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_texdetectdoctextrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_document_analysis
      IMPORTING
                !iv_jobid        TYPE /aws1/texjobid
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_texgetdocalyresponse
      RAISING   /aws1/cx_rt_generic.
    METHODS start_document_analysis
      IMPORTING
                !iv_s3object     TYPE /aws1/texs3objectname
                !iv_s3bucket     TYPE /aws1/texs3bucket
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_texstartdocalyrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS start_document_text_detection
      IMPORTING
                !iv_s3object     TYPE /aws1/texs3objectname
                !iv_s3bucket     TYPE /aws1/texs3bucket
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_texstartdoctextdetrsp
      RAISING   /aws1/cx_rt_generic.
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_TEX_ACTIONS IMPLEMENTATION.


  METHOD analyze_document.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).

    "Textract requires endpoint region to be same as the bucket region.
    "Retrieve the region name defined as a logical resource in SDK configuration.
    CONSTANTS cv_lbucket TYPE string VALUE 'ZEX_TEX_BUCKET_REGION'.
    DATA lv_bucket_region TYPE /aws1/rt_region_id.
    lv_bucket_region = lo_session->resolve_lresource( cv_lbucket ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create(
      io_session = lo_session
      iv_region = lv_bucket_region ).
    "snippet-start:[tex.abapv1.analyze_document]

    "Detects text and additional elements, such as forms or tables,"
    "in a local image file or from in-memory byte data."
    "The image must be in PNG or JPG format."


    "Create ABAP objects for feature type."
    "Add TABLES to return information about the tables."
    "Add FORMS to return detected form data."
    "To perform both types of analysis, add TABLES and FORMS to FeatureTypes."

    DATA(lt_featuretypes) = VALUE /aws1/cl_texfeaturetypes_w=>tt_featuretypes(
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'FORMS' ) )
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'TABLES' ) ) ).

    "Create an ABAP object for the Amazon Simple Storage Service (Amazon S3) object."
    DATA(lo_s3object) = NEW /aws1/cl_texs3object( iv_bucket = iv_s3bucket
      iv_name   = iv_s3object ).

    "Create an ABAP object for the document."
    DATA(lo_document) = NEW /aws1/cl_texdocument( io_s3object = lo_s3object ).

    "Analyze document stored in Amazon S3."
    TRY.
        oo_result = lo_tex->analyzedocument(      "oo_result is returned for testing purposes."
          io_document        = lo_document
          it_featuretypes    = lt_featuretypes ).
        LOOP AT oo_result->get_blocks( ) INTO DATA(lo_block).
          IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
            MESSAGE 'Found text in the doc: ' && lo_block->get_text( ) TYPE 'I'.
          ENDIF.
        ENDLOOP.
        MESSAGE 'Analyze document completed.' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex.
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex.
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex.
        MESSAGE 'The document is too large.' TYPE 'E'.
      CATCH /aws1/cx_texhlquotaexceededex.
        MESSAGE 'Human loop quota exceeded.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr.
        MESSAGE 'Internal server error.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex.
        MESSAGE 'Request has non-valid parameters.' TYPE 'E'.

      CATCH /aws1/cx_texinvalids3objectex.
        MESSAGE 'Amazon S3 object is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex.
        MESSAGE 'Provisioned throughput exceeded limit.' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex.
        MESSAGE 'The request processing exceeded the limit.' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex.
        MESSAGE 'The document is not supported.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.analyze_document]
  ENDMETHOD.


  METHOD detect_document_text.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).

    "Textract requires endpoint region to be same as the bucket region.
    "Retrieve the region name defined as a logical resource in SDK configuration.
    CONSTANTS cv_lbucket TYPE string VALUE 'ZEX_TEX_BUCKET_REGION'.
    DATA lv_bucket_region TYPE /aws1/rt_region_id.
    lv_bucket_region = lo_session->resolve_lresource( cv_lbucket ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create(
      io_session = lo_session
      iv_region = lv_bucket_region ).

    "snippet-start:[tex.abapv1.detect_document_text]

    "Detects text in the input document."
    "Amazon Textract can detect lines of text and the words that make up a line of text."
    "The input document must be in one of the following image formats: JPEG, PNG, PDF, or TIFF."

    "Create an ABAP object for the Amazon S3 object."
    DATA(lo_s3object) = NEW /aws1/cl_texs3object( iv_bucket = iv_s3bucket
      iv_name   = iv_s3object ).

    "Create an ABAP object for the document."
    DATA(lo_document) = NEW /aws1/cl_texdocument( io_s3object = lo_s3object ).
    "Analyze document stored in Amazon S3."
    TRY.
        oo_result = lo_tex->detectdocumenttext( io_document = lo_document ).         "oo_result is returned for testing purposes."
        LOOP AT oo_result->get_blocks( ) INTO DATA(lo_block).
          IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
            MESSAGE 'Found text in the doc: ' && lo_block->get_text( ) TYPE 'I'.
          ENDIF.
        ENDLOOP.
        DATA(lo_metadata) = oo_result->get_documentmetadata( ).
        MESSAGE 'The number of pages in the document is ' && lo_metadata->ask_pages( ) TYPE 'I'.
        MESSAGE 'Detect document text completed.' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex.
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex.
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex.
        MESSAGE 'The document is too large.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr.
        MESSAGE 'Internal server error.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex.
        MESSAGE 'Request has non-valid parameters.' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex.
        MESSAGE 'Amazon S3 object is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex.
        MESSAGE 'Provisioned throughput exceeded limit.' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex.
        MESSAGE 'The request processing exceeded the limit' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex.
        MESSAGE 'The document is not supported.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.detect_document_text]
  ENDMETHOD.


  METHOD get_document_analysis.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).

    "Textract requires endpoint region to be same as the bucket region.
    "Retrieve the region name defined as a logical resource in SDK configuration.
    CONSTANTS cv_lbucket TYPE string VALUE 'ZEX_TEX_BUCKET_REGION'.
    DATA lv_bucket_region TYPE /aws1/rt_region_id.
    lv_bucket_region = lo_session->resolve_lresource( cv_lbucket ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create(
      io_session = lo_session
      iv_region = lv_bucket_region ).

    "snippet-start:[tex.abapv1.get_document_analysis]

    "Gets the results for an Amazon Textract"
    "asynchronous operation that analyzes text in a document."
    TRY.
        oo_result = lo_tex->getdocumentanalysis( iv_jobid = iv_jobid ).    "oo_result is returned for testing purposes."
        WHILE oo_result->get_jobstatus( ) <> 'SUCCEEDED'.
          IF sy-index = 10.
            EXIT.               "Maximum 300 seconds.
          ENDIF.
          WAIT UP TO 30 SECONDS.
          oo_result = lo_tex->getdocumentanalysis( iv_jobid = iv_jobid ).
        ENDWHILE.

        DATA(lt_blocks) = oo_result->get_blocks( ).
        LOOP AT lt_blocks INTO DATA(lo_block).
          IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
            MESSAGE 'Found text in the doc: ' && lo_block->get_text( ) TYPE 'I'.
          ENDIF.
        ENDLOOP.
        MESSAGE 'Document analysis retrieved.' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex.
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr.
        MESSAGE 'Internal server error.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidjobidex.
        MESSAGE 'Job ID is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex.
        MESSAGE 'AWS KMS key is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex.
        MESSAGE 'Request has non-valid parameters.' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex.
        MESSAGE 'Amazon S3 object is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex.
        MESSAGE 'Provisioned throughput exceeded limit.' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex.
        MESSAGE 'The request processing exceeded the limit.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.get_document_analysis]

  ENDMETHOD.


  METHOD start_document_analysis.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).

    "Textract requires endpoint region to be same as the bucket region.
    "Retrieve the region name defined as a logical resource in SDK configuration.
    CONSTANTS cv_lbucket TYPE string VALUE 'ZEX_TEX_BUCKET_REGION'.
    DATA lv_bucket_region TYPE /aws1/rt_region_id.
    lv_bucket_region = lo_session->resolve_lresource( cv_lbucket ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create(
      io_session = lo_session
      iv_region = lv_bucket_region ).

    "snippet-start:[tex.abapv1.start_document_analysis]

    "Starts the asynchronous analysis of an input document for relationships"
    "between detected items such as key-value pairs, tables, and selection elements."

    "Create ABAP objects for feature type."
    "Add TABLES to return information about the tables."
    "Add FORMS to return detected form data."
    "To perform both types of analysis, add TABLES and FORMS to FeatureTypes."

    DATA(lt_featuretypes) = VALUE /aws1/cl_texfeaturetypes_w=>tt_featuretypes(
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'FORMS' ) )
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'TABLES' ) ) ).
    "Create an ABAP object for the Amazon S3 object."
    DATA(lo_s3object) = NEW /aws1/cl_texs3object( iv_bucket = iv_s3bucket
      iv_name   = iv_s3object ).
    "Create an ABAP object for the document."
    DATA(lo_documentlocation) = NEW /aws1/cl_texdocumentlocation( io_s3object = lo_s3object ).

    "Start async document analysis."
    TRY.
        oo_result = lo_tex->startdocumentanalysis(      "oo_result is returned for testing purposes."
          io_documentlocation     = lo_documentlocation
          it_featuretypes         = lt_featuretypes ).
        DATA(lv_jobid) = oo_result->get_jobid( ).

        MESSAGE 'Document analysis started.' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex.
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex.
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex.
        MESSAGE 'The document is too large.' TYPE 'E'.
      CATCH /aws1/cx_texidempotentprmmis00.
        MESSAGE 'Idempotent parameter mismatch exception.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr.
        MESSAGE 'Internal server error.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex.
        MESSAGE 'AWS KMS key is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex.
        MESSAGE 'Request has non-valid parameters.' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex.
        MESSAGE 'Amazon S3 object is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texlimitexceededex.
        MESSAGE 'An Amazon Textract service limit was exceeded.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex.
        MESSAGE 'Provisioned throughput exceeded limit.' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex.
        MESSAGE 'The request processing exceeded the limit.' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex.
        MESSAGE 'The document is not supported.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.start_document_analysis]
  ENDMETHOD.


  METHOD start_document_text_detection.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).

    "Textract requires endpoint region to be same as the bucket region.
    "Retrieve the region name defined as a logical resource in SDK configuration.
    CONSTANTS cv_lbucket TYPE string VALUE 'ZEX_TEX_BUCKET_REGION'.
    DATA lv_bucket_region TYPE /aws1/rt_region_id.
    lv_bucket_region = lo_session->resolve_lresource( cv_lbucket ).
    DATA(lo_tex) = /aws1/cl_tex_factory=>create(
      io_session = lo_session
      iv_region = lv_bucket_region ).

    "snippet-start:[tex.abapv1.start_document_text_detection]

    "Starts the asynchronous detection of text in a document."
    "Amazon Textract can detect lines of text and the words that make up a line of text."

    "Create an ABAP object for the Amazon S3 object."
    DATA(lo_s3object) = NEW /aws1/cl_texs3object( iv_bucket = iv_s3bucket
      iv_name   = iv_s3object ).
    "Create an ABAP object for the document."
    DATA(lo_documentlocation) = NEW /aws1/cl_texdocumentlocation( io_s3object = lo_s3object ).
    "Start document analysis."
    TRY.
        oo_result = lo_tex->startdocumenttextdetection( io_documentlocation = lo_documentlocation ).
        DATA(lv_jobid) = oo_result->get_jobid( ).             "oo_result is returned for testing purposes."
        MESSAGE 'Document analysis started.' TYPE 'I'.
      CATCH /aws1/cx_texaccessdeniedex.
        MESSAGE 'You do not have permission to perform this action.' TYPE 'E'.
      CATCH /aws1/cx_texbaddocumentex.
        MESSAGE 'Amazon Textract is not able to read the document.' TYPE 'E'.
      CATCH /aws1/cx_texdocumenttoolargeex.
        MESSAGE 'The document is too large.' TYPE 'E'.
      CATCH /aws1/cx_texidempotentprmmis00.
        MESSAGE 'Idempotent parameter mismatch exception.' TYPE 'E'.
      CATCH /aws1/cx_texinternalservererr.
        MESSAGE 'Internal server error.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidkmskeyex.
        MESSAGE 'AWS KMS key is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texinvalidparameterex.
        MESSAGE 'Request has non-valid parameters.' TYPE 'E'.
      CATCH /aws1/cx_texinvalids3objectex.
        MESSAGE 'Amazon S3 object is not valid.' TYPE 'E'.
      CATCH /aws1/cx_texlimitexceededex.
        MESSAGE 'An Amazon Textract service limit was exceeded.' TYPE 'E'.
      CATCH /aws1/cx_texprovthruputexcdex.
        MESSAGE 'Provisioned throughput exceeded limit.' TYPE 'E'.
      CATCH /aws1/cx_texthrottlingex.
        MESSAGE 'The request processing exceeded the limit.' TYPE 'E'.
      CATCH /aws1/cx_texunsupporteddocex.
        MESSAGE 'The document is not supported.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[tex.abapv1.start_document_text_detection]
  ENDMETHOD.
ENDCLASS.
