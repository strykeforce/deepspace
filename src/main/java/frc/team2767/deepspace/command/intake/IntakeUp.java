package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeUp extends InstantCommand {
  IntakeSubsystem intakeSubsystem = Robot.IntakeSubsystem;

  IntakeUp() {
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.setPosition(IntakeSubsystem.ShoulderPosition.UP);
  }
}
