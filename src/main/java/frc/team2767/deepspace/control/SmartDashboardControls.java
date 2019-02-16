package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.biscuit.BiscuitSetStatesCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopDownCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopUpCommand;
import frc.team2767.deepspace.command.elevator.ElevatorStopCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.*;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.DeactivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.VacuumStopCommand;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  //  private SendableChooser level;
  //  private SendableChooser action;
  //  private SendableChooser gamePiece;

  public SmartDashboardControls() {
    addMatchCommands();
    addPitCommands();
    addTestCommands();
    addVisionCommands();
  }

  private void addMatchCommands() {
    logger.debug("creating match commands");
    SmartDashboard.putData("Game/IntakeIn", new IntakeInCommand());
    SmartDashboard.putData("Game/IntakeOut", new IntakeOutCommand());
  }

  private void addPitCommands() {
    addTestCommands();
    addIntakeCommands();
    addElevatorCommands();
    addVacuumCommands();
    SmartDashboard.putData("Pit/ElevatorZero", new ElevatorZeroCommand());
  }

  private void addTestCommands() {
    //    level = new SendableChooser();
    //    action = new SendableChooser();
    //    gamePiece = new SendableChooser();
    //
    //    level.addOption("Level 3", BiscuitSubsystem.Level.THREE);
    //    level.addOption("Level 2", BiscuitSubsystem.Level.TWO);
    //    level.addOption("Level 1", BiscuitSubsystem.Level.ONE);
    //
    //    action.addOption("Pickup", BiscuitSubsystem.Action.PICKUP);
    //    action.addOption("Place", BiscuitSubsystem.Action.PLACE);
    //
    //    gamePiece.addOption("Hatch", BiscuitSubsystem.GamePiece.HATCH);
    //    gamePiece.addOption("Cargo", BiscuitSubsystem.GamePiece.CARGO);
    //
    //    SmartDashboard.putData("Pit/Level", level);
    //    SmartDashboard.putData("Pit/Action", action);
    //    SmartDashboard.putData("Pit/GamePiece", gamePiece);

    SmartDashboard.putData(
        "Pit/3CU",
        new BiscuitSetStatesCommand(
            BiscuitSubsystem.Level.THREE,
            BiscuitSubsystem.GamePiece.CARGO,
            BiscuitSubsystem.Action.PICKUP));
    SmartDashboard.putData(
        "Pit/2HU",
        new BiscuitSetStatesCommand(
            BiscuitSubsystem.Level.TWO,
            BiscuitSubsystem.GamePiece.HATCH,
            BiscuitSubsystem.Action.PICKUP));
    SmartDashboard.putData(
        "Pit/2CP",
        new BiscuitSetStatesCommand(
            BiscuitSubsystem.Level.TWO,
            BiscuitSubsystem.GamePiece.CARGO,
            BiscuitSubsystem.Action.PLACE));
    logger.debug("creating test commands");
  }

  private void addVisionCommands() {
    logger.debug("creating vision commands");
    SmartDashboard.putData("Pit/LightsOn", new LightsOnCommand());
    SmartDashboard.putData("Pit/LightsOff", new LightsOffCommand());
  }

  private void addIntakeCommands() {
    SmartDashboard.putData("Pit/IntakeOut", new IntakeDownCommand());
    SmartDashboard.putData("Pit/IntakeStop", new IntakeStopCommand());
    SmartDashboard.putData("Pit/IntakeIn", new IntakeUpCommand());
  }

  private void addElevatorCommands() {
    SmartDashboard.putData("Pit/ElevatorUp", new ElevatorOpenLoopUpCommand());
    SmartDashboard.putData("Pit/ElevatorStop", new ElevatorStopCommand());
    SmartDashboard.putData("Pit/ElevatorDown", new ElevatorOpenLoopDownCommand());
  }

  private void addVacuumCommands() {

    SmartDashboard.putData(
        "Pit/TridentValveActivate", new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    SmartDashboard.putData(
        "Pit/TridentValveDeactivate", new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));

    SmartDashboard.putData("Pit/VacuumStop", new VacuumStopCommand());
    SmartDashboard.putData(
        "Pit/VacuumHatch", new PressureSetCommand(VacuumSubsystem.VacuumPressure.HATCH));
    SmartDashboard.putData(
        "Pit/Vacuum/Ball", new PressureSetCommand(VacuumSubsystem.VacuumPressure.BALL));
    SmartDashboard.putData(
        "Pit/Vacuum/Climb", new PressureSetCommand(VacuumSubsystem.VacuumPressure.CLIMB));
  }

  private void addStatesCommand() {}
}
