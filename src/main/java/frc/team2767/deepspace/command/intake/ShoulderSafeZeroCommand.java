package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class ShoulderSafeZeroCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public ShoulderSafeZeroCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.shoulderZeroWithLimitSwitch();
  }
}
