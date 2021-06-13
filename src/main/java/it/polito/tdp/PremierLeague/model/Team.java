package it.polito.tdp.PremierLeague.model;

public class Team 
{
	private Integer teamID;
	private String name;

	
	public Team(Integer teamID, String name) 
	{
		this.teamID = teamID;
		this.name = name;
	}
	
	public Integer getTeamID() 
	{
		return this.teamID;
	}

	public String getName() 
	{
		return this.name;
	}

	@Override
	public String toString() 
	{
		return this.name;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((teamID == null) ? 0 : teamID.hashCode());
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
		Team other = (Team) obj;
		if (teamID == null) {
			if (other.teamID != null)
				return false;
		} else if (!teamID.equals(other.teamID))
			return false;
		return true;
	}
}
