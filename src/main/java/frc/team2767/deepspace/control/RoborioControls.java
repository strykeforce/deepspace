package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.deepspace.command.vision.VisionTuneCommand;

public class RoborioControls {

  public RoborioControls() {
    DigitalInput tuneTrigger = new DigitalInput(9);
    Trigger visionButton =
        new Trigger() {
          @Override
          public boolean get() {
            return tuneTrigger.get();
          }
        };
    Trigger userButton =
        new Trigger() {
          @Override
          public boolean get() {
            return RobotController.getUserButton();
          }
        };

    visionButton.whenActive(new VisionTuneCommand(0));
    userButton.whenActive(new VisionTuneCommand(1));
  }
}
