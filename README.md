# Prueba S4N Scala

## Problem
[backend-dev-technical-test](./backend-dev-technical-test.pdf)

## Description
In the solution, a modularized application is designed, using *typeclasses* and *tagless-final*, where: 
1. **core:** is an internal *DSL* that represents the actions and evaluations of the drone.
2. **infrastructure/file:** Is the layer that allows us to write and read files.
3. **location-management:** Is the subdomain in charge of registering the positions of a drone.
4. **delivery-management:** Is the subdomain responsible for order delivery.
5. **test:** Contains utilities for testing

## Requirements:
* JDK >= 1.8
* SBT
* Scala 2.13.x
    
## Create ENV VARS:
```[shell]
$ export IN_PATH={your_in_path}
$ export OUT_PATH={your_out_path}
$ export DELIVERY_MAX_CONCURRENT={your_number_value}
$ export DELIVERY_LIMIT={your_number_value}
$ export DELIVERY_RANGE={your_number_value}
```
   
## Run Test:
```[shell]
$ sbt test
```

## Assembly:
```[shell]
$ sbt assembly
```
 
## Run Jar:
```[shell]
$ java -jar target/scala-2.13/main.jar
```
   