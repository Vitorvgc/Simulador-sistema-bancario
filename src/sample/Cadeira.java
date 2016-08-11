package sample;

import com.sun.javafx.geom.Vec2d;

public class Cadeira {

    private Boolean ocupada;
    private Vec2d posicao;

    public Cadeira(Vec2d posicao) {
        this.posicao = posicao;
        this.ocupada = false;
    }

    public void setOcupada(Boolean bool) {
        this.ocupada = bool;
    }

    public Boolean getOcupada() {
        return ocupada;
    }

    public Vec2d getPosicao() {
        return new Vec2d(posicao.x - 2, posicao.y - 35);
    }
}
