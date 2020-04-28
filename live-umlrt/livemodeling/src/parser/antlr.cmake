## original source code copied from https://github.com/antlr/antlr4/blob/master/runtime/Cpp/cmake/ExternalAntlr4Cpp.cmake
cmake_minimum_required(VERSION 3.5)

find_package(Java QUIET COMPONENTS Runtime)

set(ANTLR4CPP_LINK_DIRS "${ANTLR4CPP_ROOT}/lib/" CACHE STRING "")
set(ANTLR4CPP_RUNTIME "${ANTLR4CPP_ROOT}/antlr4-runtime/" CACHE STRING "")
set(antlr4_shared ${ANTLR4CPP_LINK_DIRS}/libantlr4-runtime.dylib CACHE STRING "")
set(antlr4_static ${ANTLR4CPP_LINK_DIRS}/libantlr4-runtime.a CACHE STRING "")

list(APPEND ANTLR4CPP_INCLUDE_DIRS ${ANTLR4CPP_RUNTIME} CACHE STRING "")
foreach(src_path misc atn dfa tree support)
  list(APPEND ANTLR4CPP_INCLUDE_DIRS ${ANTLR4CPP_RUNTIME}/${src_path})
endforeach(src_path)

set(ANTLR4CPP_JAR_LOCATION ${ANTLR_EXECUTABLE} CACHE STRING "")
if (NOT ANTLR4CPP_GENERATED_SRC_DIR)
  set(ANTLR4CPP_GENERATED_SRC_DIR ${CMAKE_BINARY_DIR}/antlr4cpp_generated_src CACHE STRING "")
endif(NOT ANTLR4CPP_GENERATED_SRC_DIR)


### macro to generate c++ from grammar

macro(antlr4cpp_process_grammar
    antlr4cpp_project
    antlr4cpp_project_namespace
    antlr4cpp_grammar)
  
  if(EXISTS "${ANTLR4CPP_JAR_LOCATION}")
    message(STATUS "Found antlr tool: ${ANTLR4CPP_JAR_LOCATION}")
  else()
    message(FATAL_ERROR "Unable to find antlr tool. ANTLR4CPP_JAR_LOCATION:${ANTLR4CPP_JAR_LOCATION}")
  endif()

  set (gen_cmd "${Java_JAVA_EXECUTABLE}" -jar "${ANTLR4CPP_JAR_LOCATION}" -Werror -Dlanguage=Cpp -listener -visitor -o "${ANTLR4CPP_GENERATED_SRC_DIR}/" -package ${antlr4cpp_project_namespace}  "${antlr4cpp_grammar}")
  #set (git_arg "-Werror -Dlanguage=Cpp -listener -visitor -o "${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}" -package ${antlr4cpp_project_namespace}  "${antlr4cpp_grammar_parser}"")
  #message(STATUS "git cmd: ${gen_cmd}")
  #message("try to generate files :" ${gen_cmd})
  #execute_process(COMMAND ${gen_cmd} 
  #WORKING_DIRECTORY ${PROJECT_SOURCE_DIR}
  #RESULT_VARIABLE gen_result
  #OUTPUT_VARIABLE gen_output)
  #message("code is generated")
  #message(STATUS "git ver[${gen_result}]: ${gen_output}")
  ## use custom command
  get_filename_component(grammerFileName  ${antlr4cpp_grammar} NAME)
  string(REPLACE "\.g4" "" grammerName ${grammerFileName})
  message("grammar name is " ${grammerName})
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}BaseListener.cpp)
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}BaseVisitor.cpp)
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}Lexer.cpp)
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}Listener.cpp)
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}Visitor.cpp)
  #list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/${grammerName}Parser.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}BaseListener.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}BaseVisitor.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Lexer.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Listener.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Visitor.cpp)
  list(APPEND cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Parser.cpp)
  #message("output is " ${cmdOut})
 # message("command is" ${gen_cmd})
 # message("java name is" ${Java_JAVA_EXECUTABLE})
  ADD_CUSTOM_COMMAND(OUTPUT ${cmdOut}
                    COMMAND ${gen_cmd}
                    #COMMAND ${CMAKE_COMMAND} -E touch generated.timestamp
                    DEPENDS ${antlr4cpp_grammar}
                    COMMENT "Generating source code from grammar" ${antlr4cpp_grammar} )
  
  if(${gen_result}!=0)
    message(FATAL_ERROR "Unable to generate code for grammer file: " ${antlr4cpp_grammar_parser})
  endif(${gen_result}!=0)
    
   # Find all the input files
  #FILE(GLOB generated_files ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}/*.cpp)

  # export generated cpp files into list
  foreach(generated_file ${cmdOut})
    if ( NOT generated_file  IN_LIST antlr4cpp_src_files_${antlr4cpp_project_namespace})
        list(APPEND antlr4cpp_src_files_${antlr4cpp_project_namespace} ${generated_file})
        if (NOT CMAKE_CXX_COMPILER_ID MATCHES "MSVC")
        set_source_files_properties(
         ${generated_file}
           PROPERTIES
           COMPILE_FLAGS -Wno-overloaded-virtual
          )
        endif ()
      endif()
  endforeach(generated_file)
  add_library(${grammerName} ${cmdOut})
  
  target_link_libraries(${grammerName} ${antlr4_static})
  #message(STATUS "Antlr4Cpp  ${antlr4cpp_project_namespace} Generated: ${generated_files}")

  # export generated include directory
  #set(antlr4cpp_include_dirs_${antlr4cpp_project_namespace} ${ANTLR4CPP_GENERATED_SRC_DIR}/ CACHE STRING "")
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}BaseListener.cpp)
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}BaseVisitor.cpp)
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Lexer.cpp)
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Listener.cpp)
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Visitor.cpp)
  list(REMOVE_ITEM cmdOut ${ANTLR4CPP_GENERATED_SRC_DIR}/${grammerName}Parser.cpp)
  #message(STATUS "Antlr4Cpp ${antlr4cpp_project_namespace} include: ${ANTLR4CPP_GENERATED_SRC_DIR}/${antlr4cpp_project_namespace}")

endmacro()
    
