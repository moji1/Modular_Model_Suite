file(GLOB SRCS *.cpp)
add_library(util ${SRCS})
target_link_libraries(util ${UMLRTS_LIB} ${Boost_LIBRARIES})