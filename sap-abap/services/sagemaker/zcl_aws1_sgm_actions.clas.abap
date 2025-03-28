" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS zcl_aws1_sgm_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_endpoint
      IMPORTING
      !iv_model_name TYPE /aws1/sgmmodelname
      !iv_endpoint_name TYPE /aws1/sgmendpointname
      !iv_endpoint_config_name TYPE /aws1/sgmendpointconfigname
      !iv_instance_type TYPE /aws1/sgminstancetype
      !iv_initial_instance_count TYPE /aws1/sgminitialtaskcount
      !iv_variant_name TYPE /aws1/sgmvariantname
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmcreateendptoutput .
    METHODS create_model
      IMPORTING
      !iv_model_name TYPE /aws1/sgmmodelname
      !iv_execution_role_arn TYPE /aws1/sgmrolearn
      !iv_model_data_url TYPE /aws1/sgmurl
      !iv_container_image TYPE /aws1/sgmcontainermode
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput .
    METHODS create_training_job
      IMPORTING
      !iv_training_job_name TYPE /aws1/sgmtrainingjobname
      !iv_role_arn TYPE /aws1/sgmrolearn
      !iv_trn_data_s3datatype TYPE /aws1/sgms3datatype
      !iv_trn_data_s3datadistribution TYPE /aws1/sgms3datadistribution
      !iv_trn_data_s3uri TYPE /aws1/sgms3uri
      !iv_trn_data_compressiontype TYPE /aws1/sgmcompressiontype
      !iv_trn_data_contenttype TYPE /aws1/sgmcontenttype
      !iv_val_data_s3datatype TYPE /aws1/sgms3datatype
      !iv_val_data_s3datadistribution TYPE /aws1/sgms3datadistribution
      !iv_val_data_s3uri TYPE /aws1/sgms3uri
      !iv_val_data_compressiontype TYPE /aws1/sgmcompressiontype
      !iv_val_data_contenttype TYPE /aws1/sgmcontenttype
      !iv_hp_max_depth TYPE /aws1/sgmhyperparametervalue
      !iv_hp_scale_pos_weight TYPE /aws1/sgmhyperparametervalue
      !iv_hp_num_round TYPE /aws1/sgmhyperparametervalue
      !iv_hp_objective TYPE /aws1/sgmhyperparametervalue
      !iv_hp_subsample TYPE /aws1/sgmhyperparametervalue
      !iv_hp_eval_metric TYPE /aws1/sgmhyperparametervalue
      !iv_hp_eta TYPE /aws1/sgmhyperparametervalue
      !iv_training_image TYPE /aws1/sgmalgorithmimage
      !iv_training_input_mode TYPE /aws1/sgmtraininginputmode
      !iv_instance_count TYPE /aws1/sgmtraininginstancecount
      !iv_instance_type TYPE /aws1/sgmtraininginstancetype
      !iv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb
      !iv_s3_output_path TYPE /aws1/sgms3uri
      !iv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmcreatetrnjobrsp .
    METHODS create_transform_job
      IMPORTING
      !iv_tf_job_name TYPE /aws1/sgmtransformjobname
      !iv_tf_data_s3datatype TYPE /aws1/sgms3datatype
      !iv_tf_data_s3uri TYPE /aws1/sgms3uri
      !iv_tf_data_compressiontype TYPE /aws1/sgmcompressiontype
      !iv_tf_data_contenttype TYPE /aws1/sgmcontenttype
      !iv_instance_count TYPE /aws1/sgmtraininginstancecount
      !iv_instance_type TYPE /aws1/sgmtraininginstancetype
      !iv_s3_output_path TYPE /aws1/sgms3uri
      !iv_tf_model_name TYPE /aws1/sgmmodelname
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmcretransformjobrsp .
    METHODS delete_endpoint
      IMPORTING
      !iv_endpoint_name TYPE /aws1/sgmendpointname
      !iv_endpoint_config_name TYPE /aws1/sgmendpointconfigname .
    METHODS delete_model
      IMPORTING
      !iv_model_name TYPE /aws1/sgmmodelname .
    METHODS describe_training_job
      IMPORTING
      !iv_training_job_name TYPE /aws1/sgmtrainingjobname
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp .
    METHODS list_algorithms
      IMPORTING
      !iv_name_contains TYPE /aws1/sgmnamecontains
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmlistalgsoutput .
    METHODS list_models
      IMPORTING
      !iv_name_contains TYPE /aws1/sgmmodelnamecontains
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput .
    METHODS list_notebook_instances
      IMPORTING
      !iv_name_contains TYPE /aws1/sgmnotebookinstnamecon00
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmlstnotebookinsts01 .
    METHODS list_training_jobs
      IMPORTING
      !iv_name_contains TYPE /aws1/sgmtrainingjobname
      !iv_max_results TYPE /aws1/sgmmaxresults
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_SGM_ACTIONS IMPLEMENTATION.


  METHOD create_endpoint.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.create_endpoint]

    DATA lt_production_variants TYPE /aws1/cl_sgmproductionvariant=>tt_productionvariantlist.
    DATA lo_production_variants TYPE REF TO /aws1/cl_sgmproductionvariant.
    DATA oo_ep_config_result TYPE REF TO /aws1/cl_sgmcreateendptcfgout.

    "Create a production variant as an ABAP object."
    "Identifies a model that you want to host and the resources chosen to deploy for hosting it."
    lo_production_variants = NEW #( iv_variantname = iv_variant_name
                                    iv_modelname = iv_model_name
                                    iv_initialinstancecount = iv_initial_instance_count
                                    iv_instancetype = iv_instance_type ).

    INSERT lo_production_variants INTO TABLE lt_production_variants.

    "Create an endpoint configuration."
    TRY.
        oo_ep_config_result = lo_sgm->createendpointconfig(
          iv_endpointconfigname = iv_endpoint_config_name
          it_productionvariants = lt_production_variants ).
        MESSAGE 'Endpoint configuration created.' TYPE 'I'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
    ENDTRY.

    "Create an endpoint."
    TRY.
        oo_result = lo_sgm->createendpoint(     " oo_result is returned for testing purposes. "
            iv_endpointconfigname = iv_endpoint_config_name
            iv_endpointname = iv_endpoint_name ).
        MESSAGE 'Endpoint created.' TYPE 'I'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
    ENDTRY.

    "snippet-end:[sgm.abapv1.create_endpoint]

  ENDMETHOD.


  METHOD create_model.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.create_model]

    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.

    "Create an ABAP object for the container image based on input variables."
    lo_primarycontainer = NEW #( iv_image = iv_container_image
                                 iv_modeldataurl = iv_model_data_url ).

    "Create an Amazon SageMaker model."
    TRY.
        oo_result = lo_sgm->createmodel(        " oo_result is returned for testing purposes. "
          iv_executionrolearn = iv_execution_role_arn
          iv_modelname = iv_model_name
          io_primarycontainer = lo_primarycontainer ).
        MESSAGE 'Model created.' TYPE 'I'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.create_model]
  ENDMETHOD.


  METHOD create_training_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.create_training_job]

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_output_data_config TYPE REF TO /aws1/cl_sgmoutputdataconfig.
    DATA lo_stopping_condition TYPE REF TO /aws1/cl_sgmstoppingcondition.

    "Create ABAP internal table for hyperparameters based on input variables."
    "These hyperparameters are based on the Amazon SageMaker built-in algorithm, XGBoost."
    lo_hyperparameters_w = NEW #( iv_value = iv_hp_max_depth ).
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_eta ).
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_eval_metric ).
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_scale_pos_weight ).
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_subsample ).
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_objective ).
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    lo_hyperparameters_w = NEW #( iv_value = iv_hp_num_round ).
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for training data sources."
    lo_trn_s3datasource = NEW #( iv_s3datatype = iv_trn_data_s3datatype
                                 iv_s3datadistributiontype = iv_trn_data_s3datadistribution
                                 iv_s3uri = iv_trn_data_s3uri ).

    lo_trn_datasource = NEW #( io_s3datasource = lo_trn_s3datasource ).

    lo_trn_channel = NEW #( iv_channelname = 'train'
                            io_datasource = lo_trn_datasource
                            iv_compressiontype = iv_trn_data_compressiontype
                            iv_contenttype = iv_trn_data_contenttype ).

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Create ABAP objects for validation data sources."
    lo_val_s3datasource = NEW #( iv_s3datatype = iv_val_data_s3datatype
                                 iv_s3datadistributiontype = iv_val_data_s3datadistribution
                                 iv_s3uri = iv_val_data_s3uri ).

    lo_val_datasource = NEW #( io_s3datasource = lo_val_s3datasource ).

    lo_val_channel = NEW #( iv_channelname = 'validation'
                            io_datasource = lo_val_datasource
                            iv_compressiontype = iv_val_data_compressiontype
                            iv_contenttype = iv_val_data_contenttype ).

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification."
    lo_algorithm_specification = NEW #( iv_trainingimage = iv_training_image
                                        iv_traininginputmode = iv_training_input_mode ).

    "Create an ABAP object for resource configuration."
    lo_resource_config = NEW #( iv_instancecount = iv_instance_count
                                iv_instancetype = iv_instance_type
                                iv_volumesizeingb = iv_volume_sizeingb ).

    "Create an ABAP object for output data configuration."
    lo_output_data_config = NEW #( iv_s3outputpath = iv_s3_output_path ).

    "Create an ABAP object for stopping condition."
    lo_stopping_condition = NEW #( iv_maxruntimeinseconds = iv_max_runtime_in_seconds ).

    "Create a training job."
    TRY.
        oo_result = lo_sgm->createtrainingjob(    " oo_result is returned for testing purposes. "
          iv_trainingjobname           = iv_training_job_name
          iv_rolearn                   = iv_role_arn
          it_hyperparameters           = lt_hyperparameters
          it_inputdataconfig           = lt_input_data_config
          io_algorithmspecification    = lo_algorithm_specification
          io_outputdataconfig          = lo_output_data_config
          io_resourceconfig            = lo_resource_config
          io_stoppingcondition         = lo_stopping_condition ).
        MESSAGE 'Training job created.' TYPE 'I'.
      CATCH /aws1/cx_sgmresourceinuse.
        MESSAGE 'Resource being accessed is in use.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcenotfound.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.create_training_job]

  ENDMETHOD.


  METHOD create_transform_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.create_transform_job]

    DATA lo_transforminput TYPE REF TO /aws1/cl_sgmtransforminput.
    DATA lo_transformoutput TYPE REF TO /aws1/cl_sgmtransformoutput.
    DATA lo_transformresources TYPE REF TO /aws1/cl_sgmtransformresources.
    DATA lo_datasource  TYPE REF TO /aws1/cl_sgmtransformdatasrc.
    DATA lo_s3datasource  TYPE REF TO /aws1/cl_sgmtransforms3datasrc.

    "Create an ABAP object for an Amazon Simple Storage Service (Amazon S3) data source."
    lo_s3datasource = NEW #( iv_s3uri = iv_tf_data_s3uri
                             iv_s3datatype = iv_tf_data_s3datatype ).

    "Create an ABAP object for data source."
    lo_datasource = NEW #( io_s3datasource = lo_s3datasource ).

    "Create an ABAP object for transform data source."
    lo_transforminput = NEW #( io_datasource = lo_datasource
                               iv_contenttype = iv_tf_data_contenttype
                               iv_compressiontype = iv_tf_data_compressiontype ).

    "Create an ABAP object for resource configuration."
    lo_transformresources = NEW #( iv_instancecount = iv_instance_count
                                   iv_instancetype = iv_instance_type ).

    "Create an ABAP object for output data configuration."
    lo_transformoutput = NEW #( iv_s3outputpath = iv_s3_output_path ).

    "Create a transform job."
    TRY.
        oo_result = lo_sgm->createtransformjob(     " oo_result is returned for testing purposes. "
            iv_modelname = iv_tf_model_name
            iv_transformjobname = iv_tf_job_name
            io_transforminput = lo_transforminput
            io_transformoutput = lo_transformoutput
            io_transformresources = lo_transformresources ).
        MESSAGE 'Transform job created.' TYPE 'I'.
      CATCH /aws1/cx_sgmresourceinuse.
        MESSAGE 'Resource being accessed is in use.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcenotfound.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.create_transform_job]

  ENDMETHOD.


  METHOD delete_endpoint.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.delete_endpoint]
    "Delete an endpoint."
    TRY.
        lo_sgm->deleteendpoint(
            iv_endpointname = iv_endpoint_name ).
        MESSAGE 'Endpoint configuration deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_endpoint_exception).
        DATA(lv_endpoint_error) = |"{ lo_endpoint_exception->av_err_code }" - { lo_endpoint_exception->av_err_msg }|.
        MESSAGE lv_endpoint_error TYPE 'E'.
    ENDTRY.

    "Delete an endpoint configuration."
    TRY.
        lo_sgm->deleteendpointconfig(
          iv_endpointconfigname = iv_endpoint_config_name ).
        MESSAGE 'Endpoint deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_endpointconfig_exception).
        DATA(lv_endpointconfig_error) = |"{ lo_endpointconfig_exception->av_err_code }" - { lo_endpointconfig_exception->av_err_msg }|.
        MESSAGE lv_endpointconfig_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.delete_endpoint]

  ENDMETHOD.


  METHOD delete_model.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.delete_model]
    TRY.
        lo_sgm->deletemodel(
                  iv_modelname = iv_model_name ).
        MESSAGE 'Model deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.delete_model]


  ENDMETHOD.


  METHOD describe_training_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.describe_training_job]
    TRY.
        oo_result = lo_sgm->describetrainingjob(      " oo_result is returned for testing purposes. "
          iv_trainingjobname = iv_training_job_name ).
        MESSAGE 'Retrieved description of training job.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.describe_training_job]

  ENDMETHOD.


  METHOD list_algorithms.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.list_algorithms]
    TRY.
        oo_result = lo_sgm->listalgorithms(         " oo_result is returned for testing purposes. "
          iv_namecontains = iv_name_contains ).
        MESSAGE 'Retrieved list of algorithms.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.list_algorithms]

  ENDMETHOD.


  METHOD list_models.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.list_models]
    TRY.
        oo_result = lo_sgm->listmodels(           " oo_result is returned for testing purposes. "
          iv_namecontains = iv_name_contains ).
        MESSAGE 'Retrieved list of models.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.list_models]

  ENDMETHOD.


  METHOD list_notebook_instances.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.list_notebook_instances]
    TRY.
        oo_result = lo_sgm->listnotebookinstances(        " oo_result is returned for testing purposes. "
          iv_namecontains = iv_name_contains ).
        MESSAGE 'Retrieved list of notebook instances.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.list_notebook_instances]
  ENDMETHOD.


  METHOD list_training_jobs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sgm) = /aws1/cl_sgm_factory=>create( lo_session ).

    "snippet-start:[sgm.abapv1.list_training_jobs]
    TRY.
        oo_result = lo_sgm->listtrainingjobs(       " oo_result is returned for testing purposes. "
          iv_namecontains = iv_name_contains
          iv_maxresults = iv_max_results ).
        MESSAGE 'Retrieved list of training jobs.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[sgm.abapv1.list_training_jobs]
  ENDMETHOD.
ENDCLASS.
