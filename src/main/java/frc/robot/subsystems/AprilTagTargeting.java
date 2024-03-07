// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class AprilTagTargeting extends SubsystemBase {
  //values from 2024 robot, all values are in inches or degrees
  private final double HEIGHT_OF_SPEAKER = 57.125; //use this for source as well
  private final double HEIGHT_OF_AMP = 53.375; 
  private final double HEIGHT_OF_STAGE = 52; 
  private final double HEIGHT_OF_CAMERA = 12;
  private final double CAMERA_MOUNT_ANGLE = 45.2;
  private double targetHeight;

  private PhotonCamera camera;
  private PhotonTrackedTarget currentTarget;

  //Red: 3 alt, 4 speaker, 5 amp, 9 source R, 10 source L, 11 stage L, 12 stage R, 13 stage B
  //Blue: 8 alt, 7 speaker, 6 amp, 1 source R, 2 source L, 15 Stage L, 16 Stage R, 14 stage B
  private final int[] RED_TAGS = {3,4,5,9,10,11,12,13};
  private final int[] BLUE_TAGS = {8,7,6,1,2,15,16,14};
  private int[] tags;
  private int desiredTag;
  private String targetingWhat;
  private boolean validTarget;
  private double x;
  private double y;
  private SendableChooser<Boolean> sideChooser;


  /** Creates a new Targeting. */
  public AprilTagTargeting() {
    camera = new PhotonCamera("tagTracker");

    sideChooser = new SendableChooser<>();
    sideChooser.setDefaultOption("Blue", false);
    sideChooser.addOption("red", true);
    SmartDashboard.putData("red or Blue", sideChooser);


    desiredTag = 7;//2024 default to blue speaker id, red is 4
    targetingWhat = "Speaker";
    targetHeight = HEIGHT_OF_SPEAKER;
    tags = BLUE_TAGS;
    //the following are out of range indicating no target found
    validTarget = false;
    x = 333;
    y = 333;

  }

  public void setSide(boolean isRed){
    if(isRed){
      tags = RED_TAGS;
    }
    else{
      tags = BLUE_TAGS;
    }
  }
  //0 alt speaker, 1 Speaker, 2 amp, 3 source R, 4 source L, 5 stage L, 6 stage R, 7 stage B
  public void changeTarget(int whichTarget){//set according to which tag to focus targeting on
    desiredTag = tags[whichTarget];
    if(whichTarget == 0){
      targetHeight = HEIGHT_OF_SPEAKER;
      targetingWhat = "Alt Speaker";
    }
    else if(whichTarget == 1){
      targetHeight = HEIGHT_OF_SPEAKER;
      targetingWhat = "Alt Speaker";
    }
    else if(whichTarget == 2){
      targetHeight = HEIGHT_OF_AMP;
      targetingWhat = "Amp";
    }
    else if(whichTarget == 3){
      targetHeight = HEIGHT_OF_SPEAKER;
      targetingWhat = "Source R";
    }
    else if(whichTarget == 4){
      targetHeight = HEIGHT_OF_SPEAKER;
      targetingWhat = "Source L";
    }
    else if(whichTarget == 5){
      targetHeight = HEIGHT_OF_STAGE;
      targetingWhat = "Stage L";
    }
    else if(whichTarget == 6){
      targetHeight = HEIGHT_OF_STAGE;
      targetingWhat = "Stage R";
    }
    else{
      targetHeight = HEIGHT_OF_STAGE;
      targetingWhat = "Stage B";
    }

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if(searchForTarget()){
      validTarget = true;
      x = currentTarget.getYaw();
      y = currentTarget.getPitch();
    }
    else{
      validTarget = false;
      x = 333;
      y = 333;
    }

    SmartDashboard.putBoolean("Valid Target Tag", validTarget);
    SmartDashboard.putNumber("x Tag", x);
    SmartDashboard.putNumber("y Tag", y);
    SmartDashboard.putString("Targeting", targetingWhat);
    SmartDashboard.putNumber("range Tag", calcRange());
  }

  public boolean searchForTarget(){
    var result = camera.getLatestResult();
    if(result.hasTargets()){
      List<PhotonTrackedTarget> targets = result.getTargets();
      for(PhotonTrackedTarget t : targets){
        if(t.getFiducialId() == desiredTag){
          currentTarget = t;
          return true;
        }
      }
    }
    return false;

  }

  public boolean getValidTarget(){
    return validTarget;
  }
  
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }


  public double calcRange() {//calculate horizontal range to target
    double d = (targetHeight - HEIGHT_OF_CAMERA) / Math.tan(Math.toRadians(CAMERA_MOUNT_ANGLE + y));
    return d;       
  }

}