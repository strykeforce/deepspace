package frc.team2767.deepspace.control;

public class Controls {

  private final DriverControls driverControls = new DriverControls(0);
  private final GameControls gameControls = new GameControls(1);
  //private final XboxControls xboxControls = new XboxControls(2);
  private final SmartDashboardControls smartDashboardControls = new SmartDashboardControls();

  public Controls() {
    //    new SmartDashboardControls();
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public GameControls getGameControls() {
    return gameControls;
  }

  /*public XboxControls getXboxControls() {
    return xboxControls;
  }*/

  public SmartDashboardControls getSmartDashboardControls() {
    return smartDashboardControls;
  }
}
