package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class LockWheelsCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public LockWheelsCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    Wheel[] wheels = DRIVE.getAllWheels();

    wheels[0].set(-0.125, 0.0);
    wheels[1].set(0.125, 0.0);
    wheels[2].set(0.125, 0.0);
    wheels[3].set(-0.125, 0.0);
  }
}
