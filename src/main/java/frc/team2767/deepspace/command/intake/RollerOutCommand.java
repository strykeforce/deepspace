package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class RollerOutCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public RollerOutCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    double percent = 0.2;
    INTAKE.rollerOpenLoop(-percent);
  }
}
