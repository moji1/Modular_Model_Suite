Project (pmd_helper C CXX)
cmake_minimum_required (VERSION 3.11)
set (CMAKE_CXX_STANDARD 11)

include(env.cmake)

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# Second, for multi-config builds of xcode 
#foreach( OUTPUTCONFIG ${CMAKE_CONFIGURATION_TYPES} )
#    string( TOUPPER ${OUTPUTCONFIG} OUTPUTCONFIG )
#    set( CMAKE_RUNTIME_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_BINARY_DIR}/bin/${OUTPUTCONFIG} )
#    set( CMAKE_LIBRARY_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_BINARY_DIR}/lib/${OUTPUTCONFIG})
#    set( CMAKE_ARCHIVE_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_BINARY_DIR}/lib/${OUTPUTCONFIG} )
#endforeach( OUTPUTCONFIG CMAKE_CONFIGURATION_TYPES )

# configure boost
set(Boost_USE_STATIC_LIBS OFF) 
set(Boost_USE_MULTITHREADED ON)  
set(Boost_USE_STATIC_RUNTIME OFF) 
set(BOOST_ALL_DYN_LINK ON) 
add_definitions( -DBOOST_ALL_DYN_LINK )
find_package(Boost COMPONENTS log thread regex) 
if(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})
    link_directories(${Boost_LINK_DIRS}) 
endif()

# configure RTS
set(UMLRTS_LIB "librts.a" CACHE STRING "")
if(APPLE)
	set(BUILDTOOLS linux.darwin)
else()
	set(BUILDTOOLS linux.x86-gcc-4.6.3)
endif()
include_directories(${UMLRTS_ROOT}/include)
link_directories(${UMLRTS_ROOT}/lib/${BUILDTOOLS})

add_subdirectory(src/parser)
add_subdirectory(src/util)
add_subdirectory(src/pme_core)
add_subdirectory(src/test)
add_subdirectory(src/test/umlrtmodels)
