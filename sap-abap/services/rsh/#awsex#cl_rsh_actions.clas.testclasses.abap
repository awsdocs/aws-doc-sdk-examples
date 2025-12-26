" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rsh_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rsh_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rsh_actions.

CLASS ltc_awsex_cl_rsh_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_cluster_id TYPE /aws1/rshstring.
    CLASS-DATA av_delete_test_cluster TYPE /aws1/rshstring.
    CLASS-DATA ao_rsh TYPE REF TO /aws1/if_rsh.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rsh_actions TYPE REF TO /awsex/cl_rsh_actions.

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

ENDCLASS.

CLASS ltc_awsex_cl_rsh_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid_string TYPE string.
    
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
        
    ao_rsh = /aws1/cl_rsh_factory=>create( ao_session ).
        
    CREATE OBJECT ao_rsh_actions TYPE /awsex/cl_rsh_actions.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    
    av_cluster_id = |rsh-main-{ lv_uuid_string }|.
    IF strlen( av_cluster_id ) > 30.
      av_cluster_id = substring( val = av_cluster_id len = 30 ).
    ENDIF.

    av_delete_test_cluster = |rsh-del-{ lv_uuid_string }|.
    IF strlen( av_delete_test_cluster ) > 30.
      av_delete_test_cluster = substring( val = av_delete_test_cluster len = 30 ).
    ENDIF.

    create_cluster_with_tags( av_cluster_id ).
    wait_for_cluster_available( av_cluster_id ).

    create_cluster_with_tags( av_delete_test_cluster ).
    wait_for_cluster_available( av_delete_test_cluster ).
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
          iv_nodetype           = 'dc2.large'
          iv_masterusername     = 'awsuser'
          iv_masteruserpassword = 'AwsUser1000'
          iv_publiclyaccessible = abap_false
          it_tags               = lt_tags
        ).
      CATCH /aws1/cx_rshclustalrdyexfault.
        MESSAGE 'Cluster already exists, continuing with existing cluster.' TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Note: Redshift clusters take a long time to delete, so we tag them with 'convert_test'
    " and let the user clean them up manually based on the tag.
    " This prevents test timeout issues.
    MESSAGE 'Redshift clusters tagged with convert_test=true should be manually cleaned up.' TYPE 'I'.
  ENDMETHOD.

  METHOD create_cluster.
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lo_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lv_uuid_string TYPE string.
    DATA lv_test_cluster_id TYPE /aws1/rshstring.
    
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    lv_test_cluster_id = |rsh-crt-{ lv_uuid_string }|.
    IF strlen( lv_test_cluster_id ) > 30.
      lv_test_cluster_id = substring( val = lv_test_cluster_id len = 30 ).
    ENDIF.
    
    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        lo_result = ao_rsh->createcluster(
          iv_clusteridentifier  = lv_test_cluster_id
          iv_nodetype           = 'dc2.large'
          iv_masterusername     = 'awsuser'
          iv_masteruserpassword = 'AwsUser1000'
          iv_publiclyaccessible = abap_false
          it_tags               = lt_tags
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Cluster creation failed for { lv_test_cluster_id }| ).

        lo_cluster = lo_result->get_cluster( ).
            
        cl_abap_unit_assert=>assert_equals(
          exp = lv_test_cluster_id
          act = lo_cluster->get_clusteridentifier( )
          msg = |Cluster identifier mismatch| ).

        wait_for_cluster_available( lv_test_cluster_id ).
        
        ao_rsh->deletecluster(
          iv_clusteridentifier = lv_test_cluster_id
          iv_skipfinalclustersnapshot = abap_true
        ).

      CATCH /aws1/cx_rshclustalrdyexfault.
        lo_describe_result = ao_rsh->describeclusters(
          iv_clusteridentifier = lv_test_cluster_id
        ).
            
        lt_clusters = lo_describe_result->get_clusters( ).
            
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_clusters
          msg = |Cluster should exist but not found| ).
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
    
    ao_rsh_actions->delete_cluster(
      iv_cluster_identifier = av_delete_test_cluster
    ).

    WAIT UP TO 5 SECONDS.

    TRY.
        lo_result = ao_rsh->describeclusters(
          iv_clusteridentifier = av_delete_test_cluster
        ).
            
        lt_clusters = lo_result->get_clusters( ).
            
        IF lines( lt_clusters ) > 0.
          READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
          lv_status = lo_cluster->get_clusterstatus( ).
              
          cl_abap_unit_assert=>assert_equals(
            exp = 'deleting'
            act = lv_status
            msg = |Cluster should be in deleting state| ).
        ENDIF.
      CATCH /aws1/cx_rshclustnotfoundfault.
        " Cluster already deleted - this is expected
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
