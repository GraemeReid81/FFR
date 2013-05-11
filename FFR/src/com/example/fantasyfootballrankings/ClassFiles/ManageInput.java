package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * A little class that should help with making user input
 * into searches...etc a little cooler, doing nicer things.
 * @author Jeff
 *
 */
public class ManageInput 
{
	/**
	 * This sets up the auto complete search with the given arraylist
	 * so that it autocompletes suggestions based on players who have
	 * already been parsed.
	 */
	public static void setupAutoCompleteSearch(Storage holder, List<String> playerNames, 
			AutoCompleteTextView input, Context cont)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(cont,
                 android.R.layout.simple_dropdown_item_1line, playerNames);
        input.setAdapter(adapter);
	}

	/**
	 * A function that just handles the checking of voice input
	 * relative to parsed players, returning only legitimate 
	 * matches
	 */
	public static List<String> voiceInput(ArrayList<String> matches, Context dialog,
			Storage holder, AutoCompleteTextView textView) 
	{
		List<String> results = new ArrayList<String>();
		for(String e:matches)
		{
			System.out.println(e);
			for(String players : holder.parsedPlayers)
			{
				if(players.contains(e))
				{
					results.add(players);
					System.out.println("MATCH FOUND: " + e + " " + players);
				}
			}
		}
		return results;
	}
	
	/**
	 * Handles the addition of an adapter to the listview
	 * on rankings and trending
	 */
	public static void handleArray(List<String> list, ListView listView, Activity cont)
	{
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(cont,
	            android.R.layout.simple_list_item_1, list);
	    listView.setAdapter(adapter);
	}
	
	/**
	 * Handles the filter quantity dialog
	 * @param cont
	 */
	public static void filterQuantity(final Context cont, final String flag, final int listSize)
	{
		final Dialog dialog = new Dialog(cont);
		dialog.setContentView(R.layout.filter_quantity);
		dialog.show();
		int filterSize = ReadFromFile.readFilterQuantitySize(cont, flag);
		final SeekBar selector = (SeekBar)dialog.findViewById(R.id.seekBar_quantity);
		final TextView display = (TextView)dialog.findViewById(R.id.quantity_display);
		if(filterSize == 0)
		{
			display.setText("None of the players");
		}
		else if(filterSize == 100)
		{
			display.setText("All of the players");
		}
		else
		{
			float percentage = (float)filterSize/100;
    		int total = (int)(listSize * percentage);
    		String newSize = total + " players";
			display.setText(newSize);
		}
		selector.setProgress(filterSize);
		selector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    	    	int prog = selector.getProgress();
    	    	String size = "";
    	    	if(prog == 0)
    	    	{
    	    		size = "None of the players";
    	    	}
    	    	else if(prog == 100)
    	    	{
    	    		size = "All of the players";
    	    	}
    	    	else
    	    	{
    	    		float percentage = (float)prog/100;
    	    		int total = (int)(listSize * percentage);
    	    		size = total + " players";
    	    	}
    	    	display.setText(size);	
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    	});
		Button cancel = (Button)dialog.findViewById(R.id.filter_size_cancel);
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button submit = (Button)dialog.findViewById(R.id.filter_size_submit);
		submit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				WriteToFile.writeFilterSize(cont, selector.getProgress(), flag);
				dialog.dismiss();
				if(flag.equals("Rankings"))
				{
					Rankings.intermediateHandleRankings((Activity)cont);
				}
	    	}	
		});
	}
}
