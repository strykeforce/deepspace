package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumCooldownCommandGroup extends CommandGroup {

  public VacuumCooldownCommandGroup() {
    addSequential(
        new ActivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.TRIDENT, VacuumSubsystem.Valve.PUMP
            }));

    addSequential(new VacuumOpenLoopCommand(0.2));
    addSequential(new SmartdashboardTemperatureCommand());
  }
}
