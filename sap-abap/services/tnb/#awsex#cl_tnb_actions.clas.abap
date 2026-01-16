" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_tnb_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS start_transcription_job
      IMPORTING
        !iv_job_name              TYPE /aws1/tnbtranscriptionjobname
        !iv_media_uri             TYPE /aws1/tnburi
        !iv_media_format          TYPE /aws1/tnbmediaformat
        !iv_language_code         TYPE /aws1/tnblanguagecode
        !iv_vocabulary_name       TYPE /aws1/tnbvocabularyname OPTIONAL
      RETURNING
        VALUE(oo_result)          TYPE REF TO /aws1/cl_tnbstrttranscriptio01
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_transcription_job
      IMPORTING
        !iv_job_name     TYPE /aws1/tnbtranscriptionjobname
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_tnbgettranscription01
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_transcription_jobs
      IMPORTING
        !iv_job_filter   TYPE /aws1/tnbtranscriptionjobname OPTIONAL
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_tnblsttranscription01
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_transcription_job
      IMPORTING
        !iv_job_name TYPE /aws1/tnbtranscriptionjobname
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_vocabulary
      IMPORTING
        !iv_vocabulary_name TYPE /aws1/tnbvocabularyname
        !iv_language_code   TYPE /aws1/tnblanguagecode
        !it_phrases         TYPE /aws1/cl_tnbphrases_w=>tt_phrases OPTIONAL
        !iv_vocab_file_uri  TYPE /aws1/tnburi OPTIONAL
      RETURNING
        VALUE(oo_result)    TYPE REF TO /aws1/cl_tnbcrevocabularyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_vocabulary
      IMPORTING
        !iv_vocabulary_name TYPE /aws1/tnbvocabularyname
      RETURNING
        VALUE(oo_result)    TYPE REF TO /aws1/cl_tnbgetvocabularyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_vocabularies
      IMPORTING
        !iv_vocab_filter TYPE /aws1/tnbvocabularyname OPTIONAL
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_tnblstvocabulariesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_vocabulary
      IMPORTING
        !iv_vocabulary_name TYPE /aws1/tnbvocabularyname
        !iv_language_code   TYPE /aws1/tnblanguagecode
        !it_phrases         TYPE /aws1/cl_tnbphrases_w=>tt_phrases OPTIONAL
        !iv_vocab_file_uri  TYPE /aws1/tnburi OPTIONAL
      RETURNING
        VALUE(oo_result)    TYPE REF TO /aws1/cl_tnbupdvocabularyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_vocabulary
      IMPORTING
        !iv_vocabulary_name TYPE /aws1/tnbvocabularyname
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_TNB_ACTIONS IMPLEMENTATION.


  METHOD start_transcription_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.start_transcription_job]
    TRY.
        DATA(lo_media) = NEW /aws1/cl_tnbmedia( iv_mediafileuri = iv_media_uri ).
        DATA(lo_settings) = NEW /aws1/cl_tnbsettings( ).
        IF iv_vocabulary_name IS NOT INITIAL.
          lo_settings = NEW /aws1/cl_tnbsettings( iv_vocabularyname = iv_vocabulary_name ).
        ENDIF.

        oo_result = lo_tnb->starttranscriptionjob(
          iv_transcriptionjobname = iv_job_name
          io_media = lo_media
          iv_mediaformat = iv_media_format
          iv_languagecode = iv_language_code
          io_settings = lo_settings ).

        MESSAGE 'Transcription job started.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnblimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex TYPE 'I'.
        RAISE EXCEPTION lo_limit_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
      CATCH /aws1/cx_tnbconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex TYPE 'I'.
        RAISE EXCEPTION lo_conflict_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.start_transcription_job]
  ENDMETHOD.


  METHOD get_transcription_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.get_transcription_job]
    TRY.
        oo_result = lo_tnb->gettranscriptionjob( iv_job_name ).
        DATA(lo_job) = oo_result->get_transcriptionjob( ).
        MESSAGE 'Retrieved transcription job details.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnbnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex TYPE 'I'.
        RAISE EXCEPTION lo_not_found_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.get_transcription_job]
  ENDMETHOD.


  METHOD list_transcription_jobs.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.list_transcription_jobs]
    TRY.
        IF iv_job_filter IS NOT INITIAL.
          oo_result = lo_tnb->listtranscriptionjobs( iv_jobnamecontains = iv_job_filter ).
        ELSE.
          oo_result = lo_tnb->listtranscriptionjobs( ).
        ENDIF.
        MESSAGE 'Retrieved transcription jobs list.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.list_transcription_jobs]
  ENDMETHOD.


  METHOD delete_transcription_job.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.delete_transcription_job]
    TRY.
        lo_tnb->deletetranscriptionjob( iv_job_name ).
        MESSAGE 'Transcription job deleted.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnblimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex TYPE 'I'.
        RAISE EXCEPTION lo_limit_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.delete_transcription_job]
  ENDMETHOD.


  METHOD create_vocabulary.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.create_vocabulary]
    TRY.
        IF it_phrases IS NOT INITIAL.
          oo_result = lo_tnb->createvocabulary(
            iv_vocabularyname = iv_vocabulary_name
            iv_languagecode = iv_language_code
            it_phrases = it_phrases ).
        ELSEIF iv_vocab_file_uri IS NOT INITIAL.
          oo_result = lo_tnb->createvocabulary(
            iv_vocabularyname = iv_vocabulary_name
            iv_languagecode = iv_language_code
            iv_vocabularyfileuri = iv_vocab_file_uri ).
        ENDIF.
        MESSAGE 'Custom vocabulary created.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnblimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex TYPE 'I'.
        RAISE EXCEPTION lo_limit_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
      CATCH /aws1/cx_tnbconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex TYPE 'I'.
        RAISE EXCEPTION lo_conflict_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.create_vocabulary]
  ENDMETHOD.


  METHOD get_vocabulary.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.get_vocabulary]
    TRY.
        oo_result = lo_tnb->getvocabulary( iv_vocabulary_name ).
        MESSAGE 'Retrieved vocabulary details.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnbnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex TYPE 'I'.
        RAISE EXCEPTION lo_not_found_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.get_vocabulary]
  ENDMETHOD.


  METHOD list_vocabularies.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.list_vocabularies]
    TRY.
        IF iv_vocab_filter IS NOT INITIAL.
          oo_result = lo_tnb->listvocabularies( iv_namecontains = iv_vocab_filter ).
        ELSE.
          oo_result = lo_tnb->listvocabularies( ).
        ENDIF.
        MESSAGE 'Retrieved vocabularies list.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.list_vocabularies]
  ENDMETHOD.


  METHOD update_vocabulary.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.update_vocabulary]
    TRY.
        IF it_phrases IS NOT INITIAL.
          oo_result = lo_tnb->updatevocabulary(
            iv_vocabularyname = iv_vocabulary_name
            iv_languagecode = iv_language_code
            it_phrases = it_phrases ).
        ELSEIF iv_vocab_file_uri IS NOT INITIAL.
          oo_result = lo_tnb->updatevocabulary(
            iv_vocabularyname = iv_vocabulary_name
            iv_languagecode = iv_language_code
            iv_vocabularyfileuri = iv_vocab_file_uri ).
        ENDIF.
        MESSAGE 'Vocabulary updated.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
      CATCH /aws1/cx_tnblimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex TYPE 'I'.
        RAISE EXCEPTION lo_limit_ex.
      CATCH /aws1/cx_tnbnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex TYPE 'I'.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
      CATCH /aws1/cx_tnbconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex TYPE 'I'.
        RAISE EXCEPTION lo_conflict_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.update_vocabulary]
  ENDMETHOD.


  METHOD delete_vocabulary.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_tnb) = /aws1/cl_tnb_factory=>create( lo_session ).

    " snippet-start:[tnb.abapv1.delete_vocabulary]
    TRY.
        lo_tnb->deletevocabulary( iv_vocabulary_name ).
        MESSAGE 'Vocabulary deleted.' TYPE 'I'.
      CATCH /aws1/cx_tnbbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex TYPE 'I'.
      CATCH /aws1/cx_tnblimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex TYPE 'I'.
        RAISE EXCEPTION lo_limit_ex.
      CATCH /aws1/cx_tnbnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex TYPE 'I'.
      CATCH /aws1/cx_tnbinternalfailureex INTO DATA(lo_internal_ex).
        MESSAGE lo_internal_ex TYPE 'I'.
        RAISE EXCEPTION lo_internal_ex.
    ENDTRY.
    " snippet-end:[tnb.abapv1.delete_vocabulary]
  ENDMETHOD.
ENDCLASS.
