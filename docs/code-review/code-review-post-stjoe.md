# Code Review Post St. Joe
### 12.03.2019


## Overall Structure

## Inspection by Sequence

- Greg Hatch
- Greg ball
- Coconut ball pickup
- Deliver Sequence

## St. Joe Fixes

- Band-aids or legitimate fixes?

#### Practice 1

| Problem | Cause | Response |
|--------|-------|-----|
| Azimuths 90 deg off | Axle loose | Tighten |
| Nothing happens in sandstorm | Waiting for pressure | Raise elevator to get seal, add pressure timeout |

#### Practice 2

| Problem | Cause | Response |
|--------|-------|----|
| No suction | Solenoids not opening | Set solenoids in TeleopInit() |
| No suction during climb | Trident solenoid open | Close trident solenoid in climb sequence |
| No suction during stow | Pressure goodEnough too low, doesn't think we have game piece (onTarget) | Increase good enough |
| Not moving in Sandstorm | Waiting for pressure  onTarget with hatch | Raise elevator more |
| Can't move elevator at certain positions | Safety subsystem limits are different | Adjust safety limits |

#### Practice 3

| Problem | Cause | Response |
|---------|------|----------|
| Difficult picking up hatches | Elevator low setpoint too low | Raise all elevator positions by 1 in. |
| No log | Unknown | None |

#### Practice 4

| Problem  | Cause | Response |
|---------|--------|----------|
| Biscuit hits shoulder gear | Elevator low setpoint too low | Raise elevator position |
| No log | Unknown | None|

#### Match 1

| Problem  | Cause | Response |
|----------|-------|----------|
| Cross hairs difficult to see | none | Change camera cross hairs to dark green |
| Driver controller behaves weird | Unknown | Set deadband to 0.09 |
| No log | Permissions wrong | Set permissions to lvuser |

#### Match 2

- Climber set higher to protect from accidental release during Hab lvl 2 drop

| Problem | Cause | Response |
|----------|-------|---------|
| Elevator won't move | Pulley rope caught in elevator roller guides | Tie end down |

#### Match 3

- CommandGroup sequences are now enclosed by "BEGIN FOO" and "END FOO"

| Problem | Cause | Response |
|----------|-------|---------|
| Elevator positions not right | Unknown | Adjust elevator heights |
| Elevator behavior | PositionExecute waiting for Biscuit | Change order of PositionExecute (Elevator -> Biscuit) |

#### Match 4

- Climb to 80%
- Suction lower to 20%
- Gremlin hunt

#### Match 5

None

#### Match 6

| Problem | Cause | Response |
|---------|---------|-------|
| Last ball stuck in intake | N/A | None |
| Can't build climb seal | Suction cup lower too fast | Set to 0.15 |

#### Match 7

| Problem  | Cause | Response | 
|----------|--------|----------|
| Missed ball pickup | Valve not opening fast enough | Open valve at beginning of coconut pickup |
| Biscuit stabs robot after Player hatch | Elevator too low, moving biscuit before elevator| Set Biscuit to safe up position if Player hatch |

#### Match 8

| Problem | Cause | Response |
|---------|---------|---------|
| Yellow card | Too tall during climb | Lower stow position |

#### Match 9

| Problem | Cause | Response |
|--------|---------|-----------|
| Biscuit always going up | Wrong bounds for conditional command | Get action from VISION instead to decide if go to safe position |

#### Match 10

| Problem | Cause | Response |
|-------|---------|----------|
| Yellow card | Too tall during climb | Open loop jog into ball to clear height restriction |

#### Match 11

None

#### Elims 1

None

#### Elims 2

None 


## Things to be fixed

- Solenoid delay

## Current/Future Projects

- Hatch pickup with vision
- Auton for Sandstorm

