" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_SGM_SCENARIO definition
  public
  final
  create public .

public section.

  methods GETTING_STARTED_WITH_SGM
    importing
      !IV_TRAINING_JOB_NAME type /AWS1/SGMTRAININGJOBNAME
      !IV_ROLE_ARN type /AWS1/SGMROLEARN
      !IV_TRN_DATA_S3DATATYPE type /AWS1/SGMS3DATATYPE
      !IV_TRN_DATA_S3DATADISTRIBUTION type /AWS1/SGMS3DATADISTRIBUTION
      !IV_TRN_DATA_S3URI type /AWS1/SGMS3URI
      !IV_TRN_DATA_COMPRESSIONTYPE type /AWS1/SGMCOMPRESSIONTYPE
      !IV_TRN_DATA_CONTENTTYPE type /AWS1/SGMCONTENTTYPE
      !IV_VAL_DATA_S3DATATYPE type /AWS1/SGMS3DATATYPE
      !IV_VAL_DATA_S3DATADISTRIBUTION type /AWS1/SGMS3DATADISTRIBUTION
      !IV_VAL_DATA_S3URI type /AWS1/SGMS3URI
      !IV_VAL_DATA_COMPRESSIONTYPE type /AWS1/SGMCOMPRESSIONTYPE
      !IV_VAL_DATA_CONTENTTYPE type /AWS1/SGMCONTENTTYPE
      !IV_HP_MAX_DEPTH type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_SCALE_POS_WEIGHT type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_NUM_ROUND type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_OBJECTIVE type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_SUBSAMPLE type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_EVAL_METRIC type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_HP_ETA type /AWS1/SGMHYPERPARAMETERVALUE
      !IV_TRAINING_IMAGE type /AWS1/SGMALGORITHMIMAGE
      !IV_TRAINING_INPUT_MODE type /AWS1/SGMTRAININGINPUTMODE
      !IV_INSTANCE_COUNT type /AWS1/SGMTRAININGINSTANCECOUNT
      !IV_INSTANCE_TYPE type /AWS1/SGMTRAININGINSTANCETYPE
      !IV_VOLUME_SIZEINGB type /AWS1/SGMVOLUMESIZEINGB
      !IV_S3_OUTPUT_PATH type /AWS1/SGMS3URI
      !IV_MAX_RUNTIME_IN_SECONDS type /AWS1/SGMMAXRUNTIMEINSECONDS
      !IV_EP_INSTANCE_TYPE type /AWS1/SGMINSTANCETYPE
      !IV_EP_INITIAL_INSTANCE_COUNT type /AWS1/SGMINITIALTASKCOUNT
      !IV_MODEL_NAME type /AWS1/SGMMODELNAME
      !IV_EP_NAME type /AWS1/SGMENDPOINTNAME
      !IV_EP_CFG_NAME type /AWS1/SGMENDPOINTCONFIGNAME
      !IV_EP_VARIANT_NAME type /AWS1/SGMVARIANTNAME
    exporting
      !OO_EP_OUTPUT type ref to /AWS1/CL_SGMCREATEENDPTOUTPUT .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_SGM_SCENARIO IMPLEMENTATION.


  METHOD getting_started_with_sgm.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "This example scenario contains the following actions."
    " 1. Model Training "
    " 2. Model Creation "
    " 3. Create endpoint configuration "
    " 4. Create endpoint "
    " 5. Delete endpoint "
    " 6. Delete an endpoint configuration "
    " 7. Delete model "

    "snippet-start:[sgm.abapv1.getting_started_with_sgm]

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_production_variants TYPE REF TO /aws1/cl_sgmproductionvariant.
    DATA lo_ep_config_result TYPE REF TO /aws1/cl_sgmcreateendptcfgout.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lt_production_variants TYPE /aws1/cl_sgmproductionvariant=>tt_productionvariantlist.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lv_model_data_url TYPE /aws1/sgmurl.

    lv_model_data_url = iv_s3_output_path && iv_training_job_name && '/output/model.tar.gz'.

    "Create ABAP internal table for hyperparameters based on input variables."
    "These hyperparameters are based on Amazon SageMaker built-in algorithm - XGBoost"
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = iv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP internal table for data based on input variables."
    "Training data"
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = iv_trn_data_s3datatype
        iv_s3datadistributiontype = iv_trn_data_s3datadistribution
        iv_s3uri                  = iv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource EXPORTING io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = iv_trn_data_compressiontype
        iv_contenttype     = iv_trn_data_contenttype.
    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data"
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = iv_val_data_s3datatype
        iv_s3datadistributiontype = iv_val_data_s3datadistribution
        iv_s3uri                  = iv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource EXPORTING io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = iv_val_data_compressiontype
        iv_contenttype     = iv_val_data_contenttype.
    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables."
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = iv_training_image
        iv_traininginputmode = iv_training_input_mode.

    "Create an ABAP object for resource configuration"
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = iv_instance_count
        iv_instancetype   = iv_instance_type
        iv_volumesizeingb = iv_volume_sizeingb.

    "Create an ABAP object for output data configuration"
    CREATE OBJECT lo_output_data_config EXPORTING iv_s3outputpath = iv_s3_output_path.

    "Create ABAP object for stopping condition"
    CREATE OBJECT lo_stopping_condition EXPORTING iv_maxruntimeinseconds = iv_max_runtime_in_seconds.

    TRY.
        lo_sgm->createtrainingjob(
          iv_trainingjobname           = iv_training_job_name
          iv_rolearn                   = iv_role_arn
          it_hyperparameters           = lt_hyperparameters
          it_inputdataconfig           = lt_input_data_config
          io_algorithmspecification    = lo_algorithm_specification
          io_outputdataconfig          = lo_output_data_config
          io_resourceconfig            = lo_resource_config
          io_stoppingcondition         = lo_stopping_condition
        ).
        MESSAGE 'Training job created' TYPE 'I'.
      CATCH /aws1/cx_sgmresourceinuse.
        MESSAGE 'Resource being accessed is in use.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcenotfound.
        MESSAGE 'Resource being access is not found.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources' TYPE 'E'.
    ENDTRY.

    "Wait for training job to be completed"
    lo_training_result = lo_sgm->describetrainingjob( iv_trainingjobname = iv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds"
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = lo_sgm->describetrainingjob( iv_trainingjobname = iv_training_job_name ).
    ENDWHILE.

    "Create ABAP object for the container image based on input variables."
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = iv_training_image
        iv_modeldataurl = lv_model_data_url.

    "Create a Sagemaker model"
    TRY.
        lo_sgm->createmodel(
          iv_executionrolearn = iv_role_arn
          iv_modelname = iv_model_name
          io_primarycontainer = lo_primarycontainer
        ).
        MESSAGE 'Model created' TYPE 'I'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources' TYPE 'E'.
    ENDTRY.

    "Create an endpoint production variant"
    CREATE OBJECT lo_production_variants
      EXPORTING
        iv_variantname          = iv_ep_variant_name
        iv_modelname            = iv_model_name
        iv_initialinstancecount = iv_ep_initial_instance_count
        iv_instancetype         = iv_ep_instance_type.
    INSERT lo_production_variants INTO TABLE lt_production_variants.

    TRY.
        "Create an endpoint configuration"
        lo_ep_config_result = lo_sgm->createendpointconfig(
          iv_endpointconfigname = iv_ep_cfg_name
          it_productionvariants = lt_production_variants
        ).
        MESSAGE 'Endpoint configuration created' TYPE 'I'.

        "Create an endpoint"
        oo_ep_output = lo_sgm->createendpoint(        " oo_ep_output is returned for testing purpose "
            iv_endpointconfigname = iv_ep_cfg_name
            iv_endpointname = iv_ep_name
        ).
        MESSAGE 'Endpoint created' TYPE 'I'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources' TYPE 'E'.
    ENDTRY.

    "Wait for endpoint creation to be completed"
    DATA(lo_endpoint_result) = lo_sgm->describeendpoint( iv_endpointname = iv_ep_name ).
    WHILE lo_endpoint_result->get_endpointstatus( ) <> 'InService'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds"
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_endpoint_result = lo_sgm->describeendpoint( iv_endpointname = iv_ep_name ).
    ENDWHILE.

    TRY.
        "Delete an endpoint"
        lo_sgm->deleteendpoint(
            iv_endpointname = iv_ep_name
        ).
        MESSAGE 'Endpoint deleted' TYPE 'I'.

        "Delete an endpoint configuration"
        lo_sgm->deleteendpointconfig(
          iv_endpointconfigname = iv_ep_cfg_name
        ).
        MESSAGE 'Endpoint configuration deleted' TYPE 'I'.

        "Delete model"
        lo_sgm->deletemodel(
                  iv_modelname = iv_model_name
                ).
        MESSAGE 'Model deleted' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_endpointconfig_exception).
        DATA(lv_endpointconfig_error) = |"{ lo_endpointconfig_exception->av_err_code }" - { lo_endpointconfig_exception->av_err_msg }|.
        MESSAGE lv_endpointconfig_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.getting_started_with_sgm]

  ENDMETHOD.
ENDCLASS.
