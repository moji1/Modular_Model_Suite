/*
 * Original Andrey Semashev 2007 - 2015.
 *
 * Distributed under the Boost Software License, Version 1.0.
 *    (See accompanying file LICENSE_1_0.txt or copy at
 *          http://www.boost.org/LICENSE_1_0.txt)
 */
 // Modified 2018 by  Mojtaba Bagherzadeh
#ifndef LOGGING_CONFIG_HH_
#define LOGGING_CONFIG_HH_

#include <boost/log/core.hpp>
#include <boost/log/trivial.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/sinks/text_file_backend.hpp>
#include <boost/log/utility/setup/file.hpp>
#include <boost/log/utility/setup/common_attributes.hpp>
#include <boost/log/sources/severity_logger.hpp>
#include <boost/log/sources/record_ostream.hpp>


namespace logging = boost::log;
namespace src = boost::log::sources;
namespace sinks = boost::log::sinks;
namespace keywords = boost::log::keywords;
using namespace std;

static bool is_log_intialized=false;

static void init_logging(string log_file_name, logging::trivial::severity_level log_level)
{
    if (not is_log_intialized){
    		logging::add_file_log
			(
					boost::log::keywords::file_name = log_file_name+"_%N.log",                                 /*< file name pattern >*/
					boost::log::keywords::rotation_size = 10 * 1024 * 1024,                                   /*< rotate files every 10 MiB... >*/
					boost::log::keywords::time_based_rotation = boost::log::sinks::file::rotation_at_time_point(0, 0, 0), /*< ...or at midnight >*/
					boost::log::keywords::format = " %ProcessID% : %ThreadID% -  [%TimeStamp%]: %Message%"                                 /*< log record format >*/
			);

    			logging::core::get()->set_filter
    					(
    							logging::trivial::severity >= log_level
    					);
    			is_log_intialized=true;
    	}
}

#endif
//static void init_logging()
//{
//    if (not is_log_intialized){
//    		logging::add_file_log
//			(
//					boost::log::keywords::file_name = "log_%N.log",                                        /*< file name pattern >*/
//					boost::log::keywords::rotation_size = 10 * 1024 * 1024,                                   /*< rotate files every 10 MiB... >*/
//					boost::log::keywords::time_based_rotation = sinks::file::rotation_at_time_point(0, 0, 0), /*< ...or at midnight >*/
//					boost::log::keywords::format = " %ProcessID% : %ThreadID% -  [%TimeStamp%]: %Message%"                                 /*< log record format >*/
//			);

//    		logging::core::get()->set_filter
//   				(
//    						logging::trivial::severity >= logging::trivial::info
//    				);
//    		is_log_intialized=true;
//    }
//}
