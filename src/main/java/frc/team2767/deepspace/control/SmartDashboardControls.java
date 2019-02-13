package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.IntakeInCommand;
import frc.team2767.deepspace.command.intake.IntakeOutCommand;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    SmartDashboard.putData("Test/ElevatorStop", new ElevatorZeroCommand());
  }

  private void addTestCommands() {
    logger.debug("creating test commands");
  }

  private void addVisionCommands() {
    logger.debug("creating vision commands");
    SmartDashboard.putData("Pit/LightsOn", new LightsOnCommand());
    SmartDashboard.putData("Pit/LightsOff", new LightsOffCommand());
  }
}
