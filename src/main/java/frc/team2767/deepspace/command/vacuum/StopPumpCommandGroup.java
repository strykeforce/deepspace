package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class StopPumpCommandGroup extends CommandGroup {

  public StopPumpCommandGroup() {
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.STOP));
    addSequential(new VacuumStopCommand());
  }
}
