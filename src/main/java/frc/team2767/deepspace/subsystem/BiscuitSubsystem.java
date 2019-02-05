package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class BiscuitSubsystem extends Subsystem {

    final int CLOSE_ENOUGH = 0;
    final int UP_POS = 0;
    final int DOWN_POS = 0;
    final int LEFT_POS = 0;
    final int RIGHT_POS = 0;
    final int BISCUIT_ID = 40;

    TalonSRX biscuit = new TalonSRX(BISCUIT_ID);

    @Override
    protected void initDefaultCommand() {}

    public void zero() {}

    public void pickDirection (Position position) {
        switch (position){
            case UP:
                if (biscuit.getSelectedSensorPosition() - UP_POS > 0){
                    run(-.20);
                }
                break;
            case DOWN:
                break;
            case FORWARD:
                break;
            case BACK:
                break;

        }

    }

    public void run (double power){
        biscuit.set(ControlMode.PercentOutput, power);
    }

    public enum Position {
        UP,
        DOWN,
        FORWARD,
        BACK
    }
/*
    need to know:
        max revs
        encoder vals of positions
        good speed
        good enough
    settings:
        closeEnough
        upPos
        downPos
        backPos
        forwardPos
    Objects: 1 motor 40, 1 absolute encoder, enum position
    Methods:
        check encoder is good
        zero/check zero
        Start(pos)-runs sequence of pick direction and run
        pickDirection(position)-decide fastest direction of rotation, use report to decide if safe
        forward-start motor in positive direction
        backwards-start motor in negative direction
        report-return the current encoder pos
        position reached (enum position)- current-goal<good enough
        stop
    enum Position
        up
        forward
        backwards
        down
*/
}
