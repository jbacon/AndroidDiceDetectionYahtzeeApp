package com.example.dicedetectorapp;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

public class DiceObject implements Comparable<DiceObject>{
	//private List<RotatedRect> pips = new ArrayList<RotatedRect>();
	private int diceValue;	//Num Pips
	private Point diceCenter = new Point();
	private int numberOfFramesDetected;
	private List<Point> pipLocations;
	private double pipArea;
	
	//Dice object Constructor.
	//Initially the Dice only has one pip/dot, and
	//more pips are added as they are detected.
	public DiceObject(RotatedRect pip) {
		Point[] points = new Point[4];
		pip.points(points);
		double side1 = Math.sqrt(Math.pow(points[0].x - points[1].x, 2) + Math.pow(points[0].y - points[1].y, 2));
		double side2 = Math.sqrt(Math.pow(points[1].x - points[2].x, 2) + Math.pow(points[1].y - points[2].y, 2));
		pipArea = side1 * side2;
		pipLocations = new ArrayList<Point>();
		pipLocations.add(pip.center);
		diceValue = 1;
		diceCenter = new Point(pip.center.x, pip.center.y);
		numberOfFramesDetected = 1;
	}
	//Gets the center Point of one of the pips from the Dice
	public Point getPipCenter(int i) {
		return pipLocations.get(i);
	}
	//Gets the distance from a new ellipse/pip point to the center of the dice
	//Used to see if a newly detected pip belongs to the dice
	public double getDistFrom(Point pip) {
		double dist = Math.sqrt(Math.pow(diceCenter.x - pip.x, 2) + Math.pow(diceCenter.y - pip.y, 2));
		return dist;
	}
	//Gets the average pip diameter of the dice
	//Used to determine the size of the Dice Square in filtering ellipse detection
	public double getAveragePipDiameter() {
		return  Math.sqrt(pipArea);
	}
	//Adds a new pip to the Dice object, 
	//Changes to dice Center to the average Point of
	//all the pip points of the dice
	//Updates Dice value
	public void addPip(RotatedRect pip) {
		//Find new average Dice center
		int newCenterX = (int) ((diceCenter.x * diceValue + pip.center.x) / (diceValue +1));
		int newCenterY = (int) ((diceCenter.y * diceValue + pip.center.y) / (diceValue +1));
		diceCenter.x = newCenterX;
		diceCenter.y = newCenterY;
		pipLocations.add(pip.center);
		diceValue++;
	} 
	
	//Gets the List of pip points part of this Dice
	public List<Point> getPipLocations() {
		return pipLocations;
	}
	//Gets the average pip Area for this Dice
	//Used to determine if a new pip is relatively the same size as all the other pips
	//that are already part of the Dice
	public double getAveragePipArea() {
		return pipArea;
	}
	
	//Gets the Dice Value
	public int getNumPips() {
		return diceValue;
	}
	//Gets the Dice center point
	//Used to determine new pip distances from the Dice
	public Point getDiceCenter(){
		return diceCenter;
	}	
	//Returns string of Dice value and dice center point on frame
	public String toString() {
		return("Dice Value: "+diceValue+" Dice Point: "+diceCenter.toString());
	}
	//Incerments the number of frames that this particular dice as been detected
	//Used in determining if a Dice is an outlier or legitmate detected Dice
	public void incermentFrameCount() {
		numberOfFramesDetected++;
	}
	//Gets the number of frames that this Dice has been detected
	public int getNumFramesDetected() {
		return numberOfFramesDetected;
	}
	//Updates all the attributes/features of this Dice (piplocations, diceCenter and pipArea)
	//Used to update a dice position based on the subtle difference in frame movement/shaking
	//So that a new Dice is not constructed, and this preexisting Dice can be attributed to a new location
	//
	//**Essential part of tracking the dice as they move across the screen between frames.
	public void updateLocationOfDice(double diffX, double diffY, double diffArea) {
		pipArea = pipArea + diffArea;
		for(int i = 0; i < diceValue; i++) {
			pipLocations.get(i).x = pipLocations.get(i).x + diffX;
			pipLocations.get(i).y = pipLocations.get(i).y + diffY;
		}
		diceCenter.x = diceCenter.x + diffX;
		diceCenter.y = diceCenter.y + diffY;
	}
	//Use so that Dice can be compared in terms of the number of frames they have been detected 
	@Override
	public int compareTo(DiceObject another) {
		if(this.getNumFramesDetected() > another.getNumFramesDetected()) {
			return 1;
		}
		else if(this.getNumFramesDetected() == another.getNumFramesDetected()) {
			return 0;
		}
		else {
			return -1;
		}
	}
}

