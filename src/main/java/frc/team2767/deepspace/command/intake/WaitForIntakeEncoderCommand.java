package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class WaitForIntakeEncoderCommand extends Command {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private int position;

  public WaitForIntakeEncoderCommand(int position) {
    this.position = position;
    requires(INTAKE);
  }

  @Override
  protected boolean isFinished() {
    return INTAKE.getPosition() > position;
  }
}
