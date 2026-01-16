" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rsh_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rsh_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rsh_actions.

CLASS ltc_awsex_cl_rsh_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_cluster_id TYPE /aws1/rshstring.
    CLASS-DATA ao_rsh TYPE REF TO /aws1/if_rsh.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rsh_actions TYPE REF TO /awsex/cl_rsh_actions.
    CLASS-DATA av_setup_failed TYPE abap_bool.
    CLASS-DATA av_setup_error_msg TYPE string.

    METHODS: create_cluster FOR TESTING RAISING /aws1/cx_rt_generic,
      modify_cluster FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_clusters FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_cluster FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_cluster_available
      IMPORTING
                iv_cluster_id TYPE /aws1/rshstring
      RAISING   /aws1/cx_rt_generic.

    CLASS-METHODS create_cluster_with_tags
      IMPORTING
                iv_cluster_id TYPE /aws1/rshstring
      RAISING   /aws1/cx_rt_generic.
    
    CLASS-METHODS cleanup_existing_test_clusters
      RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_rsh_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid_string TYPE string.
    
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
        
    ao_rsh = /aws1/cl_rsh_factory=>create( ao_session ).
        
    ao_rsh_actions = NEW /awsex/cl_rsh_actions( ).

    " Initialize setup status
    av_setup_failed = abap_false.
    CLEAR av_setup_error_msg.

    " First, cleanup any existing test clusters to free up quota
    TRY.
        cleanup_existing_test_clusters( ).
      CATCH /aws1/cx_rt_generic.
        " Continue even if cleanup fails
    ENDTRY.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    
    " Use single shared cluster for most tests to avoid quota issues
    av_cluster_id = |rsh-test-{ lv_uuid_string }|.
    IF strlen( av_cluster_id ) > 30.
      av_cluster_id = substring( val = av_cluster_id len = 30 ).
    ENDIF.

    " Create single shared cluster
    TRY.
        create_cluster_with_tags( av_cluster_id ).
        wait_for_cluster_available( av_cluster_id ).
      CATCH /aws1/cx_rshnoofnodesquotaex00 INTO DATA(lx_quota).
        " Set flag to skip tests if quota exceeded
        av_setup_failed = abap_true.
        av_setup_error_msg = |Redshift node quota exceeded. Please clean up existing clusters with 'convert_test' tag or increase quota.|.
        " Fail the setup explicitly
        cl_abap_unit_assert=>fail( msg = av_setup_error_msg ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_cluster_with_tags.
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lo_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    
    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        lo_result = ao_rsh->createcluster(
          iv_clusteridentifier  = iv_cluster_id
          iv_nodetype           = 'ra3.xlplus'
          iv_masterusername     = 'awsuser'
          iv_masteruserpassword = 'AwsUser1000'
          iv_publiclyaccessible = abap_false
          iv_clustertype        = 'single-node'
          it_tags               = lt_tags
        ).
      CATCH /aws1/cx_rshclustalrdyexfault.
        " Cluster already exists, continue with existing cluster
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_status TYPE /aws1/rshstring.

    " Only attempt cleanup if setup succeeded
    IF av_setup_failed = abap_true.
      RETURN.
    ENDIF.

    " Delete shared cluster
    IF av_cluster_id IS NOT INITIAL.
      TRY.
          ao_rsh->deletecluster(
            iv_clusteridentifier = av_cluster_id
            iv_skipfinalclustersnapshot = abap_true
          ).
          
          " Wait a moment for deletion to begin
          WAIT UP TO 5 SECONDS.
          
          " Verify deletion status
          TRY.
              lo_describe_result = ao_rsh->describeclusters(
                iv_clusteridentifier = av_cluster_id
              ).
              
              lt_clusters = lo_describe_result->get_clusters( ).
              
              IF lines( lt_clusters ) > 0.
                READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
                lv_status = lo_cluster->get_clusterstatus( ).
              ENDIF.
            CATCH /aws1/cx_rshclustnotfoundfault.
              " Cluster not found - already deleted successfully
          ENDTRY.
        CATCH /aws1/cx_rshclustnotfoundfault.
          " Cluster already deleted
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lv_uuid_string TYPE string.
    DATA lv_test_cluster_id TYPE /aws1/rshstring.
    DATA lv_status TYPE /aws1/rshstring.
    
    " Check if setup failed due to quota
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( msg = av_setup_error_msg ).
    ENDIF.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    lv_test_cluster_id = |rsh-crt-{ lv_uuid_string }|.
    IF strlen( lv_test_cluster_id ) > 30.
      lv_test_cluster_id = substring( val = lv_test_cluster_id len = 30 ).
    ENDIF.

    TRY.
        " Test the create_cluster action method
        " Note: Using 2 nodes to demonstrate multi-node cluster creation
        lo_result = ao_rsh_actions->create_cluster(
          iv_cluster_identifier  = lv_test_cluster_id
          iv_node_type           = 'ra3.xlplus'
          iv_master_username     = 'awsuser'
          iv_master_password     = 'AwsUser1000'
          iv_publicly_accessible = abap_false
          iv_number_of_nodes     = 2
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Cluster creation failed for { lv_test_cluster_id }| ).

        lo_cluster = lo_result->get_cluster( ).
            
        cl_abap_unit_assert=>assert_equals(
          exp = lv_test_cluster_id
          act = lo_cluster->get_clusteridentifier( )
          msg = |Cluster identifier mismatch| ).

        " Wait for cluster to become available before deletion
        wait_for_cluster_available( lv_test_cluster_id ).
        
        " Clean up the test cluster
        ao_rsh->deletecluster(
          iv_clusteridentifier = lv_test_cluster_id
          iv_skipfinalclustersnapshot = abap_true
        ).

        " Verify cleanup
        WAIT UP TO 5 SECONDS.
        
        TRY.
            lo_describe_result = ao_rsh->describeclusters(
              iv_clusteridentifier = lv_test_cluster_id
            ).
            
            lt_clusters = lo_describe_result->get_clusters( ).
            
            IF lines( lt_clusters ) > 0.
              READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
              lv_status = lo_cluster->get_clusterstatus( ).
              
              " Verify cluster is deleting
              cl_abap_unit_assert=>assert_equals(
                exp = 'deleting'
                act = lv_status
                msg = |Cluster should be in deleting state after deletion| ).
            ENDIF.
          CATCH /aws1/cx_rshclustnotfoundfault.
            " Cluster not found - already deleted
        ENDTRY.

      CATCH /aws1/cx_rshclustalrdyexfault.
        " If cluster already exists, describe it and verify
        lo_describe_result = ao_rsh_actions->describe_clusters(
          iv_cluster_identifier = lv_test_cluster_id
        ).
            
        lt_clusters = lo_describe_result->get_clusters( ).
            
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_clusters
          msg = |Cluster should exist but not found| ).
          
        " Clean up even if it already existed
        TRY.
            ao_rsh->deletecluster(
              iv_clusteridentifier = lv_test_cluster_id
              iv_skipfinalclustersnapshot = abap_true
            ).
          CATCH /aws1/cx_rshclustnotfoundfault.
            " Already deleted
        ENDTRY.
      CATCH /aws1/cx_rshnoofnodesquotaex00.
        " Quota exceeded - fail the test with clear message
        cl_abap_unit_assert=>fail(
          msg = |Cannot create test cluster: Redshift node quota exceeded. Please clean up existing clusters.| ).
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_cluster_available.
    DATA lv_status TYPE /aws1/rshstring.
    DATA lv_wait_count TYPE i VALUE 0.
    DATA lv_max_waits TYPE i VALUE 60.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.

    DO lv_max_waits TIMES.
      WAIT UP TO 30 SECONDS.
      
      TRY.
          lo_describe_result = ao_rsh->describeclusters(
            iv_clusteridentifier = iv_cluster_id
          ).
              
          lt_clusters = lo_describe_result->get_clusters( ).
              
          IF lines( lt_clusters ) > 0.
            READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
            lv_status = lo_cluster->get_clusterstatus( ).

            IF lv_status = 'available'.
              RETURN.
            ELSEIF lv_status = 'creating' OR lv_status = 'modifying'.
              lv_wait_count = lv_wait_count + 1.
            ELSE.
              cl_abap_unit_assert=>fail(
                msg = |Unexpected cluster status: { lv_status }| ).
            ENDIF.
          ENDIF.
        CATCH /aws1/cx_rshclustnotfoundfault.
          " Cluster not found yet, continue waiting
      ENDTRY.
    ENDDO.

    cl_abap_unit_assert=>fail(
      msg = |Cluster { iv_cluster_id } did not become available within timeout| ).
  ENDMETHOD.

  METHOD modify_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_maintenance_window TYPE /aws1/rshstring.
    
    " Check if setup failed due to quota
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( msg = av_setup_error_msg ).
    ENDIF.

    ao_rsh_actions->modify_cluster(
      iv_cluster_identifier = av_cluster_id
      iv_pref_maintenance_wn = 'wed:07:30-wed:08:00'
    ).

    wait_for_cluster_available( av_cluster_id ).

    lo_result = ao_rsh->describeclusters(
      iv_clusteridentifier = av_cluster_id
    ).

    lt_clusters = lo_result->get_clusters( ).
        
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_clusters
      msg = |Cluster not found after modification| ).

    READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
    lv_maintenance_window = lo_cluster->get_preferredmaintenancewi00( ).
        
    cl_abap_unit_assert=>assert_equals(
      exp = 'wed:07:30-wed:08:00'
      act = lv_maintenance_window
      msg = |Maintenance window was not modified| ).
  ENDMETHOD.

  METHOD describe_clusters.
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lo_all_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_all_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lv_cluster_id TYPE /aws1/rshstring.
    
    " Check if setup failed due to quota
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( msg = av_setup_error_msg ).
    ENDIF.

    lo_result = ao_rsh_actions->describe_clusters(
      iv_cluster_identifier = av_cluster_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Describe clusters failed| ).

    lt_clusters = lo_result->get_clusters( ).
        
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_clusters
      msg = |No clusters returned| ).

    READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
    lv_cluster_id = lo_cluster->get_clusteridentifier( ).
        
    cl_abap_unit_assert=>assert_equals(
      exp = av_cluster_id
      act = lv_cluster_id
      msg = |Cluster identifier mismatch| ).

    lo_all_result = ao_rsh_actions->describe_clusters( ).
        
    lt_all_clusters = lo_all_result->get_clusters( ).
        
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_all_clusters
      msg = |No clusters returned when listing all| ).
  ENDMETHOD.

  METHOD delete_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_status TYPE /aws1/rshstring.
    DATA lv_cleanup_success TYPE abap_bool.
    DATA lv_uuid_string TYPE string.
    DATA lv_delete_cluster_id TYPE /aws1/rshstring.
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lo_create_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    
    " Check if setup failed due to quota
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( msg = av_setup_error_msg ).
    ENDIF.

    " Create a unique cluster ID for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    lv_delete_cluster_id = |rsh-del-{ lv_uuid_string }|.
    IF strlen( lv_delete_cluster_id ) > 30.
      lv_delete_cluster_id = substring( val = lv_delete_cluster_id len = 30 ).
    ENDIF.

    " Create a cluster specifically for deletion test
    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        lo_create_result = ao_rsh->createcluster(
          iv_clusteridentifier  = lv_delete_cluster_id
          iv_nodetype           = 'ra3.xlplus'
          iv_masterusername     = 'awsuser'
          iv_masteruserpassword = 'AwsUser1000'
          iv_publiclyaccessible = abap_false
          iv_clustertype        = 'single-node'
          it_tags               = lt_tags
        ).
        
        " Wait for cluster to be available
        wait_for_cluster_available( lv_delete_cluster_id ).
      CATCH /aws1/cx_rshclustalrdyexfault.
        " Cluster already exists, continue with existing cluster
      CATCH /aws1/cx_rshnoofnodesquotaex00.
        " Quota exceeded - fail the test with clear message
        cl_abap_unit_assert=>fail(
          msg = |Cannot create test cluster for deletion: Redshift node quota exceeded.| ).
    ENDTRY.
    
    " Now test the delete_cluster action method
    ao_rsh_actions->delete_cluster(
      iv_cluster_identifier = lv_delete_cluster_id
    ).

    " Wait for deletion to begin processing
    WAIT UP TO 5 SECONDS.

    " Verify cleanup using DescribeClusters
    TRY.
        lo_result = ao_rsh->describeclusters(
          iv_clusteridentifier = lv_delete_cluster_id
        ).
            
        lt_clusters = lo_result->get_clusters( ).
            
        IF lines( lt_clusters ) = 0.
          " No cluster returned - deletion successful
          lv_cleanup_success = abap_true.
        ELSE.
          " Cluster returned - check if status is 'deleting'
          READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
          lv_status = lo_cluster->get_clusterstatus( ).
              
          IF lv_status = 'deleting'.
            " Cluster is in deleting state - cleanup is in progress
            lv_cleanup_success = abap_true.
          ELSE.
            " Unexpected status
            cl_abap_unit_assert=>fail(
              msg = |Unexpected cluster status: { lv_status }. Expected 'deleting' or cluster not found.| ).
          ENDIF.
        ENDIF.
      CATCH /aws1/cx_rshclustnotfoundfault.
        " Cluster not found - deletion completed successfully
        lv_cleanup_success = abap_true.
    ENDTRY.

    " Assert that cleanup was successful
    cl_abap_unit_assert=>assert_equals(
      exp = abap_true
      act = lv_cleanup_success
      msg = |Cluster deletion verification failed for { lv_delete_cluster_id }| ).
  ENDMETHOD.

  METHOD cleanup_existing_test_clusters.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lv_cluster_id TYPE /aws1/rshstring.
    DATA lv_has_test_tag TYPE abap_bool.
    DATA lv_status TYPE /aws1/rshstring.

    " Get all clusters in the account
    TRY.
        lo_describe_result = ao_rsh->describeclusters( ).
        lt_clusters = lo_describe_result->get_clusters( ).

        LOOP AT lt_clusters INTO lo_cluster.
          lv_has_test_tag = abap_false.
          lv_cluster_id = lo_cluster->get_clusteridentifier( ).
          lv_status = lo_cluster->get_clusterstatus( ).

          " Skip clusters that are already being deleted
          IF lv_status = 'deleting'.
            CONTINUE.
          ENDIF.

          " Check if cluster has convert_test tag
          lt_tags = lo_cluster->get_tags( ).
          LOOP AT lt_tags INTO lo_tag.
            IF lo_tag->get_key( ) = 'convert_test' AND lo_tag->get_value( ) = 'true'.
              lv_has_test_tag = abap_true.
              EXIT.
            ENDIF.
          ENDLOOP.

          " Delete clusters with test tag
          IF lv_has_test_tag = abap_true.
            TRY.
                ao_rsh->deletecluster(
                  iv_clusteridentifier = lv_cluster_id
                  iv_skipfinalclustersnapshot = abap_true
                ).
              CATCH /aws1/cx_rshclustnotfoundfault.
                " Already deleted
              CATCH /aws1/cx_rshinvcluststatefault.
                " Invalid state - skip
            ENDTRY.
          ENDIF.
        ENDLOOP.

        " Wait briefly for deletions to initiate
        IF lv_has_test_tag = abap_true.
          WAIT UP TO 5 SECONDS.
        ENDIF.

      CATCH /aws1/cx_rt_generic.
        " If we can't list clusters, continue anyway
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
