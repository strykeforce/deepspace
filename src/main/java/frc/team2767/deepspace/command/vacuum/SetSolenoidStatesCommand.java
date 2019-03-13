package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class SetSolenoidStatesCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  private final VacuumSubsystem.SolenoidStates state;

  public SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates state) {
    this.state = state;
  }

  @Override
  protected void initialize() {
    VACUUM.setSolenoidsState(state);
  }
}
