package sample;

import com.sun.javafx.geom.Vec2d;
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

    @FXML
    public void initialize() {

        this.atualizaSenhaLabel(0);
    }

    @FXML
    private void novoSistema() {
        System.out.println(cenario);
        SystemManager.novoSistema(2, this);
        System.out.println("Foi");
    }

    @FXML
    private void novoCliente() {
        SystemManager.getInstance().novoCliente(10);
    }

    @FXML
    private void reset() {

    }

    public void atualizaSenhaLabel(int senha) {
        Platform.runLater(() -> senhaLabel.setText(String.format("%03d", senha)));
    }

    public Vec2d getEntradaPos() {
        return new Vec2d(800, 350);
    }
}
