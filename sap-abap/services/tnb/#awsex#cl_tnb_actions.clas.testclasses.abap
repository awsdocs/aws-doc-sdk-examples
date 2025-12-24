" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_tnb_actions DEFINITION DEFERRED.
CLASS /awsex/cl_tnb_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_tnb_actions.

CLASS ltc_awsex_cl_tnb_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_tnb TYPE REF TO /aws1/if_tnb.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_tnb_actions TYPE REF TO /awsex/cl_tnb_actions.

    CLASS-DATA av_media_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_media_key TYPE /aws1/s3_objectkey.
    CLASS-DATA av_media_uri TYPE /aws1/tnburi.
    CLASS-DATA av_output_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_iam_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_iam_role_arn TYPE /aws1/iamarntype.

    METHODS:
      start_transcription_job FOR TESTING RAISING /aws1/cx_rt_generic,
      get_transcription_job FOR TESTING RAISING /aws1/cx_rt_generic,
      list_transcription_jobs FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_transcription_job FOR TESTING RAISING /aws1/cx_rt_generic,
      create_vocabulary FOR TESTING RAISING /aws1/cx_rt_generic,
      get_vocabulary FOR TESTING RAISING /aws1/cx_rt_generic,
      list_vocabularies FOR TESTING RAISING /aws1/cx_rt_generic,
      update_vocabulary FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_vocabulary FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_job_completion
      IMPORTING
        iv_job_name        TYPE /aws1/tnbtranscriptionjobname
        iv_max_wait_sec    TYPE i DEFAULT 300
      RETURNING
        VALUE(rv_status)   TYPE /aws1/tnbtranscriptionjobstat
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_vocab_ready
      IMPORTING
        iv_vocab_name    TYPE /aws1/tnbvocabularyname
        iv_max_wait_sec  TYPE i DEFAULT 300
      RETURNING
        VALUE(rv_state)  TYPE /aws1/tnbvocabularystate
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_tnb_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_trust_policy TYPE string.
    DATA lv_policy_doc TYPE string.
    DATA lv_policy_arn TYPE /aws1/iamarntype.
    DATA lv_uuid_string TYPE string.
    DATA lt_iam_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.
    DATA lt_s3_tags TYPE /aws1/cl_s3_tag=>tt_tagset.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_tnb = /aws1/cl_tnb_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_tnb_actions = NEW /awsex/cl_tnb_actions( ).

    " Generate unique names using utility function
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    " Get account ID for bucket names
    DATA(lv_acct) = ao_session->get_account_id( ).
    av_media_bucket = |tnb-media-{ lv_acct }-{ lv_uuid_string+0(8) }|.
    av_output_bucket = |tnb-output-{ lv_acct }-{ lv_uuid_string+0(8) }|.
    av_media_key = 'test-audio.mp3'.
    av_iam_role_name = |tnb-test-role-{ lv_uuid_string+0(10) }|.

    " Create IAM tags for test resources
    lt_iam_tags = VALUE #(
      ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
    ).

    " Create S3 tags for test resources
    lt_s3_tags = VALUE #(
      ( NEW /aws1/cl_s3_tag( iv_key = 'TestType' iv_value = 'convert_test' ) )
    ).

    " Create IAM role for Transcribe with necessary permissions
    lv_trust_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"transcribe.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.

    TRY.
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_iam_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          iv_description = 'Test role for Transcribe unit tests'
          it_tags = lt_iam_tags ).
        av_iam_role_arn = lo_role_result->get_role( )->get_arn( ).
        MESSAGE |Created IAM role: { av_iam_role_name }| TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        " Role exists, get its ARN
        DATA(lo_get_role) = ao_iam->getrole( iv_rolename = av_iam_role_name ).
        av_iam_role_arn = lo_get_role->get_role( )->get_arn( ).
        MESSAGE |IAM role already exists: { av_iam_role_name }| TYPE 'I'.
    ENDTRY.

    WAIT UP TO 3 SECONDS.

    " Create and attach policy for S3 access
    lv_policy_doc = '{"Version":"2012-10-17","Statement":[' &&
                    '{"Effect":"Allow","Action":["s3:GetObject","s3:PutObject","s3:ListBucket"],' &&
                    '"Resource":["arn:aws:s3:::' && av_media_bucket && '/*","arn:aws:s3:::' && av_media_bucket && '",' &&
                    '"arn:aws:s3:::' && av_output_bucket && '/*","arn:aws:s3:::' && av_output_bucket && '"]}' &&
                    ']}'.

    TRY.
        DATA(lo_policy_result) = ao_iam->createpolicy(
          iv_policyname = |tnb-s3-policy-{ lv_uuid_string+0(10) }|
          iv_policydocument = lv_policy_doc
          iv_description = 'S3 access for Transcribe test role'
          it_tags = lt_iam_tags ).
        lv_policy_arn = lo_policy_result->get_policy( )->get_arn( ).
        MESSAGE |Created IAM policy| TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        " Policy exists, get its ARN
        DATA(lo_list_policies) = ao_iam->listpolicies( iv_scope = 'Local' ).
        LOOP AT lo_list_policies->get_policies( ) INTO DATA(lo_policy).
          IF lo_policy->get_policyname( ) CS 'tnb-s3-policy'.
            lv_policy_arn = lo_policy->get_arn( ).
            EXIT.
          ENDIF.
        ENDLOOP.
    ENDTRY.

    " Attach policy to role
    IF lv_policy_arn IS NOT INITIAL.
      TRY.
          ao_iam->attachrolepolicy(
            iv_rolename = av_iam_role_name
            iv_policyarn = lv_policy_arn ).
          MESSAGE |Attached policy to role| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          " Policy might already be attached
      ENDTRY.
    ENDIF.

    " Wait for IAM propagation
    WAIT UP TO 10 SECONDS.

    " Create S3 buckets for media files and output
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_media_bucket
          io_s3 = ao_s3
          io_session = ao_session ).

        " Tag the bucket
        ao_s3->putbuckettagging(
          iv_bucket = av_media_bucket
          io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_s3_tags ) ).

        MESSAGE |Created media bucket: { av_media_bucket }| TYPE 'I'.
      CATCH /aws1/cx_s3_bktalrdyownedbyyou /aws1/cx_s3_bucketalrdyexists.
        MESSAGE |Media bucket already exists: { av_media_bucket }| TYPE 'I'.
    ENDTRY.

    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_output_bucket
          io_s3 = ao_s3
          io_session = ao_session ).

        " Tag the bucket
        ao_s3->putbuckettagging(
          iv_bucket = av_output_bucket
          io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_s3_tags ) ).

        MESSAGE |Created output bucket: { av_output_bucket }| TYPE 'I'.
      CATCH /aws1/cx_s3_bktalrdyownedbyyou /aws1/cx_s3_bucketalrdyexists.
        MESSAGE |Output bucket already exists: { av_output_bucket }| TYPE 'I'.
    ENDTRY.

    " Upload a minimal test audio file
    " This creates a minimal valid MP3 file with ID3v2 header
    DATA lv_test_content TYPE xstring.
    " ID3v2 header + minimal MP3 frame
    lv_test_content = '4944330400000000000B54495432000000030000006162'.

    TRY.
        " Build tagging header string (format: key1=value1&key2=value2)
        DATA lv_tagging_header TYPE /aws1/s3_taggingheader.
        lv_tagging_header = 'TestType=convert_test'.
        
        ao_s3->putobject(
          iv_bucket = av_media_bucket
          iv_key = av_media_key
          iv_body = lv_test_content
          iv_tagging = lv_tagging_header ).

        av_media_uri = |s3://{ av_media_bucket }/{ av_media_key }|.
        MESSAGE |Uploaded test media file| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lx_upload_error).
        cl_abap_unit_assert=>fail( msg = |Failed to upload test media file: { lx_upload_error->get_text( ) }| ).
    ENDTRY.

    " Verify all shared resources were created
    IF av_media_bucket IS INITIAL OR av_output_bucket IS INITIAL OR
       av_iam_role_arn IS INITIAL OR av_media_uri IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create shared test resources in class_setup' ).
    ENDIF.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: Resources are tagged with 'convert_test' for manual cleanup if needed

    " Clean up IAM resources
    IF av_iam_role_name IS NOT INITIAL.
      " Detach all policies from role
      TRY.
          DATA(lo_policies) = ao_iam->listattachedrolepolicies( iv_rolename = av_iam_role_name ).
          LOOP AT lo_policies->get_attachedpolicies( ) INTO DATA(lo_policy).
            TRY.
                ao_iam->detachrolepolicy(
                  iv_rolename = av_iam_role_name
                  iv_policyarn = lo_policy->get_policyarn( ) ).

                " Delete the policy if it's a test policy
                IF lo_policy->get_policyname( ) CS 'tnb-s3-policy'.
                  " Delete all non-default policy versions first
                  TRY.
                      DATA(lo_versions) = ao_iam->listpolicyversions( iv_policyarn = lo_policy->get_policyarn( ) ).
                      LOOP AT lo_versions->get_versions( ) INTO DATA(lo_version).
                        IF lo_version->get_isdefaultversion( ) = abap_false.
                          TRY.
                              ao_iam->deletepolicyversion(
                                iv_policyarn = lo_policy->get_policyarn( )
                                iv_versionid = lo_version->get_versionid( ) ).
                            CATCH /aws1/cx_rt_generic.
                          ENDTRY.
                        ENDIF.
                      ENDLOOP.
                    CATCH /aws1/cx_rt_generic.
                  ENDTRY.

                  " Delete the policy
                  TRY.
                      ao_iam->deletepolicy( iv_policyarn = lo_policy->get_policyarn( ) ).
                    CATCH /aws1/cx_rt_generic.
                  ENDTRY.
                ENDIF.
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      " Delete inline policies
      TRY.
          DATA(lo_inline_policies) = ao_iam->listrolepolicies( iv_rolename = av_iam_role_name ).
          LOOP AT lo_inline_policies->get_policynames( ) INTO DATA(lo_policy_name_wrapper).
            TRY.
                ao_iam->deleterolepolicy(
                  iv_rolename = av_iam_role_name
                  iv_policyname = lo_policy_name_wrapper->get_value( ) ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      " Delete the role
      TRY.
          ao_iam->deleterole( iv_rolename = av_iam_role_name ).
          MESSAGE |Deleted IAM role: { av_iam_role_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          MESSAGE |Could not delete IAM role: { av_iam_role_name }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Note: S3 buckets may contain transcription outputs
    " We won't delete them automatically due to potential long cleanup
    " They are tagged with 'convert_test' for manual cleanup
    MESSAGE |S3 buckets tagged with convert_test for manual cleanup| TYPE 'I'.

  ENDMETHOD.

  METHOD start_transcription_job.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    " Use unique job name for this test
    DATA(lv_test_job_name) = |test-job-{ lv_uuid_string+0(10) }|.

    " Start transcription job
    DATA(lo_result) = ao_tnb_actions->start_transcription_job(
      iv_job_name = lv_test_job_name
      iv_media_uri = av_media_uri
      iv_media_format = 'mp3'
      iv_language_code = 'en-US' ).

    " Verify job was started
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Start transcription job result should not be initial' ).

    DATA(lo_job) = lo_result->get_transcriptionjob( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_job
      msg = 'Transcription job object should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_job_name
      act = lo_job->get_transcriptionjobname( )
      msg = 'Job name should match' ).

    " Wait for job to complete or fail - must not skip test
    DATA(lv_status) = wait_for_job_completion( lv_test_job_name ).

    IF lv_status <> 'COMPLETED' AND lv_status <> 'FAILED'.
      cl_abap_unit_assert=>fail( msg = |Job did not complete within timeout, status: { lv_status }| ).
    ENDIF.

    " Clean up
    TRY.
        ao_tnb->deletetranscriptionjob( lv_test_job_name ).
      CATCH /aws1/cx_tnbbadrequestex /aws1/cx_tnblimitexceededex.
        " Job might still be processing
    ENDTRY.
  ENDMETHOD.

  METHOD get_transcription_job.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_job_name) = |test-get-{ lv_uuid_string+0(10) }|.

    " Create a job first - must not skip if creation fails
    TRY.
        ao_tnb->starttranscriptionjob(
          iv_transcriptionjobname = lv_test_job_name
          io_media = NEW /aws1/cl_tnbmedia( iv_mediafileuri = av_media_uri )
          iv_mediaformat = 'mp3'
          iv_languagecode = 'en-US' ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create job for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Get the job
    DATA(lo_result) = ao_tnb_actions->get_transcription_job( lv_test_job_name ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get transcription job result should not be initial' ).

    DATA(lo_job) = lo_result->get_transcriptionjob( ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_job_name
      act = lo_job->get_transcriptionjobname( )
      msg = 'Retrieved job name should match' ).

    " Wait for completion before cleanup
    wait_for_job_completion( lv_test_job_name ).

    " Clean up
    TRY.
        ao_tnb->deletetranscriptionjob( lv_test_job_name ).
      CATCH /aws1/cx_tnbbadrequestex /aws1/cx_tnblimitexceededex.
    ENDTRY.
  ENDMETHOD.

  METHOD list_transcription_jobs.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_prefix) = |test-list-{ lv_uuid_string+0(6) }|.
    DATA(lv_test_job_name) = |{ lv_prefix }-job|.

    " Create a test job - must not skip if creation fails
    TRY.
        ao_tnb->starttranscriptionjob(
          iv_transcriptionjobname = lv_test_job_name
          io_media = NEW /aws1/cl_tnbmedia( iv_mediafileuri = av_media_uri )
          iv_mediaformat = 'mp3'
          iv_languagecode = 'en-US' ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create job for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " List jobs with filter
    DATA(lo_result) = ao_tnb_actions->list_transcription_jobs( lv_prefix ).

    " Verify results
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List transcription jobs result should not be initial' ).

    DATA(lt_jobs) = lo_result->get_transcriptionjobsums( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_jobs
      msg = 'Should have at least one job in the list' ).

    " Verify our job is in the list
    DATA lv_found TYPE abap_bool.
    LOOP AT lt_jobs INTO DATA(lo_job_summary).
      IF lo_job_summary->get_transcriptionjobname( ) = lv_test_job_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Job { lv_test_job_name } should be in the list| ).

    " Wait for completion before cleanup
    wait_for_job_completion( lv_test_job_name ).

    " Clean up
    TRY.
        ao_tnb->deletetranscriptionjob( lv_test_job_name ).
      CATCH /aws1/cx_tnbbadrequestex /aws1/cx_tnblimitexceededex.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_transcription_job.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_job_name) = |test-del-{ lv_uuid_string+0(10) }|.

    " Create a job - must not skip if creation fails
    TRY.
        ao_tnb->starttranscriptionjob(
          iv_transcriptionjobname = lv_test_job_name
          io_media = NEW /aws1/cl_tnbmedia( iv_mediafileuri = av_media_uri )
          iv_mediaformat = 'mp3'
          iv_languagecode = 'en-US' ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create job for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Wait for job to complete
    DATA(lv_status) = wait_for_job_completion( lv_test_job_name ).
    IF lv_status <> 'COMPLETED' AND lv_status <> 'FAILED'.
      cl_abap_unit_assert=>fail( msg = |Job must complete before deletion, status: { lv_status }| ).
    ENDIF.

    " Delete the job
    ao_tnb_actions->delete_transcription_job( lv_test_job_name ).

    " Verify job is deleted
    TRY.
        ao_tnb->gettranscriptionjob( lv_test_job_name ).
        cl_abap_unit_assert=>fail( 'Job should have been deleted' ).
      CATCH /aws1/cx_tnbbadrequestex /aws1/cx_tnbnotfoundexception.
        " Expected - job was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD create_vocabulary.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_vocab_name) = |vocab-{ lv_uuid_string+0(10) }|.

    " Create test phrases
    DATA(lt_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |brillig| ) )
      ( NEW /aws1/cl_tnbphrases_w( |slithy| ) )
      ( NEW /aws1/cl_tnbphrases_w( |toves| ) )
    ).

    " Create vocabulary
    DATA(lo_result) = ao_tnb_actions->create_vocabulary(
      iv_vocabulary_name = lv_test_vocab_name
      iv_language_code = 'en-US'
      it_phrases = lt_phrases ).

    " Verify vocabulary was created
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Create vocabulary result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_vocab_name
      act = lo_result->get_vocabularyname( )
      msg = 'Vocabulary name should match' ).

    " Wait for vocabulary to be ready - must not skip test
    DATA(lv_state) = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY' AND lv_state <> 'FAILED'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary did not become ready, state: { lv_state }| ).
    ENDIF.

    " Clean up
    TRY.
        ao_tnb->deletevocabulary( lv_test_vocab_name ).
      CATCH /aws1/cx_tnbnotfoundexception /aws1/cx_tnbbadrequestex.
    ENDTRY.
  ENDMETHOD.

  METHOD get_vocabulary.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_vocab_name) = |getvoc-{ lv_uuid_string+0(10) }|.

    " Create a vocabulary first - must not skip if creation fails
    DATA(lt_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |test| ) )
    ).

    TRY.
        ao_tnb->createvocabulary(
          iv_vocabularyname = lv_test_vocab_name
          iv_languagecode = 'en-US'
          it_phrases = lt_phrases ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create vocabulary for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Wait for vocabulary to be ready
    DATA(lv_state) = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary must be ready for test, state: { lv_state }| ).
    ENDIF.

    " Get the vocabulary
    DATA(lo_result) = ao_tnb_actions->get_vocabulary( lv_test_vocab_name ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get vocabulary result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_vocab_name
      act = lo_result->get_vocabularyname( )
      msg = 'Retrieved vocabulary name should match' ).

    " Clean up
    TRY.
        ao_tnb->deletevocabulary( lv_test_vocab_name ).
      CATCH /aws1/cx_tnbnotfoundexception.
    ENDTRY.
  ENDMETHOD.

  METHOD list_vocabularies.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_prefix) = |listvoc{ lv_uuid_string+0(6) }|.
    DATA(lv_test_vocab_name) = |{ lv_prefix }vocab|.

    " Create a test vocabulary - must not skip if creation fails
    DATA(lt_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |test| ) )
    ).

    TRY.
        ao_tnb->createvocabulary(
          iv_vocabularyname = lv_test_vocab_name
          iv_languagecode = 'en-US'
          it_phrases = lt_phrases ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create vocabulary for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Wait for vocabulary to be ready
    DATA(lv_state) = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary must be ready for test, state: { lv_state }| ).
    ENDIF.

    " List vocabularies with filter
    DATA(lo_result) = ao_tnb_actions->list_vocabularies( lv_prefix ).

    " Verify results
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List vocabularies result should not be initial' ).

    DATA(lt_vocabs) = lo_result->get_vocabularies( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_vocabs
      msg = 'Should have at least one vocabulary in the list' ).

    " Verify our vocabulary is in the list
    DATA lv_found TYPE abap_bool.
    LOOP AT lt_vocabs INTO DATA(lo_vocab_info).
      IF lo_vocab_info->get_vocabularyname( ) = lv_test_vocab_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Vocabulary { lv_test_vocab_name } should be in the list| ).

    " Clean up
    TRY.
        ao_tnb->deletevocabulary( lv_test_vocab_name ).
      CATCH /aws1/cx_tnbnotfoundexception.
    ENDTRY.
  ENDMETHOD.

  METHOD update_vocabulary.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_vocab_name) = |upd-{ lv_uuid_string+0(10) }|.

    " Create a vocabulary first - must not skip if creation fails
    DATA(lt_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |original| ) )
    ).

    TRY.
        ao_tnb->createvocabulary(
          iv_vocabularyname = lv_test_vocab_name
          iv_languagecode = 'en-US'
          it_phrases = lt_phrases ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create vocabulary for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Wait for vocabulary to be ready
    DATA(lv_state) = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary must be ready for test, state: { lv_state }| ).
    ENDIF.

    " Update the vocabulary with new phrases
    DATA(lt_new_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |updated| ) )
      ( NEW /aws1/cl_tnbphrases_w( |phrases| ) )
    ).

    DATA(lo_result) = ao_tnb_actions->update_vocabulary(
      iv_vocabulary_name = lv_test_vocab_name
      iv_language_code = 'en-US'
      it_phrases = lt_new_phrases ).

    " Verify update
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Update vocabulary result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_vocab_name
      act = lo_result->get_vocabularyname( )
      msg = 'Updated vocabulary name should match' ).

    " Wait for update to complete
    lv_state = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary update did not complete, state: { lv_state }| ).
    ENDIF.

    " Clean up
    TRY.
        ao_tnb->deletevocabulary( lv_test_vocab_name ).
      CATCH /aws1/cx_tnbnotfoundexception.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_vocabulary.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    DATA(lv_test_vocab_name) = |delvoc-{ lv_uuid_string+0(10) }|.

    " Create a vocabulary - must not skip if creation fails
    DATA(lt_phrases) = VALUE /aws1/cl_tnbphrases_w=>tt_phrases(
      ( NEW /aws1/cl_tnbphrases_w( |test| ) )
    ).

    TRY.
        ao_tnb->createvocabulary(
          iv_vocabularyname = lv_test_vocab_name
          iv_languagecode = 'en-US'
          it_phrases = lt_phrases ).
      CATCH /aws1/cx_rt_generic INTO DATA(lx_create_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create vocabulary for test: { lx_create_error->get_text( ) }| ).
    ENDTRY.

    " Wait for vocabulary to be ready
    DATA(lv_state) = wait_for_vocab_ready( lv_test_vocab_name ).
    IF lv_state <> 'READY'.
      cl_abap_unit_assert=>fail( msg = |Vocabulary must be ready for test, state: { lv_state }| ).
    ENDIF.

    " Delete the vocabulary
    ao_tnb_actions->delete_vocabulary( lv_test_vocab_name ).

    " Verify vocabulary is deleted
    TRY.
        ao_tnb->getvocabulary( lv_test_vocab_name ).
        cl_abap_unit_assert=>fail( 'Vocabulary should have been deleted' ).
      CATCH /aws1/cx_tnbnotfoundexception.
        " Expected - vocabulary was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_job_completion.
    DATA lv_start_time TYPE timestampl.
    DATA lv_current_time TYPE timestampl.
    DATA lv_elapsed_sec TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_result) = ao_tnb->gettranscriptionjob( iv_job_name ).
          DATA(lo_job) = lo_result->get_transcriptionjob( ).
          rv_status = lo_job->get_transcriptionjobstatus( ).

          IF rv_status = 'COMPLETED' OR rv_status = 'FAILED'.
            RETURN.
          ENDIF.

        CATCH /aws1/cx_tnbnotfoundexception /aws1/cx_tnbbadrequestex.
          " Job might not be available yet, continue waiting
      ENDTRY.

      " Check timeout
      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_sec = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_sec >= iv_max_wait_sec.
        " Return current status even if timeout
        RETURN.
      ENDIF.

      " Wait 5 seconds before checking again
      WAIT UP TO 5 SECONDS.
    ENDDO.
  ENDMETHOD.

  METHOD wait_for_vocab_ready.
    DATA lv_start_time TYPE timestampl.
    DATA lv_current_time TYPE timestampl.
    DATA lv_elapsed_sec TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_result) = ao_tnb->getvocabulary( iv_vocab_name ).
          rv_state = lo_result->get_vocabularystate( ).

          IF rv_state = 'READY' OR rv_state = 'FAILED'.
            RETURN.
          ENDIF.

        CATCH /aws1/cx_tnbnotfoundexception.
          " Vocabulary might not be available yet, continue waiting
      ENDTRY.

      " Check timeout
      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_sec = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_sec >= iv_max_wait_sec.
        " Return current state even if timeout
        RETURN.
      ENDIF.

      " Wait 5 seconds before checking again
      WAIT UP TO 5 SECONDS.
    ENDDO.
  ENDMETHOD.

ENDCLASS.
