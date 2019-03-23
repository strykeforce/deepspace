package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.command.approach.CalculateRotationCommand;
import frc.team2767.deepspace.command.approach.OpenLoopDriveUntilCurrentCommand;
import frc.team2767.deepspace.command.approach.TalonConfigCommand;
import frc.team2767.deepspace.command.approach.YawToTargetCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class HatchPlaceCommandGroup extends CommandGroup {

  public HatchPlaceCommandGroup() {
    addSequential(new LogCommand("BEGIN HATCH PLACE"));

    addSequential(
        new ConditionalCommand(new SetFieldDirectionCommand(FieldDirection.LEFT)) {
          @Override
          protected boolean condition() {
            return DriverStation.getInstance().isAutonomous();
          }
        });
    addSequential(new LightsOnCommand());
    addSequential(new QueryPyeyeCommand());
    addParallel(new TalonConfigCommand(DriveSubsystem.DriveTalonConfig.YAW_CONFIG));
    addSequential(new CalculateRotationCommand());
    addSequential(new YawToTargetCommand());
    addSequential(
        new ConditionalCommand(
            new CommandGroup() {
              {
                addParallel(new ElevatorExecutePlanCommand());
                addParallel(new BiscuitExecutePlanCommand());
              }
            }) {
          @Override
          protected boolean condition() {
            return DriverStation.getInstance().isAutonomous();
          }
        });
    addSequential(new LogCommand("FINISHED MOVING AXIS"));
    addSequential(new TalonConfigCommand(DriveSubsystem.DriveTalonConfig.DRIVE_CONFIG));
    addSequential(new OpenLoopDriveUntilCurrentCommand(), 5.0);
    addSequential(new LogCommand("END HATCH PLACE"));
  }
}
