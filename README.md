# Api contains business logic for eGAR processing

## Configuring the service
To run the service create an application.properties file in the same folder as the spring jar. Additionally as this is a SpringBoot application the values can be stored as systems environment properties. See [Springboot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for full details.
As a short hand through out this document these are refered to as *parameters*.

## Configuring Submission cutoff times
There are two optional submission cutoff parameters:
```
workflow.submission.departure.cutoff.time.s={time}
workflow.submission.arrival.cutoff.time.s={time}
```
In both cases these parameters default to null. The submission cutoff time can be set to a positive or negative number and is in seconds. A positive number indicates a threshold after the location and a negative indicates a threshold before the threshold.
E.g. 
```
--workflow.submission.departure.cutoff.time.s=-7200
```
This would give a submission threshold of two hours before departure. 
## Configuring cancellation thresholds
There are two optional cancellation parameters that are configurable:
```
--workflow.cancellation.departure.threshold.time.s={time}
--workflow.cancellation.arrival.threshold.time.s={time}
```
In both cases these parameters default to null. The cancellation time can be set to a positive or negative number and is in seconds. A positive number indicates a threshold after the location and a negative indicates a threshold before the threshold.
E.g. 
```
--workflow.cancellation.departure.threshold.time.s=-7200
```
This would give a cancellation threshold of two hours before departure. 
##  Configuring file parameters
All three file parameters need to be set:
```
--workflow.max.file.number={max_file_number}
--workflow.max.total.file.size={tot_size}
--workflow.max.file.size={size}
```
The file sizes are measured in bytes.
## Setting the server's port

By default the service runs by default on port 9090. But to change this, add to the configuration the following:
```
--server.port=8080
```
This would put the service on port 8080.

## Monitoring the service
The service exposes a number of end-points for monitoring:
* `/health` A standard status health end-point, which will show the downstream apis in use and their health.
* `/info` A standard informational endpoint which reveals the application name & version.
* `/healthz` An K8 endpoint for monitoring if the application is up. Http:200 or Http:500 if in trouble (No body content).
* `/readiness` A copy of the above; Subject to change.
* `/metrics` A K8 endpoint for monitoring the memory usage and other stats of the service.

## Test and code coverage
When the following test command runs:
```
mvn test
```
The unit tests for the application will execute.

This will include generation of a Jacoco report at the following location:
```
/target/jacoco-ut/
```

As the project has used lombok there is a lot of auto generated code which would normally not be picked up under code coverage. 
The addition of the lombok.config file on the top level allows for the "@Generated" tag to be applied to lombok methods.
This will then be excluded from code coverage tests enabling us to generate a more accurate code coverage report.