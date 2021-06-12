package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model 
{
	private Graph<Team, DefaultWeightedEdge> graph;
	private final PremierLeagueDAO dao;
	private final Map<Integer, Team> teamsIdMap;
	private final Map<Integer, Match> matchesIdMap;
	
	private Map<Team, Integer> teamsTable;
	
	
	public Model()
	{
		this.dao = new PremierLeagueDAO();
		this.teamsIdMap = new HashMap<>();	
		this.matchesIdMap = new HashMap<>();
	}
	
	public void createGraph()
	{
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.teamsTable = new HashMap<>();
		
		//add vertices
		Collection<Team> allTeams = this.dao.getAllTeams(teamsIdMap);
		Graphs.addAllVertices(this.graph, allTeams);
		allTeams.forEach(team -> this.teamsTable.put(team, 0));	// initialise teams table
		
		//retrieve all matches and compute teams table at the end of championship
		Collection<Match> allMatches = this.dao.getAllMatches(matchesIdMap);
		
		for(Match match : allMatches)
		{
			Team teamHome = this.teamsIdMap.get(match.getTeamHomeID());
			Team teamAway = this.teamsIdMap.get(match.getTeamAwayID());
			
			if(teamHome == null || teamAway == null)
				throw new RuntimeException("Team home or away not found from ID. Match: " + match.toString());
			
			int result = match.getResultOfTeamHome();
			this.updateTableFromMatchResult(teamHome, teamAway, result);
		}
		
		//add all edges
		for(var row1 : this.teamsTable.entrySet())
		{
			Team team1 = row1.getKey();
			int points1 = row1.getValue();
			
			for(var row2 : this.teamsTable.entrySet())
			{
				Team team2 = row2.getKey();
				int points2 = row2.getValue();
				
				if(team1.equals(team2) || points1 == points2)
					continue;	//no edge to set
				
				double edgeWeight = Math.abs(points1 - points2);
				
				if(points1 > points2 && !this.graph.containsEdge(team1, team2))
					Graphs.addEdge(this.graph, team1, team2, edgeWeight);
				
				else if(points2 > points1 && !this.graph.containsEdge(team2, team1))
					Graphs.addEdge(this.graph, team2, team1, edgeWeight);
			}
		}
	}

	private void updateTableFromMatchResult(Team teamHome, Team teamAway, int result)
	{
		int oldPoints = 0;
		
		switch(result)
		{
			case 1:
				// add +3 to Team Home
				oldPoints = this.teamsTable.get(teamHome);
				this.teamsTable.put(teamHome, oldPoints + 3);
				break;
				
			case -1:
				//add +3 to Team Away
				oldPoints = this.teamsTable.get(teamAway);
				this.teamsTable.put(teamAway, oldPoints + 3);
				break;
				
			case 0:
				//add +1 to both teams
				oldPoints = this.teamsTable.get(teamHome);
				this.teamsTable.put(teamHome, oldPoints + 1);
				
				oldPoints = this.teamsTable.get(teamAway);
				this.teamsTable.put(teamAway, oldPoints + 1);
				break;
		}
	}

	public List<Team> getAllOrderedTeams()
	{
		List<Team> orderedTeams = new ArrayList<>(this.graph.vertexSet());
		//order alphabetically
		orderedTeams.sort((team1, team2) -> team1.getName().compareTo(team2.getName()));
		
		return orderedTeams;
	}
	
	public int getNumVertices() { return this.graph.vertexSet().size(); }
	public int getNumEdges() { return this.graph.edgeSet().size(); }

	public Map<Team, Double> getBetterTeamsOf(Team selectedTeam)
	{
		Map<Team, Double> betterTeams = new HashMap<>();
		
		Collection<DefaultWeightedEdge> incomingEdges = this.graph.incomingEdgesOf(selectedTeam);
		
		for(var edge : incomingEdges)
		{
			Team betterTeam = Graphs.getOppositeVertex(this.graph, edge, selectedTeam);
			double diffPoints = this.graph.getEdgeWeight(edge);
			betterTeams.put(betterTeam, diffPoints);
		}

		return betterTeams;
	}

	public Map<Team, Double> getWorseTeamsOf(Team selectedTeam)
	{
		Map<Team, Double> worseTeams = new HashMap<>();
		
		Collection<DefaultWeightedEdge> outgoingEdges = this.graph.outgoingEdgesOf(selectedTeam);
		
		for(var edge : outgoingEdges)
		{
			Team worseTeam = Graphs.getOppositeVertex(this.graph, edge, selectedTeam);
			double diffPoints = this.graph.getEdgeWeight(edge);
			worseTeams.put(worseTeam, diffPoints);
		}

		return worseTeams;
	}
}
