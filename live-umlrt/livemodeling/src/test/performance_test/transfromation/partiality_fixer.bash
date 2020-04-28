#!/bin/bash 
set -e
### setup
#eol_executor="../../../../tools/umlrttransformer.jar"
eol_executor="/Users/mojtababagherzadeh/git/EpsilonCLIForUMLRT/UMLRTTransformer/target/umlrttransformer-jar-with-dependencies.jar"
script_path="../../../../../src/EpsilonCode/script/"

## generation of partail model
#eol_executor="/Users/mojtababagherzadeh/git/partialmodels/pmd_helper/tools/umlrttransformer.jar"

#declare -a models=("models/CarMultiDoorLockM/CarDoorLock.uml")

declare -a models2=("models/Rover/rover.uml"
					"models/ParcelRouter/model.uml" 
					"models/DigitalWatch/DigitalWatch.uml"
					"models/Replication/Replication.uml"
					"models/CarMultiDoorLockM/CarDoorLock.uml"
					"models/Refined-Replication/Replication.uml"
					)
declare -a models2=("models/Refined-Replication/Replication.uml")

declare -a models=("models/ParcelRouter/model.uml" 
					"models/DigitalWatch/DigitalWatch.uml"
					"models/Replication/Replication.uml" 
					"models/CarMultiDoorLockM/CarDoorLock.uml"
					"models/Rover/rover.uml"
					"models/DebuggableReplication/DebuggableReplication.uml"
					)

declare -a models=("models/DigitalWatch/DigitalWatch.uml")

declare -a percents=(10 20 30 40 50 60 70 80 90)
#"models/CarMultiDoorLockM/CarDoorLock.uml"
#"models/ParcelRouter/model.uml" 
#"models/DigitalWatch/DigitalWatch.uml"
#"models/Replication/Replication.uml" 
#declare -a percents=(30)

script_name="RefineForPMD.eol"
failed=0
declare -a range=(1 2 3 4 5 6 7 8 9 10)

failed=0
for i in "${range[@]}" 
do
for model in "${models[@]}"
	do
		java -Xms4084m -Xmx7g   -jar $eol_executor  -m  "$model"    \
				-s "$script_path""$script_name"  -p config.txt  -profile 
		for percent in "${percents[@]}"
			do
				if ! java -Xms4084m -Xmx7g   -jar $eol_executor  -m  $(dirname "$model")/Partial_"$percent"_$(basename "$model")   \
				-s "$script_path""$script_name"  -p config.txt  -profile ; then
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
done
if [ "$failed" -ne 0 ]; then
	echo "script failed for some cases"
	exit -1
fi