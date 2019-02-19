package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class PressureAccumulateCommandGroup extends CommandGroup {

  public PressureAccumulateCommandGroup() {
    addSequential(new ActivateValveCommand(VacuumSubsystem.Valve.PUMP));
    addSequential(new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
  }
}
