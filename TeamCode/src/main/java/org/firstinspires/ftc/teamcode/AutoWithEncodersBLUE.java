package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Size;

/**
 * Created by bm121 on 12/14/2016.
 */
@Autonomous(name="BLUE Auto Drive By Encoder", group="Robot")
public class AutoWithEncodersBLUE extends LinearVisionOpMode {
    HardwareHFbot robot = new HardwareHFbot();
    private ElapsedTime runtime = new ElapsedTime();

    static final double     DRIVE_SPEED             = 0.25;  //Sets autonomous drive speed
    static final double     COUNTS_PER_INCH_FB         = 10000/108.0; //Sets motor counts to go one inch
    static int frontLeftDist; //Declares variables for distance in motor counts to move
    static int backRightDist;
    static int frontRightDist;
    static int backLeftDist;


    @Override
    public void runOpMode() throws InterruptedException{ //Runs through once

        robot.init(hardwareMap); //Calls HardwareHFbot init

        // Send telemetry message to show encoder status
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        //Resets encoders for motors
        robot.frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitOneFullHardwareCycle();//Waits for all hardware tasks to complete before moving on.

        //Sets all motors to run using built in encoders
        robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Waits for OpenCV initialize
        waitForVisionStart();
        this.setCamera(Cameras.PRIMARY); //Sets camera to back camera.
        this.setFrameSize(new Size(900, 900)); //Sets size of frame


        enableExtension(Extensions.BEACON);         //Beacon detection
        enableExtension(Extensions.ROTATION);       //Automatic screen rotation correction
        enableExtension(Extensions.CAMERA_CONTROL); //Manual camera control

        beacon.setAnalysisMethod(Beacon.AnalysisMethod.COMPLEX); //More accurate

        beacon.setColorToleranceRed(0); //Medium tolerance for red and blue
        beacon.setColorToleranceBlue(0);

        rotation.setIsUsingSecondaryCamera(false); //Does not use secondary camera
        rotation.disableAutoRotate(); //Disable phone to rotate camera
        rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT); //Sets orientation to mounted position

        //Sets camera to auto determine settings based on best conditions
        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO); //
        cameraControl.setAutoExposureCompensation();

        waitForStart(); //Wait for driver to start autonomous


        //Drives at (DRIVE_SPEED, distance in inches, direction, timeout in seconds)
        encoderDrive(DRIVE_SPEED,  39 + robot.LENGTH, RobotDirectionDrive.FORWARD, 30.0); //Drives to beacon
        robot.waitForTick(500);
        encoderDrive(DRIVE_SPEED,  16, RobotDirectionDrive.RIGHT, 30.0);
        robot.waitForTick(500);
        encoderDrive(DRIVE_SPEED, 3.625 + (robot.LENGTH /2.0), RobotDirectionDrive.FORWARD, 30.0);
        robot.waitForTick(500);
        getLeftOrRightColor().setPosition(.5);
    }

    public void encoderDrive(double speed,
                             double inches, RobotDirectionDrive dir,
                             double timeoutS) {                        //Method to drive to position in inches with timeout in seconds
        //Also uses direction and speed

        //Calculates motor counts to move motor
        frontLeftDist = (int) (inches * COUNTS_PER_INCH_FB);
        backRightDist = (int) (inches * COUNTS_PER_INCH_FB);
        frontRightDist = (int) (inches * COUNTS_PER_INCH_FB);
        backLeftDist = (int) (inches * COUNTS_PER_INCH_FB);

        //Declares target for motors to run to.
        int newFrontLeftTarget;
        int newBackRightTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            setDirection(dir); //Calls method setDirection with parameter of direction moving
            //In the method values are manipulated to move motors in correct direction

            // Determine new target position
            newFrontLeftTarget = robot.frontleftMotor.getCurrentPosition() + frontLeftDist;
            newBackRightTarget = robot.backrightMotor.getCurrentPosition() + backRightDist;
            newFrontRightTarget = robot.frontrightMotor.getCurrentPosition() + frontRightDist;
            newBackLeftTarget = robot.backleftMotor.getCurrentPosition() + backLeftDist;

            //Adds telemetry to show target position
            telemetry.addData("Target", newFrontLeftTarget);

            //Sets target position for each motor
            robot.frontleftMotor.setTargetPosition(newFrontLeftTarget);
            robot.backrightMotor.setTargetPosition(newBackRightTarget);
            robot.frontrightMotor.setTargetPosition(newFrontRightTarget);
            robot.backleftMotor.setTargetPosition(newBackLeftTarget);

            // Turn On RUN_TO_POSITION
            robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();

            //Sets motor power based on method parameter
            robot.frontleftMotor.setPower(Math.abs(speed * .75)); //Front motors are run at .75 speed due to gearing
            robot.frontrightMotor.setPower(Math.abs(speed * .75));
            robot.backleftMotor.setPower(Math.abs(speed));
            robot.backrightMotor.setPower(Math.abs(speed));

            // Keep looping while opmode is still active, and there is time left, and both motors are running.
            // Insures motor reaches position before turning off
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontleftMotor.isBusy() && robot.frontrightMotor.isBusy() && robot.backleftMotor.isBusy() && robot.backrightMotor.isBusy())) {

                // Display beacon color for the driver.
                telemetry.addData("Beacon Color", beacon.getAnalysis().getColorString()); //Shows "left color, right color"
                telemetry.update();
            }


            // Stops all motion;
            robot.frontleftMotor.setPower(0);
            robot.backrightMotor.setPower(0);
            robot.frontrightMotor.setPower(0);
            robot.backleftMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
    }

    public Servo getLeftOrRightColor(){ //Returns servo to put down based on color gotten from beacon\
        String full;
        do {
            full = beacon.getAnalysis().getColorString(); //Gets color of beacons. Returns "left color, right color"
        }while(full.equals("???, ???"));                  //Loops code till values are returned
        int indexOfComma = full.indexOf(",");             //Gets index of the comma in full in preperation of splitting string
        String left = full.substring(0,indexOfComma);     //Splits left part of string
        String right = full.substring(indexOfComma + 2, full.length());     //Splits right part of string

        //Checks which side equals "red" and returns that side
        telemetry.addData("left String: ", left);
        telemetry.addData("right String: ", right);
        if(left.toLowerCase().equals("blue")){
            return robot.leftBeacon;
        }
        if(right.toLowerCase().equals("blue")){
            return robot.rightBeacon;
        }
        return robot.lifter;
    }

    public static void setDirection(RobotDirectionDrive dir){
        //Takes direction and manipulates variables to insure motors are moving in correct direction
        //to move the robot in the right direction
        //A value of 1 means move the robot motor forward while -1 means move it backwards. 0 means don't move at all

        int fl = 1; int br = 1; int fr = 1; int bl = 1; //fl = Front Left Motor and so on
        switch (dir) {
            case FORWARD: {
                //Default
                break;
            }
            case BACK: {
                fl = -1;
                br = -1;
                fr = -1;
                bl = -1;
                break;
            }
            case LEFT: {
                fl = -1;
                br = -1;
                break;
            }
            case RIGHT: {
                fr = -1;
                bl = -1;
                break;
            }
            case DFLEFT: {
                fl = 0;
                br = 0;
                break;
            }
            case DFRIGHT: {
                fr = 0;
                bl = 0;
                break;
            }
            case DBLEFT: {
                fl = -1;
                br = -1;
                fr = 0;
                bl = 0;
                break;
            }
            case DBRIGHT: {
                fl = 0;
                br = 0;
                fr = -1;
                bl = -1;
                break;
            }
            case SPINLEFT: {
                fl = -1;
                bl = -1;
                break;
            }
            case SPINRIGHT: {
                fr = -1;
                bl = -1;
                break;
            }
        }
        //Multiplies distance to travel by -1, 1, 0 to move motors in correct direction
        frontLeftDist *= fl;
        backRightDist *= br;
        frontRightDist *= fr;
        backLeftDist *= bl;
    }
}
