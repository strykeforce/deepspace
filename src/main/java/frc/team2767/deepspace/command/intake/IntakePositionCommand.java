package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import frc.team2767.deepspace.subsystem.ShoulderPosition;

public class IntakePositionCommand extends Command {

  private final IntakeSubsystem INTAKE = Robot.IntakeSubsystem;

  private final ShoulderPosition position;

  public IntakePositionCommand(ShoulderPosition position) {
    this.position = position;
    requires(INTAKE);
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.onTarget();
  }

  @Override
  protected void initialize() {
    INTAKE.setPosition(ShoulderPosition.UP);
  }
}
