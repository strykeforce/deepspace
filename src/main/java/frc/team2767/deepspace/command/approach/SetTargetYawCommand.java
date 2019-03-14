package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetTargetYawCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private double targetYaw;

  public SetTargetYawCommand(double targetYaw) {
    this.targetYaw = targetYaw;
  }

  @Override
  protected void initialize() {
    VISION.setTargetYaw(targetYaw);
  }
}
