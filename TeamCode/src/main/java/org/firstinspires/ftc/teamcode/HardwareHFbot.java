package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class HardwareHFbot {
    //Defines motors and servos for public use.
    public DcMotor frontleftMotor = null;
    public DcMotor frontrightMotor = null;
    public DcMotor backrightMotor = null;
    public DcMotor backleftMotor = null;
    public DcMotor sweeper = null;
    public DcMotor shooter = null;
    public Servo   lifter = null;
    public Servo   leftBeacon = null;
    public Servo   rightBeacon = null;
    public static final double LENGTH = 17.25;  //The length and width of robot. Used for autonomous.
    public static final double WIDTH = 17.375;
    public static final double FRONT_WHEEL_SLOW_RATIO = 0.75; //Used to slow front wheels because of gearing
    public Boolean reversed = null; // Boolean to tell if driving backwards


    /* local OpMode members. */
    HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    /* Constructor */
    public HardwareHFbot() {

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;
        reversed = false;

        // Define and Initialize Motors
        backrightMotor = hwMap.dcMotor.get("br");
        frontleftMotor = hwMap.dcMotor.get("fl");
        frontrightMotor = hwMap.dcMotor.get("fr");
        backleftMotor = hwMap.dcMotor.get("bl");
        shooter = hwMap.dcMotor.get("sh");
        sweeper = hwMap.dcMotor.get("sw");
        // Define and Initialize Servos
        lifter = hwMap.servo.get("li");
        leftBeacon = hwMap.servo.get("leftB");
        rightBeacon = hwMap.servo.get("rightB");


        frontleftMotor.setDirection(DcMotor.Direction.FORWARD); //Sets correct motor direction based on Mecanum wheels.
        frontrightMotor.setDirection(DcMotor.Direction.REVERSE);
        backleftMotor.setDirection(DcMotor.Direction.FORWARD);
        backrightMotor.setDirection(DcMotor.Direction.REVERSE);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        sweeper.setDirection(DcMotor.Direction.REVERSE);

        // Set all motors to zero power
        frontleftMotor.setPower(0);
        backleftMotor.setPower(0);
        frontrightMotor.setPower(0);
        backrightMotor.setPower(0);
        shooter.setPower(0);
        sweeper.setPower(0);

        //Sets all servo positions to 0;
        lifter.setPosition(0);
        leftBeacon.setPosition(0);
        rightBeacon.setPosition(0);

        //Sets driving motors to run with encoders. Used for autonomous
        frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Sets sweeper and ball shooter to run without encoders because it does require exact measure.
        sweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /***
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long remaining = periodMs - (long) period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }

    public void drive(double fl, double fr, double bl, double br) { //"main drive" Sets power of motors. Always called to move robot in TeleOP.

            if (reversed) { //Reverses motor speed for reverse controls
                frontrightMotor.setPower(-fr * FRONT_WHEEL_SLOW_RATIO);
                frontleftMotor.setPower(-fl * FRONT_WHEEL_SLOW_RATIO);
                backrightMotor.setPower(-br);
                backleftMotor.setPower(-bl);
            } else { //Sets motor speed based on arguments
                frontrightMotor.setPower(fr * FRONT_WHEEL_SLOW_RATIO);
                frontleftMotor.setPower(fl * FRONT_WHEEL_SLOW_RATIO);
                backrightMotor.setPower(br);
                backleftMotor.setPower(bl);
            }
        }

    public void drive(double flbr, double frbl) { //"Secondary" Overloaded drive method that calls "main" drive method.
                                                  // Used for simplification when applicable

        drive(flbr, frbl, frbl, flbr);
    }

    public void drive(RobotDirectionDrive dir, double speed){ //Overloaded drive method that calls "Secondary" drive method.
                                                              // Uses enums defined in RobotDirectionDrive.
                                                              //Allows for easily coding in new movements

        switch (dir){ //Sets drive to correct value based on variable dir
            case FORWARD: {
                drive(speed,speed); //Calls "Secondary" drive method
                break;              //Breaks out of switch statement.
            }
            case BACK: {
                drive(-speed,-speed);
                break;
            }
            case LEFT: {
                drive(speed, -speed);
                break;
            }
            case RIGHT: {
                drive(-speed, speed);
                break;
            }
            case DFLEFT: {
                drive(0, speed);
                break;
            }
            case DFRIGHT: {
                drive(speed, 0);
                break;
            }
            case DBLEFT: {
                drive(-speed, 0);
                break;
            }
            case DBRIGHT: {
                drive(0, -speed);
                break;
            }
            case SPINLEFT: {
                drive(-speed,speed,-speed,speed); //Calls "main" drive due to unique nature of spinning with Mecanum wheels.
                break;
            }
            case SPINRIGHT: {
                drive(speed,-speed,speed,-speed);
                break;
            }

        }
    }


    public void shoot(boolean running) {  //Shoots the ball from flicker.
        if(running) {  //Checks if flicker should be running.
            shooter.setPower(1); //Sets it running
        }else{
            shooter.setPower(0); //Sets it off
        }

    }
}

