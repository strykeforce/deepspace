package frc.team2767.deepspace.control;

public class Controls {

  private final DriverControls driverControls = new DriverControls(0);
  private final GameControls gameControls = new GameControls(1);

  public Controls() {
    //    new SmartDashboardControls();
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public GameControls getGameControls() {
    return gameControls;
  }
}
