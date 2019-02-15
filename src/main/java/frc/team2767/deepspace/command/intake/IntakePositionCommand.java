package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakePositionCommand extends Command {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private final IntakeSubsystem.ShoulderPosition position;

  public IntakePositionCommand(IntakeSubsystem.ShoulderPosition position) {
    this.position = position;
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onTarget();
  }
}
