package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumOpenLoopCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final double setpoint;

  public VacuumOpenLoopCommand(double setpoint) {
    this.setpoint = setpoint;
    requires(VACUUM);
  }

  @Override
  protected void initialize() {
    VACUUM.runOpenLoop(setpoint);
  }
}
