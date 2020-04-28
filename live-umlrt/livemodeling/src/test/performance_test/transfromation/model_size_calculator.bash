#!/bin/bash 
set -e
### setup
#eol_executor="../../../../tools/umlrttransformer.jar"
eol_executor="/Users/mojtababagherzadeh/git/EpsilonCLIForUMLRT/UMLRTTransformer/target/umlrttransformer-jar-with-dependencies.jar"
script_path="../../../../../src/EpsilonCode/script/"

## generation of partail model
#eol_executor="/Users/mojtababagherzadeh/git/partialmodels/pmd_helper/tools/umlrttransformer.jar"

postfix=""

#declare -a models=("models/CarMultiDoorLockM/CarDoorLock.uml")



declare -a models=( "models/ParcelRouter/model.uml" 
					"models/DigitalWatch/DigitalWatch.uml"
					"models/Replication/Replication.uml" 
					"models/CarMultiDoorLockM/CarDoorLock.uml"
					"models/Rover/rover.uml"
					"models/DebuggableReplication/DebuggableReplication.uml")

declare -a postfixes=("" "Refined_")

#declare -a models=("models/DigitalWatch/DigitalWatch.uml")
declare -a percents=(10 20 30 40 50 60 70 80 90)
#declare -a percents=(30)

script_name="ModelComplexityCalculator.eol"
result_file=models_size2.csv
failed=0

for postfix in "${postfixes[@]}"
do
for model in "${models[@]}"
	do
		output=$(java -Xms4084m -Xmx7g   -jar $eol_executor  -m  $(dirname "$model")/"$postfix"$(basename "$model")   \
			  -s "$script_path""$script_name"    -inplace  -args  "$postfix"$(basename "$model"))
		include_out=0
		echo "$output"
		while read -r line; do
			if [ "$line" = "------Beginning of the script result--------" ];then
				include_out=1
			fi
			if [ "$line" = "------End of the script result--------" ];then
				include_out=0
			fi
			if [ "$include_out" -eq 1 ] && [ "$line"  != "------Beginning of the script result--------" ];then
				echo "$line" >> models_size"$postfix".csv
			fi
		done <<< "$output"
	
		for percent in "${percents[@]}"
		do
			output=$(java -Xms4084m -Xmx7g  -jar $eol_executor  -m  $(dirname "$model")/"$postfix"Partial_"$percent"_$(basename "$model")   \
				-s "$script_path""$script_name"  -args "$postfix"Partial_"$percent"_$(basename "$model")   -inplace) 
			include_out=0
			echo "$output"
			while read -r line; do
				if [ "$line" = "------Beginning of the script result--------" ];then
					include_out=1
				fi
				if [ "$line" = "------End of the script result--------" ];then
					include_out=0
				fi
				if [ "$include_out" -eq 1 ] && [ "$line"  != "------Beginning of the script result--------" ];then
					echo "$line"  >> models_size"$postfix".csv
				fi
			done <<< "$output"
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



