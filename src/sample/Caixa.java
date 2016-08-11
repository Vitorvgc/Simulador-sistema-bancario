package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.concurrent.Semaphore;

/** Thread Caixa **/
public class Caixa extends Thread {

    private int id;
    private boolean disponivel;
    private Cliente atendido;
    private PersonagemView view;

    Semaphore caixas;              // semáforo compartilhado que representa o número de caixas que estão disponíveis para atendimento
    Semaphore ativo;               // semáforo do caixa que representa se o caixa está acordado atendendo ou dormindo esperando
    Semaphore mutexCaixasDormindo; // semáforo para proteger a variável compartilhada numCaixasDormindo

    static Image currentImage;

    // Inicializa o caixa com seu respectivo id
    public Caixa(int id, Semaphore caixas, Semaphore mutexCaixasDormindo, Controller controller) {
        this.id = id;
        this.disponivel = true;
        this.atendido = null;
        this.caixas = caixas;
        this.mutexCaixasDormindo = mutexCaixasDormindo;
        this.ativo = new Semaphore(0);
        this.view = new PersonagemView(0, this.id, controller, TipoEnum.CAIXA, 0);
    }

    // Rotina rexecutada pela thread
    @Override
    public void run() {
        while(true) {
            try {
                // Caixa dorme até que algum cliente o acorde
                ativo.acquire();
                // atende o cliente
                atender(atendido);
                // volta a estar disponível para atender um novo cliente
                disponivel = true;
                // incrementa o número de caixas a dormir (pois ele passará a dormir)
                this.mutexCaixasDormindo.acquire();
                SystemManager.getInstance().setNumeroCaixasDormindo(SystemManager.getInstance().getNumeroCaixasDormindo() + 1);
                this.mutexCaixasDormindo.release();
                // acorda o próximo cliente, caso haja algum
                SystemManager.getInstance().chamarProximoCliente();
                // incrementa a quantidade de caixas que etão disponíveis
                caixas.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Animação de atender
    private void atender(Cliente cliente) {

        Image interage[] = new Image[3];
        for(int i = 0; i < 3; i++)
            interage[i] = new Image(String.format("/imagens/personagens/caixas/interage%d.png",i));

        long tempo[] = {System.currentTimeMillis(), System.currentTimeMillis()};
        for(int i = 0, contagem = cliente.getTempoAtendimento(); contagem > 0;) {
            long aux[] = {System.currentTimeMillis(), System.currentTimeMillis()};
            if(aux[0] - tempo[0] >= 1000) {
                contagem--;
                tempo[0] = aux[0];
            }
            if(aux[1] - tempo[1] >= 280) {
                currentImage = interage[i];
                Platform.runLater(() -> this.view.sprite.setImage(currentImage));
                i = (i + 1) % 3;
                tempo[1] = aux[1];
            }
        }
        Platform.runLater(() -> this.getView().sprite.setImage(new Image("imagens/personagens/caixas/idle.png")));
    }

    public void setAtendido(Cliente atendido) {
        this.atendido = atendido;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public boolean getDisponivel() {
        return this.disponivel;
    }

    public int getCaixaId() {
        return this.id;
    }

    public PersonagemView getView() {
        return view;
    }

    // Retorna a posição a frente do caixa, que é para onde o cliente deve ir
    public Vec2d getPosAtendimento() {
        Vec2d topLeft = this.view.getPos();
        topLeft.x += 25;
        topLeft.y += this.view.sprite.getFitHeight() * 1.1;
        return topLeft;
    }

}
