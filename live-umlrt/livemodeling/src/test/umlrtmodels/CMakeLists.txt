FIND_PACKAGE(Java COMPONENTS Runtime REQUIRED)
include_directories(../../pme_core)
include_directories(../../util)

get_filename_component(input_model_file_name ${INPUT_MODEL}  NAME)
get_filename_component(input_model_file_dir   ${INPUT_MODEL}   DIRECTORY)
message("output model dir:" ${input_model_file_dir})
message("output model file: " ${input_model_file_name})

set(output_model "${input_model_file_dir}/Refined_${input_model_file_name}")
message("output model:" ${output_model})

set(GENERATED_SRC_DIR  ${CMAKE_BINARY_DIR}/Refined_${input_model_file_name}_generated_code)

# prepare a command for model transformation 
set(refine_cmd "${Java_JAVA_EXECUTABLE}" -jar "${TRANSFORMER_EXEC}"  -s "${REFINEMENT_SCRIPT}" -m "${INPUT_MODEL}")
set (gen_cmd "${Java_JAVA_EXECUTABLE}" -jar "${CODEGEN_EXEC}"  ${output_model} -t ${TOP_CAPSULE}  -p ${PAPYRUSRT_PLUGINS} -o "${GENERATED_SRC_DIR}/")
message("code gen command is: " ${gen_cmd})

## transformation rule
ADD_CUSTOM_COMMAND(OUTPUT ${output_model} 
                	    COMMAND ${refine_cmd}
                    DEPENDS ${refinmentscript} ${INPUT_MODEL}  
                    COMMENT "Transformation of input model " ${INPUT_MODEL} )

## code generation rule
list(TRANSFORM GENERATED_FILES PREPEND "${GENERATED_SRC_DIR}/src/")
ADD_CUSTOM_COMMAND(OUTPUT   ${GENERATED_FILES}
                    COMMAND ${gen_cmd}
                    #COMMAND ${CMAKE_COMMAND} -E touch "${GENERATED_FILES}"
                    DEPENDS ${output_model}
                   COMMENT "Generating source code from umlfile " ${refine_cmd} )

## compilation of the generated code

### create config file if not exist 
file(GLOB generated_src_cc_files  ${GENERATED_SRC_DIR}/src/*.cc)

add_executable("${TOP_CAPSULE}_Refined"  ${GENERATED_FILES} )
include_directories(${ANTLR4CPP_RUNTIME})
include_directories(${ANTLR4CPP_GENERATED_SRC_DIR})
target_link_libraries("${TOP_CAPSULE}_Refined" ${UMLRTS_LIB} ${Boost_LIBRARIES} pmecore)

### create config file if not exist 
#message("CMAKE_CONFIGURATION_TYPES = " ${CMAKE_CONFIGURATION_TYPES})
#message("CMAKE_CONFIGURATION_TYPES = " ${CMAKE_CFG_INTDIR})
#message("CMAKE_CONFIGURATION_TYPES = " ${CMAKE_DEBUG_POSTFIX})
set(config_file  "${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/${CMAKE_CFG_INTDIR}/config")


#configure_file("${util_inc}/config" "${config_file}" COPYONLY)

add_custom_command(OUTPUT ${config_file}
  COMMAND cp "${CMAKE_CURRENT_SOURCE_DIR}/../../util/config" "${config_file}"
  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/../../util/config"
  COMMENT "create default configuration file"
  VERBATIM)

add_custom_target(configure_files ALL
  DEPENDS ${config_file})	


