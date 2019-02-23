package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class StowAllCommandGroup extends CommandGroup {
  public StowAllCommandGroup() {
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPosition));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kStowPosition));
    addSequential(new IntakePositionCommand(IntakeSubsystem.kUpPosition));
  }
}
