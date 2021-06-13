package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO 
{
	public Collection<Team> getAllTeams(Map<Integer, Team> teamsIdMap)
	{
		String sql = "SELECT * FROM Teams";
		Set<Team> result = new HashSet<Team>();
		Connection connection = DBConnect.getConnection();

		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet res = statement.executeQuery();
			while (res.next()) 
			{
				int teamId = res.getInt("TeamID");
				
				if(!teamsIdMap.containsKey(teamId))
				{
					Team newTeam = new Team(teamId, res.getString("Name"));
					teamsIdMap.put(teamId, newTeam);
				}
				
				result.add(teamsIdMap.get(teamId));
			}
			res.close();
			statement.close();
			connection.close();
			return result;
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			return null;
		}
	}

	public Collection<Match> getAllMatches(Map<Integer, Match> matchesIdMap)
	{
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.resultOfTeamHome, m.date "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		
		Set<Match> result = new HashSet<Match>();
		Connection connection = DBConnect.getConnection();

		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet res = statement.executeQuery();
			while (res.next()) 
			{
				int matchId = res.getInt("m.MatchID");
				
				if(!matchesIdMap.containsKey(matchId))
				{
					Match newMatch = new Match(matchId, res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), 
							res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime());
					
					matchesIdMap.put(matchId, newMatch);
				}	
				
				result.add(matchesIdMap.get(matchId));
			}
			res.close();
			statement.close();
			connection.close();
			return result;
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			return null;
		}
	}
}
