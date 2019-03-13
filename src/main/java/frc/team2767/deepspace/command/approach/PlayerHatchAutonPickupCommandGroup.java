package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.command.vision.SelectCameraCommand;

public class PlayerHatchAutonPickupCommandGroup extends CommandGroup {

  public PlayerHatchAutonPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTON HATCH PICKUP"));
    addSequential(new LightsOnCommand());
    addSequential(
        new CommandGroup() {
          {
            addSequential(new SelectCameraCommand());
            addParallel(new ChooseTargetYawCommand());
          }
        });

    addSequential(new QueryPyeyeCommand());
    addSequential(new LogCommand("END AUTON HATCH PICKUP"));
  }
}
