package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetLevelTiltCommand;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class BiscuitGoToSideCommand extends CommandGroup {
  public BiscuitGoToSideCommand(FieldDirection direction) {
    addSequential(new SetFieldDirectionCommand(direction));
    addSequential(new SetLevelTiltCommand());
    addSequential(new BiscuitExecutePlanCommand());
  }
}
