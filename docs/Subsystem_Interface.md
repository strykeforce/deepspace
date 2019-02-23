# Subsystem Interface Requirements

## Real-World Units by Subsystem
* Elevator: inches from bellypan
* Biscuit: degrees from straight up
* Intake: degrees of shoulder from straight up
* Vacuum: pressure in inHg
* Climb: inches from floor

## Methods to Implement:
* getter for current position of subsystem in real-world units
    * `double getPosition()`
* setter for position in real-world units
    * `void setPosition(double position)`
* getter that returns a list of all talons in subsystem
    * `java.util.List getTalons()`
* cleanup enums - replace with public static final doubles in subsystem
