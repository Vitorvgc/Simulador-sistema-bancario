package sample;

import javafx.scene.image.Image;

import java.util.concurrent.Semaphore;

/**
 * Created by Vítor on 22/07/2016.
 */
public class Cliente extends Thread {

    private int id;
    private int tempoAtendimento;
    private int senha;
    private PersonagemView view;

    Semaphore clientes;
    Semaphore caixas;

    public Cliente(int id, int tempoAtendimento, int senha, Semaphore clientes, Semaphore caixas, Controller controller, Image sprite) {
        this.id = id;
        this.tempoAtendimento = tempoAtendimento;
        this.senha = senha;
        this.view = new PersonagemView(senha, id, controller, sprite);
        this.clientes = clientes;
        this.caixas = caixas;
    }

    @Override
    public void run() {
        System.out.println("Cliente " + this.id + " nasceu");
        try {
            caixas.acquire();
            Caixa caixa = SystemManager.getInstance().getCaixaDisponivel();
            caixa.setAtendido(this);
            caixa.setDisponivel(false);
            SystemManager.getInstance().atualizaSenha();
            moverPara(caixa);
            this.view.moverPara(caixa);
            SystemManager.getInstance().ativaCaixa(caixa);
            serAtendido();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Cliente " + this.id + " morreu");
    }

    private void serAtendido() {

        System.out.println("Cliente " + this.id + " sendo atendido");
        try {
            sleep(this.tempoAtendimento * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void moverPara(Caixa caixa) {
        System.out.println("Cliente " + this.id + " indo para caixa " + caixa.getCaixaId());
    }

    public PersonagemView getView() {
        return this.view;
    }

    public int getTempoAtendimento() {
        return this.tempoAtendimento;
    }

    public int getIdentificador() {
        return this.id;
    }

}
