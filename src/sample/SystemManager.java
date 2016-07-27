
package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.scene.image.Image;

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
    private SystemManager(int numeroCaixas, Controller controller) {
        this.senhaAtual = 0;
        this.numeroSenhas = 0;
        this.clienteId = 0;
        this.numeroCaixas = numeroCaixas;
        this.controller = controller;
        this.clientesS = new Semaphore(0);
        this.caixasS = new Semaphore(numeroCaixas);
        this.clientes = new ArrayList<>();
        this.caixas = new ArrayList<>();
        Double razao = this.controller.cenario.getWidth() / (this.numeroCaixas + 1);
        for(int i = 0; i < numeroCaixas; i++) {
            Caixa caixa = new Caixa(i + 1, clientesS, caixasS, controller, new Image("/imagens/noam-chomsky.jpg"));
            //System.out.println(caixa.getView().getWidth()); => printando 0.0
            Double xPos = (i+1) * razao - 50; // hardcodado
            caixa.getView().desenha(new Vec2d(xPos, 150));
            System.out.println(xPos.toString() + " 150");
            System.out.println(caixa.getView().getPos().toString());
            caixas.add(caixa);
        }
        caixas.forEach(Caixa::start);
    }

    // retorna a instância compartilhada pelo Singleton
    public static SystemManager getInstance() {
        return instance;
    }

    // reseta a instância para uma nova com o número de caixas passado por parâmetro
    public static void novoSistema(int numeroCaixas, Controller controller) {
        SystemManager.instance = new SystemManager(numeroCaixas, controller);
    }

    // instancia um novo cliente com o tempo de atendimento determinado em segundos
    public void novoCliente(int tempoAtendimento) {
        Cliente cliente = new Cliente(++clienteId, tempoAtendimento, ++numeroSenhas, clientesS, caixasS, controller, new Image("/imagens/mikufront.png"), (numeroSenhas-1)%10);
        clientes.add(cliente);
        cliente.getView().desenha(controller.getEntradaPos());
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
