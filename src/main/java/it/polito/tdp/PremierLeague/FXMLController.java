/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.SimulationResult;
import it.polito.tdp.PremierLeague.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController 
{	
    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML 
    private Button btnClassifica; 

    @FXML 
    private Button btnSimula; 

    @FXML 
    private ComboBox<Team> cmbSquadra;

    @FXML 
    private TextField txtN; 

    @FXML 
    private TextField txtX;

    @FXML 
    private TextArea txtResult;
    
	private Model model;

	
	@FXML
    void doCreaGrafo(ActionEvent event) 
    {
		this.model.createGraph();
		List<Team> allOrderedTeams = this.model.getAllOrderedTeams();
		
		this.cmbSquadra.getItems().clear();
		this.cmbSquadra.getItems().addAll(allOrderedTeams);
		
		int numVertices = this.model.getNumVertices();
		int numEdges = this.model.getNumEdges();
		
		this.txtResult.setText(
				String.format("Grafo creato\n# Vertici: %d\n# Archi: %d", numVertices, numEdges));
    }

    @FXML
    void doClassifica(ActionEvent event) 
    {
    	Team selectedTeam = this.cmbSquadra.getValue();
    	
    	if(selectedTeam == null) 
    	{
    		this.txtResult.setText("Errore: devi prima selezionare una squadra dal menù a tendina!");
    		return;
    	}
    	
    	Map<Team, Double> betterTeams = this.model.getBetterTeamsDiffOf(selectedTeam);
    	Map<Team, Double> worseTeams = this.model.getWorseTeamsDiffOf(selectedTeam);
    	
    	List<Team> orderedBetterTeams = new ArrayList<>(betterTeams.keySet());
    	List<Team> orderedWorseTeams = new ArrayList<>(worseTeams.keySet());
    	
    	orderedBetterTeams.sort((t1,t2) -> Double.compare(betterTeams.get(t1), betterTeams.get(t2)));
    	orderedWorseTeams.sort((t1,t2) -> Double.compare(worseTeams.get(t1), worseTeams.get(t2)));

    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("SQUADRE MIGLIORI:\n");
    	sb.append(this.printOrderedTeamsMap(betterTeams, orderedBetterTeams));
    	
    	sb.append("\n\nSQUADRE PEGGIORI:\n");
    	sb.append(this.printOrderedTeamsMap(worseTeams, orderedWorseTeams));
    	this.txtResult.setText(sb.toString());
    }
    
    private String printOrderedTeamsMap(Map<Team, Double> map, List<Team> orderedTeams)
    {
    	if(map.isEmpty()) return "nessuna";
    	
    	StringBuilder sb = new StringBuilder();
    	
    	for(Team team : orderedTeams)
    	{
    		if(sb.length() > 0)
    			sb.append("\n");
    		
    		double diff = map.get(team);
    		
    		sb.append("- ").append(team.toString()).append("(").append((int)diff).append(")");
    	}
    	
    	return sb.toString();
    }

    @FXML
    void doSimula(ActionEvent event) 
    {
    	String numReportersInput = this.txtN.getText();
    	
    	if(numReportersInput == null)
    	{
    		this.txtResult.setText("Errore: devi prima inserire un valore intero di N!");
    		return;
    	}
    	
    	int numReporters;
    	
    	try
		{
			numReporters = Integer.parseInt(numReportersInput);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Errore di formato: inserire un valore intero di N!");
    		return;
		}
    	
    	if(numReporters <= 0)
    	{
    		this.txtResult.setText("Errore di formato: inserire un valore positivo di N!");
    		return;
    	}
    	
    	String numMinReportersInput = this.txtX.getText();
    	
    	if(numMinReportersInput == null)
    	{
    		this.txtResult.setText("Errore: devi prima inserire un valore intero di X!");
    		return;
    	}
    	
    	int numMinReporters;
    	
    	try
		{
    		numMinReporters = Integer.parseInt(numMinReportersInput);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Errore di formato: inserire un valore intero di X!");
    		return;
		}
    	
    	if(numMinReporters <= 0)
    	{
    		this.txtResult.setText("Errore di formato: inserire un valore positivo di X!");
    		return;
    	}
    	
    	SimulationResult result = this.model.runSimulation(numReporters, numMinReporters);
    	
    	if(result == null)
    	{
    		this.txtResult.setText("Errore: devi prima creare il grafo!");
    		return;
    	}
    	
    	this.txtResult.setText(String.format(
    	"Ad ogni partita hanno assistito in media: %.3f reporters\nIl numero di partite in cui il numero totale di reporters era minore di X(%d) è: %d partite",
    	result.getReportersAvgPerMatch(), numMinReporters, result.getMatchesWithoutEnoughReporters()));
    }

    @FXML 
    void initialize() 
    {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnClassifica != null : "fx:id=\"btnClassifica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbSquadra != null : "fx:id=\"cmbSquadra\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    }
}
