package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Vector;

/**
 * Created by Vítor on 21/07/2016.
 */
public class PersonagemView extends VBox {

    Label senha;
    Label id;
    ImageView sprite;
    Controller controller;

    TipoEnum tipo;
    int personagemId;
    String path;

    public PersonagemView(int senha, int id, Controller controller, Image sprite, TipoEnum tipo, int personagemId) {
        this.senha = new Label(String.format("%03d", senha));
        this.id = new Label(String.format("%02d", id));
        this.tipo = tipo;
        this.personagemId = personagemId;
        this.path = "/imagens/personagens/";
        this.path += tipo == TipoEnum.CLIENTE ? String.format("clientes/%d", personagemId) : "caixas";
        this.sprite = new ImageView(sprite);
        this.controller = controller;
        this.getChildren().addAll(this.senha, this.id, this.sprite);
    }

    public boolean desenha(Vec2d position) {
        this.setWidth(150);
        this.setHeight(150);
        this.sprite.setFitWidth(100);
        this.sprite.setFitHeight(100);
        AnchorPane pane = this.controller.cenario;
        this.setTranslateX(position.x);//pane.getWidth() - this.sprite.getFitWidth());
        this.setTranslateY(position.y);//pane.getHeight() - 2 * this.sprite.getFitHeight());
        pane.getChildren().add(this);
        return true;
    }

    public void moverPara(Caixa caixa) {
        Vec2d pos = caixa.getView().getPos();
        this.sprite.setImage(new Image(path + "/esquerda0.gif"));
        Timeline moveLeft = new Timeline(60, new KeyFrame(Duration.seconds(3), new KeyValue(this.translateXProperty(), pos.x)));
        moveLeft.setCycleCount(1);
        moveLeft.setAutoReverse(false);
        moveLeft.playFromStart();
        moveLeft.setOnFinished(event -> {
            this.sprite.setImage(new Image(path + "/cima0.gif"));
            Timeline moveUp = new Timeline(60, new KeyFrame(Duration.seconds(1.5), new KeyValue(this.translateYProperty(), pos.y)));
            moveUp.setCycleCount(1);
            moveUp.setAutoReverse(false);
            moveUp.playFromStart();
        });
    }

    public void sair() {
        Vec2d pos = new Vec2d(-this.sprite.getFitWidth(), 350);
        this.sprite.setImage(new Image(path + "/baixo0.gif"));
        Timeline moveDown = new Timeline(60, new KeyFrame(Duration.seconds(1.5), new KeyValue(this.translateYProperty(), pos.y)));
        moveDown.setCycleCount(1);
        moveDown.setAutoReverse(false);
        moveDown.playFromStart();
        moveDown.setOnFinished(event -> {
            this.sprite.setImage(new Image(path + "/esquerda0.gif"));
            Timeline moveLeft = new Timeline(60, new KeyFrame(Duration.seconds(3), new KeyValue(this.translateXProperty(), pos.x)));
            moveLeft.setCycleCount(1);
            moveLeft.setAutoReverse(false);
            moveLeft.playFromStart();
            moveLeft.setOnFinished(event1 -> {
                // destruir view
            });
        });
    }

    public Vec2d getPos() {

        Point2D bounds = this.localToScene(Point2D.ZERO);

        //System.out.println(String.format("Bounds: %f %f", bounds.getWidth(), bounds.getHeight()));
        return new Vec2d(bounds.getX(), bounds.getY() * 1.1);
        //pos.x += this.getWidth();
        //pos.y += this.getHeight() * 1.1;
    }

}
