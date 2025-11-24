CLASS ltc_zcl_aws1_cpd_actions DEFINITION DEFERRED.
CLASS zcl_aws1_cpd_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_cpd_actions.

CLASS ltc_zcl_aws1_cpd_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_cpd TYPE REF TO /aws1/if_cpd.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_cpd_actions TYPE REF TO zcl_aws1_cpd_actions.

    CLASS-DATA av_classifier_name TYPE /aws1/cpdcomprehendarnname.
    CLASS-DATA av_classifier_arn TYPE /aws1/cpddocumentclassifierarn.
    CLASS-DATA av_training_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_output_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_role_arn TYPE /aws1/cpdiamrolearn.
    CLASS-DATA av_doc_class_job_id TYPE /aws1/cpdjobid.
    CLASS-DATA av_topics_job_id TYPE /aws1/cpdjobid.
    CLASS-DATA av_lv_account_id TYPE string.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS detect_dominant_language FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detect_entities FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detect_key_phrases FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detect_pii_entities FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detect_sentiment FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detect_syntax FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_document_classifier FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_doc_classifier FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_document_classifiers FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_doc_classifier FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_doc_class_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_doc_class_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_doc_class_jobs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_topics_detect_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_topics_detect_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_topics_detection_jobs FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup_training_data
      IMPORTING
        io_s3 TYPE REF TO /aws1/if_s3
        iv_bucket TYPE /aws1/s3_bucketname
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_classifier
      IMPORTING
        iv_classifier_arn TYPE /aws1/cpddocumentclassifierarn
        iv_max_wait_mins TYPE i DEFAULT 60
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_job
      IMPORTING
        iv_job_id TYPE /aws1/cpdjobid
        iv_job_type TYPE string
        iv_max_wait_mins TYPE i DEFAULT 30
      RAISING /aws1/cx_rt_generic.

    METHODS setup_job_input_data
      IMPORTING
        io_s3 TYPE REF TO /aws1/if_s3
        iv_bucket TYPE /aws1/s3_bucketname
      RAISING /aws1/cx_rt_generic.

ENDCLASS.


CLASS ltc_zcl_aws1_cpd_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_cpd = /aws1/cl_cpd_factory=>create( ao_session ).
    ao_cpd_actions = NEW zcl_aws1_cpd_actions( ).

    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    av_lv_account_id = ao_session->get_account_id( ).

    " Setup buckets for classifier training and job output
    av_training_bucket = |cpd-training-{ av_lv_account_id }|.
    av_output_bucket = |cpd-output-{ av_lv_account_id }|.

    " Truncate bucket names if they exceed S3 limits (63 characters)
    IF strlen( av_training_bucket ) > 63.
      av_training_bucket = av_training_bucket+0(63).
    ENDIF.
    IF strlen( av_output_bucket ) > 63.
      av_output_bucket = av_output_bucket+0(63).
    ENDIF.

    " Create S3 buckets
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( ao_session ).

    TRY.
        lo_s3->createbucket( iv_bucket = av_training_bucket ).
        WAIT UP TO 2 SECONDS.
      CATCH /aws1/cx_s3_bucketalrdyexists /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists, continue
    ENDTRY.

    TRY.
        lo_s3->createbucket( iv_bucket = av_output_bucket ).
        WAIT UP TO 2 SECONDS.
      CATCH /aws1/cx_s3_bucketalrdyexists /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists, continue
    ENDTRY.

    " Setup training data
    setup_training_data(
      io_s3 = lo_s3
      iv_bucket = av_training_bucket
    ).

    " Setup job input data
    setup_job_input_data(
      io_s3 = lo_s3
      iv_bucket = av_training_bucket
    ).

    " Create IAM role for Comprehend with necessary permissions
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( ao_session ).

    DATA lv_role_name TYPE /aws1/iamrolename.
    lv_role_name = |ComprehendS3AccessRole|.

    DATA lv_assume_role_policy TYPE /aws1/iampolicydocumenttype.
    lv_assume_role_policy = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"comprehend.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]\}|.

    TRY.
        DATA(lo_role_result) = lo_iam->createrole(
          iv_rolename = lv_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          iv_description = 'Role for Comprehend to access S3'
        ).
        av_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Attach S3 full access policy
        lo_iam->attachrolepolicy(
          iv_rolename = lv_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/AmazonS3FullAccess'
        ).

        " Wait for role to propagate
        WAIT UP TO 10 SECONDS.

      CATCH /aws1/cx_iamentityalrdyexists.
        " Role already exists, get it
        DATA(lo_get_role) = lo_iam->getrole( iv_rolename = lv_role_name ).
        av_role_arn = lo_get_role->get_role( )->get_arn( ).
    ENDTRY.

    " Create a document classifier for testing
    av_classifier_name = |test-classifier-{ lv_uuid+0(8) }|.

    DATA(lo_classifier_result) = ao_cpd->createdocumentclassifier(
      iv_documentclassifiername = av_classifier_name
      iv_languagecode = 'en'
      io_inputdataconfig = NEW /aws1/cl_cpddocclifierinpdat00(
        iv_s3uri = |s3://{ av_training_bucket }/training-data.csv|
      )
      iv_dataaccessrolearn = av_role_arn
      iv_mode = 'MULTI_CLASS'
    ).
    av_classifier_arn = lo_classifier_result->get_documentclassifierarn( ).

    " Wait for classifier to be trained (this can take a long time)
    " For testing purposes, we'll start the wait but may time out
    TRY.
        wait_for_classifier(
          iv_classifier_arn = av_classifier_arn
          iv_max_wait_mins = 60
        ).
      CATCH /aws1/cx_rt_generic.
        " Classifier may not be trained in time, but we can still test other methods
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up S3 buckets
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( ao_session ).

    " Clean up training bucket
    TRY.
        DATA(lo_list_result) = lo_s3->listobjectsv2( iv_bucket = av_training_bucket ).
        LOOP AT lo_list_result->get_contents( ) INTO DATA(lo_object).
          lo_s3->deleteobject(
            iv_bucket = av_training_bucket
            iv_key = lo_object->get_key( )
          ).
        ENDLOOP.
        lo_s3->deletebucket( iv_bucket = av_training_bucket ).
      CATCH /aws1/cx_s3_nosuchbucket.
        " Bucket doesn't exist
    ENDTRY.

    " Clean up output bucket
    TRY.
        lo_list_result = lo_s3->listobjectsv2( iv_bucket = av_output_bucket ).
        LOOP AT lo_list_result->get_contents( ) INTO lo_object.
          lo_s3->deleteobject(
            iv_bucket = av_output_bucket
            iv_key = lo_object->get_key( )
          ).
        ENDLOOP.
        lo_s3->deletebucket( iv_bucket = av_output_bucket ).
      CATCH /aws1/cx_s3_nosuchbucket.
        " Bucket doesn't exist
    ENDTRY.

    " Delete classifier if it exists
    IF av_classifier_arn IS NOT INITIAL.
      TRY.
          ao_cpd->deletedocumentclassifier( iv_documentclassifierarn = av_classifier_arn ).
        CATCH /aws1/cx_cpdresourcenotfoundex /aws1/cx_cpdresourceinuseex.
          " Classifier doesn't exist or is in use
      ENDTRY.
    ENDIF.

    " Clean up IAM role
    TRY.
        DATA(lo_iam) = /aws1/cl_iam_factory=>create( ao_session ).
        DATA(lv_role_name) = |ComprehendS3AccessRole|.

        " Detach policies first
        TRY.
            lo_iam->detachrolepolicy(
              iv_rolename = lv_role_name
              iv_policyarn = 'arn:aws:iam::aws:policy/AmazonS3FullAccess'
            ).
          CATCH /aws1/cx_iamnosuchentity.
            " Policy not attached
        ENDTRY.

        " Delete role
        lo_iam->deleterole( iv_rolename = lv_role_name ).
      CATCH /aws1/cx_iamnosuchentity.
        " Role doesn't exist
    ENDTRY.

  ENDMETHOD.

  METHOD setup_training_data.
    " Create sample training data for document classification
    " Format: label,text (CSV format)
    DATA(lv_training_data) = |CLASS1,This is a positive example document.\n| &&
                              |CLASS2,This is a negative example document.\n| &&
                              |CLASS1,Another positive example for training.\n| &&
                              |CLASS2,Another negative example for training.\n| &&
                              |CLASS1,Positive sentiment and good quality.\n| &&
                              |CLASS2,Negative sentiment and poor quality.\n| &&
                              |CLASS1,Excellent service and great experience.\n| &&
                              |CLASS2,Poor quality and bad experience.\n| &&
                              |CLASS1,Wonderful product highly recommend.\n| &&
                              |CLASS2,Terrible product would not recommend.\n|.

    DATA(lv_training_data_xstring) = cl_abap_conv_codepage=>create_out( )->convert( lv_training_data ).

    io_s3->putobject(
      iv_bucket = iv_bucket
      iv_key = 'training-data.csv'
      iv_body = lv_training_data_xstring
    ).

  ENDMETHOD.

  METHOD setup_job_input_data.
    " Create sample input data for classification and topic detection jobs
    DATA(lv_input_data) = |This is a sample document for classification.\n| &&
                           |Another document with different content.\n| &&
                           |Third document for testing purposes.\n| &&
                           |Fourth document with various topics.\n| &&
                           |Fifth document discussing technology.\n|.

    DATA(lv_input_data_xstring) = cl_abap_conv_codepage=>create_out( )->convert( lv_input_data ).

    io_s3->putobject(
      iv_bucket = iv_bucket
      iv_key = 'input-data.txt'
      iv_body = lv_input_data_xstring
    ).

  ENDMETHOD.

  METHOD wait_for_classifier.
    DATA lv_wait_time TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/cpdmodelstatus.

    DO.
      WAIT UP TO 30 SECONDS.
      lv_wait_time = lv_wait_time + 1.

      DATA(lo_result) = ao_cpd->describedocumentclassifier(
        iv_documentclassifierarn = iv_classifier_arn
      ).

      lv_status = lo_result->get_documentclassifierprps( )->get_status( ).

      IF lv_status = 'TRAINED'.
        RETURN.
      ELSEIF lv_status = 'IN_ERROR'.
        DATA(lv_message) = lo_result->get_documentclassifierprps( )->get_message( ).
        MESSAGE |Classifier training failed: { lv_message }| TYPE 'E'.
      ENDIF.

      IF lv_wait_time >= iv_max_wait_mins * 2. " 30 second intervals
        MESSAGE 'Classifier training timed out' TYPE 'E'.
      ENDIF.
    ENDDO.

  ENDMETHOD.

  METHOD wait_for_job.
    DATA lv_wait_time TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/cpdjobstatus.

    DO.
      WAIT UP TO 30 SECONDS.
      lv_wait_time = lv_wait_time + 1.

      CASE iv_job_type.
        WHEN 'DOCUMENT_CLASSIFICATION'.
          DATA(lo_doc_job_result) = ao_cpd->describedocclassificationjob(
            iv_jobid = iv_job_id
          ).
          lv_status = lo_doc_job_result->get_docclassificationjobprps( )->get_jobstatus( ).

        WHEN 'TOPICS_DETECTION'.
          DATA(lo_topics_job_result) = ao_cpd->describetopicsdetectionjob(
            iv_jobid = iv_job_id
          ).
          lv_status = lo_topics_job_result->get_topicsdetectionjobprps( )->get_jobstatus( ).
      ENDCASE.

      IF lv_status = 'COMPLETED'.
        RETURN.
      ELSEIF lv_status = 'FAILED'.
        MESSAGE 'Job failed' TYPE 'E'.
      ENDIF.

      IF lv_wait_time >= iv_max_wait_mins * 2. " 30 second intervals
        MESSAGE 'Job timed out' TYPE 'E'.
      ENDIF.
    ENDDO.

  ENDMETHOD.

  METHOD detect_dominant_language.
    DATA(lv_text) = |This is a sample text in English for language detection.|.

    DATA(lo_result) = ao_cpd_actions->detect_dominant_language( lv_text ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lt_languages) = lo_result->get_languages( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_languages
      msg = 'Languages should be detected'
    ).

    " Check that English is detected
    DATA lv_found_english TYPE abap_bool VALUE abap_false.
    LOOP AT lt_languages INTO DATA(lo_language).
      IF lo_language->get_languagecode( ) = 'en'.
        lv_found_english = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found_english
      msg = 'English should be detected'
    ).

  ENDMETHOD.

  METHOD detect_entities.
    DATA(lv_text) = |Amazon Web Services is located in Seattle, Washington.|.

    DATA(lo_result) = ao_cpd_actions->detect_entities(
      iv_text = lv_text
      iv_language_code = 'en'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lt_entities) = lo_result->get_entities( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_entities
      msg = 'Entities should be detected'
    ).

  ENDMETHOD.

  METHOD detect_key_phrases.
    DATA(lv_text) = |I love using Amazon Comprehend for natural language processing tasks.|.

    DATA(lo_result) = ao_cpd_actions->detect_key_phrases(
      iv_text = lv_text
      iv_language_code = 'en'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lt_phrases) = lo_result->get_keyphrases( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_phrases
      msg = 'Key phrases should be detected'
    ).

  ENDMETHOD.

  METHOD detect_pii_entities.
    DATA(lv_text) = |My email is john.doe@example.com and my phone number is 555-1234.|.

    DATA(lo_result) = ao_cpd_actions->detect_pii_entities(
      iv_text = lv_text
      iv_language_code = 'en'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lt_entities) = lo_result->get_entities( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_entities
      msg = 'PII entities should be detected'
    ).

  ENDMETHOD.

  METHOD detect_sentiment.
    DATA(lv_text) = |I am very happy with the excellent service provided.|.

    DATA(lo_result) = ao_cpd_actions->detect_sentiment(
      iv_text = lv_text
      iv_language_code = 'en'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lv_sentiment) = lo_result->get_sentiment( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_sentiment
      msg = 'Sentiment should be detected'
    ).

    " Check that sentiment is POSITIVE
    cl_abap_unit_assert=>assert_equals(
      act = lv_sentiment
      exp = 'POSITIVE'
      msg = 'Sentiment should be POSITIVE'
    ).

  ENDMETHOD.

  METHOD detect_syntax.
    DATA(lv_text) = |This is a simple sentence.|.

    DATA(lo_result) = ao_cpd_actions->detect_syntax(
      iv_text = lv_text
      iv_language_code = 'en'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lt_tokens) = lo_result->get_syntaxtokens( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_tokens
      msg = 'Syntax tokens should be detected'
    ).

  ENDMETHOD.

  METHOD create_document_classifier.
    " This test creates a new classifier (separate from the one in class_setup)
    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    DATA(lv_test_classifier_name) = |test-create-{ lv_uuid+0(8) }|.

    DATA(lo_result) = ao_cpd_actions->create_document_classifier(
      iv_classifier_name = lv_test_classifier_name
      iv_language_code = 'en'
      iv_training_s3_uri = |s3://{ av_training_bucket }/training-data.csv|
      iv_data_access_role_arn = av_role_arn
      iv_mode = 'MULTI_CLASS'
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lv_classifier_arn) = lo_result->get_documentclassifierarn( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_classifier_arn
      msg = 'Classifier ARN should be returned'
    ).

    " Clean up - delete the test classifier
    TRY.
        ao_cpd->deletedocumentclassifier( iv_documentclassifierarn = lv_classifier_arn ).
      CATCH /aws1/cx_cpdresourceinuseex.
        " Classifier is training, will be cleaned up later
    ENDTRY.

  ENDMETHOD.

  METHOD describe_doc_classifier.
    " Use the classifier created in class_setup
    DATA(lo_result) = ao_cpd_actions->describe_doc_classifier(
      iv_classifier_arn = av_classifier_arn
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lo_properties) = lo_result->get_documentclassifierprps( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Classifier properties should not be null'
    ).

    DATA(lv_status) = lo_properties->get_status( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = 'Classifier status should not be empty'
    ).

  ENDMETHOD.

  METHOD list_document_classifiers.
    DATA(lo_result) = ao_cpd_actions->list_document_classifiers( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    " List should return successfully (may be empty or contain classifiers)

  ENDMETHOD.

  METHOD delete_doc_classifier.
    " Create a classifier specifically for deletion
    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    DATA(lv_del_classifier_name) = |test-delete-{ lv_uuid+0(8) }|.

    DATA(lo_create_result) = ao_cpd->createdocumentclassifier(
      iv_documentclassifiername = lv_del_classifier_name
      iv_languagecode = 'en'
      io_inputdataconfig = NEW /aws1/cl_cpddocclifierinpdat00(
        iv_s3uri = |s3://{ av_training_bucket }/training-data.csv|
      )
      iv_dataaccessrolearn = av_role_arn
      iv_mode = 'MULTI_CLASS'
    ).
    DATA(lv_del_classifier_arn) = lo_create_result->get_documentclassifierarn( ).

    " Wait a bit for the classifier to be created (not trained)
    WAIT UP TO 5 SECONDS.

    " Now delete it
    DATA(lo_result) = ao_cpd_actions->delete_doc_classifier(
      iv_classifier_arn = lv_del_classifier_arn
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    " Verify deletion by trying to describe (should fail)
    TRY.
        ao_cpd->describedocumentclassifier( iv_documentclassifierarn = lv_del_classifier_arn ).
        cl_abap_unit_assert=>fail( msg = 'Classifier should have been deleted' ).
      CATCH /aws1/cx_cpdresourcenotfoundex.
        " Expected - classifier was deleted
    ENDTRY.

  ENDMETHOD.

  METHOD start_doc_class_job.
    " Only start job if classifier is trained
    DATA(lo_desc_result) = ao_cpd->describedocumentclassifier(
      iv_documentclassifierarn = av_classifier_arn
    ).
    DATA(lv_status) = lo_desc_result->get_documentclassifierprps( )->get_status( ).

    IF lv_status <> 'TRAINED'.
      " Skip test if classifier is not trained
      RETURN.
    ENDIF.

    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    DATA(lv_job_name) = |test-job-{ lv_uuid+0(8) }|.

    DATA(lo_result) = ao_cpd_actions->start_doc_class_job(
      iv_job_name = lv_job_name
      iv_classifier_arn = av_classifier_arn
      iv_input_s3_uri = |s3://{ av_training_bucket }/input-data.txt|
      iv_input_format = 'ONE_DOC_PER_LINE'
      iv_output_s3_uri = |s3://{ av_output_bucket }/output/|
      iv_data_access_role_arn = av_role_arn
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    av_doc_class_job_id = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_doc_class_job_id
      msg = 'Job ID should be returned'
    ).

  ENDMETHOD.

  METHOD describe_doc_class_job.
    " Only test if we have a job ID from start_doc_class_job
    IF av_doc_class_job_id IS INITIAL.
      RETURN.
    ENDIF.

    DATA(lo_result) = ao_cpd_actions->describe_doc_class_job(
      iv_job_id = av_doc_class_job_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lo_properties) = lo_result->get_docclassificationjobprps( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Job properties should not be null'
    ).

    DATA(lv_status) = lo_properties->get_jobstatus( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = 'Job status should not be empty'
    ).

  ENDMETHOD.

  METHOD list_doc_class_jobs.
    DATA(lo_result) = ao_cpd_actions->list_doc_class_jobs( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    " List should return successfully (may be empty or contain jobs)

  ENDMETHOD.

  METHOD start_topics_detect_job.
    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    DATA(lv_job_name) = |test-topics-{ lv_uuid+0(8) }|.

    DATA(lo_result) = ao_cpd_actions->start_topics_detection_job(
      iv_job_name = lv_job_name
      iv_input_s3_uri = |s3://{ av_training_bucket }/input-data.txt|
      iv_input_format = 'ONE_DOC_PER_LINE'
      iv_output_s3_uri = |s3://{ av_output_bucket }/topics-output/|
      iv_data_access_role_arn = av_role_arn
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    av_topics_job_id = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_topics_job_id
      msg = 'Job ID should be returned'
    ).

  ENDMETHOD.

  METHOD describe_topics_detect_job.
    " Only test if we have a job ID from start_topics_detect_job
    IF av_topics_job_id IS INITIAL.
      RETURN.
    ENDIF.

    DATA(lo_result) = ao_cpd_actions->describe_topics_detect_job(
      iv_job_id = av_topics_job_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    DATA(lo_properties) = lo_result->get_topicsdetectionjobprps( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Job properties should not be null'
    ).

    DATA(lv_status) = lo_properties->get_jobstatus( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = 'Job status should not be empty'
    ).

  ENDMETHOD.

  METHOD list_topics_detection_jobs.
    DATA(lo_result) = ao_cpd_actions->list_topics_detection_jobs( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be null'
    ).

    " List should return successfully (may be empty or contain jobs)

  ENDMETHOD.

ENDCLASS.
