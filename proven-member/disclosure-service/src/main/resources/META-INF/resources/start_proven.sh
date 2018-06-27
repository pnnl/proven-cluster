#!/bin/bash

. .provenpass

echo ProvEn Port: $1

if [ -x "$1" ] 
  then 
    echo "ProvEn Server port value is required"
    exit
fi



echo InfluxDb connection settings
echo -------------------------------
echo URL      : $idbUrl
echo Username : $idbUsername
echo Password : See .provenpass file

echo Starting ProvEn ... see proven_log file for status
nohup java -jar ./payara-micro-4.1.1.164.jar --deploy proven.war --port $1  --no-cluster &>> proven_log &


