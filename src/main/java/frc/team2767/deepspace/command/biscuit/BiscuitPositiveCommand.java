package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositiveCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  private double setpoint = 0.0;

  public BiscuitPositiveCommand() {
    requires(BISCUIT);
  }

  public BiscuitPositiveCommand(double setpoint) {
    this.setpoint = setpoint;
  }

  @Override
  protected void initialize() {
    BISCUIT.runOpenLoop(.15);
  }
}
