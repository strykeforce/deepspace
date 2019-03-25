package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.StowAllCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class DeploySequenceCommand extends CommandGroup {
  public DeploySequenceCommand() {
    addSequential(new LogCommand("BEGIN DEPLOY SEQUENCE"));
    addParallel(
        new CommandGroup() {
          {
            addSequential(new StowAllCommandGroup());
            addSequential(new ElevatorSetPositionCommand(5.0), 0.5);
          }
        });
    addSequential(new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.CLIMB));
    addSequential(new EngageRatchetCommand(false));
    addSequential(new ClimbPositionCommand(ClimbSubsystem.kLowReleaseIn));
    addSequential(new ClimbPositionCommand(ClimbSubsystem.kHighReleaseIn));
    addSequential(new LogCommand("END DEPLOY SEQUENCE"));
  }
}
