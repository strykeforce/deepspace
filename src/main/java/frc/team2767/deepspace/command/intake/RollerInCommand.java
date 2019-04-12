package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class RollerInCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private final double output;

  public RollerInCommand() {
    this(0.9);
  }

  public RollerInCommand(double output) {
    this.output = output;
  }

  @Override
  protected void initialize() {
    INTAKE.rollerOpenLoop(output);
  }
}
