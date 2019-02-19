package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitPositiveCommand;

public class WiggleCommandGroup extends CommandGroup {

  public WiggleCommandGroup() {
    addSequential(new BiscuitPositiveCommand());
  }
}
