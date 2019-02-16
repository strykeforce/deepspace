# Wheel Zero Guidelines 2019
###### Zeroing the wheels using Thirdcoast Telemetry (TCT)

* Difficulty: Medium
* Time (minutes):  5 (first time users), 3 (experienced)
* Students needed: 2 (recommended), 1 (minimum)

1. Connect to the correct network

   a. If Wi-Fi, connect to network using Wi-Fi  
   b. If not Wi-Fi connected (e.g. at a competition), connect using a network cable to the radio
   
2. Use SSH (Linux) or Putty (Windows)

   a. ssh: type this in terminal `ssh roborio`  
   b. putty: select roboRIO (`address is admin@roborio-2767-frc.local`)
   
3. When the black screen comes up with `admin@roboRIO-2767-FRC:~#`, type tct and hit enter.

4. Wait until you see a menu of options 1 through 5 and Q

5. Enable the robot using a driver’s station

6. Select `5: work with swerve` by hitting 5 (NOTE: TCT will automatically select the option when the number is 
selected, DO NOT hit Enter)

7. Select `2: update azimuth positions` by hitting 2

8. Work wheel by wheel using an alignment bar (a straight bar) and align the wheels with each other and the robot. 
They should point forward, the bevel gear is on the robot’s left

   a. Select `2: active azimuth` by hitting 2   
   b. Enter the wheel number, hit enter 
   c. Select `3: active azimuth adjustment` by hitting 3    
   d. Add or subtract a tick count to the existing number in order to adjust the wheel CW/CCW   
   e. When the wheel is aligned, go back to 7.a., repeat steps a through d until all the wheels are aligned
   
9.	Select `1: save azimuth zero positions` (or else you’ll lose the current positions)
10.	Select `Q: quit TCT` to quit (it’s a capital Q so hit “Shift” + “q”)

###### Last Updated 16 JAN 2019 by STRYKE FORCE SOFTWARE TEAM
