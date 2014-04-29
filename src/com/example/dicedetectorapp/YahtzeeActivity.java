package com.example.dicedetectorapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
	
	private static int upperScore = 0;
	private static int lowerScore = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yahtzee_pad);
		
		Bundle extras = getIntent().getExtras();
		if(extras == null) {
			Log.e(TAG, "ERROR: Extras are Null");
			finish();
		}
		else {
			diceList = extras.getIntegerArrayList("DICE_LIST");
		}
		
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
		
		Collections.sort(diceList);
		
		setButtonImages();
		enableButtons();
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
		
		aces.setText(String.valueOf(one));
		twos.setText(String.valueOf(two));
		threes.setText(String.valueOf(three));
		fours.setText(String.valueOf(four));
		fives.setText(String.valueOf(five));
		sixes.setText(String.valueOf(six));
		
		if(get3())
		{
			kind3.setText(String.valueOf(one + two + three + four + five + six));
		}
		if(get4())
		{
			kind4.setText(String.valueOf(one + two + three + four + five + six));
		}
		if(getFullHouse())
		{
			fullHouse.setText("25");
		}
		if(getSmallStraight())
		{
			smStr.setText("30");
		}
		if(getLargeStraight())
		{
			lgStr.setText("40");
		}
		if(getYahtzee())
		{
			yahtzee.setText("50");
		}
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
	public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.acesButton:
			upperScore += Integer.parseInt(aces.getText().toString());
			aces.setEnabled(false);
			break;
		case R.id.twoButton:
			upperScore += Integer.parseInt(twos.getText().toString());
			twos.setEnabled(false);
			break;
		case R.id.threeButton:
			upperScore += Integer.parseInt(threes.getText().toString());
			threes.setEnabled(false);
			break;
		case R.id.fourButton:
			upperScore += Integer.parseInt(fours.getText().toString());
			fours.setEnabled(false);
			break;
		case R.id.fiveButton:
			upperScore += Integer.parseInt(fives.getText().toString());
			fives.setEnabled(false);
			break;
		case R.id.sixButton:
			upperScore += Integer.parseInt(sixes.getText().toString());
			sixes.setEnabled(false);
			break;
		case R.id.kind3Button:
			lowerScore += Integer.parseInt(kind3.getText().toString());
			kind3.setEnabled(false);
			break;
		case R.id.kind4Button:
			lowerScore += Integer.parseInt(kind4.getText().toString());
			kind4.setEnabled(false);
			break;
		case R.id.fullHouseButton:
			lowerScore += Integer.parseInt(fullHouse.getText().toString());
			fullHouse.setEnabled(false);
			break;
		case R.id.smStrButton:
			lowerScore += Integer.parseInt(smStr.getText().toString());
			smStr.setEnabled(false);
			break;
		case R.id.lgStrButton:
			lowerScore += Integer.parseInt(lgStr.getText().toString());
			lgStr.setEnabled(false);
			break;
		case R.id.yahtzeeButton:
			lowerScore += Integer.parseInt(yahtzee.getText().toString());
			yahtzee.setEnabled(false);
			break;
		case R.id.chanceButton:
			lowerScore += Integer.parseInt(chance.getText().toString());
			chance.setEnabled(false);
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
