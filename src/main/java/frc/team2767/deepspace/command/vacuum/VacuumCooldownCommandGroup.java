package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumCooldownCommandGroup extends CommandGroup {

  public VacuumCooldownCommandGroup() {
    addSequential(new VacuumCurrentLimitCommand(1.0));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.COOL_DOWN));

    addSequential(new VacuumOpenLoopCommand(0.2));
  }
}
