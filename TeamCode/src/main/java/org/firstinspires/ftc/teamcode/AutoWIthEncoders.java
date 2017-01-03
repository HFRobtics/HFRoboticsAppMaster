package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by bm121 on 12/14/2016.
 */
@Autonomous(name="Auto Drive By Encoder", group="Robot")
public class AutoWIthEncoders extends LinearOpMode {
    HardwareHFbot robot = new HardwareHFbot();
    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 280 ;    // eg: TETRIX Motor Encoder
    static final double     BACK_DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     FRONT_DRIVE_GEAR_REDUCTION    = .75 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 3.75 ;     // For figuring circumference
    static final double     BACK_COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * BACK_DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     FRONT_COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * FRONT_DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        robot.frontleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontrightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderDrive(DRIVE_SPEED,  100, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
          // S3: Reverse 24 Inches with 4 Sec timeout
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
            newFrontLeftTarget = robot.frontleftMotor.getCurrentPosition() + (int) (inches * FRONT_COUNTS_PER_INCH);
            newBackRightTarget = robot.backrightMotor.getCurrentPosition() + (int) (inches * BACK_COUNTS_PER_INCH);
            newFrontRightTarget = robot.frontrightMotor.getCurrentPosition() + (int) (inches * FRONT_COUNTS_PER_INCH);
            newBackLeftTarget = robot.backleftMotor.getCurrentPosition() + (int) (inches * BACK_COUNTS_PER_INCH);
            robot.frontleftMotor.setTargetPosition(newFrontLeftTarget);
            telemetry.addData("Front Targer", newFrontLeftTarget);
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


        robot.frontleftMotor.setPower(Math.abs(speed));
        robot.frontrightMotor.setPower(Math.abs(speed));
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
}
