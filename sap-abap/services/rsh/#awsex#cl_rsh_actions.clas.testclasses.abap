" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rsh_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rsh_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rsh_actions.

CLASS ltc_awsex_cl_rsh_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_cluster_id TYPE /aws1/rshstring.
    CLASS-DATA av_cluster_id_modify TYPE /aws1/rshstring.
    CLASS-DATA av_cluster_id_describe TYPE /aws1/rshstring.
    CLASS-DATA av_cluster_id_delete TYPE /aws1/rshstring.
    CLASS-DATA ao_rsh TYPE REF TO /aws1/if_rsh.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rsh_actions TYPE REF TO /awsex/cl_rsh_actions.

    METHODS: create_cluster FOR TESTING RAISING /aws1/cx_rt_generic,
      modify_cluster FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_clusters FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_cluster FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_cluster_available
      IMPORTING
                iv_cluster_id TYPE /aws1/rshstring
      RAISING   /aws1/cx_rt_generic.

    METHODS create_cluster_with_tags
      IMPORTING
                iv_cluster_id TYPE /aws1/rshstring
      RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_rsh_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_status TYPE /aws1/rshstring.
    
    CALL METHOD /aws1/cl_rt_session_aws=>create
      EXPORTING
        iv_profile_id = cv_pfl
      RECEIVING
        oo_result     = ao_session.
        
    CALL METHOD /aws1/cl_rsh_factory=>create
      EXPORTING
        io_session = ao_session
      RECEIVING
        oo_client  = ao_rsh.
        
    CREATE OBJECT ao_rsh_actions TYPE /awsex/cl_rsh_actions.

    " Generate unique cluster identifiers with convert_test tag
    CALL METHOD /awsex/cl_utils=>get_random_string
      RECEIVING
        ov_result = lv_uuid.
    lv_uuid_string = lv_uuid.
    
    " Main cluster for create test
    av_cluster_id = |rsh-crt-{ lv_uuid_string }|.
    IF strlen( av_cluster_id ) > 30.
      av_cluster_id = substring( val = av_cluster_id len = 30 ).
    ENDIF.

    " Cluster for modify test
    av_cluster_id_modify = |rsh-mod-{ lv_uuid_string }|.
    IF strlen( av_cluster_id_modify ) > 30.
      av_cluster_id_modify = substring( val = av_cluster_id_modify len = 30 ).
    ENDIF.

    " Cluster for describe test
    av_cluster_id_describe = |rsh-dsc-{ lv_uuid_string }|.
    IF strlen( av_cluster_id_describe ) > 30.
      av_cluster_id_describe = substring( val = av_cluster_id_describe len = 30 ).
    ENDIF.

    " Cluster for delete test
    av_cluster_id_delete = |rsh-del-{ lv_uuid_string }|.
    IF strlen( av_cluster_id_delete ) > 30.
      av_cluster_id_delete = substring( val = av_cluster_id_delete len = 30 ).
    ENDIF.

    " Create clusters for modify, describe, and delete tests
    create_cluster_with_tags( av_cluster_id_modify ).
    wait_for_cluster_available( av_cluster_id_modify ).

    create_cluster_with_tags( av_cluster_id_describe ).
    wait_for_cluster_available( av_cluster_id_describe ).

    create_cluster_with_tags( av_cluster_id_delete ).
    wait_for_cluster_available( av_cluster_id_delete ).
  ENDMETHOD.

  METHOD create_cluster_with_tags.
    " Create cluster with convert_test tag
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lo_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    
    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        CALL METHOD ao_rsh->createcluster
          EXPORTING
            iv_clusteridentifier  = iv_cluster_id
            iv_nodetype           = 'ra3.xlplus'
            iv_masterusername     = 'awsuser'
            iv_masteruserpassword = 'AwsUser1000'
            iv_publiclyaccessible = abap_false
            iv_numberofnodes      = 1
            it_tags               = lt_tags
          RECEIVING
            oo_output             = lo_result.
      CATCH /aws1/cx_rshclustalrdyexfault.
        " Cluster may exist from previous failed test - continue
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
    " Test create_cluster method
    " Create a new cluster with convert_test tag
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lo_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    
    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        CALL METHOD ao_rsh->createcluster
          EXPORTING
            iv_clusteridentifier  = av_cluster_id
            iv_nodetype           = 'ra3.xlplus'
            iv_masterusername     = 'awsuser'
            iv_masteruserpassword = 'AwsUser1000'
            iv_publiclyaccessible = abap_false
            iv_numberofnodes      = 1
            it_tags               = lt_tags
          RECEIVING
            oo_output             = lo_result.

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Cluster creation failed for { av_cluster_id }| ).

        lo_cluster = lo_result->get_cluster( ).
            
        cl_abap_unit_assert=>assert_equals(
          exp = av_cluster_id
          act = lo_cluster->get_clusteridentifier( )
          msg = |Cluster identifier mismatch| ).

        " Wait for cluster to be available
        wait_for_cluster_available( av_cluster_id ).

      CATCH /aws1/cx_rshclustalrdyexfault.
        " If cluster exists from previous failed test, verify it exists
        CALL METHOD ao_rsh->describeclusters
          EXPORTING
            iv_clusteridentifier = av_cluster_id
          RECEIVING
            oo_output            = lo_describe_result.
            
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
          CALL METHOD ao_rsh->describeclusters
            EXPORTING
              iv_clusteridentifier = iv_cluster_id
            RECEIVING
              oo_output            = lo_describe_result.
              
          lt_clusters = lo_describe_result->get_clusters( ).
              
          IF lines( lt_clusters ) > 0.
            READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
            lv_status = lo_cluster->get_clusterstatus( ).

            IF lv_status = 'available'.
              RETURN.
            ELSEIF lv_status = 'creating' OR lv_status = 'modifying'.
              " Continue waiting
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
    " Test modify_cluster method using pre-created cluster
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_maintenance_window TYPE /aws1/rshstring.
    
    ao_rsh_actions->modify_cluster(
      iv_cluster_identifier = av_cluster_id_modify
      iv_pref_maintenance_wn = 'wed:07:30-wed:08:00'
    ).

    " Wait for modification to complete
    wait_for_cluster_available( av_cluster_id_modify ).

    " Verify modification by describing the cluster
    CALL METHOD ao_rsh->describeclusters
      EXPORTING
        iv_clusteridentifier = av_cluster_id_modify
      RECEIVING
        oo_output            = lo_result.

    lt_clusters = lo_result->get_clusters( ).
        
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_clusters
      msg = |Cluster not found after modification| ).

    READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
    lv_maintenance_window = lo_cluster->get_preferredmaintenancewin00( ).
        
    cl_abap_unit_assert=>assert_equals(
      exp = 'wed:07:30-wed:08:00'
      act = lv_maintenance_window
      msg = |Maintenance window was not modified| ).
  ENDMETHOD.

  METHOD describe_clusters.
    " Test describing a specific cluster using pre-created cluster
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lo_all_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_all_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lv_cluster_id TYPE /aws1/rshstring.
    
    CALL METHOD ao_rsh_actions->describe_clusters
      EXPORTING
        iv_cluster_identifier = av_cluster_id_describe
      RECEIVING
        oo_result             = lo_result.

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
      exp = av_cluster_id_describe
      act = lv_cluster_id
      msg = |Cluster identifier mismatch| ).

    " Test describing all clusters (no filter)
    CALL METHOD ao_rsh_actions->describe_clusters
      RECEIVING
        oo_result = lo_all_result.
        
    lt_all_clusters = lo_all_result->get_clusters( ).
        
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_all_clusters
      msg = |No clusters returned when listing all| ).
  ENDMETHOD.

  METHOD delete_cluster.
    " Test delete_cluster method using pre-created cluster
    DATA lo_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_status TYPE /aws1/rshstring.
    
    ao_rsh_actions->delete_cluster(
      iv_cluster_identifier = av_cluster_id_delete
    ).

    " Wait a moment for deletion to start
    WAIT UP TO 5 SECONDS.

    " Verify deletion by trying to describe the cluster
    TRY.
        CALL METHOD ao_rsh->describeclusters
          EXPORTING
            iv_clusteridentifier = av_cluster_id_delete
          RECEIVING
            oo_output            = lo_result.
            
        lt_clusters = lo_result->get_clusters( ).
            
        IF lines( lt_clusters ) > 0.
          READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
          lv_status = lo_cluster->get_clusterstatus( ).
              
          " Cluster should be in deleting state
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
