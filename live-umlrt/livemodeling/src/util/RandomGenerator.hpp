//
//  RandomGenerator.hpp
//  util
//
//  Created by Mojtaba Bagherzadeh on 2018-11-02.
//

#ifndef RandomGenerator_hpp
#define RandomGenerator_hpp

#include <iostream>
#include "boost/random.hpp"
#include "boost/generator_iterator.hpp"

#endif /* RandomGenerator_hpp */
namespace pmd{
    typedef boost::mt19937 RNGType;
    class RandomGenerator {
    public:
        static int generateInt();
        static int generateInt(int min, int max);
    
    };
}
