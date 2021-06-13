package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Team;
import it.polito.tdp.PremierLeague.simulation.SimulationResult;
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
    private Button creaGrafoButton;

    @FXML 
    private Button classificaButton; 

    @FXML 
    private Button simulaButton; 

    @FXML 
    private ComboBox<Team> teamsComboBox;

    @FXML 
    private TextField textFieldN; 

    @FXML 
    private TextField textFieldX;

    @FXML 
    private TextArea resultTextArea;
    
	private Model model;

	
	@FXML
    void doCreaGrafo(ActionEvent event) 
    {
		this.model.createGraph();
		List<Team> allOrderedTeams = this.model.getAllOrderedTeams();
		
		this.teamsComboBox.getItems().clear();
		this.teamsComboBox.getItems().addAll(allOrderedTeams);
		
		int numVertices = this.model.getNumVertices();
		int numEdges = this.model.getNumEdges();
		
		this.resultTextArea.setText(
				String.format("Grafo creato\n# Vertici: %d\n# Archi: %d", numVertices, numEdges));
    }

    @FXML
    void doClassifica(ActionEvent event) 
    {
    	Team selectedTeam = this.teamsComboBox.getValue();
    	
    	if(selectedTeam == null) 
    	{
    		this.resultTextArea.setText("Errore: devi prima selezionare una squadra dal menù a tendina!");
    		return;
    	}
    	
    	Map<Team, Double> betterTeams = this.model.getBetterTeamsMapOf(selectedTeam);
    	Map<Team, Double> worseTeams = this.model.getWorseTeamsMapOf(selectedTeam);
    	
    	List<Team> orderedBetterTeams = new ArrayList<>(betterTeams.keySet());
    	List<Team> orderedWorseTeams = new ArrayList<>(worseTeams.keySet());
    	
    	orderedBetterTeams.sort((t1,t2) -> Double.compare(betterTeams.get(t1), betterTeams.get(t2)));
    	orderedWorseTeams.sort((t1,t2) -> Double.compare(worseTeams.get(t1), worseTeams.get(t2)));

    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("SQUADRE MIGLIORI:\n");
    	sb.append(this.printOrderedTeamsMap(betterTeams, orderedBetterTeams));
    	
    	sb.append("\n\nSQUADRE PEGGIORI:\n");
    	sb.append(this.printOrderedTeamsMap(worseTeams, orderedWorseTeams));
    	this.resultTextArea.setText(sb.toString());
    }
    
    private String printOrderedTeamsMap(Map<Team, Double> map, List<Team> orderedTeams)
    {
    	if(map.isEmpty()) return "(nessuna)";
    	
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
    	String numReportersInput = this.textFieldN.getText();
    	
    	if(numReportersInput == null)
    	{
    		this.resultTextArea.setText("Errore: devi prima inserire un valore intero di N!");
    		return;
    	}
    	
    	int numReporters;
    	
    	try
		{
			numReporters = Integer.parseInt(numReportersInput);
		}
		catch(NumberFormatException nfe)
		{
			this.resultTextArea.setText("Errore di formato: inserire un valore intero di N!");
    		return;
		}
    	
    	if(numReporters <= 0)
    	{
    		this.resultTextArea.setText("Errore di formato: inserire un valore positivo di N!");
    		return;
    	}
    	
    	String numMinReportersInput = this.textFieldX.getText();
    	
    	if(numMinReportersInput == null)
    	{
    		this.resultTextArea.setText("Errore: devi prima inserire un valore intero di X!");
    		return;
    	}
    	
    	int numMinReporters;
    	
    	try
		{
    		numMinReporters = Integer.parseInt(numMinReportersInput);
		}
		catch(NumberFormatException nfe)
		{
			this.resultTextArea.setText("Errore di formato: inserire un valore intero di X!");
    		return;
		}
    	
    	if(numMinReporters <= 0)
    	{
    		this.resultTextArea.setText("Errore di formato: inserire un valore positivo di X!");
    		return;
    	}
    	
    	SimulationResult result = this.model.runSimulation(numReporters, numMinReporters);
    	
    	if(result == null)
    	{
    		this.resultTextArea.setText("Errore: devi prima creare il grafo!");
    		return;
    	}
    	
    	this.resultTextArea.setText(String.format(
    	"Ad ogni partita hanno assistito in media: %.3f reporters\nIl numero di partite in cui il numero totale di reporters era minore di X(%d) è: %d partite",
    	result.getReportersAvgPerMatch(), numMinReporters, result.getMatchesWithoutEnoughReporters()));
    }

    @FXML 
    void initialize() 
    {
        assert creaGrafoButton != null : "fx:id=\"creaGrafoButton\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert classificaButton != null : "fx:id=\"classificaButton\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert simulaButton != null : "fx:id=\"simulaButton\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert teamsComboBox != null : "fx:id=\"teamsComboBox\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert textFieldN != null : "fx:id=\"textFieldN\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert textFieldX != null : "fx:id=\"textFieldX\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
        assert resultTextArea != null : "fx:id=\"resultTextArea\" was not injected: check your FXML file 'Scene_2020-07-14.fxml'.";
    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    }
}
