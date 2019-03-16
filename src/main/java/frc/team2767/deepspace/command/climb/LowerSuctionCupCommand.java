package frc.team2767.deepspace.command.climb;

import static frc.team2767.deepspace.subsystem.VacuumSubsystem.kClimbPressureInHg;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class LowerSuctionCupCommand extends Command {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  public LowerSuctionCupCommand() {
    requires(CLIMB);
    requires(VACUUM);
  }

  @Override
  protected void initialize() {
    CLIMB.lowerSuctionCup();
    VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.CLIMB);
    VACUUM.setPressure(kClimbPressureInHg);
  }

  @Override
  protected boolean isFinished() {
    return VACUUM.isClimbOnTarget();
  }
}
