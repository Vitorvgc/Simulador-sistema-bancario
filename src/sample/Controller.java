package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    @FXML
    AnchorPane cenario;

    @FXML
    Label senhaLabel;

    @FXML
    TextArea log;

    @FXML
    Button abrir, novo, fechar;

    @FXML
    private TableView<Cliente> tabelaClientes;

    @FXML
    private TableColumn<Cliente, String> colunaID;
    @FXML
    private TableColumn<Cliente, String> colunaSenha;
    @FXML
    private TableColumn<Cliente, String> colunaTempo;

    @FXML
    VBox menu;

    private ObservableList<Cliente> data =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        this.atualizaSenhaLabel(0);
        this.colunaSenha.setCellValueFactory(cellData -> cellData.getValue().senhaPropertyProperty());
        this.colunaID.setCellValueFactory(cellData -> cellData.getValue().idPropertyProperty());
        this.colunaTempo.setCellValueFactory(cellData -> cellData.getValue().tempoPropertyProperty());

        tabelaClientes.setItems(data);
        BackgroundImage bg = new BackgroundImage(new Image("/imagens/BackGround7.png", cenario.getWidth(), cenario.getHeight(), true, true), BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        cenario.setBackground(new Background(bg));
    }

    @FXML
    private void novoSistema() {

        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("Número de caixas");
        dialog.setHeaderText("Quantos caixas?");
        dialog.setContentText("Digite o número de caixas:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(numero -> {
            SystemManager.novoSistema(Integer.parseInt(numero), this);
            this.senhaLabel.setVisible(true);
            this.abrir.setDisable(true);
            this.fechar.setDisable(false);
            this.novo.setDisable(false);
        });
    }

    @FXML
    private void novoCliente() {

        TextInputDialog dialog = new TextInputDialog("4");
        dialog.setTitle("Tempo de espera");
        dialog.setHeaderText("Quanto tempo de atendimento?");
        dialog.setContentText("Digite o tempo que o cliente levará para ser atendido:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tempo -> {
            SystemManager.getInstance().novoCliente(Integer.parseInt(tempo));
            ArrayList<Cliente> clientes = SystemManager.getInstance().getClientes();
            if(clientes.size() > 0)
                data.add(clientes.get(clientes.size() - 1));
        });
    }

    @FXML
    private void reset() {
        SystemManager.getInstance().getClientes().forEach(sample.Cliente::stop);
        this.cenario.getChildren().clear();
        this.abrir.setDisable(false);
        this.fechar.setDisable(true);
        this.novo.setDisable(true);
        this.getData().clear();
        this.abrir.setDisable(false);
        this.fechar.setDisable(true);
        this.novo.setDisable(true);
        this.log.setText("");
    }

    public void atualizaSenhaLabel(int senha) {
        Platform.runLater(() -> senhaLabel.setText(String.format("%03d", senha)));
    }

    public Vec2d getEntradaPos() {
        return new Vec2d(-50, 295);
    }

    public Vec2d getSaidaPos() {
        return new Vec2d(800, 295);
    }

    public ObservableList<Cliente> getData() {
        return data;
    }

    public void elimina(Cliente cliente) {

        Platform.runLater(() -> {
            this.cenario.getChildren().remove(cliente.getView());
            data.remove(this);
        });
    }
}
