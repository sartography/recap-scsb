# scsb
Shared Collection Service Bus

The SCSB application acts as a microservice which exposes the core functionality APIs used in the application. Each API call made in the SCSB application reaches this microservice application as a starting point and the microservice decides which microservice(scsb-circ,scsb-solr-client,scsb-etl,scsb-batch-scheduler) to be redirected to make the received request complete successfully.

