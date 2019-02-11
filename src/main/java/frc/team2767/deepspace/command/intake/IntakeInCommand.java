package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeInCommand extends InstantCommand {
  private final IntakeSubsystem INTAKE = Robot.IntakeSubsystem;

  public IntakeInCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.rollerOpenLoop(0.2);
  }
}
