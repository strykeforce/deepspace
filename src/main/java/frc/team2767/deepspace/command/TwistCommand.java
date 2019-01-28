package frc.team2767.deepspace.command;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class TwistCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final double heading;
  private final int distance;
  private final double targetYaw;
  private final NetworkTableInstance instance = NetworkTableInstance.getDefault();
  private final NetworkTable table;

  public TwistCommand(double heading, int distance, double targetYaw) {
    table = instance.getTable("Shuffleboard");
    this.heading = heading;
    this.distance = distance;
    this.targetYaw = targetYaw;
    requires(DRIVE);
    setInterruptible(true);
  }

  @Override
  protected void initialize() {

    DRIVE.startTwist(heading, distance, targetYaw);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isTwistFinished();
  }

  @Override
  protected void interrupted() {
    DRIVE.interruptTwist();
  }
}
