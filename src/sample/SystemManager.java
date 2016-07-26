
package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;

/**
 * Created by Vítor on 22/07/2016.
 */
public class SystemManager {

    private int numeroSenhas;
    private int senhaAtual;
    private int clienteId;
    private int numeroCaixas;
    private ArrayList<Caixa> caixas;
    private ArrayList<Cliente> clientes;

    private Controller controller;

    private Semaphore clientesS;
    private Semaphore caixasS;

    private static SystemManager instance = null;

    // inicializa a instância quando recriada
    private SystemManager(int numeroCaixas) {
        this.senhaAtual = 0;
        this.numeroSenhas = 0;
        this.clienteId = 0;
        this.numeroCaixas = numeroCaixas;
        this.clientesS = new Semaphore(0);
        this.caixasS = new Semaphore(numeroCaixas);
        this.clientes = new ArrayList<>();
        this.caixas = new ArrayList<>();
        for(int i = 0; i < numeroCaixas; i++) {
            Caixa caixa = new Caixa(i + 1, clientesS, caixasS, controller,  new Image("/imagens/noam-chomsky.jpg"));
            caixa.getView().desenha();

        }
        caixas.forEach(Caixa::start);
    }

    // retorna a instância compartilhada pelo Singleton
    public static SystemManager getInstance() {
        return instance;
    }

    // reseta a instância para uma nova com o número de caixas passado por parâmetro
    public static void novoSistema(int numeroCaixas) {
        SystemManager.instance = new SystemManager(numeroCaixas);
    }

    // instancia um novo cliente com o tempo de atendimento determinado em segundos
    public void novoCliente(int tempoAtendimento) {
        Cliente cliente = new Cliente(++clienteId, tempoAtendimento, ++numeroSenhas, clientesS, caixasS, controller, new Image("/imagens/mikufront.png"));
        clientes.add(cliente);
        cliente.view.desenha();
        cliente.start();
    }

    // acorda um caixa específico (usado pelo Cliente)
    public void ativaCaixa(Caixa caixa) {
        caixas.get(caixa.getCaixaId() - 1).ativo.release();
    }

    // retorna o primeiro caixa disponível dentre todos os caixas do array, ou nulo caso não haja nenhum (usado pelo Cliente)
    public Caixa getCaixaDisponivel() {
        for(int i = 0; i < caixas.size(); i++)
            if(caixas.get(i).getDisponivel())
                return caixas.get(i);
        return null;
    }

    // retorna o cliente que possui a senha atual, ou nulo caso não haja cliente (usado pelo Caixa)
    public Cliente getProximoCliente() {

        for(int i = 0; i < clientes.size(); i++)
            if(clientes.get(i).senha == this.senhaAtual)
                return clientes.get(i);
        return null;
    }

    // remove um cliente do array de clientes
    public void removerCliente(Cliente cliente) {
        clientes.remove(cliente);
    }

    // atualiza a senha atual
    public void atualizaSenha() {
        this.senhaAtual++;
        controller.atualizaSenhaLabel(senhaAtual);
    }

    public int getSenhaAtual() {
        return this.senhaAtual;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
