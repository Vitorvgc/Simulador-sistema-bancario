package sample;

import com.sun.javafx.geom.Vec2d;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;

/** Singleton responsável por instanciar, eliminar e gerenciar os estados dos caixas e clientes **/
public class SystemManager {

    final int NUMERO_CADEIRAS = 10; // número máximo de cadeiras

    private int numeroSenhas;
    private int senhaAtual;
    private int clienteId;
    private ArrayList<Caixa> caixas;
    private ArrayList<Cliente> clientes;
    private ArrayList<Cliente> clientesEsperando;
    private Cadeira cadeiras[];
    private int numeroCaixasDormindo;

    private Controller controller;

    private Semaphore[] clienteSemaphores;

    private static SystemManager instance = null;

    // inicializa a instância quando recriada
    private SystemManager(int numeroCaixas, Controller controller) {
        this.senhaAtual = 0;
        this.numeroSenhas = 0;
        this.clienteId = 0;
        this.numeroCaixasDormindo = numeroCaixas;
        this.controller = controller;
        Semaphore caixasS = new Semaphore(numeroCaixas);
        Semaphore mutexCaixasDormindo = new Semaphore(numeroCaixas);
        this.clienteSemaphores = new Semaphore[]{caixasS, new Semaphore(1), new Semaphore(1), new Semaphore(NUMERO_CADEIRAS)};
        this.clientes = new ArrayList<>();
        this.clientesEsperando = new ArrayList<>();
        // inicializa e coloca os caixas no cenário
        this.caixas = new ArrayList<>();
        Double razao = this.controller.cenario.getWidth() / (numeroCaixas + 1);
        for(int i = 0; i < numeroCaixas; i++) {
            Caixa caixa = new Caixa(i + 1, caixasS, mutexCaixasDormindo,controller);
            Double xPos = (i+1) * razao - 50;
            caixa.getView().desenha(new Vec2d(xPos, 100), 100, 100);
            caixas.add(caixa);
        }
        // ativa todos os caixas
        caixas.forEach(Caixa::start);
        // instanciação das cadeiras
        this.cadeiras = new Cadeira[10];
        for(int i = 0, xPos = 63; i < 10; i++, xPos += 74)
            cadeiras[i] = new Cadeira(new Vec2d(xPos, 410));
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
        Cliente cliente = new Cliente(++clienteId, tempoAtendimento, ++numeroSenhas, clienteSemaphores, controller, (numeroSenhas-1)%9);
        clientes.add(cliente);
        cliente.getView().desenha(controller.getEntradaPos(), 50, 50);
        cliente.start();
    }

    // acorda um caixa específico
    public void ativaCaixa(Caixa caixa) {
        caixas.get(caixa.getCaixaId() - 1).ativo.release();
    }

    // retorna o primeiro caixa disponível dentre todos os caixas do array, ou nulo caso não haja nenhum
    public Caixa getCaixaDisponivel() {
        for (Caixa caixa : caixas)
            if (caixa.getDisponivel())
                return caixa;
        return null;
    }

    // retorna a primeira cadeira desocupada dentre todas as cadeiras do array, ou nulo caso não haja nenhuma
    public Cadeira getCadeiraDesocupada() {
        for (Cadeira cadeira : cadeiras)
            if (!(cadeira.getOcupada()))
                return cadeira;
        return null;
    }

    // indica que a cadeira passada por parâmetro está desocupada
    public void desocupaCadeira(Cadeira cadeiraOcupada) {
        for (Cadeira cadeira : cadeiras)
            if (cadeira == cadeiraOcupada) {
                cadeira.setOcupada(false);
                return;
            }
    }

    // atualiza a senha atual
    public void atualizaSenha() {
        this.senhaAtual++;
        controller.atualizaSenhaLabel(senhaAtual);
    }

    // caso haja algum cliente esperando, atualiza a senha e acorda o cliente com a senha atual
    public void chamarProximoCliente() {
        if(clientesEsperando.size() > 0) {
            atualizaSenha();
            for (Cliente cliente : clientesEsperando)
                if (cliente.getSenha() == senhaAtual) {
                    cliente.getEsperando().release();
                    return;
                }
        }
    }

    public int getSenhaAtual() {
        return this.senhaAtual;
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public void setClienteEsperando(Cliente cliente, Boolean esperando) {
        if(esperando)
            this.clientesEsperando.add(cliente);
        else
            this.clientesEsperando.remove(cliente);
    }

    public void setNumeroCaixasDormindo(int numeroCaixasDormindo) {
        this.numeroCaixasDormindo = numeroCaixasDormindo;
    }

    public int getNumeroCaixasDormindo() {
        return numeroCaixasDormindo;
    }

}
