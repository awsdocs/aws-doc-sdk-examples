" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ssm_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ssm_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ssm_actions.

CLASS ltc_awsex_cl_ssm_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ssm TYPE REF TO /aws1/if_ssm.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ssm_actions TYPE REF TO /awsex/cl_ssm_actions.
    CLASS-DATA ao_ec2 TYPE REF TO /aws1/if_ec2.

    CLASS-DATA av_document_name TYPE /aws1/ssmdocumentname.
    CLASS-DATA av_maintenance_window_id TYPE /aws1/ssmmaintenancewindowid.
    CLASS-DATA av_ops_item_id TYPE /aws1/ssmopsitemid.
    CLASS-DATA av_instance_id TYPE /aws1/ssminstanceid.

    METHODS:
      create_document FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_document FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_document FOR TESTING RAISING /aws1/cx_rt_generic,
      send_command FOR TESTING RAISING /aws1/cx_rt_generic,
      list_command_invocations FOR TESTING RAISING /aws1/cx_rt_generic,
      create_maintenance_window FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_maintenance_window FOR TESTING RAISING /aws1/cx_rt_generic,
      update_maintenance_window FOR TESTING RAISING /aws1/cx_rt_generic,
      create_ops_item FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_ops_item FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_ops_items FOR TESTING RAISING /aws1/cx_rt_generic,
      update_ops_item FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic.

    METHODS wait_for_document_active
      IMPORTING
        iv_document_name TYPE /aws1/ssmdocumentname
        iv_max_attempts  TYPE i DEFAULT 30
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_ssm_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ssm = /aws1/cl_ssm_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_ssm_actions = NEW /awsex/cl_ssm_actions( ).

    " Create unique test resource names
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.

    " Generate unique names for test resources
    av_document_name = |test-doc-{ lv_uuid_string }|.

    " Create a test document for use in multiple tests
    " Example SSM document content in YAML format
    DATA(lv_content) = |schemaVersion: '2.2'\n| &&
                       |description: Test document for SSM examples\n| &&
                       |mainSteps:\n| &&
                       |  - action: 'aws:runShellScript'\n| &&
                       |    name: runEchoCommand\n| &&
                       |    inputs:\n| &&
                       |      runCommand:\n| &&
                       |        - echo "Hello from SSM"|.

    " Create document with convert_test tag
    DATA lt_tags TYPE /aws1/cl_ssmtag=>tt_taglist.
    APPEND NEW /aws1/cl_ssmtag(
      iv_key = 'convert_test'
      iv_value = 'true'
    ) TO lt_tags.

    TRY.
        ao_ssm->createdocument(
          iv_name = av_document_name
          iv_content = lv_content
          iv_documenttype = 'Command'
          it_tags = lt_tags
        ).

        " Wait for document to become active
        DATA lv_attempt TYPE i VALUE 0.
        DATA lv_status TYPE /aws1/ssmdocumentstatus.
        WHILE lv_attempt < 30.
          lv_attempt = lv_attempt + 1.
          TRY.
              DATA(lo_desc) = ao_ssm->describedocument( iv_name = av_document_name ).
              lv_status = lo_desc->get_document( )->get_status( ).
              IF lv_status = 'Active'.
                EXIT.
              ENDIF.
            CATCH /aws1/cx_ssminvaliddocument.
              " Document not yet available
          ENDTRY.
          WAIT UP TO 2 SECONDS.
        ENDWHILE.

      CATCH /aws1/cx_ssmdocalreadyexists.
        " Document already exists from previous test, continue
    ENDTRY.

    " Create a test maintenance window
    DATA(lv_window_name) = |test-mw-{ lv_uuid_string }|.
    TRY.
        " Example: every day at 2 AM UTC for 3 hours with 1 hour cutoff
        DATA(lo_mw_result) = ao_ssm->createmaintenancewindow(
          iv_name = lv_window_name
          iv_schedule = 'cron(0 2 * * ? *)'
          iv_duration = 3
          iv_cutoff = 1
          iv_allowunassociatedtargets = abap_true
          it_tags = lt_tags
        ).
        av_maintenance_window_id = lo_mw_result->get_windowid( ).
      CATCH /aws1/cx_ssmresrclimitexcdex.
        " Resource limit reached, continue without window
    ENDTRY.

    " Create a test OpsItem
    TRY.
        " Example: Create with severity 4
        DATA(lo_ops_result) = ao_ssm->createopsitem(
          iv_title = |Test OpsItem { lv_uuid_string }|
          iv_source = 'UnitTest'
          iv_category = 'Availability'
          iv_severity = '4'
          iv_description = 'Test OpsItem for unit tests'
          it_tags = lt_tags
        ).
        av_ops_item_id = lo_ops_result->get_opsitemid( ).
      CATCH /aws1/cx_ssmopsitemlimitexcdex.
        " OpsItem limit reached, continue without OpsItem
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up any leftover resources with convert_test tag
    TRY.
        " Clean up documents
        IF av_document_name IS NOT INITIAL.
          TRY.
              ao_ssm->deletedocument( iv_name = av_document_name ).
            CATCH /aws1/cx_ssminvaliddocument.
              " Document doesn't exist, continue
          ENDTRY.
        ENDIF.

        " Clean up maintenance windows
        IF av_maintenance_window_id IS NOT INITIAL.
          TRY.
              ao_ssm->deletemaintenancewindow( iv_windowid = av_maintenance_window_id ).
            CATCH /aws1/cx_ssminternalservererr.
              " Window doesn't exist, continue
          ENDTRY.
        ENDIF.

        " Clean up OpsItems
        IF av_ops_item_id IS NOT INITIAL.
          TRY.
              ao_ssm->deleteopsitem( iv_opsitemid = av_ops_item_id ).
            CATCH /aws1/cx_ssmopsiteminvparamex.
              " OpsItem doesn't exist, continue
          ENDTRY.
        ENDIF.

      CATCH /aws1/cx_rt_generic.
        " Log but don't fail cleanup
    ENDTRY.
  ENDMETHOD.

  METHOD setup.
    " Reset class variables for each test
  ENDMETHOD.

  METHOD create_document.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_doc_name) = |doc-create-{ lv_uuid_string }|.

    " Example SSM document content in YAML format
    DATA(lv_content) = |schemaVersion: '2.2'\n| &&
                       |description: Simple document\n| &&
                       |mainSteps:\n| &&
                       |  - action: 'aws:runShellScript'\n| &&
                       |    name: runEchoCommand\n| &&
                       |    inputs:\n| &&
                       |      runCommand:\n| &&
                       |        - echo "Hello World"|.

    DATA(lo_result) = ao_ssm_actions->create_document(
      iv_name = lv_doc_name
      iv_content = lv_content
      iv_document_type = 'Command'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = |Document { lv_doc_name } was not created| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_documentdescription( )
      msg = |Document description should not be empty| ).

    " Wait for document to become active
    wait_for_document_active( lv_doc_name ).

    " Clean up
    ao_ssm->deletedocument( iv_name = lv_doc_name ).

  ENDMETHOD.

  METHOD delete_document.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_doc_name) = |doc-delete-{ lv_uuid_string }|.

    " Create document first
    DATA(lv_content) = |schemaVersion: '2.2'\n| &&
                       |description: Document for delete test\n| &&
                       |mainSteps:\n| &&
                       |  - action: 'aws:runShellScript'\n| &&
                       |    name: runEchoCommand\n| &&
                       |    inputs:\n| &&
                       |      runCommand:\n| &&
                       |        - echo "Delete test"|.

    ao_ssm->createdocument(
      iv_name = lv_doc_name
      iv_content = lv_content
      iv_documenttype = 'Command'
    ).

    wait_for_document_active( lv_doc_name ).

    " Test delete
    ao_ssm_actions->delete_document( lv_doc_name ).

    " Verify deletion
    TRY.
        ao_ssm->describedocument( iv_name = lv_doc_name ).
        cl_abap_unit_assert=>fail( msg = |Document { lv_doc_name } should have been deleted| ).
      CATCH /aws1/cx_ssminvaliddocument.
        " Expected exception, document deleted successfully
    ENDTRY.

  ENDMETHOD.

  METHOD describe_document.
    " Use the shared document created in class_setup
    IF av_document_name IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Shared document was not created in class_setup' ).
    ENDIF.

    " Test describe using shared document
    DATA(lv_status) = ao_ssm_actions->describe_document( av_document_name ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'Active'
      act = lv_status
      msg = |Document { av_document_name } status should be Active| ).

  ENDMETHOD.

  METHOD send_command.
    " This test requires a managed EC2 instance with SSM agent
    " which is complex to set up, so we'll create a document
    " and attempt to send a command to a non-existent instance
    " to verify the API works

    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_doc_name) = |AWS-RunShellScript|. " Use built-in document

    DATA lt_instance_ids TYPE /aws1/cl_ssminstanceidlist_w=>tt_instanceidlist.
    " Using a known non-existent instance ID format
    DATA(lv_test_instance_id) = |i-0000000000000000|.
    APPEND NEW /aws1/cl_ssminstanceidlist_w( iv_value = lv_test_instance_id ) TO lt_instance_ids.

    TRY.
        DATA(lv_command_id) = ao_ssm_actions->send_command(
          iv_document_name = lv_doc_name
          it_instance_ids = lt_instance_ids
        ).
        " If we get here with an invalid instance, something is wrong
        " But the API may not immediately validate instance ID
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_command_id
          msg = 'Command ID should be returned' ).
      CATCH /aws1/cx_ssminvalidinstanceid.
        " Expected exception for invalid instance
    ENDTRY.

  ENDMETHOD.

  METHOD list_command_invocations.
    " Test listing command invocations for a non-existent instance
    " This should return an empty list without error
    DATA(lv_test_instance_id) = |i-0000000000000000|.

    TRY.
        ao_ssm_actions->list_command_invocations( lv_test_instance_id ).
        " Method outputs messages, we're just checking it doesn't crash
      CATCH /aws1/cx_ssminvalidinstanceid.
        " This is acceptable
    ENDTRY.

  ENDMETHOD.

  METHOD create_maintenance_window.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_window_name) = |test-mw-{ lv_uuid_string }|.

    " Example: every day at 1 AM UTC for 4 hours with 1 hour cutoff
    DATA(lv_window_id) = ao_ssm_actions->create_maintenance_window(
      iv_name = lv_window_name
      iv_schedule = 'cron(0 1 * * ? *)'
      iv_duration = 4
      iv_cutoff = 1
      iv_allow_unassociated_targets = abap_true
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_window_id
      msg = |Maintenance window { lv_window_name } was not created| ).

    " Clean up
    ao_ssm->deletemaintenancewindow( iv_windowid = lv_window_id ).

  ENDMETHOD.

  METHOD delete_maintenance_window.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_window_name) = |test-mw-del-{ lv_uuid_string }|.

    " Create window first
    " Example: every day at 1 AM UTC
    DATA(lo_result) = ao_ssm->createmaintenancewindow(
      iv_name = lv_window_name
      iv_schedule = 'cron(0 1 * * ? *)'
      iv_duration = 4
      iv_cutoff = 1
      iv_allowunassociatedtargets = abap_true
    ).
    DATA(lv_window_id) = lo_result->get_windowid( ).

    " Test delete
    ao_ssm_actions->delete_maintenance_window( lv_window_id ).

    " Verify deletion - should throw exception
    TRY.
        ao_ssm->getmaintenancewindow( iv_windowid = lv_window_id ).
        cl_abap_unit_assert=>fail( msg = |Window { lv_window_id } should have been deleted| ).
      CATCH /aws1/cx_ssmdoesnotexistex.
        " Expected exception
    ENDTRY.

  ENDMETHOD.

  METHOD update_maintenance_window.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    DATA(lv_window_name) = |test-mw-update-{ lv_uuid_string }|.
    DATA(lv_new_name) = |test-mw-updated-{ lv_uuid_string }|.

    " Create window first
    " Example: every day at 1 AM UTC
    DATA(lo_result) = ao_ssm->createmaintenancewindow(
      iv_name = lv_window_name
      iv_schedule = 'cron(0 1 * * ? *)'
      iv_duration = 4
      iv_cutoff = 1
      iv_allowunassociatedtargets = abap_true
    ).
    DATA(lv_window_id) = lo_result->get_windowid( ).

    " Test update
    ao_ssm_actions->update_maintenance_window(
      iv_window_id = lv_window_id
      iv_name = lv_new_name
      iv_enabled = abap_false
    ).

    " Verify update
    DATA(lo_get_result) = ao_ssm->getmaintenancewindow( iv_windowid = lv_window_id ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_new_name
      act = lo_get_result->get_name( )
      msg = |Window name should have been updated to { lv_new_name }| ).
    cl_abap_unit_assert=>assert_equals(
      exp = abap_false
      act = lo_get_result->get_enabled( )
      msg = 'Window should be disabled' ).

    " Clean up
    ao_ssm->deletemaintenancewindow( iv_windowid = lv_window_id ).

  ENDMETHOD.

  METHOD create_ops_item.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.

    " Example: Create OpsItem with severity 3
    DATA(lv_ops_item_id) = ao_ssm_actions->create_ops_item(
      iv_title = |Test OpsItem { lv_uuid_string }|
      iv_source = 'UnitTest'
      iv_category = 'Availability'
      iv_severity = '3'
      iv_description = 'Test OpsItem created by unit test'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_ops_item_id
      msg = 'OpsItem ID should not be empty' ).

    " Clean up
    ao_ssm->deleteopsitem( iv_opsitemid = lv_ops_item_id ).

  ENDMETHOD.

  METHOD delete_ops_item.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.

    " Create OpsItem first
    " Example: Create with severity 4
    DATA(lo_result) = ao_ssm->createopsitem(
      iv_title = |Test OpsItem Delete { lv_uuid_string }|
      iv_source = 'UnitTest'
      iv_category = 'Availability'
      iv_severity = '4'
      iv_description = 'Test OpsItem for deletion'
    ).
    DATA(lv_ops_item_id) = lo_result->get_opsitemid( ).

    " Test delete
    ao_ssm_actions->delete_ops_item( lv_ops_item_id ).

    " Verify deletion
    DATA(lv_found) = ao_ssm_actions->describe_ops_items( lv_ops_item_id ).
    cl_abap_unit_assert=>assert_equals(
      exp = abap_false
      act = lv_found
      msg = |OpsItem { lv_ops_item_id } should have been deleted| ).

  ENDMETHOD.

  METHOD describe_ops_items.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.

    " Create OpsItem first
    " Example: Create with severity 2
    DATA(lo_result) = ao_ssm->createopsitem(
      iv_title = |Test OpsItem Describe { lv_uuid_string }|
      iv_source = 'UnitTest'
      iv_category = 'Security'
      iv_severity = '2'
      iv_description = 'Test OpsItem for describe'
    ).
    DATA(lv_ops_item_id) = lo_result->get_opsitemid( ).

    " Test describe
    DATA(lv_found) = ao_ssm_actions->describe_ops_items( lv_ops_item_id ).

    cl_abap_unit_assert=>assert_equals(
      exp = abap_true
      act = lv_found
      msg = |OpsItem { lv_ops_item_id } should have been found| ).

    " Clean up
    ao_ssm->deleteopsitem( iv_opsitemid = lv_ops_item_id ).

  ENDMETHOD.

  METHOD update_ops_item.
    DATA lv_uuid_string TYPE string.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.

    " Create OpsItem first
    " Example: Initial severity 3
    DATA(lo_result) = ao_ssm->createopsitem(
      iv_title = |Test OpsItem Update { lv_uuid_string }|
      iv_source = 'UnitTest'
      iv_category = 'Performance'
      iv_severity = '3'
      iv_description = 'Test OpsItem for update'
    ).
    DATA(lv_ops_item_id) = lo_result->get_opsitemid( ).

    " Test update
    " Example: Update to severity 1 and status Resolved
    DATA(lv_new_title) = |Updated OpsItem { lv_uuid_string }|.
    ao_ssm_actions->update_ops_item(
      iv_ops_item_id = lv_ops_item_id
      iv_title = lv_new_title
      iv_description = 'Updated description'
      iv_status = 'Resolved'
    ).

    " Verify update
    DATA(lo_get_result) = ao_ssm->getopsitem( iv_opsitemid = lv_ops_item_id ).
    DATA(lo_ops_item) = lo_get_result->get_opsitem( ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_new_title
      act = lo_ops_item->get_title( )
      msg = |OpsItem title should have been updated to { lv_new_title }| ).
    cl_abap_unit_assert=>assert_equals(
      exp = 'Resolved'
      act = lo_ops_item->get_status( )
      msg = 'OpsItem status should be Resolved' ).

    " Clean up
    ao_ssm->deleteopsitem( iv_opsitemid = lv_ops_item_id ).

  ENDMETHOD.

  METHOD wait_for_document_active.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/ssmdocumentstatus.

    WHILE lv_attempt < iv_max_attempts.
      lv_attempt = lv_attempt + 1.

      TRY.
          lv_status = ao_ssm_actions->describe_document( iv_document_name ).

          IF lv_status = 'Active'.
            RETURN.
          ENDIF.

        CATCH /aws1/cx_ssminvaliddocument.
          " Document not yet available
      ENDTRY.

      " Wait 2 seconds between checks
      WAIT UP TO 2 SECONDS.
    ENDWHILE.

    " If we get here, document didn't become active
    cl_abap_unit_assert=>fail(
      msg = |Document { iv_document_name } did not become Active after { iv_max_attempts } attempts| ).

  ENDMETHOD.

ENDCLASS.
