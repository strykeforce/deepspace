package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.Vision.LightsOff;
import frc.team2767.deepspace.command.Vision.LightsOn;

public class SmartDashboardControls {

  public SmartDashboardControls() {
    SmartDashboard.putData("Lights On", new LightsOn());
    SmartDashboard.putData("Lights Off", new LightsOff());
  }
}
