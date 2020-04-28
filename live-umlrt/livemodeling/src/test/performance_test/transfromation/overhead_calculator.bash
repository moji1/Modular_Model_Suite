#!/bin/bash 
set -e

find models/* -name "*.uml" -exec java -jar ../../../../tools/m_activity_test.jar -o ./tmp -p /Users/mojtababagherzadeh/Desktop/Papyrus-RT.app/Contents/Eclipse/plugins/ {} \; > /dev/null 2> modeling_activities_result.txt