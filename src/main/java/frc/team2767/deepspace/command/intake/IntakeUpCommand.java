package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeUpCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public IntakeUpCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.shoulderOpenLoop(-0.3);
  }
}
