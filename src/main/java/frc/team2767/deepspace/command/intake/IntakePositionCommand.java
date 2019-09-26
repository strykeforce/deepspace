package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakePositionCommand extends Command {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private final double angle;

  public IntakePositionCommand(double angle) {
    this.angle = angle;
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    if (angle == IntakeSubsystem.kStowPositionDeg)
      INTAKE.setCurrentLimit(IntakeSubsystem.ShoulderILimit.STOW);
    else INTAKE.setCurrentLimit(IntakeSubsystem.ShoulderILimit.NORMAL);

    INTAKE.setPosition(angle);
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onTarget();
  }
}
