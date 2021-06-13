package it.polito.tdp.PremierLeague.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Team;

public class Simulator implements SimulationResult
{
	// constants
	public static final double REPORTER_PROMOTION_PROB = 0.5;
	public static final double REPORTERS_REJECTION_PROB = 0.2;
			
	//input
	private Graph<Team, DefaultWeightedEdge> graph;
	private Map<Integer, Team> teamsIdMap;
	private int reportersMinimumPerMatch;
	
	//queue
	private PriorityQueue<Match> matchesQueue;
	
	//world status
	private Map<Team, Integer> numReportersByTeam;
	private int completedMatches;
	
	//output
	private double reportersAvgPerMatch;
	private int matchesWithoutEnoughReporters;
	
	
	public void initialize(Graph<Team, DefaultWeightedEdge> graph, Map<Integer, Team> teamsIdMap,
			Collection<Match> matches, int numReportersPerTeam, int reportersMinimumPerMatch)
	{
		this.graph = graph;
		this.teamsIdMap = teamsIdMap;
		this.reportersMinimumPerMatch = reportersMinimumPerMatch;
		
		// fill the queue
		this.matchesQueue = new PriorityQueue<Match>((match1, match2) -> 
										match1.getDate().compareTo(match2.getDate()));
		this.matchesQueue.addAll(matches);
		
		this.numReportersByTeam = new HashMap<>();
		this.graph.vertexSet().forEach(team -> this.numReportersByTeam.put(team, numReportersPerTeam));
		this.completedMatches = 0;
		
		this.reportersAvgPerMatch = 0;
		this.matchesWithoutEnoughReporters = 0;
	}
	
	public SimulationResult run()
	{
		Match match;
		
		while((match = this.matchesQueue.poll()) != null)
		{
			Team teamHome = this.teamsIdMap.get(match.getTeamHomeID());
			Team teamAway = this.teamsIdMap.get(match.getTeamAwayID());
			
			if(teamHome == null || teamAway == null)
				throw new RuntimeException("Teams Id Map not complete");
			
			//update output variables: examine num of reporters
			int teamHomeReporters = this.numReportersByTeam.get(teamHome);
			int teamAwayReporters = this.numReportersByTeam.get(teamAway);
			int totMatchReporters = teamHomeReporters + teamAwayReporters;
			
			if(totMatchReporters < this.reportersMinimumPerMatch)
				this.matchesWithoutEnoughReporters++;
			
			this.reportersAvgPerMatch = 
					(this.reportersAvgPerMatch * (double)this.completedMatches + (double)totMatchReporters) / (double)(this.completedMatches + 1);
			
			this.completedMatches++;
			
			//process event: change teams' num of reporters
			int result = match.getResultOfTeamHome();
			
			switch(result)
			{
				case 1:
					// win team home
					this.updateWinningTeamReporters(teamHome);
					this.updateLosingTeamReporters(teamAway);
					break;
					
				case -1:
					//win team away
					this.updateLosingTeamReporters(teamHome);
					this.updateWinningTeamReporters(teamAway);
					break;
					
				case 0:
					//nothing changes
					break;
			}
		}
		
		return this;
	}
	
	private void updateWinningTeamReporters(Team winningTeam)
	{
		if(Math.random() < 1 - REPORTER_PROMOTION_PROB)
			return;
		
		int numTeamReporters = this.numReportersByTeam.get(winningTeam);
		
		if(numTeamReporters == 0) return;	//no reporters
		
		List<Team> betterTeams = this.betterTeamsOf(winningTeam);
		
		if(betterTeams.isEmpty()) return;	//no better teams
		
		//promoting the reporter:
		
		//get a random better team
		int randomIndex = (int)(betterTeams.size() * Math.random());
		Team randomTeam = betterTeams.get(randomIndex);

		//update teams' num of reporters
		int oldNum = this.numReportersByTeam.get(randomTeam);
		this.numReportersByTeam.put(randomTeam, oldNum + 1);
		this.numReportersByTeam.put(winningTeam, numTeamReporters - 1);
	}
	
	private void updateLosingTeamReporters(Team losingTeam)
	{
		if(Math.random() < 1 - REPORTERS_REJECTION_PROB)
			return;
		
		int numTeamReporters = this.numReportersByTeam.get(losingTeam);
		
		if(numTeamReporters == 0) return; //no enough reporters to reject
		
		List<Team> worseTeams = this.worseTeamsOf(losingTeam);
		
		if(worseTeams.isEmpty()) return; //no worse teams
		
		//rejecting reporters:
		
		//establishing how many reporters to reject
		int numRejections = 1 + (int)(numTeamReporters * Math.random());
		
		//get a random worse team
		int randomIndex = (int)(worseTeams.size() * Math.random());
		Team randomTeam = worseTeams.get(randomIndex);

		//update teams' num of reporters
		int oldNum = this.numReportersByTeam.get(randomTeam);
		this.numReportersByTeam.put(randomTeam, oldNum + numRejections);
		this.numReportersByTeam.put(losingTeam, numTeamReporters - numRejections);
	}
	
	
	private List<Team> betterTeamsOf(Team team)
	{
		return Graphs.predecessorListOf(this.graph, team);
	}
	
	private List<Team> worseTeamsOf(Team team)
	{
		return Graphs.successorListOf(this.graph, team);
	}

	@Override
	public double getReportersAvgPerMatch()
	{
		return this.reportersAvgPerMatch;
	}

	@Override
	public int getMatchesWithoutEnoughReporters()
	{
		return this.matchesWithoutEnoughReporters;
	}
 }
