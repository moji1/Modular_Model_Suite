//
//  RandomGenerator.cpp
//  util
//
//  Created by Mojtaba Bagherzadeh on 2018-11-02.
//

#include "RandomGenerator.hpp"

using namespace pmd;

int RandomGenerator::generateInt() {
    return generateInt(INT_MIN, INT_MAX);
}

int RandomGenerator::generateInt(int min, int max) {
    RNGType rng(std::time(0));
    boost::uniform_int<> range( min, max );
    boost::variate_generator< RNGType, boost::uniform_int<> > dice(rng,range);
    return dice();
}
