package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;

/**
 * Stores the roster numbers
 * @author Jeff
 *
 */
public class Roster 
{
	public int teams;
	public int qbs;
	public int rbs;
	public int wrs;
	public int tes;
	public int def;
	public int k;
	public int flex;
	
	/**
	 * Stores the roster numbers
	 */
	public Roster(int teamCt, int qbCt, int rbCt, int wrCt, int teCt, int flexCt, int defCt, int kCt)
	{
		teams = teamCt;
		qbs = qbCt;
		rbs = rbCt;
		wrs = wrCt;
		tes = teCt;
		flex = flexCt;
		def = defCt;
		k = defCt;
	}
	
	/**
	 * empty constructor for the initial set up
	 */
	public Roster()
	{
		teams = 0;
		qbs = 0;
		rbs = 0;
		wrs = 0;
		tes = 0;
		flex = 0;
		k = 0;
		def = 0;
	}
	
	/**
	 * Determines if the player in question is in the league's roster settings
	 * @param player
	 * @return
	 */
	public boolean isRostered(PlayerObject player)
	{
		if(player.info.position.equals("QB") && qbs == 0)
		{
			return false;
		}
		if(player.info.position.equals("RB") && rbs == 0 && flex == 0)
		{
			return false;
		}
		if(player.info.position.equals("WR") && wrs == 0 && flex == 0)
		{
			return false;
		}
		if(player.info.position.equals("TE") && tes == 0)
		{
			return false;
		}
		if(player.info.position.equals("D/ST") && def == 0)
		{
			return false;
		}
		if(player.info.position.equals("K") && k == 0)
		{
			return false;
		}
		return true;
	}
}
