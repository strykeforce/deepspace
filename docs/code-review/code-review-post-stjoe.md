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

| Problem | Cause | Response | Outcome |
|--------|-------|-----|----------------|
| Azimuths 90 deg off | Axle loose | Tighten | Successful |
| Nothing happens in sandstorm | Waiting for pressure | Raise elevator to get seal, add pressure timeout | Unsuccessful, need more |

#### Practice 2

| Problem | Cause | Response | Outcome |
|--------|-------|----|-----------------|
| No suction on teleop transition | Solenoids not opening | Set solenoids in TeleopInit() | Successful |
| No suction during climb | Trident solenoid open | Close trident solenoid in climb sequence | Successful |
| No suction during stow | Pressure goodEnough too low, doesn't think we have game piece (onTarget) | Increase good enough | Successful |
| Not moving in Sandstorm | Waiting for pressure  onTarget with hatch | Raise elevator more | Successful |
| Can't move elevator at certain positions | Safety subsystem limits are different | Adjust safety limits | Mostly successful, didn't change limit in of() method |

#### Practice 3

| Problem | Cause | Response | Outcome |
|---------|------|----------|----------|
| Difficult picking up hatches | Elevator low setpoint too low | Raise all elevator positions by 1 in. | Unsuccessful |
| No log | Unknown | None | N/A |

#### Practice 4

| Problem  | Cause | Response | Outcome |
|---------|--------|----------|---------|
| Biscuit hits shoulder gear | Elevator low setpoint too low | Raise elevator position | Successful |
| No log | Unknown | None | N/A |

#### Match 1

| Problem  | Cause | Response | Outcome |
|----------|-------|----------|----------|
| Cross hairs difficult to see | none | Change camera cross hairs to dark green | Successful |
| Driver controller behaves weird | Unknown | Set deadband to 0.09 | Unknown, may not have been root cause |
| No log | Permissions wrong | Set permissions to lvuser | Successful |

#### Match 2

- Climber set higher to protect from accidental release during Hab lvl 2 drop

| Problem | Cause | Response | Outcome |
|----------|-------|---------|----------|
| Elevator won't move | Pulley rope caught in elevator roller guides | Tie end down | Successful |

#### Match 3

- CommandGroup sequences are now enclosed by "BEGIN FOO" and "END FOO"

| Problem | Cause | Response | Outcome |
|----------|-------|---------|--------|
| Elevator positions not right | Unknown | Adjust elevator heights | Successful |
| Elevator behavior | PositionExecute waiting for Biscuit | Change order of PositionExecute (Elevator -> Biscuit) | 
Successful |

#### Match 4

- Climb to 80%
- Suction lower to 20%
- Gremlin hunt

#### Match 5

None

#### Match 6

| Problem | Cause | Response | Outcome |
|---------|---------|-------|----------|
| Last ball stuck in intake | N/A | None | N/A |
| Can't build climb seal | Suction cup lower too fast | Set to 0.15 | Successful |

#### Match 7

| Problem  | Cause | Response | Outcome |
|----------|--------|----------|---------|
| Missed ball pickup | Valve not opening fast enough | Open valve at beginning of coconut pickup | Successful |
| Biscuit stabs robot after Player hatch | Elevator too low, moving biscuit before elevator | Set Biscuit to safe up position if Player hatch | Successful |

#### Match 8

| Problem | Cause | Response | Outcome |
|---------|---------|---------|---------|
| Yellow card | Too tall during climb | Lower stow position | Unsuccessful |

#### Match 9

| Problem | Cause | Response | Outcome |
|--------|---------|-----------|---------|
| Biscuit always going up | Wrong bounds for conditional command | Get action from VISION instead to decide if go to safe position | Successful |

#### Match 10

| Problem | Cause | Response | Outcome |
|-------|---------|----------|---------|
| Yellow card | Too tall during climb | Open loop jog into ball to clear height restriction | Successful |

#### Match 11

None

#### Elims 1

None

#### Elims 2

None 


## Things to be fixed



## To Do

- Hatch pickup with vision
- Hatch place level 2 & 3 with vision
- Game controller: move not stage
- Auton for Sandstorm

