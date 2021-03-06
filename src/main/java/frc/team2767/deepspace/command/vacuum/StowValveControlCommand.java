package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class StowValveControlCommand extends ConditionalCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  public StowValveControlCommand() {
    super(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
  }

  // Not on Target -> Close Trident Valve
  // on Target -> leave Trident Valve in current state
  @Override
  protected boolean condition() {
    return !VACUUM.onTarget();
  }
}
