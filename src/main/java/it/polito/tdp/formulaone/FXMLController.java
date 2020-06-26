package it.polito.tdp.formulaone;

import java.net.URL;

import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//controller del turno A --> switchare al branch master_turnoB o master_turnoC per turno B o C

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Integer> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	Integer anno = boxAnno.getValue();
    	if(anno == null) {
    		txtResult.appendText("Devi selezionare un anno!\n");
    		return;
    	}
    	model.creaGrafo(anno);
    	txtResult.appendText(String.format("Grafo creato con %d vertici e %d archi!\n", model.nVertici(), model.nArchi()));
    	txtResult.appendText(String.format("Driver migliore: %s. Punteggio: %d\n", model.getBest().getSurname(), model.getPunteggio()));
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	txtResult.clear();
    	Integer k;
    	try {
    		k = Integer.parseInt(this.textInputK.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Devi inserire un numero!\n");
    		return;
    	}
    	for(Driver d: model.trovaDreamTeam(k))
    		txtResult.appendText(String.format("%s\n", d.getSurname()));
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		setBox();
	}

	private void setBox() {
		this.boxAnno.getItems().addAll(model.getYears());
	}
}
