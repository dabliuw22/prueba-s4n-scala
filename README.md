# Prueba S4N Scala

### Description

In the solution, a modularized application is designed, where: 
**core:** is an internal *DSL* that represents the actions and evaluations of the drone.
**infrastructure/file:** Is the layer that allows us to write and read files.
**location-management:** Is the subdomain in charge of registering the positions of a drone.
**delivery-management:** Is the subdomain responsible for order delivery.
**test:** Contains utilities for testing

### Requirements:
    * JDK >= 1.8
    * SBT
    * Scala 2.13.x
    
#### 1. Create ENV VARS:
    ```
    $ export IN_PATH={your_in_path}
    $ export OUT_PATH={your_out_path}
    $ export DELIVERY_MAX_CONCURRENT={your_number_value}
    $ export DELIVERY_LIMIT={your_number_value}
    $ export DELIVERY_RANGE={your_number_value}
    ```
   
#### 2. Run Test:
    ```
    $ sbt test
    ```

#### 3. Assembly:
    ```
    $ sbt assembly
    ```
 
#### 4. Run Jar:
    ```
    $ java -jar target/scala-2.13/main.jar
    ```
   