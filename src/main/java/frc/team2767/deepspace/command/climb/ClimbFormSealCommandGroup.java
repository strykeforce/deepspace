package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.vision.BlinkLightsCommand;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class ClimbFormSealCommandGroup extends CommandGroup {
  public ClimbFormSealCommandGroup() {
    addSequential(new LowerSuctionCupCommand());
    addSequential(new BlinkLightsCommand(VisionSubsystem.LightPattern.CLIMB_GOOD));
  }
}
