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
 *
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 *
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Mecanum", group="ROBOT")
//@Disabled
public class BenTeleOp extends OpMode {

    /* Declare OpMode members.s */
    HardwareHFbot robot       = new HardwareHFbot(); // use the class created to define a Pushbot's hardware
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

        double rawRstickX = gamepad.right_stick_x;
        double rawRstickY = -gamepad.right_stick_y;
        double rightStickX = circleToSquare(rawRStickX);
        double rightStickY = circleToSquare(rawRStickY);
        double linearPower = rawRstickY;
        double rotationalPower = rightStickY - rightStickX;
        double leftTrigger = gamepad1.left_trigger;
        double rightTrigger = gamepad1.right_trigger;
        int quadrant = getQuadrant(rightStickX,rightStickY);
        double BaseSpeed = 0.5;

        if(rightStickX > thresh || rightStickY > thresh || rightStickX < -thresh || rightStickY < -thresh){ //using joystick
            switch (quadrant){
                case 0:
                    robot.drive(0,0,0,0);
                case 1 :
                    robot.drive(linearPower, rotationalPower, rotationalPower, linearPower); //Sets FR && BL to Rotational in Quadrant 1
                    break;
                case 2:
                    robot.drive(rotationalPower,linearPower,linearPower,rotationalPower); //Sets FL && BR to Rotational in Quadrant 2
                    break;
                case 3 :
                    robot.drive(linearPower, rotationalPower, rotationalPower, linearPower); //Sets FR && BL to Rotational in Quadrant 3
                    break;
                case 4:
                    robot.drive(rotationalPower,linearPower,linearPower,rotationalPower); //Sets FL && BR to Rotational in Quadrant 4
                    break;
                case 5:
                    robot.drive(rightStickX,-rightStickX,-rightStickX,rightStickX); //Sets motors FL && BR to opposite X-vaule of FR && BL
                    break;
            }
        }
        else if ( rightTrigger > thresh){
            robot.drive(rightTrigger,-rightTrigger,rightTrigger,-rightTrigger);
        }
        else if ( leftTrigger > thresh){
            robot.drive(-leftTrigger,leftTrigger,-leftTrigger,leftTrigger);
        }
        /*else if (rightTrigger > thresh || leftTrigger > thresh) { //code throws infinite loop
            while ((leftTrigger > 0 || rightTrigger > 0) && !(leftTrigger > 0 && rightTrigger > 0)) { //Checks is one trigger is down but not both
                if (leftTrigger > 0) {
                    robot.drive(0, leftTrigger, leftTrigger, 0); //Sets FR && BL to leftTrigger vaule and will rotate robot left
                }
                if (rightTrigger > 0) {
                    robot.drive(rightTrigger, 0, 0, rightTrigger); //Sets FL && BR to rightTrigger vaule and will rotate robot right
                }
            }
        }*/
        else if(gamepad1.dpad_up)
            robot.drive(BaseSpeed,BaseSpeed,BaseSpeed,BaseSpeed);
        else if(gamepad1.dpad_down)
            robot.drive(-BaseSpeed,-BaseSpeed,-BaseSpeed,-BaseSpeed);
        else if(gamepad1.dpad_left)
            robot.drive(-BaseSpeed,BaseSpeed,BaseSpeed,-BaseSpeed);
        else if(gamepad1.dpad_right)
            robot.drive(BaseSpeed,-BaseSpeed,-BaseSpeed,BaseSpeed);
        else
            robot.drive(0,0,0,0);

        telemetry.addData("Joy Y", rightStickY);
        telemetry.addData("Joy X", rightStickX);
        telemetry.addData("FLBR", linearPower);
        telemetry.addData("FRBL", rotationalPower);
        telemetry.addData("Quadrant", quadrant);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robot.drive(0,0,0,0);
    }

    public int getQuadrant(double xJoyPos, double yJoyPos){ //Returns Quadrant of joystick
        if (yJoyPos == 0 && xJoyPos == 0) return 0;
        if (yJoyPos > 0 && xJoyPos > 0) return 1;
        if (yJoyPos < 0 && xJoyPos > 0) return 2;
        if (yJoyPos < 0 && xJoyPos < 0) return 3;
        if (yJoyPos > 0 && xJoyPos < 0) return 4;
        if (yJoyPos == 0 && (xJoyPos == 1 || xJoyPos == -1)) return 5;
        return 1;
    }

    public double circleToSquare(double joyInput)
    {
        if(joyInput < 0)
        {
            return -(Math.pow(joyInput, 2))
        }else{
            return Math.pow(joyInput, 2)
        }
    }
}
