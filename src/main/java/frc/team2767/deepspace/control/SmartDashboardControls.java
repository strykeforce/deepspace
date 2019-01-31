package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;

// FIXME
public class SmartDashboardControls {

  public SmartDashboardControls() {
    SmartDashboard.putData("Lights On", new LightsOnCommand());
    SmartDashboard.putData("Lights Off", new LightsOffCommand());
  }
}
