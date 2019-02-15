package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeOutCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public IntakeOutCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    double percent = 0.2;
    INTAKE.rollerOpenLoop(-percent);
  }
}
