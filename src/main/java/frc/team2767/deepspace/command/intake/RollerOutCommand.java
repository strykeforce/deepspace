package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class RollerOutCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private final double output;

  public RollerOutCommand() {
    this(1.0);
  }

  public RollerOutCommand(double output) {
    this.output = output;
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.rollerOpenLoop(-output);
  }
}
