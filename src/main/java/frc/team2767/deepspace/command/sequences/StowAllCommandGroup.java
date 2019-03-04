package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.teleop.StowElevatorConditionalCommand;
import frc.team2767.deepspace.command.vacuum.StowValveControlCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class StowAllCommandGroup extends CommandGroup {
  public StowAllCommandGroup() {
    addSequential(new StowValveControlCommand());
    addSequential(new StowElevatorConditionalCommand());
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    addSequential(new ElevatorSetPositionCommand(12.0));
    addSequential(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
  }
}
