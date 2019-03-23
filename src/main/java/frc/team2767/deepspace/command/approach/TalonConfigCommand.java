package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TalonConfigCommand extends InstantCommand {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final DriveSubsystem.DriveTalonConfig config;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public TalonConfigCommand(DriveSubsystem.DriveTalonConfig config) {
    this.config = config;
  }

  @Override
  protected void initialize() {
    DRIVE.setSlotConfig(config);
    logger.debug("set config to {}", config);
  }
}
