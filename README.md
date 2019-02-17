# 2019 FIRST DEEP SPACE

## Talons

Subsystem    | Talon   | ID | PDP
------------ | ----------- | -- | ---
Drive        | azimuth     | 0  | 11
Drive        | azimuth     | 1  | 9
Drive        | azimuth     | 2  | 10
Drive        | azimuth     | 3  | 8
Drive        | drive       | 10 | 14
Drive        | drive       | 11 | 13
Drive        | drive       | 12 | 15
Drive        | drive       | 13 | 12
Intake       | shoulder    | 20 | 4
Intake       | rollers     | 21 | 5
Elevator     | elevator    | 30 | 0
Biscuit      | rotate      | 40 | 7
Climb        | leftSlave   | 50 | 2
Climb        | rightMaster | 51 | 3
Vacuum       | vacuum      | 60 | 1

## Servos

Subsystem   | Servo | PWM
----------- | ----- | ---
CargoCenter | left  | 0
CargoCenter | right | 1

## CANifier

Subsystem | CANifier | ID
--------- | -------- | --
Trident   | trident  | 0

Pin # | Name | Attached To
----- | ---- | -----------
8     | LIMF | compression output

## DIO

Subsystem   | I/O    | DIO
----------- | ------ | ---
AutonSwitch |  0     | 0
AutonSwitch |  1     | 1
AutonSwitch |  2     | 2
AutonSwitch |  3     | 3
AutonSwitch |  4     | 4
AutonSwitch |  5     | 5
Vision      | lights | 6

## PCM

Subsystem | Valve        | Solenoid
--------- | ------------ | --------
Vacuum    | trident      | 0
Vacuum    | pump         | 1
Vacuum    | climb        | 2
