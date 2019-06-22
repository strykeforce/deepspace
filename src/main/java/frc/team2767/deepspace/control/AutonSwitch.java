package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonSwitch {
  private static final int BITS = 4;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final List<DigitalInput> digitalInputs = new ArrayList<>();

  public AutonSwitch() {
    logger.debug("initializing autonomous switch");
    for (int i = 0; i < BITS; i++) {
      digitalInputs.add(i, new DigitalInput(i));
    }
  }

  /**
   * Read the selected autonomous mode from the binary-code hexadecimal switch. Don't be fooled by
   * hex numbers when debugging, for example switch position 24 (hex) = 36 (dec).
   *
   * <p>The switch wiring labelled 0-5 are connected to corresponding DIO ports 0-5.
   *
   * @return the switch position
   */
  public int position() {
    int val = 0;
    for (int i = BITS; i-- > 0; ) {
      val = val << 1;
      val = (val & 0xFE) | (digitalInputs.get(i).get() ? 0 : 1);
    }
    return val;
  }
}
