package frc.team2767.deepspace.command.climb;

import static frc.team2767.deepspace.subsystem.VacuumSubsystem.kClimbPressureInHg;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class LowerSuctionCupCommand extends InstantCommand {
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  public LowerSuctionCupCommand() {
    requires(CLIMB);
    requires(VACUUM);
  }

  @Override
  protected void initialize() {
    CLIMB.lowerSuctionCup();
    VACUUM.setSolenoid(VacuumSubsystem.Valve.CLIMB, true);
    VACUUM.setPressure(kClimbPressureInHg);
  }
}
