package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a HFBot.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"s
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class HardwareHFbot
{
    /* Public OpMode members. */
    public DcMotor frontleftMotor   = null;
    public DcMotor frontrightMotor  = null;
    public DcMotor backrightMotor  = null;
    public DcMotor backleftMotor  = null;
    public DcMotor shooterLeft  = null;
    public DcMotor shooterRight  = null;
    public DcMotor evelator  = null;
    //public Servo    rightClaw   = null;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareHFbot(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        frontleftMotor   = hwMap.dcMotor.get("fl");
        frontrightMotor  = hwMap.dcMotor.get("fr");
        backleftMotor   = hwMap.dcMotor.get("bl");
        backrightMotor  = hwMap.dcMotor.get("br");
        shooterLeft     = hwMap.dcMotor.get("sl");
        shooterRight     = hwMap.dcMotor.get("sr");
        evelator        = hwMap.dcMotor.get("el");

        frontleftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        frontrightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        backleftMotor.setDirection(DcMotor.Direction.FORWARD);
        backrightMotor.setDirection(DcMotor.Direction.REVERSE);
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);
        shooterRight.setDirection(DcMotor.Direction.FORWARD);
        evelator.setDirection(DcMotor.Direction.REVERSE);

        // Set all motors to zero power
        frontleftMotor.setPower(0);
        backleftMotor.setPower(0);
        frontrightMotor.setPower(0);
        backrightMotor.setPower(0);
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
        evelator.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        evelator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define and initialize ALL installed servos.
//        leftClaw = hwMap.servo.get("left_hand");
//        rightClaw = hwMap.servo.get("right_hand");
//        leftClaw.setPosition(MID_SERVO);
//        rightClaw.setPosition(MID_SERVO);
    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long  remaining = periodMs - (long)period.milliseconds();

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
    public void drive(double fl, double fr, double bl, double br ){
        if(fl <= 1 && fr <= 1 && bl <= 1 && br <= 1 && fl >= -1 && fr >= -1 && bl >= -1 && br >= -1 ) { //make sure in range of motor
            double slowRatio = (6.75 / 10);
            frontrightMotor.setPower(fr * slowRatio);
            frontleftMotor.setPower(fl * slowRatio);
            backrightMotor.setPower(br);
            backleftMotor.setPower(bl);
        }
        else{
            drive(0,0,0,0);
        }
    }
    public void drive(double flbr, double frbl){
        drive(flbr,frbl,frbl,flbr);
    }
    public void shoot (double speed){
        //gamemode 1
        //give JA7ja minecraft:diamond 64
        //op JA7ja
        //give JA7ja minecract:diamond 64
        //tp JA7ja ~0 ~100 ~0
        shooterLeft.setPower(speed);
        shooterRight.setPower(speed);
        evelator.setPower(speed);
    }
}

