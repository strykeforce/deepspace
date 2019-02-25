package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.ZeroAxisCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSafeZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.BiscuitDumpCommand;
import frc.team2767.deepspace.command.log.ElevatorDumpCommand;
import frc.team2767.deepspace.command.log.IntakeDumpCommand;
import frc.team2767.deepspace.command.log.VacuumDumpCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.*;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.*;
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

    SmartDashboard.putData(
        "Test/Intake Cargo", new IntakePositionCommand(IntakeSubsystem.kCargoPlayerPositionDeg));
    SmartDashboard.putData(
        "Test/Intake Load", new IntakePositionCommand(IntakeSubsystem.kLoadPositionDeg));
    SmartDashboard.putData(
        "Test/Intake Middle", new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
    SmartDashboard.putData(
        "Test/Intake Stow", new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
    SmartDashboard.putData("Test/Intake Dump", new IntakeDumpCommand());

    SmartDashboard.putData(
        "Test/Biscuit Up", new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Left", new BiscuitSetPositionCommand(BiscuitSubsystem.kLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Right", new BiscuitSetPositionCommand(BiscuitSubsystem.kRightPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit BS Left",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kBackStopLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit BS Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kBackStopRightPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Down Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kDownRightPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit TU Left",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kTiltUpLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit TU Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kTiltUpRightPositionDeg));
    SmartDashboard.putData("Test/Biscuit Dump", new BiscuitDumpCommand());

    SmartDashboard.putData(
        "Test/Elevator Cargo High",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoHighPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Low",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoLowPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Med",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoMediumPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Pickup",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPickupPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Player",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPlayerPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch High",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchHighPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch Low",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch Med",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchMediumPositionInches));
    SmartDashboard.putData("Test/Elevator Dump", new ElevatorDumpCommand());
    SmartDashboard.putData("Test/Elevator Zero", new ElevatorSafeZeroCommand());

    SmartDashboard.putData("Test/Vacuum Dump", new VacuumDumpCommand());
    SmartDashboard.putData("Test/Vacuum Cargo", new PressureSetCommand(VACUUM.kBallPressureInHg));
    SmartDashboard.putData("Test/Vacuum Hatch", new PressureSetCommand(VACUUM.kHatchPressureInHg));
    SmartDashboard.putData("Test/Vacuum Climb", new PressureSetCommand(VACUUM.kClimbPressureInHg));
    SmartDashboard.putData(
        "Test/Pump Enable", new ActivateValveCommand(VacuumSubsystem.Valve.PUMP));
    SmartDashboard.putData(
        "Test/Pump Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.PUMP));
    SmartDashboard.putData(
        "Test/Trident Enable", new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    SmartDashboard.putData(
        "Test/Trident Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    SmartDashboard.putData(
        "Test/Climb Enable", new ActivateValveCommand(VacuumSubsystem.Valve.CLIMB));
    SmartDashboard.putData(
        "Test/Climb Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.CLIMB));
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
        "Pit/Vacuum/Climb", new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg));

    SmartDashboard.putData(
        "Pit/Vacuum/Hatch", new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    SmartDashboard.putData(
        "Pit/Vacuum/Cargo", new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));
  }
}
