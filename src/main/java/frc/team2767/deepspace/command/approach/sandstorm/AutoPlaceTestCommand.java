package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPlaceCommandGroup;
import frc.team2767.deepspace.command.sequences.pickup.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class AutoPlaceTestCommand extends CommandGroup {

  public AutoPlaceTestCommand() {
    addSequential(new SandstormHatchPickupCommandGroup(), 0.5);
    addSequential(new SetFieldDirectionCommand(FieldDirection.RIGHT));
    addSequential(new AutoHatchPlaceCommandGroup(0.0));
  }
}
