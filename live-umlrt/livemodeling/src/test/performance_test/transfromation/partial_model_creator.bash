#!/bin/bash 
set -e
### setup
#eol_executor="../../../../tools/umlrttransformer.jar"
eol_executor="/Users/mojtababagherzadeh/git/EpsilonCLIForUMLRT/UMLRTTransformer/target/umlrttransformer-jar-with-dependencies.jar"
script_path="../../../../../src/EpsilonCode/script/"

## generation of partail model
#eol_executor="/Users/mojtababagherzadeh/git/partialmodels/pmd_helper/tools/umlrttransformer.jar"



declare -a models1=("models/ParcelRouter/model.uml" 
					"models/DigitalWatch/DigitalWatch.uml"
					"models/Replication/Replication.uml" 
					"models/CarMultiDoorLockM/CarDoorLock.uml"
					"models/Rover/rover.uml"
					"models/DebuggableReplication/DebuggableReplication.uml"
					)

declare -a models=("models/DigitalWatch/DigitalWatch.uml")
#declare -a models=("models/CarMultiDoorLockM/CarDoorLock.uml")
#declare -a models=("models/DebuggableReplication/DebuggableReplication.uml"	)

#declare -a percents=(30)
declare -a percents=(10 20 30 40 50 60 70 80 90)
#declare -a percents=(10 30)
script_name="PartialModelCreator.eol"
failed=0
for model in "${models[@]}"
	do
		for percent in "${percents[@]}"
			do
				if ! java -Xms4084m -Xmx7g   -jar $eol_executor  -m  "$model"   \
				-s "$script_path""$script_name"   -args "$percent"  -profile -o  $(dirname "$model")/Partial_"$percent"_$(basename "$model"); then
					echo script failed
					failed=1
					break
				fi
			done
		if [ "$failed" -ne 0 ]; then
			echo script failed.......
			exit -1
		fi
	done 

if [ "$failed" -ne 0 ]; then
	echo "script failed for some cases"
	exit -1
fi



