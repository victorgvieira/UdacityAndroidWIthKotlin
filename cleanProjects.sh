#! /bin/bash

for f in $(ls . | grep andfun-kotlin)
do
	echo "Cleaning folder: $f"
	cd "$f"
	if [[ -f gradlew ]] ; then
		if ! [[ -x gradlew ]] ; then 
			chmod +x gradlew
		fi
		./gradlew clean
	fi
	cd ..
	echo "Folder $f done"
done
