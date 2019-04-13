package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbSlowVelocityCommand extends Command {
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimbSlowVelocityCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    logger.info("Manual Lower Suction Cup Slow Velocity");
    CLIMB.setSlowTalonConfig(true);
    CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
  }

  public void execute() {
    SmartDashboard.putBoolean("Game/climbOnTarget", VACUUM.isClimbOnTarget());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
