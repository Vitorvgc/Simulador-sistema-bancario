package sample;

import javafx.scene.image.Image;

import java.util.concurrent.Semaphore;

/**
 * Created by Vítor on 22/07/2016.
 */
public class Caixa extends Thread {

    private int id;
    private boolean disponivel;
    private Cliente atendido;
    private PersonagemView view;

    Semaphore clientes;
    Semaphore caixas;
    Semaphore ativo;

    public Caixa(int id, Semaphore clientes, Semaphore caixas, Controller controller, Image sprite) {
        this.id = id;
        this.disponivel = true;
        this.atendido = null;
        this.clientes = clientes;
        this.caixas = caixas;
        this.ativo = new Semaphore(0);
        this.view = new PersonagemView(0, this.id, controller, sprite);
    }

    @Override
    public void run() {
        System.out.println("Caixa " + this.id + " nasceu");
        while(true) {
            try {
                ativo.acquire();
                //clientes.acquire();
                //disponivel = false;
                atender(atendido);
                //SystemManager.getInstance().atualizaSenha();
                disponivel = true;
                caixas.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Caixa " + this.id + " morreu");
    }

    private void atender(Cliente cliente) {
        System.out.println("Caixa " + this.id + " atendendo cliente " + cliente.id);
        try {
            sleep(cliente.tempoAtendimento * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

}
