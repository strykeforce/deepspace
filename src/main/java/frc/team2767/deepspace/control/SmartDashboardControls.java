package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.ZeroAxisCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.*;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.GamePiece;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {
    addMatchCommands();
    addPitCommands();
    addTestCommands();
    addVisionCommands();
  }

  private void addMatchCommands() {
    logger.debug("creating match commands");
    SmartDashboard.putData("Game/zeroAll", new ZeroAxisCommand());
    SmartDashboard.putData("Game/tridentSol", VACUUM.getTridentSolenoid());
    SmartDashboard.putData("Game/pumpSol", VACUUM.getPumpSolenoid());
  }

  private void addPitCommands() {
    addTestCommands();
    addIntakeCommands();
    addElevatorCommands();
    addVacuumCommands();
    SmartDashboard.putData("Pit/ElevatorZero", new ElevatorZeroCommand());
  }

  private void addTestCommands() {
    SmartDashboard.putData("Pit/SetPickup", new SetActionCommand(Action.PICKUP));
    SmartDashboard.putData("Pit/SetPlace", new SetActionCommand(Action.PLACE));

    SmartDashboard.putData("Pit/Hatch", new SetGamePieceCommand(GamePiece.HATCH));
    SmartDashboard.putData("Pit/cargo", new SetGamePieceCommand(GamePiece.CARGO));
  }

  private void addVisionCommands() {
    SmartDashboard.putData("Pit/LightsOn", new LightsOnCommand());
    SmartDashboard.putData("Pit/LightsOff", new LightsOffCommand());
  }

  private void addIntakeCommands() {
    //    SmartDashboard.putData("Pit/IntakeOut", new IntakeDownCommand());
    //    SmartDashboard.putData("Pit/IntakeStop", new ShoulderStopCommand());
    //    SmartDashboard.putData("Pit/IntakeIn", new IntakeUpCommand());
  }

  private void addElevatorCommands() {
    //    SmartDashboard.putData("Pit/ElevatorUp", new ElevatorOpenLoopUpCommand());
    //    SmartDashboard.putData("Pit/ElevatorStop", new ElevatorStopCommand());
    //    SmartDashboard.putData("Pit/ElevatorDown", new ElevatorOpenLoopDownCommand());
  }

  private void addVacuumCommands() {

    SmartDashboard.putData("Vacuum/cool", new VacuumCooldownCommandGroup());
    SmartDashboard.putData(
        "Pit/TridentValveActivate",
        new ActivateValveCommand(new VacuumSubsystem.Valve[] {VacuumSubsystem.Valve.TRIDENT}));
    SmartDashboard.putData("Pit/BuildPressure", new PressureAccumulateCommandGroup());

    SmartDashboard.putData("Pit/VacuumStop", new StopPumpCommandGroup());
    SmartDashboard.putData(
        "Pit/Vacuum/Climb", new PressureSetCommand(VacuumSubsystem.kClimbPressure));

    SmartDashboard.putData(
        "Pit/Vacuum/Hatch", new PressureSetCommand(VacuumSubsystem.kHatchPressure));
    SmartDashboard.putData(
        "Pit/Vacuum/Cargo", new PressureSetCommand(VacuumSubsystem.kBallPressure));
  }
}
