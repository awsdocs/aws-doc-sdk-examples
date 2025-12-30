" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_cpd_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS detect_dominant_language
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectdominantla01 .

    METHODS detect_entities
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
        !iv_language_code TYPE /aws1/cpdlanguagecode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectentsresponse .

    METHODS detect_key_phrases
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
        !iv_language_code TYPE /aws1/cpdlanguagecode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectkeyphrases01 .

    METHODS detect_pii_entities
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
        !iv_language_code TYPE /aws1/cpdlanguagecode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectpiientsrsp .

    METHODS detect_sentiment
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
        !iv_language_code TYPE /aws1/cpdlanguagecode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectsentimentrsp .

    METHODS detect_syntax
      IMPORTING
        !iv_text TYPE /aws1/cpdstring
        !iv_language_code TYPE /aws1/cpdsyntaxlanguagecode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddetectsyntaxrsp .

    METHODS create_document_classifier
      IMPORTING
        !iv_classifier_name TYPE /aws1/cpdcomprehendarnname
        !iv_language_code TYPE /aws1/cpdlanguagecode
        !iv_training_s3_uri TYPE /aws1/cpds3uri
        !iv_data_access_role_arn TYPE /aws1/cpdiamrolearn
        !iv_mode TYPE /aws1/cpddocclassifiermode
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdcredocclifierrsp .

    METHODS describe_doc_classifier
      IMPORTING
        !iv_classifier_arn TYPE /aws1/cpddocumentclassifierarn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddescrdocclifierrsp .

    METHODS list_document_classifiers
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdlistdocclifiersrsp .

    METHODS delete_doc_classifier
      IMPORTING
        !iv_classifier_arn TYPE /aws1/cpddocumentclassifierarn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddeldocclifierrsp .

    METHODS start_doc_class_job
      IMPORTING
        !iv_job_name TYPE /aws1/cpdjobname
        !iv_classifier_arn TYPE /aws1/cpddocumentclassifierarn
        !iv_input_s3_uri TYPE /aws1/cpds3uri
        !iv_input_format TYPE /aws1/cpdinputformat
        !iv_output_s3_uri TYPE /aws1/cpds3uri
        !iv_data_access_role_arn TYPE /aws1/cpdiamrolearn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdstrtdocclificati01 .

    METHODS describe_doc_class_job
      IMPORTING
        !iv_job_id TYPE /aws1/cpdjobid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddscdocclificatio01 .

    METHODS list_doc_class_jobs
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdlstdocclificatio01 .

    METHODS start_topics_detection_job
      IMPORTING
        !iv_job_name TYPE /aws1/cpdjobname
        !iv_input_s3_uri TYPE /aws1/cpds3uri
        !iv_input_format TYPE /aws1/cpdinputformat
        !iv_output_s3_uri TYPE /aws1/cpds3uri
        !iv_data_access_role_arn TYPE /aws1/cpdiamrolearn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdstrttpcsdetjobrsp .

    METHODS describe_topics_detect_job
      IMPORTING
        !iv_job_id TYPE /aws1/cpdjobid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpddsctopicsdetjobrsp .

    METHODS list_topics_detection_jobs
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cpdlisttpcsdetjobsrsp .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CPD_ACTIONS IMPLEMENTATION.


  METHOD detect_dominant_language.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_dominant_language]
    TRY.
        oo_result = lo_cpd->detectdominantlanguage( iv_text = iv_text ).
        MESSAGE 'Languages detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_dominant_language]

  ENDMETHOD.


  METHOD detect_entities.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_entities]
    TRY.
        oo_result = lo_cpd->detectentities(
          iv_text = iv_text
          iv_languagecode = iv_language_code
        ).
        MESSAGE 'Entities detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdunsuppedlanguageex.
        MESSAGE 'Unsupported language.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_entities]

  ENDMETHOD.


  METHOD detect_key_phrases.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_key_phrases]
    TRY.
        oo_result = lo_cpd->detectkeyphrases(
          iv_text = iv_text
          iv_languagecode = iv_language_code
        ).
        MESSAGE 'Key phrases detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdunsuppedlanguageex.
        MESSAGE 'Unsupported language.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_key_phrases]

  ENDMETHOD.


  METHOD detect_pii_entities.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_pii_entities]
    TRY.
        oo_result = lo_cpd->detectpiientities(
          iv_text = iv_text
          iv_languagecode = iv_language_code
        ).
        MESSAGE 'PII entities detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdunsuppedlanguageex.
        MESSAGE 'Unsupported language.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_pii_entities]

  ENDMETHOD.


  METHOD detect_sentiment.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_sentiment]
    TRY.
        oo_result = lo_cpd->detectsentiment(
          iv_text = iv_text
          iv_languagecode = iv_language_code
        ).
        MESSAGE 'Sentiment detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdunsuppedlanguageex.
        MESSAGE 'Unsupported language.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_sentiment]

  ENDMETHOD.


  METHOD detect_syntax.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.detect_syntax]
    TRY.
        oo_result = lo_cpd->detectsyntax(
          iv_text = iv_text
          iv_languagecode = iv_language_code
        ).
        MESSAGE 'Syntax tokens detected.' TYPE 'I'.
      CATCH /aws1/cx_cpdtextsizelmtexcdex.
        MESSAGE 'Text size exceeds limit.' TYPE 'E'.
      CATCH /aws1/cx_cpdunsuppedlanguageex.
        MESSAGE 'Unsupported language.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.detect_syntax]

  ENDMETHOD.


  METHOD create_document_classifier.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.create_document_classifier]
    TRY.
        oo_result = lo_cpd->createdocumentclassifier(
          iv_documentclassifiername = iv_classifier_name
          iv_languagecode = iv_language_code
          io_inputdataconfig = NEW /aws1/cl_cpddocclifierinpdat00(
            iv_s3uri = iv_training_s3_uri
          )
          iv_dataaccessrolearn = iv_data_access_role_arn
          iv_mode = iv_mode
        ).
        MESSAGE 'Document classifier creation started.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdresrclimitexcdex.
        MESSAGE 'Resource limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanytagsex.
        MESSAGE 'Too many tags.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.create_document_classifier]

  ENDMETHOD.


  METHOD describe_doc_classifier.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.describe_document_classifier]
    TRY.
        oo_result = lo_cpd->describedocumentclassifier(
          iv_documentclassifierarn = iv_classifier_arn
        ).
        MESSAGE 'Document classifier described.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.describe_document_classifier]

  ENDMETHOD.


  METHOD list_document_classifiers.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.list_document_classifiers]
    TRY.
        oo_result = lo_cpd->listdocumentclassifiers( ).
        MESSAGE 'Document classifiers listed.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidfilterex.
        MESSAGE 'Invalid filter.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.list_document_classifiers]

  ENDMETHOD.


  METHOD delete_doc_classifier.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.delete_document_classifier]
    TRY.
        oo_result = lo_cpd->deletedocumentclassifier(
          iv_documentclassifierarn = iv_classifier_arn
        ).
        MESSAGE 'Document classifier deleted.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'E'.
      CATCH /aws1/cx_cpdresourceinuseex.
        MESSAGE 'Resource in use.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.delete_document_classifier]

  ENDMETHOD.


  METHOD start_doc_class_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.start_document_classification_job]
    TRY.
        oo_result = lo_cpd->startdocclassificationjob(
          iv_jobname = iv_job_name
          iv_documentclassifierarn = iv_classifier_arn
          io_inputdataconfig = NEW /aws1/cl_cpdinputdataconfig(
            iv_s3uri = iv_input_s3_uri
            iv_inputformat = iv_input_format
          )
          io_outputdataconfig = NEW /aws1/cl_cpdoutputdataconfig(
            iv_s3uri = iv_output_s3_uri
          )
          iv_dataaccessrolearn = iv_data_access_role_arn
        ).
        MESSAGE 'Document classification job started.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'E'.
      CATCH /aws1/cx_cpdresourceunavailex.
        MESSAGE 'Resource unavailable.' TYPE 'E'.
      CATCH /aws1/cx_cpdkmskeyvalidationex.
        MESSAGE 'KMS key validation error.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanytagsex.
        MESSAGE 'Too many tags.' TYPE 'E'.
      CATCH /aws1/cx_cpdresrclimitexcdex.
        MESSAGE 'Resource limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.start_document_classification_job]

  ENDMETHOD.


  METHOD describe_doc_class_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.describe_document_classification_job]
    TRY.
        oo_result = lo_cpd->describedocclassificationjob(
          iv_jobid = iv_job_id
        ).
        MESSAGE 'Document classification job described.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdjobnotfoundex.
        MESSAGE 'Job not found.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.describe_document_classification_job]

  ENDMETHOD.


  METHOD list_doc_class_jobs.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.list_document_classification_jobs]
    TRY.
        oo_result = lo_cpd->listdocclassificationjobs( ).
        MESSAGE 'Document classification jobs listed.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidfilterex.
        MESSAGE 'Invalid filter.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.list_document_classification_jobs]

  ENDMETHOD.


  METHOD start_topics_detection_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.start_topics_detection_job]
    TRY.
        oo_result = lo_cpd->starttopicsdetectionjob(
          iv_jobname = iv_job_name
          io_inputdataconfig = NEW /aws1/cl_cpdinputdataconfig(
            iv_s3uri = iv_input_s3_uri
            iv_inputformat = iv_input_format
          )
          io_outputdataconfig = NEW /aws1/cl_cpdoutputdataconfig(
            iv_s3uri = iv_output_s3_uri
          )
          iv_dataaccessrolearn = iv_data_access_role_arn
        ).
        MESSAGE 'Topics detection job started.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdkmskeyvalidationex.
        MESSAGE 'KMS key validation error.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanytagsex.
        MESSAGE 'Too many tags.' TYPE 'E'.
      CATCH /aws1/cx_cpdresrclimitexcdex.
        MESSAGE 'Resource limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.start_topics_detection_job]

  ENDMETHOD.


  METHOD describe_topics_detect_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.describe_topics_detection_job]
    TRY.
        oo_result = lo_cpd->describetopicsdetectionjob(
          iv_jobid = iv_job_id
        ).
        MESSAGE 'Topics detection job described.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdjobnotfoundex.
        MESSAGE 'Job not found.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.describe_topics_detection_job]

  ENDMETHOD.


  METHOD list_topics_detection_jobs.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cpd) = /aws1/cl_cpd_factory=>create( lo_session ).

    " snippet-start:[cpd.abapv1.list_topics_detection_jobs]
    TRY.
        oo_result = lo_cpd->listtopicsdetectionjobs( ).
        MESSAGE 'Topics detection jobs listed.' TYPE 'I'.
      CATCH /aws1/cx_cpdinvalidrequestex.
        MESSAGE 'Invalid request.' TYPE 'E'.
      CATCH /aws1/cx_cpdtoomanyrequestsex.
        MESSAGE 'Too many requests.' TYPE 'E'.
      CATCH /aws1/cx_cpdinvalidfilterex.
        MESSAGE 'Invalid filter.' TYPE 'E'.
      CATCH /aws1/cx_cpdinternalserverex.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cpd.abapv1.list_topics_detection_jobs]

  ENDMETHOD.
ENDCLASS.