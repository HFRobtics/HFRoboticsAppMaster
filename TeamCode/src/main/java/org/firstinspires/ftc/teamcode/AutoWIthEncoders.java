package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
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
@Autonomous(name="Auto Drive By Encoder", group="Robot")
public class AutoWIthEncoders extends LinearVisionOpMode {
    HardwareHFbot robot = new HardwareHFbot();
    private ElapsedTime runtime = new ElapsedTime();

    static final double     DRIVE_SPEED             = 0.25;
    static final double     COUNTS_PER_INCH_FB         = 10000/108.0;
    static final String     TEAMRED                    = "red";
    static final String     TEAMBLUE                    = "blue";


    @Override
    public void runOpMode() throws InterruptedException{

        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        robot.frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        waitOneFullHardwareCycle();

        robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        waitForVisionStart();
        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(900, 900));


        enableExtension(Extensions.BEACON);         //Beacon detection
        enableExtension(Extensions.ROTATION);       //Automatic screen rotation correction
        enableExtension(Extensions.CAMERA_CONTROL); //Manual camera control

        beacon.setAnalysisMethod(Beacon.AnalysisMethod.COMPLEX);

        beacon.setColorToleranceRed(0);
        beacon.setColorToleranceBlue(0);

        rotation.setIsUsingSecondaryCamera(false);
        rotation.disableAutoRotate();
        rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT);

        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
        cameraControl.setAutoExposureCompensation();

        waitForStart();


        encoderDrive(DRIVE_SPEED,  39, 20.0);
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double inches,
                             double timeoutS) {
        int newFrontLeftTarget;
        int newBackRightTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newFrontLeftTarget = robot.frontleftMotor.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH_FB);
            newBackRightTarget = robot.backrightMotor.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH_FB);
            newFrontRightTarget = robot.frontrightMotor.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH_FB);
            newBackLeftTarget = robot.backleftMotor.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH_FB);

            telemetry.addData("Target", newFrontLeftTarget);

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


        robot.frontleftMotor.setPower(Math.abs(speed * .75));
        robot.frontrightMotor.setPower(Math.abs(speed * .75));
        robot.backleftMotor.setPower(Math.abs(speed));
        robot.backrightMotor.setPower(Math.abs(speed));

        // keep looping while we are still active, and there is time left, and both motors are running.
        while (opModeIsActive() &&
                (runtime.seconds() < timeoutS) &&
                (robot.frontleftMotor.isBusy() && robot.frontrightMotor.isBusy() && robot.backleftMotor.isBusy() && robot.backrightMotor.isBusy())) {

            // Display it for the driver.
            //telemetry.addData("Path1",  "Running to %7d :%7d", newFrontLeftTarget,  newFrontRightTarget);
            telemetry.addData("Path2", "Running at %7d :%7d",
                    robot.frontleftMotor.getCurrentPosition(),
                    robot.frontrightMotor.getCurrentPosition());
            telemetry.addData("Beacon Color", beacon.getAnalysis().getColorString());
            telemetry.update();
        }


            // Stop all motion;
            robot.frontleftMotor.setPower(0);
            robot.backrightMotor.setPower(0);
            robot.frontrightMotor.setPower(0);
            robot.backleftMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }

    public static String getLeftOrRightColor(){
        String full = beacon.getAnalysis().getColorString();
        int indexOfComma = full.indexOf(",");
        String left = full.substring(0,indexOfComma);
        String right = full.substring(indexOfComma + 1, full.length());
        if(left.toLowerCase().equals(TEAMRED)){
            return left;
        }
        if(right.toLowerCase().equals(TEAMRED)){
            return right;
        }
        return "none";
    }

    public static void rotate(double degrees){

    }

    public static void lowerServo(){

    }
}
