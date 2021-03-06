package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.teleop.StowElevatorConditionalCommand;
import frc.team2767.deepspace.command.vacuum.StowValveControlCommand;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class StowAllCommandGroup extends CommandGroup {
  public StowAllCommandGroup() {
    addSequential(new LogCommand("BEGIN STOW ALL"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new StowValveControlCommand());
            addParallel(new StowElevatorConditionalCommand());
            addParallel(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
            addParallel(new LightsOffCommand());
          }
        });
    addSequential(new LogCommand("END STOW ALL"));
  }
}
