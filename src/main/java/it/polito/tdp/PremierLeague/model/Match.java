package it.polito.tdp.PremierLeague.model;

import java.time.LocalDateTime;

public class Match 
{
	private Integer matchID;
	private Integer teamHomeID;
	private Integer teamAwayID;

	private Integer resultOfTeamHome;

	private LocalDateTime date;
	
	
	public Match(Integer matchID, Integer teamHomeID, Integer teamAwayID, 
			Integer resultOfTeamHome, LocalDateTime date) 
	{
		this.matchID = matchID;
		this.teamHomeID = teamHomeID;
		this.teamAwayID = teamAwayID;

		this.resultOfTeamHome = resultOfTeamHome;

		this.date = date;
	}
	
	public Integer getMatchID() 
	{
		return this.matchID;
	}

	public Integer getTeamHomeID() 
	{
		return this.teamHomeID;
	}

	public Integer getTeamAwayID() 
	{
		return this.teamAwayID;
	}

	public LocalDateTime getDate() 
	{
		return this.date;
	}

	public Integer getResultOfTeamHome() 
	{
		return this.resultOfTeamHome;
	}

	@Override
	public String toString() 
	{
		return "[" + this.matchID + "] " + this.teamHomeID + " vs. " + this.teamAwayID;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.matchID == null) ? 0 : this.matchID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Match other = (Match) obj;
		if (this.matchID == null) 
		{
			if (other.matchID != null)
				return false;
		} 
		else if (!this.matchID.equals(other.matchID))
			return false;
		return true;
	}
}
