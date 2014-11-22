akka-taxi-system
================

Example of a simple Akka-based Actor System.
![actor supervision hierarchy](/doc/TaxiSystemActorSupervisionHierarchy.png?raw=true)
### Actors
#### Management Centre
Is the root level user Actor. It creates and supervises several underlying Taxi Actors and the Tube Location Service Actor. 
It listens to LocationReport messages from Taxi actors and simply logs these on the console.

#### Taxi
The Taxi actor supervises it's own GPS and Scheduler Actors. The Scheduler will fire a ReportLocation message every second until stopped.

#### GPS
The GPS is a dedicated actor and every Taxi actor has it's own. Although for the purpose of this excercise, this would not have been necessary,
we are trying to emulate here the real world scenario where every taxi would have it's own GPS device installed. The position the GPS device gives is specific to every instance of Taxi.

#### Scheduler
Simirarly to the  GPS actor, a Scheduler is created for every Taxi which triggers it to report it's own position to the Management Centre. This is again an attempt to emulate the real world, i.e. the Taxi driver's duty to check in with the Dispatcher periodically.

#### Tube Location Service
This is a service through which a specific Location can be checked whether it is nearby a Tube station. The implementation here is very basic, we expect we are near a Tube station any time we are witin one minute longitude or latitude of 50.0 N 0.0 E.
