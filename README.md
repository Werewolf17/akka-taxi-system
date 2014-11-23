akka-taxi-system
================

Example of a simple Akka-based Actor System.
### Actor Supervision Hierarchy
![actor supervision hierarchy](/doc/TaxiSystemActorSupervisionHierarchy.png?raw=true)
### Actors
#### Management Centre
Is the root level user Actor. It creates and supervises a pre-configured number of underlying Taxi Actors and a singleton Tube Location Service
Actor. It listens to LocationReport messages from the supervised Taxi actors and simply logs these on the console.

#### Taxi
The Taxi actor supervises it's own GPS and Scheduler Actors. The Scheduler will fire a ReportLocation message every second until stopped.
When prodded by the scheduler it requests it's position from the GPS actor, then checks if the position is near the Tube station and in case
it is, it will report the position to the Management Centre.

#### GPS
The GPS is a dedicated actor and every Taxi actor has it's own. Although for the purpose of this exercise, this would not have been necessary,
we are trying to emulate here the real world scenario where every taxi would have it's own GPS device installed.
The position the GPS device gives is specific to every instance of Taxi.

#### Scheduler
Similarly to the  GPS actor, a Scheduler is created for every Taxi which triggers it to report it's own position to the Management Centre.
This is again an attempt to emulate the real world, i.e. the Taxi driver's duty to check in with the Dispatcher periodically.

#### Tube Location Service
This is a service through which a specific Location can be checked whether it is nearby a Tube station.
The implementation here is very basic, we expect we are near a Tube station any time we are in a square delimited within one minute
longitude or latitude of each side of coordinates N50.0 E0.0.

### Instructions
In order to run the program execute:
```
% git clone https://github.com/briskware/akka-taxi-system.git
% cd akka-taxi-system
% sbt run
```
For unit tests run the following:
```
% sbt test
```
