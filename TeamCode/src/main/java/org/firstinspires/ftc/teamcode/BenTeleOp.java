/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code sis structured as an Iterative OpMode
 * <p>
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 * <p>
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Mecanum", group = "ROBOT")
//@Disabled
public class BenTeleOp extends OpMode {

    /* Declare OpMode members.s */
    HardwareHFbot robot = new HardwareHFbot(); // use the class created to define a Pushbot's hardware
    double thresh = 0.05;
    /*
     * Code to run ONCE when the driver hits INIT
     */


    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");
        telemetry.addData("Song", "This Grill");


    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }


    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        gamepad1.setJoystickDeadzone(0.1f);
        double leftTrigger = gamepad1.left_trigger;
        double rightTrigger = gamepad1.right_trigger;
        double BaseSpeed = 0.5;

        if (rightTrigger > .1f) {
            robot.drive(rightTrigger, -rightTrigger, rightTrigger, -rightTrigger);
        } else if (leftTrigger > .1f) {
            robot.drive(-leftTrigger, leftTrigger, -leftTrigger, leftTrigger);
        } else if (gamepad1.dpad_up && gamepad1.dpad_left)
            robot.drive(0, BaseSpeed);
        else if (gamepad1.dpad_up && gamepad1.dpad_right)
            robot.drive(BaseSpeed, 0);
        else if (gamepad1.dpad_down && gamepad1.dpad_left)
            robot.drive(-BaseSpeed, 0);
        else if (gamepad1.dpad_down && gamepad1.dpad_right)
            robot.drive(0, -BaseSpeed);
        else if (gamepad1.dpad_up)
            robot.drive(BaseSpeed, BaseSpeed);
        else if (gamepad1.dpad_down)
            robot.drive(-BaseSpeed, -BaseSpeed);
        else if (gamepad1.dpad_left)
            robot.drive(-BaseSpeed, BaseSpeed);
        else if (gamepad1.dpad_right)
            robot.drive(BaseSpeed, -BaseSpeed);
        else
            robot.drive(0, 0, 0, 0);

        if (gamepad2.x) {
            robot.shoot(1);
        } else {
            robot.shoot(0);
        }
        if (gamepad2.b) {
            robot.antiShoot(-1);
        }

        if (gamepad1.y) {
            robot.reversed = !robot.reversed;
        }

        robot.drive(RobotDirectionDrive.FOWARD, 10);

        telemetry.addData("Reversed", robot.reversed);
        telemetry.addData("Direction", robot.frontleftMotor.getDirection());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robot.drive(0, 0, 0, 0);
    }

    public int getQuadrant(double xJoyPos, double yJoyPos) { //Returns Quadrant of joystick
        if (yJoyPos == 0 && xJoyPos == 0) return 0;
        if (yJoyPos > 0 && xJoyPos > 0) return 1;
        if (yJoyPos < 0 && xJoyPos > 0) return 2;
        if (yJoyPos < 0 && xJoyPos < 0) return 3;
        if (yJoyPos > 0 && xJoyPos < 0) return 4;
        if (yJoyPos == 0 && (xJoyPos == 1 || xJoyPos == -1)) return 5;
        return 1;
    }

    public static double squareToCirlce(double joyvalue) {
        if (joyvalue < 1) {
            return -(joyvalue * joyvalue);
        } else {
            return (joyvalue * joyvalue);
        }
    }

}
