package group50.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class ComparisonController {

    @FXML
    private TableView<ComparisonData> comparisonTable;
    @FXML
    private TableColumn<ComparisonData, String> parameterColumn;
    @FXML
    private TableColumn<ComparisonData, Integer> oldValueColumn;
    @FXML
    private TableColumn<ComparisonData, Integer> newValueColumn;
    @FXML
    private TableColumn<ComparisonData, Integer> differenceColumn;
    @FXML
    private TextArea calculationDetailsArea;

    @FXML
    public void initialize() {
        parameterColumn.setCellValueFactory(new PropertyValueFactory<>("parameter"));
        oldValueColumn.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        differenceColumn.setCellValueFactory(new PropertyValueFactory<>("difference"));
    }

    public void setData(int oldTORA, int newTORA, int oldTODA, int newTODA, int oldASDA, int newASDA, int oldLDA, int newLDA,
                        int length, int clearwayLength, int stopwayLength, int displacedThreshold) {
        ObservableList<ComparisonData> data = FXCollections.observableArrayList(
                new ComparisonData("TORA", oldTORA, newTORA),
                new ComparisonData("TODA", oldTODA, newTODA),
                new ComparisonData("ASDA", oldASDA, newASDA),
                new ComparisonData("LDA", oldLDA, newLDA)
        );
        comparisonTable.setItems(data);

        String calculationDetails = String.format(
                "TORA = Length = %d\n" +
                        "TODA = TORA + Clearway Length = %d + %d = %d\n" +
                        "ASDA = TORA + Stopway Length = %d + %d = %d\n" +
                        "LDA = TORA - Displaced Threshold = %d - %d = %d",
                length, length, clearwayLength, newTODA, length, stopwayLength, newASDA, length, displacedThreshold, newLDA
        );
        calculationDetailsArea.setText(calculationDetails);
    }

    public static class ComparisonData {
        private final String parameter;
        private final int oldValue;
        private final int newValue;
        private final int difference;

        public ComparisonData(String parameter, int oldValue, int newValue) {
            this.parameter = parameter;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.difference = newValue - oldValue;
        }

        public String getParameter() { return parameter; }
        public int getOldValue() { return oldValue; }
        public int getNewValue() { return newValue; }
        public int getDifference() { return difference; }
    }
}
