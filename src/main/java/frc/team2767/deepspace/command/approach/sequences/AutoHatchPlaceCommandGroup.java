package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.approach.CalculateRotationCommand;
import frc.team2767.deepspace.command.approach.OpenLoopDriveUntilCurrentCommand;
import frc.team2767.deepspace.command.approach.YawToTargetCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import kotlinx.html.B;

public class AutoHatchPlaceCommandGroup extends CommandGroup {

  public AutoHatchPlaceCommandGroup() {
    addSequential(new LightsOnCommand());

    // clear camera line of sight if trident/hatch in the way
    addSequential(new ConditionalCommand(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg)) {
      final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

      @Override
      protected boolean condition() {
        return ELEVATOR.getPosition() < 16.0;
      }

    });
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateRotationCommand());
    addSequential(new YawToTargetCommand());
    addSequential(new OpenLoopDriveUntilCurrentCommand());
  }
}
