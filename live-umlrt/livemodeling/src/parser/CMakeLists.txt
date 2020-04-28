include(antlr.cmake)

FILE(GLOB grammar_files *.g4)
#FILE(GLOB grammar_lexer_files *lexer.g4)
  
foreach(grammar_file ${grammar_files})
	#message("genrating parser for grammar file: " ${grammar_file})
	get_filename_component(grammerFileName  ${grammar_file} NAME)
  	string(REPLACE "\.g4" "" grammerName ${grammerFileName})
	antlr4cpp_process_grammar("pmd" "pmd"  ${grammar_file}) ## we should specify lexer if we have any as third argument, second argument is namespace
	list(APPEND grammerLibs ${grammerName})
endforeach(grammar_file grammar_files)

#message("antlr include dirs" ${ANTLR4CPP_INCLUDE_DIRS})
include_directories(${ANTLR4CPP_INCLUDE_DIRS})
#message("antrl include dir: " ${ANTLR4CPP_INCLUDE_DIRS})
set(antlr_generated_src_dir ${ANTLR4CPP_GENERATED_SRC_DIR})
include_directories(${antlr_generated_src_dir})
FILE(GLOB gen_src_files ${antlr_generated_src_dir}/*.cpp)

#message("source folder are: " ${antlr_generated_src_dir})
#message("source files are: " ${src_files})
link_directories(${ANTLR4CPP_LIBS})
#add_library(pre_parser_generated ${gen_src_files})

#add_dependencies (pre_parser_generated grammar_files)

add_custom_target(
    parser ALL
    DEPENDS ${grammerLibs}
    )

#set_target_properties(pre_parser_generated PROPERTIES LANGUAGE CXX)
#set_target_properties(pre_parser_generated PROPERTIES LINKER_LANGUAGE CXX)
#target_link_libraries(pre_parser_generated ${antlr4_static})

#add_executable(test_parser test_parser.cpp)
#target_link_libraries(test_parser ${grammerLibs})
#target_link_libraries(test_parser ${grammerLibs})
#target_link_libraries(test_parser util)
#target_link_libraries(test_parser pre_parser_generated)
#if(Boost_FOUND)
#    target_link_libraries(test_parser ${Boost_LIBRARIES})
#endif()



