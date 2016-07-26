package sample;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Vec2d;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Created by Vítor on 21/07/2016.
 */
public class PersonagemView extends VBox {

    Label senha;
    Label id;
    ImageView sprite;
    Controller controller;

    public PersonagemView(int senha, int id, Controller controller, Image sprite) {
        this.senha = new Label(String.format("%03d", senha));
        this.id = new Label(String.format("%02d", id));
        this.sprite = new ImageView(sprite);
        this.controller = controller;
        this.getChildren().addAll(this.senha, this.id, this.sprite);
    }

    public void moverPara(Caixa caixa) {
        Vec2d pos = caixa.getView().getPos();
        Timeline loop = new Timeline(60, new KeyFrame(Duration.seconds(3), new KeyValue(this.translateXProperty(), pos.x),
                                                             new KeyValue(this.translateYProperty(), pos.y)));
        loop.setCycleCount(1);
        loop.setAutoReverse(false);
        //loop.getKeyFrames().add();
        loop.playFromStart();
    }

    public boolean desenha() {
        this.setWidth(150);
        this.setHeight(150);
        this.sprite.setFitWidth(100);
        this.sprite.setFitHeight(100);
        AnchorPane pane = this.controller.cenario;
        System.out.println(pane);
        this.setTranslateX(200);//pane.getWidth() - this.sprite.getFitWidth());
        this.setTranslateY(200);//pane.getHeight() - 2 * this.sprite.getFitHeight());
        pane.getChildren().add(this);
        return true;
    }

    public Vec2d getPos() {

        Vec2d pos = this.getPos();
        pos.x += this.getWidth();
        pos.y += this.getHeight() * 1.1;
        return pos;
    }

}
