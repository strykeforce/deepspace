package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeDumpCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  @Override
  protected void initialize() {
    INTAKE.dump();
  }
}
