package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

/** Classe responsável por movimentar e animar os sprites dos clientes e caixas **/
public class PersonagemView extends VBox {

    Label senha;
    Label id;
    ImageView sprite;
    Controller controller;

    TipoEnum tipo;
    int personagemId;
    String path;

    static Image currentImage;

    /* Inicializa o cliente ou caixa com seu respectivo id e senha */
    public PersonagemView(int senha, int id, Controller controller, TipoEnum tipo, int personagemId) {
        this.tipo = tipo;
        this.personagemId = personagemId;
        this.path = "/imagens/personagens/";
        this.path += tipo == TipoEnum.CLIENTE ? String.format("clientes/%d", personagemId) : "caixas";
        this.controller = controller;
        this.senha = new Label(tipo == TipoEnum.CLIENTE ? String.format("%03d", senha) : "CAIXA");
        this.senha.setAlignment(Pos.CENTER);
        this.senha.setTextFill(javafx.scene.paint.Paint.valueOf("#0000ff"));
        this.id = new Label(String.format("%02d", id));
        this.id.setAlignment(Pos.CENTER);
        this.id.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
        this.sprite = new ImageView(new Image(String.format("%s/%s", path, tipo == TipoEnum.CLIENTE ? "esquerda1.gif" : "idle.png")));
        this.getChildren().addAll(this.senha, this.id, this.sprite);
    }

    // Configura o tamanho da imagem e a coloca no cenário
    public boolean desenha(Vec2d position, int width, int height) {
        this.setWidth(150);
        this.setHeight(150);
        this.sprite.setFitWidth(width);
        this.sprite.setFitHeight(height);
        this.senha.setMinWidth(width);
        this.id.setMinWidth(width);
        AnchorPane pane = this.controller.cenario;
        this.setTranslateX(position.x);
        this.setTranslateY(position.y);
        pane.getChildren().add(this);
        return true;
    }

    /*  Move o personagem para a posição especificada em pos, podendo
     *  movimentar-se primeiro pela horizontal ou pela vertical
     */
    public void moverPara(Vec2d pos, Mode mode) {

        switch(mode) {
            case HORIZONTAL_FIRST:
                moverHorizontal(pos.x);
                moverVertical(pos.y);
                break;
            case VERTICAL_FIRST:
                moverVertical(pos.y);
                moverHorizontal(pos.x);
                break;
        }
    }

    // Movimenta o personagem apenas na horizontal para o ponto do eixo x especificado
    private void moverHorizontal(double xPos) {

        Image direcao[] = new Image[3];
        String direcaoString = this.getPos().x < xPos ? "direita" : "esquerda";
        int fator = (Objects.equals(direcaoString, "esquerda") ? -1 : 1);

        for(int i = 0; i < 3; i++)
            direcao[i] = new Image(String.format("%s/%s%d.gif",path,direcaoString,i));

        Platform.runLater(() -> this.sprite.setImage(direcao[2]));
        long tempo[] = {System.currentTimeMillis(), System.currentTimeMillis()};
        for (int i = 0; this.getPos().x * fator < xPos * fator; ) {
            long aux[] = {System.currentTimeMillis(), System.currentTimeMillis()};
            if (aux[0] - tempo[0] >= 30) {
                Platform.runLater(() -> this.setTranslateX(this.getTranslateX() + 2*fator));
                tempo[0] = aux[0];
            }
            if (aux[1] - tempo[1] >= 140) {
                currentImage = direcao[i];
                Platform.runLater(() -> this.sprite.setImage(currentImage));
                i = (i + 1) % 3;
                tempo[1] = aux[1];
            }
        }
    }

    // Movimenta o personagem apenas na vertical para o ponto do eixo y especificado
    private void moverVertical(double yPos) {

        Image direcao[] = new Image[3];
        String direcaoString = this.getPos().y < yPos ? "baixo" : "cima";
        int fator = (Objects.equals(direcaoString, "cima") ? -1 : 1);

        for(int i = 0; i < 3; i++)
            direcao[i] = new Image(String.format("%s/%s%d.gif",path,direcaoString,i));

        Platform.runLater(() -> this.sprite.setImage(direcao[2]));
        long tempo[] = {System.currentTimeMillis(), System.currentTimeMillis()};
        for (int i = 0; this.getPos().y * fator < yPos * fator; ) {
            long aux[] = {System.currentTimeMillis(), System.currentTimeMillis()};
            if (aux[0] - tempo[0] >= 30) {
                Platform.runLater(() -> this.setTranslateY(this.getTranslateY() + 2 * fator));
                tempo[0] = aux[0];
            }
            if (aux[1] - tempo[1] >= 140) {
                currentImage = direcao[i];
                Platform.runLater(() -> this.sprite.setImage(currentImage));
                i = (i + 1) % 3;
                tempo[1] = aux[1];
            }
        }
    }

    // Retorna a posição do personagem no cenário
    public Vec2d getPos() {
        Point2D bounds = this.localToScene(Point2D.ZERO);
        return new Vec2d(bounds.getX(), bounds.getY());
    }

    // Faz o personagem virar para a frente
    public void virarParaFrente() {
        Platform.runLater(() -> this.sprite.setImage(new Image(String.format("%s/cima1.gif", path))));
    }

}
