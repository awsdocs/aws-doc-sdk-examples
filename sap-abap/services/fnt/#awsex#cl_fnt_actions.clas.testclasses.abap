" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_fnt_actions DEFINITION DEFERRED.
CLASS /awsex/cl_fnt_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_fnt_actions.

CLASS ltc_awsex_cl_fnt_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_fnt TYPE REF TO /aws1/if_fnt.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_fnt_actions TYPE REF TO /awsex/cl_fnt_actions.

    CLASS-DATA av_s3_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_distribution_id TYPE /aws1/fntstring.
    CLASS-DATA av_distribution_etag TYPE /aws1/fntstring.
    CLASS-DATA av_distribution_domain TYPE /aws1/fntstring.

    METHODS: list_distributions FOR TESTING RAISING /aws1/cx_rt_generic,
      update_distribution FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS create_cloudfront_distribution
      RETURNING
        VALUE(rv_distribution_id) TYPE /aws1/fntstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_distribution_deployed
      IMPORTING
                iv_distribution_id TYPE /aws1/fntstring
      RAISING   /aws1/cx_rt_generic.

    CLASS-METHODS tag_distribution
      IMPORTING
                iv_distribution_arn TYPE /aws1/fntresourcearn
      RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_fnt_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_fnt = /aws1/cl_fnt_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_fnt_actions = NEW /awsex/cl_fnt_actions( ).

    " Create a unique S3 bucket name for CloudFront origin using util function "
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_acct) = ao_session->get_account_id( ).
    av_s3_bucket = |sap-abap-fnt-demo-{ lv_acct }-{ lv_uuid }|.
    av_s3_bucket = to_lower( av_s3_bucket ).

    " Create S3 bucket for CloudFront origin using util function "
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_s3_bucket
      io_s3 = ao_s3
      io_session = ao_session ).

    " Tag the S3 bucket with convert_test for cleanup "
    TRY.
        DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
        DATA(lo_tag) = NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ).
        APPEND lo_tag TO lt_tags.
        DATA(lo_tagging) = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ).
        ao_s3->putbuckettagging(
          iv_bucket = av_s3_bucket
          io_tagging = lo_tagging ).
      CATCH /aws1/cx_rt_generic.
        " Tagging failed but continue "
    ENDTRY.

    " Wait a moment for S3 bucket to be fully available "
    WAIT UP TO 5 SECONDS.

    " Create a CloudFront distribution for testing "
    av_distribution_id = create_cloudfront_distribution( ).

    " Wait for distribution to be deployed "
    wait_for_distribution_deployed( iv_distribution_id = av_distribution_id ).

  ENDMETHOD.

  METHOD class_teardown.
    " Disable and delete the CloudFront distribution "
    IF av_distribution_id IS NOT INITIAL.
      TRY.
          " Get the current distribution configuration "
          DATA(lo_config_result) = ao_fnt->getdistributionconfig( iv_id = av_distribution_id ).
          DATA(lo_config) = lo_config_result->get_distributionconfig( ).
          DATA(lv_etag) = lo_config_result->get_etag( ).

          " Disable the distribution if it's currently enabled "
          IF lo_config->get_enabled( ) = abap_true.
            lo_config->set_enabled( abap_false ).
            ao_fnt->updatedistribution(
              io_distributionconfig = lo_config
              iv_id = av_distribution_id
              iv_ifmatch = lv_etag ).

            " Wait for distribution to be deployed with disabled status "
            " This can take several minutes "
            DATA lv_start_time TYPE timestamp.
            DATA lv_current_time TYPE timestamp.
            DATA lv_elapsed_seconds TYPE i.
            DATA lv_max_wait_seconds TYPE i VALUE 1800. " 30 minutes "

            GET TIME STAMP FIELD lv_start_time.

            DO.
              TRY.
                  DATA(lo_dist_result) = ao_fnt->getdistribution( iv_id = av_distribution_id ).
                  DATA(lo_dist) = lo_dist_result->get_distribution( ).
                  DATA(lv_status) = lo_dist->get_status( ).

                  IF lv_status = 'Deployed'.
                    " Get the latest ETag "
                    lv_etag = lo_dist_result->get_etag( ).
                    EXIT.
                  ENDIF.

                  GET TIME STAMP FIELD lv_current_time.
                  lv_elapsed_seconds = cl_abap_tstmp=>subtract(
                    tstmp1 = lv_current_time
                    tstmp2 = lv_start_time ).

                  IF lv_elapsed_seconds > lv_max_wait_seconds.
                    " Timeout - cannot delete distribution yet "
                    MESSAGE 'Distribution still deploying - tagged for manual cleanup' TYPE 'W'.
                    EXIT.
                  ENDIF.

                  " Wait 60 seconds before checking again "
                  WAIT UP TO 60 SECONDS.

                CATCH /aws1/cx_rt_generic.
                  EXIT.
              ENDTRY.
            ENDDO.

            " Now delete the distribution if it's deployed "
            IF lv_status = 'Deployed'.
              TRY.
                  ao_fnt->deletedistribution(
                    iv_id = av_distribution_id
                    iv_ifmatch = lv_etag ).
                CATCH /aws1/cx_rt_generic.
                  " Error deleting distribution - it's tagged for manual cleanup "
                  MESSAGE 'Error deleting distribution - tagged for manual cleanup' TYPE 'W'.
              ENDTRY.
            ENDIF.
          ENDIF.

        CATCH /aws1/cx_rt_generic.
          " Error processing distribution - it's tagged for manual cleanup "
          MESSAGE 'Error processing distribution - tagged for manual cleanup' TYPE 'W'.
      ENDTRY.
    ENDIF.

    " Note: We do NOT delete the S3 bucket here because the CloudFront distribution "
    " may still be in the process of being deleted (can take 15+ minutes). "
    " Both the distribution and S3 bucket are tagged with 'convert_test' for manual cleanup. "

  ENDMETHOD.

  METHOD create_cloudfront_distribution.
    " Create a unique caller reference "
    DATA lv_uuid TYPE string.
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    DATA lv_caller_reference TYPE /aws1/fntstring.
    lv_caller_reference = |abap-test-{ sy-datum }{ sy-uzeit }-{ lv_uuid }|.

    " Create S3 origin configuration "
    DATA(lo_s3_origin_config) = NEW /aws1/cl_fnts3originconfig(
      iv_originaccessidentity = || ).

    " Create origin "
    DATA(lo_origin) = NEW /aws1/cl_fntorigin(
      iv_id = |S3-{ av_s3_bucket }|
      iv_domainname = |{ av_s3_bucket }.s3.amazonaws.com|
      io_s3originconfig = lo_s3_origin_config ).

    DATA lt_origins TYPE /aws1/cl_fntorigin=>tt_originlist.
    APPEND lo_origin TO lt_origins.
    DATA(lo_origins) = NEW /aws1/cl_fntorigins(
      iv_quantity = 1
      it_items = lt_origins ).

    " Create default cache behavior "
    DATA(lo_trusted_signers) = NEW /aws1/cl_fnttrustedsigners(
      iv_enabled = abap_false
      iv_quantity = 0 ).

    DATA(lo_trusted_key_groups) = NEW /aws1/cl_fnttrustedkeygroups(
      iv_enabled = abap_false
      iv_quantity = 0 ).

    DATA(lo_forwarded_values) = NEW /aws1/cl_fntforwardedvalues(
      iv_querystring = abap_false
      io_cookies = NEW /aws1/cl_fntcookiepreference( iv_forward = 'none' ) ).

    DATA(lo_default_cache_behavior) = NEW /aws1/cl_fntdefaultcachebehav(
      iv_targetoriginid = |S3-{ av_s3_bucket }|
      io_forwardedvalues = lo_forwarded_values
      io_trustedsigners = lo_trusted_signers
      io_trustedkeygroups = lo_trusted_key_groups
      iv_viewerprotocolpolicy = 'allow-all'
      iv_minttl = 0 ).

    " Create distribution configuration "
    DATA(lo_distribution_config) = NEW /aws1/cl_fntdistributionconfig(
      iv_callerreference = lv_caller_reference
      io_origins = lo_origins
      io_defaultcachebehavior = lo_default_cache_behavior
      iv_comment = 'Test distribution for ABAP SDK - convert_test'
      iv_enabled = abap_true ).

    " Create the distribution "
    DATA(lo_create_result) = ao_fnt->createdistribution(
      io_distributionconfig = lo_distribution_config ).

    DATA(lo_distribution) = lo_create_result->get_distribution( ).
    rv_distribution_id = lo_distribution->get_id( ).
    av_distribution_etag = lo_create_result->get_etag( ).
    av_distribution_domain = lo_distribution->get_domainname( ).

    " Tag the distribution for cleanup "
    DATA(lv_distribution_arn) = lo_distribution->get_arn( ).
    tag_distribution( iv_distribution_arn = lv_distribution_arn ).

  ENDMETHOD.

  METHOD wait_for_distribution_deployed.
    " Wait for distribution to be deployed (can take 15-30 minutes) "
    " We'll wait up to 30 minutes with 60 second checks "
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed_seconds TYPE i.
    DATA lv_max_wait_seconds TYPE i VALUE 1800. " 30 minutes "

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_dist_result) = ao_fnt->getdistribution( iv_id = iv_distribution_id ).
          DATA(lo_dist) = lo_dist_result->get_distribution( ).
          DATA(lv_status) = lo_dist->get_status( ).

          IF lv_status = 'Deployed'.
            " Distribution is ready "
            RETURN.
          ENDIF.

          GET TIME STAMP FIELD lv_current_time.
          lv_elapsed_seconds = cl_abap_tstmp=>subtract(
            tstmp1 = lv_current_time
            tstmp2 = lv_start_time ).

          IF lv_elapsed_seconds > lv_max_wait_seconds.
            " Timeout - distribution is taking too long to deploy "
            RAISE EXCEPTION TYPE /aws1/cx_rt_generic
              EXPORTING
                textid      = /aws1/cx_rt_generic=>generic_error
                av_err_code = 'TIMEOUT'
                av_err_msg  = 'Distribution deployment timeout after 30 minutes'.
          ENDIF.

          " Wait 60 seconds before checking again "
          WAIT UP TO 60 SECONDS.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          " Re-raise the exception "
          RAISE EXCEPTION lo_ex.
      ENDTRY.
    ENDDO.

  ENDMETHOD.

  METHOD tag_distribution.
    " Tag the CloudFront distribution with convert_test "
    TRY.
        DATA lt_tag_keys TYPE /aws1/cl_fnttag=>tt_taglist.
        DATA(lo_tag) = NEW /aws1/cl_fnttag(
          iv_key = 'convert_test'
          iv_value = 'true' ).
        APPEND lo_tag TO lt_tag_keys.

        DATA(lo_tags) = NEW /aws1/cl_fnttags( it_items = lt_tag_keys ).

        ao_fnt->tagresource(
          iv_resource = iv_distribution_arn
          io_tags = lo_tags ).

      CATCH /aws1/cx_rt_generic.
        " Tagging failed but continue - distribution will still be identifiable "
        " by the comment field which includes 'convert_test' "
    ENDTRY.

  ENDMETHOD.

  METHOD list_distributions.
    " Test the list_distributions method "
    DATA lo_result TYPE REF TO /aws1/cl_fntlstdistributionsrs.

    ao_fnt_actions->list_distributions(
      IMPORTING
        oo_result = lo_result ).

    " Assert that result is not null "
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List distributions result should not be null' ).

    " Assert that we have a distribution list "
    DATA(lo_distribution_list) = lo_result->get_distributionlist( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_distribution_list
      msg = 'Distribution list should not be null' ).

    " Verify that our test distribution is in the list "
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_distribution_list->get_items( ) INTO DATA(lo_summary).
      IF lo_summary->get_id( ) = av_distribution_id.
        lv_found = abap_true.
        " Also verify some key properties "
        cl_abap_unit_assert=>assert_not_initial(
          act = lo_summary->get_domainname( )
          msg = 'Distribution domain name should not be empty' ).
        cl_abap_unit_assert=>assert_equals(
          act = lo_summary->get_enabled( )
          exp = abap_true
          msg = 'Distribution should be enabled' ).
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test distribution { av_distribution_id } should be in the list| ).

  ENDMETHOD.

  METHOD update_distribution.
    " Test the update_distribution method "
    DATA lv_new_comment TYPE /aws1/fntcommenttype.
    DATA lv_uuid TYPE string.
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_new_comment = |Updated comment at { sy-datum } { sy-uzeit } - { lv_uuid }|.

    " Update the distribution comment "
    ao_fnt_actions->update_distribution(
      iv_distribution_id = av_distribution_id
      iv_comment = lv_new_comment ).

    " Verify the comment was updated "
    DATA(lo_dist_result) = ao_fnt->getdistribution( iv_id = av_distribution_id ).
    DATA(lo_dist) = lo_dist_result->get_distribution( ).
    DATA(lo_dist_config) = lo_dist->get_distributionconfig( ).
    DATA(lv_updated_comment) = lo_dist_config->get_comment( ).

    cl_abap_unit_assert=>assert_equals(
      act = lv_updated_comment
      exp = lv_new_comment
      msg = 'Distribution comment should match the updated value' ).

    " Verify that the distribution is still enabled "
    cl_abap_unit_assert=>assert_equals(
      act = lo_dist_config->get_enabled( )
      exp = abap_true
      msg = 'Distribution should still be enabled after update' ).

  ENDMETHOD.

ENDCLASS.
