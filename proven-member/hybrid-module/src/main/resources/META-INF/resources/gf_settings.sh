#!/bin/sh

##
## JVM Settings
##
asadmin --user admin --passwordfile .gfpass --port %PORT% delete-jvm-options --target server-config -Xmx512m
asadmin --user admin --passwordfile .gfpass --port %PORT% delete-jvm-options --target server-config -- -client  
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config -Xmx4096m
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config -Xms4096m
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config -- -server
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:+UseParallelGC"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:ParallelGCThreads=2"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:MaxGCPauseMillis=200"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:GCTimeRatio=19"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:+UseParallelOldGC"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config "-XX\:+UseTLAB"
asadmin --user admin --passwordfile .gfpass --port %PORT% create-jvm-options --target server-config -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false

##
## Common Thread Pool settings
##
asadmin --user admin --passwordfile .gfpass --port %PORT% set server.thread-pools.thread-pool.thread-pool-1.max-queue-size=8192
asadmin --user admin --passwordfile .gfpass --port %PORT% set server.thread-pools.thread-pool.thread-pool-1.max-thread-pool-size=600
asadmin --user admin --passwordfile .gfpass --port %PORT% set server.thread-pools.thread-pool.thread-pool-1.min-thread-pool-size=50

##
## EJB Pool/Cache/Thread settings
##
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.steady-pool-size=16
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.max-pool-size=80
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.pool-resize-quantity=16
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.pool-idle-timeout-in-seconds=600
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.cache-resize-quantity=60
asadmin --user admin --passwordfile .gfpass --port %PORT% set configs.config.server-config.ejb-container.max-cache-size=960


##
## Connection pools settings
##
asadmin --user admin --passwordfile .gfpass --port %PORT% set resources.jdbc-connection-pool.jdbc/oscar3.max-pool-size=128
asadmin --user admin --passwordfile .gfpass --port %PORT% set resources.jdbc-connection-pool.jdbc/oscar3.steady-pool-size=32



##
## General settings
##
asadmin --user admin --passwordfile .gfpass --port %PORT% set server.admin-service.das-config.autodeploy-enabled=false
asadmin --user admin --passwordfile .gfpass --port %PORT% set server.admin-service.das-config.dynamic-reload-enabled=false

exit
