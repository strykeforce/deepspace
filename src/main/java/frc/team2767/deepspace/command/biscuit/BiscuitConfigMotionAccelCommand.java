package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitConfigMotionAccelCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private int accel;

  public BiscuitConfigMotionAccelCommand(int accel) {
    this.accel = accel;
  }

  @Override
  protected void initialize() {
    BISCUIT.setMotionMagicAccel(accel);
  }
}
