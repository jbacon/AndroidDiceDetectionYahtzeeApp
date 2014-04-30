/**
 * Authors: Josh Bacon, DJ Corisis, and Derek Bennet
 * Date: 4/30/14
 * */

package com.example.dicedetectorapp;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class YahtzeeActivity extends Activity implements OnClickListener {
	
	public static final String TAG	= "com.example.dicedetctorapp.UIActivity";
	
	private static ArrayList<Integer> diceList = new ArrayList<Integer>();
	
	private static boolean hasTurnBeenTaken = false;
	
	private static TextView upper;
	private static TextView totalUpper;
	private static TextView bonus;
	private static TextView lower;
	private static TextView grandTotal;
	
	
	private static ImageButton dice1;
	private static ImageButton dice2;
	private static ImageButton dice3;
	private static ImageButton dice4;
	private static ImageButton dice5;
	
	private static Button aces;
	private static Button twos;
	private static Button threes;
	private static Button fours;
	private static Button fives;
	private static Button sixes;
	private static Button kind3;
	private static Button kind4;
	private static Button fullHouse;
	private static Button smStr;
	private static Button lgStr;
	private static Button yahtzee;
	private static Button chance;
	
	private static int acesScore;
	private static int twosScore;
	private static int threesScore;
	private static int foursScore;
	private static int fivesScore;
	private static int sixesScore;
	private static int kind3Score;
	private static int kind4Score;
	private static int fullHouseScore;
	private static int smStrScore;
	private static int lgStrScore;
	private static int yahtzeeScore;
	private static int chanceScore;
	
	private static int upperScore = 0;
	private static int lowerScore = 0;
	private static boolean[] activeButtons = new boolean[13];

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			acesScore 	= savedInstanceState.getInt("ACES_SCORE");
			twosScore 	= savedInstanceState.getInt("TWOS_SCORE");
			threesScore = savedInstanceState.getInt("THREES_SCORE");
			foursScore 	= savedInstanceState.getInt("FOURS_SCORE");
			fivesScore 	= savedInstanceState.getInt("FIVES_SCORE");
			sixesScore 	= savedInstanceState.getInt("SIXES_SCORE");
			kind3Score 	= savedInstanceState.getInt("KIND3_SCORE");
			kind4Score 	= savedInstanceState.getInt("KIND4_SCORE");
			fullHouseScore = savedInstanceState.getInt("FULLHOUSE_SCORE");
			smStrScore 	= savedInstanceState.getInt("SMSTR_SCORE");
			lgStrScore 	= savedInstanceState.getInt("LGSTR_SCORE");
			yahtzeeScore = savedInstanceState.getInt("YAHTZEE_SCORE");
			chanceScore = savedInstanceState.getInt("CHANCE_SCORE");
			
			upperScore = savedInstanceState.getInt("UPPER_SCORE");
			lowerScore = savedInstanceState.getInt("LOWER_SCORE");
			activeButtons = savedInstanceState.getBooleanArray("ACTIVE_BUTTONS");
		}
		else {
			for(int i = 0; i < 13; i++) {
				activeButtons[i] = true;
			}
		}
			
		setContentView(R.layout.activity_yahtzee_pad);
		
		dice1 = (ImageButton)findViewById(R.id.dice1);
		dice2 = (ImageButton)findViewById(R.id.dice2);
		dice3 = (ImageButton)findViewById(R.id.dice3);
		dice4 = (ImageButton)findViewById(R.id.dice4);
		dice5 = (ImageButton)findViewById(R.id.dice5);
		
		
		aces = (Button)findViewById(R.id.acesButton);
		twos = (Button)findViewById(R.id.twoButton);
		threes = (Button)findViewById(R.id.threeButton);
		fours = (Button)findViewById(R.id.fourButton);
		fives = (Button)findViewById(R.id.fiveButton);
		sixes = (Button)findViewById(R.id.sixButton);
		kind3 = (Button)findViewById(R.id.kind3Button);
		kind4 = (Button)findViewById(R.id.kind4Button);
		fullHouse = (Button)findViewById(R.id.fullHouseButton);
		smStr = (Button)findViewById(R.id.smStrButton);
		lgStr = (Button)findViewById(R.id.lgStrButton);
		yahtzee = (Button)findViewById(R.id.yahtzeeButton);
		chance = (Button)findViewById(R.id.chanceButton);
		
		upper = (TextView)findViewById(R.id.topScore);
		totalUpper = (TextView)findViewById(R.id.topTotal);
		bonus = (TextView)findViewById(R.id.bonusTotal);
		lower = (TextView)findViewById(R.id.lowerTotal);
		grandTotal = (TextView)findViewById(R.id.grandTotal);
		
		if(!diceList.isEmpty()) {
			Collections.sort(diceList);
			setButtonImages();	
			enableButtons();
		}
		else {
			dice1.setImageResource(R.drawable.diceicon);
			dice2.setImageResource(R.drawable.diceicon);
			dice3.setImageResource(R.drawable.diceicon);
			dice4.setImageResource(R.drawable.diceicon);
			dice5.setImageResource(R.drawable.diceicon);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!diceList.isEmpty()) {
			Collections.sort(diceList);
			setButtonImages();	
			enableButtons();
		}
		else {
			dice1.setImageResource(R.drawable.diceicon);
			dice2.setImageResource(R.drawable.diceicon);
			dice3.setImageResource(R.drawable.diceicon);
			dice4.setImageResource(R.drawable.diceicon);
			dice5.setImageResource(R.drawable.diceicon);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("ACES_SCORE", acesScore);
		outState.putInt("TWOS_SCORE", twosScore);
		outState.putInt("THREES_SCORE", threesScore);
		outState.putInt("FOURS_SCORE", foursScore);
		outState.putInt("FIVES_SCORE", fivesScore);
		outState.putInt("SIXES_SCORE", sixesScore);
		outState.putInt("KIND3_SCORE", kind3Score);
		outState.putInt("KIND4_SCORE", kind4Score);
		outState.putInt("FULLHOUSE_SCORE", fullHouseScore);
		outState.putInt("SMSTR_SCORE", smStrScore);
		outState.putInt("LGSTR_SCORE", lgStrScore);
		outState.putInt("YAHTZEE_SCORE", yahtzeeScore);
		outState.putInt("CHANCE_SCORE", chanceScore);
		
		outState.putInt("UPPER_SCORE", upperScore);
		outState.putInt("LOWER_SCORE", lowerScore);
		outState.putBooleanArray("ACTIVE_BUTTONS", activeButtons);
	}
	
	public static void setButtonImages() { 
		switch(diceList.get(0)) {
			case 1:
				dice1.setImageResource(R.drawable.dice1);
				break;
			case 2:
				dice1.setImageResource(R.drawable.dice2);
				break;
			case 3:
				dice1.setImageResource(R.drawable.dice3);
				break;
			case 4:
				dice1.setImageResource(R.drawable.dice4);
				break;
			case 5:
				dice1.setImageResource(R.drawable.dice5);
				break;
			case 6:
				dice1.setImageResource(R.drawable.dice6);
				break;
		}
		
		switch(diceList.get(1))
		{
		case 1:
			dice2.setImageResource(R.drawable.dice1);
			break;
		case 2:
			dice2.setImageResource(R.drawable.dice2);
			break;
		case 3:
			dice2.setImageResource(R.drawable.dice3);
			break;
		case 4:
			dice2.setImageResource(R.drawable.dice4);
			break;
		case 5:
			dice2.setImageResource(R.drawable.dice5);
			break;
		case 6:
			dice2.setImageResource(R.drawable.dice6);
			break;
		}
		
		switch(diceList.get(2))
		{
		case 1:
			dice3.setImageResource(R.drawable.dice1);
			break;
		case 2:
			dice3.setImageResource(R.drawable.dice2);
			break;
		case 3:
			dice3.setImageResource(R.drawable.dice3);
			break;
		case 4:
			dice3.setImageResource(R.drawable.dice4);
			break;
		case 5:
			dice3.setImageResource(R.drawable.dice5);
			break;
		case 6:
			dice3.setImageResource(R.drawable.dice6);
			break;
		}
		
		switch(diceList.get(3))
		{
		case 1:
			dice4.setImageResource(R.drawable.dice1);
			break;
		case 2:
			dice4.setImageResource(R.drawable.dice2);
			break;
		case 3:
			dice4.setImageResource(R.drawable.dice3);
			break;
		case 4:
			dice4.setImageResource(R.drawable.dice4);
			break;
		case 5:
			dice4.setImageResource(R.drawable.dice5);
			break;
		case 6:
			dice4.setImageResource(R.drawable.dice6);
			break;
		}
		
		switch(diceList.get(4))
		{
		case 1:
			dice5.setImageResource(R.drawable.dice1);
			break;
		case 2:
			dice5.setImageResource(R.drawable.dice2);
			break;
		case 3:
			dice5.setImageResource(R.drawable.dice3);
			break;
		case 4:
			dice5.setImageResource(R.drawable.dice4);
			break;
		case 5:
			dice5.setImageResource(R.drawable.dice5);
			break;
		case 6:
			dice5.setImageResource(R.drawable.dice6);
			break;
		}
	}

	public static void enableButtons()
	{
		if(!activeButtons[0]) {
			aces.setEnabled(false);
			aces.setText(String.valueOf(acesScore));
		}
		if(!activeButtons[1]) {
			twos.setEnabled(false);
			twos.setText(String.valueOf(twosScore));
		}
		if(!activeButtons[2]) {
			threes.setEnabled(false);
			threes.setText(String.valueOf(threesScore));
		}
		if(!activeButtons[3]) {
			fours.setEnabled(false);
			fours.setText(String.valueOf(foursScore));
		}
		if(!activeButtons[4]) {
			fives.setEnabled(false);
			fives.setText(String.valueOf(fivesScore));
		}
		if(!activeButtons[5]) {
			sixes.setEnabled(false);
			sixes.setText(String.valueOf(sixesScore));
		}
		if(!activeButtons[6]) {
			kind3.setEnabled(false);
			kind3.setText(String.valueOf(kind3Score));
		}
		if(!activeButtons[7]) {
			kind4.setEnabled(false);
			kind4.setText(String.valueOf(kind4Score));
		}
		if(!activeButtons[8]) {
			fullHouse.setEnabled(false);
			fullHouse.setText(String.valueOf(fullHouseScore));
		}
		if(!activeButtons[9]) {
			smStr.setEnabled(false);
			smStr.setText(String.valueOf(smStrScore));
		}
		if(!activeButtons[10]) {
			lgStr.setEnabled(false);
			lgStr.setText(String.valueOf(lgStrScore));
		}
		if(!activeButtons[11]) {
			yahtzee.setEnabled(false);
			yahtzee.setText(String.valueOf(yahtzeeScore));
		}
		if(!activeButtons[12]) {
			chance.setEnabled(false);
			chance.setText(String.valueOf(chanceScore));
		}
		int one = 0;
		int two = 0;
		int three = 0;
		int four = 0;
		int five = 0;
		int six = 0;
		
		for(Integer e: diceList)
		{
			switch(e)
			{
			case 1:
				one++;
				break;
			case 2:
				two += 2;
				break;
			case 3:
				three += 3;
				break;
			case 4:
				four += 4;
				break;
			case 5:
				five += 5;
				break;
			case 6:
				six += 6;
				break;
			}
		}
		if(activeButtons[0] == true)
			aces.setText(String.valueOf(one));
		if(activeButtons[1] == true)
			twos.setText(String.valueOf(two));
		if(activeButtons[2] == true)
			threes.setText(String.valueOf(three));
		if(activeButtons[3] == true)
			fours.setText(String.valueOf(four));
		if(activeButtons[4] == true)
			fives.setText(String.valueOf(five));
		if(activeButtons[5] == true)
			sixes.setText(String.valueOf(six));
		
		if(activeButtons[6] && get3())
		{
			kind3.setText(String.valueOf(one + two + three + four + five + six));
		}
		if(activeButtons[7] && get4())
		{
			kind4.setText(String.valueOf(one + two + three + four + five + six));
		}
		if(activeButtons[8] && getFullHouse())
		{
			fullHouse.setText("25");
		}
		if(activeButtons[9] && getSmallStraight())
		{
			smStr.setText("30");
		}
		if(activeButtons[10] && getLargeStraight())
		{
			lgStr.setText("40");
		}
		if(activeButtons[11] && getYahtzee())
		{
			yahtzee.setText("50");
		}
		if(activeButtons[12])
			chance.setText(String.valueOf(one + two + three + four + five + six));
	}
	
	private static Boolean getYahtzee() {
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if(val1 == val2 && val2 == val3 && val3 == val4 && val4 == val5)
			return true;
		return false;
	}
	
	private static Boolean getLargeStraight()
	{
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if( val1+1 == val2 && val2+1 == val3 && val3+1 == val4 && val4+1 == val5)
			return true;
		
		return false;
	}
	
	private static Boolean getSmallStraight()
	{
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if( val1+1 == val2 && val2+1 == val3 && val3+1 == val4 )
			return true;
		else if(val2+1 == val3 && val3+1 == val4 && val4+1 == val5)
			return true;
		
		return false;
	}
	
	private static Boolean getFullHouse()
	{
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if(val1 == val2)
		{
			if(val1 == val3 && val4 == val5)
				return true;
			else if(val1 == val4 && val3 == val5)
				return true;
			else if(val3 == val4 && val3 == val5)
				return true;
		}
		else if(val1 == val3)
		{
			if(val1 == val4 && val2 == val5)
				return true;
			else if(val2 == val4 && val4 == val5)
				return true;
		}
		else if(val2 == val3)
		{
			if(val2 == val4 && val1 == val5)
				return true;
			else if(val2 == val5 && val1 == val4)
				return true;
		}
		return false;
	}
	
	private static Boolean get4()
	{
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if(val1 == val2 && val2 == val3)
		{
			if(val3 == val4 || val3 == val5)
				return true;
		}
		else if(val1 == val3 && val3 == val4 && val4 == val5)
			return true;
		else if(val2 == val3 && val3 == val4 && val4 == val5)
			return true;
		return false;
	}
	
	private static Boolean get3()
	{
		int val1 = diceList.get(0);
		int val2 = diceList.get(1);
		int val3 = diceList.get(2);
		int val4 = diceList.get(3);
		int val5 = diceList.get(4);
		
		if(val1 == val2)
		{
			if(val2 == val3 || val2 == val4 || val2 == val5)
				return true;
			else if(val3 == val4 && val4 == val5)
				return true;
		}
		else if(val1 == val3)
		{
			if(val3 == val4 || val3 == val5)
				return true;
		}
		else if(val1 == val4)
		{
			if(val4 == val5)
				return true;
		}
		else if(val2 == val3)
		{
			if(val3 == val4 || val3 == val5)
				return true;
		}
		else if(val2 == val4)
		{
			if(val4 == val5)
				return true;
		}
		else if(val3 == val4)
		{
			if(val3 == val5)
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_yahtzee, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				diceList = data.getIntegerArrayListExtra("DICE_LIST");
				Log.e(TAG, "GET EXTRA ARRAY"+diceList.toString());
				hasTurnBeenTaken = false;
			}
			if(resultCode == RESULT_CANCELED){
				diceList.clear();
				hasTurnBeenTaken = true;
			}
		}
		
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.button_detect_dice:
			Intent intent = new Intent(this, DiceDetectorActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.acesButton: 
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(aces.getText().toString());
				aces.setEnabled(false);
				activeButtons[0] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.twoButton:
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(twos.getText().toString());
				twos.setEnabled(false);
				activeButtons[1] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.threeButton:
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(threes.getText().toString());
				threes.setEnabled(false);
				activeButtons[2] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.fourButton:
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(fours.getText().toString());
				fours.setEnabled(false);
				activeButtons[3] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.fiveButton:
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(fives.getText().toString());
				fives.setEnabled(false);
				activeButtons[4] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.sixButton:
			if(!hasTurnBeenTaken) {
				upperScore += Integer.parseInt(sixes.getText().toString());
				sixes.setEnabled(false);
				activeButtons[5] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.kind3Button:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(kind3.getText().toString());
				kind3.setEnabled(false);
				activeButtons[6] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.kind4Button:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(kind4.getText().toString());
				kind4.setEnabled(false);
				activeButtons[7] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.fullHouseButton:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(fullHouse.getText().toString());
				fullHouse.setEnabled(false);
				activeButtons[8] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.smStrButton:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(smStr.getText().toString());
				smStr.setEnabled(false);
				activeButtons[9] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.lgStrButton:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(lgStr.getText().toString());
				lgStr.setEnabled(false);
				activeButtons[10] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.yahtzeeButton:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(yahtzee.getText().toString());
				yahtzee.setEnabled(false);
				activeButtons[11] = false;
				hasTurnBeenTaken = true;
			}
			break;
		case R.id.chanceButton:
			if(!hasTurnBeenTaken) {
				lowerScore += Integer.parseInt(chance.getText().toString());
				chance.setEnabled(false);
				activeButtons[12] = false;
				hasTurnBeenTaken = true;
			}	
			break;
		}
		

		if( !(aces.isEnabled() || twos.isEnabled() || threes.isEnabled() || fours.isEnabled() || fives.isEnabled() || sixes.isEnabled()) )
		{
			if(upperScore >= 63)
			{	
				totalUpper.setText(String.valueOf(upperScore + 35));
				bonus.setText("35");
			}
			else
			{
				totalUpper.setText(String.valueOf(upperScore));
				bonus.setText("0");
			}
			
			if( !(kind3.isEnabled() || kind4.isEnabled() || fullHouse.isEnabled() || smStr.isEnabled() || lgStr.isEnabled() || yahtzee.isEnabled() || chance.isEnabled()))
			{
				int grand;
				grand = lowerScore + upperScore;
				grandTotal.setText(String.valueOf(grand));
			}
			
		}
		
		upper.setText(String.valueOf(upperScore));
		lower.setText(String.valueOf(lowerScore));

	}

}
