package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class Controller {

    @FXML
    AnchorPane cenario;

    @FXML
    Label senhaLabel;

    @FXML
    TextArea log;

    @FXML
    TableView tabelaClientes;

    private SystemManager manager;

    public Controller() {
        // fazer isso no ViewDidAppear
        SystemManager.novoSistema(2);
        manager = SystemManager.getInstance();
        manager.setController(this);
        if(senhaLabel != null) senhaLabel.setText(String.format("%03d", 5));
        else System.out.println("FU");

        if(log == null) System.out.println("log FU");
        if(cenario == null) System.out.println("cenario FU");
    }

    @FXML
    private void novoSistema() {
        //manager.novoSistema(2);
        System.out.println("Foi");
    }

    @FXML
    private void novoCliente() {
        //senhaLabel.setText(new Date().toInstant().toString());
        manager.novoCliente(10);
    }

    @FXML
    private void reset() {

    }

    public void atualizaSenhaLabel(int senha) {
        Platform.runLater(() -> senhaLabel.setText(String.format("%03d", senha)));
    }
}
