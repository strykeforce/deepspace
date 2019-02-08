package frc.team2767.deepspace.motion;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trajectory {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private double[] velocity;
  private double[] acceleration;
  private double[] heading;
  private double[] position;
  private int length;
  private BufferedReader bufferedReader;
  private File csvFile;

  public Trajectory(File csvFile) {
    this.csvFile = csvFile;
    length = 0;
    findLength();

    velocity = new double[length];
    acceleration = new double[length];
    heading = new double[length];
    position = new double[length];

    read();
  }

  private void findLength() {
    try {

      if (csvFile.exists()) {

        FileReader fileReader = new FileReader(csvFile);
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);

        length = 0;

        while (lineNumberReader.readLine() != null) {
          length++;
        }
        lineNumberReader.close();

      } else {
        logger.debug("File does not exist");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      bufferedReader = new BufferedReader(new FileReader(csvFile));
      int iteration = 0;
      String line;
      String headers = bufferedReader.readLine();
      logger.debug("{}", headers);
      while ((line = bufferedReader.readLine()) != null) {
        // use comma as separator
        String csvSplit = ",";
        String[] values = line.split(csvSplit);

        position[iteration] = Double.parseDouble(values[3]);
        velocity[iteration] = Double.parseDouble(values[4]);
        acceleration[iteration] = Double.parseDouble(values[5]);
        heading[iteration] = Double.parseDouble(values[7]);

        logger.debug(
            "{}, {}, {}, {}",
            position[iteration],
            velocity[iteration],
            acceleration[iteration],
            heading[iteration]);

        iteration++;
      }

    } catch (IOException e) {
      logger.error("{}", e);
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          logger.error("{}", e);
        }
      }
    }
  }

  public int length() {
    return length;
  }

  public Segment getIteration(int iteration) {
    return new Segment(
        heading[iteration], acceleration[iteration], velocity[iteration], position[iteration]);
  }

  static class Segment {
    double heading;
    double acceleration;
    double velocity;
    double position;

    Segment(double heading, double acceleration, double velocity, double position) {
      this.heading = heading;
      this.acceleration = acceleration;
      this.velocity = velocity;
      this.position = position;
    }
  }
}
