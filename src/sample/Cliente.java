package sample;

import com.sun.javafx.geom.Vec2d;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;

import java.util.concurrent.Semaphore;

/** Thread Cliente **/
public class Cliente extends Thread {

    private int id;
    private int tempoAtendimento;
    private int senha;
    private PersonagemView view;
    private String path;
    private final StringProperty idProperty, tempoProperty, senhaProperty;

    private Semaphore caixas;          // semáforo compartilhado que representa o número de caixas que estão disponíveis para atendimento
    private Semaphore mutexDisponivel; // semáforo para proteger os atributos de disponível dos caixas
    private Semaphore mutexLog;        // semáforo para impedir múltiplas escritos ao log
    private Semaphore cadeiras;        // semáforo que representa número de cadeiras desocupadas
    private Semaphore esperando;       // semáforo do cliente que representa se ele está dormindo na cadeira esperando sua senha ser chamada

    // Inicializa o cliente com seu respectivo id, senha e sprite
    public Cliente(int id, int tempoAtendimento, int senha, Semaphore[] semaphores, Controller controller, int personagemId) {
        this.id = id;
        this.tempoAtendimento = tempoAtendimento;
        this.senha = senha;
        this.view = new PersonagemView(senha, id, controller, TipoEnum.CLIENTE, personagemId);
        this.caixas = semaphores[0];
        this.mutexDisponivel = semaphores[1];
        this.mutexLog = semaphores[2];
        this.cadeiras = semaphores[3];
        this.esperando = new Semaphore(0);
        this.path = "/imagens/personagens/";
        this.path += this.view.tipo == TipoEnum.CLIENTE ? String.format("clientes/%d", this.view.personagemId) : "caixas";
        idProperty = new SimpleStringProperty(String.format("%d", id));
        tempoProperty = new SimpleStringProperty(String.format("%d", tempoAtendimento));
        senhaProperty = new SimpleStringProperty(String.format("%d", senha));
    }

    // Rotina executada pela thread
    @Override
    public void run() {
        try {
            // verifica se há cadeiras disponíveis
            cadeiras.acquire();
            // registra no log a entrada do cliente
            mutexLog.acquire();
            logEntrarNoBanco();
            mutexLog.release();
            // procura uma cadeira desocupada e se direciona até ela
            Cadeira cadeira = SystemManager.getInstance().getCadeiraDesocupada();
            cadeira.setOcupada(true);
            this.view.moverPara(cadeira.getPosicao(), Mode.HORIZONTAL_FIRST);
            this.view.virarParaFrente();
            /*  analisa se há caixas disponíveis para atender o cliente. Caso sim, a senha é atualizada e o cliente  *
             *  prossegue em direção ao caixa. Caso contrário, o cliente dorme na cadeira.                           */
            int numCaixasDormindo = SystemManager.getInstance().getNumeroCaixasDormindo();
            if(numCaixasDormindo > 0 && this.senha <= SystemManager.getInstance().getSenhaAtual() + numCaixasDormindo)
                SystemManager.getInstance().atualizaSenha();
            else {
                SystemManager.getInstance().setClienteEsperando(this, true);
                esperando.acquire();
            }
            // acorda o cliente e verifica se há caixas disponíveis
            SystemManager.getInstance().setClienteEsperando(this, false);
            caixas.acquire();
            // procura um caixa disponível
            mutexDisponivel.acquire();
            SystemManager.getInstance().setNumeroCaixasDormindo(SystemManager.getInstance().getNumeroCaixasDormindo() - 1);
            Caixa caixa = SystemManager.getInstance().getCaixaDisponivel();
            caixa.setAtendido(this);
            caixa.setDisponivel(false);
            mutexDisponivel.release();
            // registra no log o momento que o cliente começou a se direcionar ao caixa
            mutexLog.acquire();
            logMoverPara(caixa);
            mutexLog.release();
            // libera a cadeira que estava ocupada
            SystemManager.getInstance().desocupaCadeira(cadeira);
            cadeira.setOcupada(false);
            cadeiras.release();
            // direciona-se até o caixa
            this.view.moverPara(new Vec2d(this.view.getPos().x, 295), Mode.VERTICAL_FIRST);
            this.view.moverPara(caixa.getPosAtendimento(), Mode.HORIZONTAL_FIRST);
            // acorda o caixa e é atendido
            SystemManager.getInstance().ativaCaixa(caixa);
            serAtendido();
            // move-se até a saída
            this.getView().moverPara(this.view.controller.getSaidaPos(), Mode.VERTICAL_FIRST);
            // registra no log a saída do cliente
            mutexLog.acquire();
            logSairDoBanco();
            mutexLog.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // elimina o cliente do cenário
        this.view.controller.getData().remove(this);
        this.view.controller.elimina(this);
    }

    // Executa a animação de ser atendido
    private void serAtendido() {
        logSerAtendido();

        Image interage[] = new Image[3];
        for(int i = 0; i < 3; i++)
            interage[i] = new Image(String.format("%s/interage%d.png",path,i));

        int contagem = Integer.parseInt(getTempoProperty());
        long tempo[] = {System.currentTimeMillis(), System.currentTimeMillis()};
        for(int i = 0; contagem > 0;) {
            long aux[] = {System.currentTimeMillis(), System.currentTimeMillis()};
            if(aux[0] - tempo[0] >= 1000) {
                tempoProperty.setValue(String.valueOf(--contagem));
                tempo[0] = aux[0];
            }
            if(aux[1] - tempo[1] >= 280) {
                this.view.sprite.setImage(interage[i]);
                i = (i + 1) % 3;
                tempo[1] = aux[1];
            }
        }
    }

    private void logEntrarNoBanco() {
        TextArea log = this.view.controller.log;
        log.setText(log.getText() + "Cliente " + this.id + " entrou no banco\n");
    }

    private void logSerAtendido() {
        TextArea log = this.view.controller.log;
        log.setText(log.getText() + "Cliente " + this.id + " sendo atendido\n");
    }

    private void logMoverPara(Caixa caixa) {
        TextArea log = this.view.controller.log;
        log.setText(log.getText() + "Cliente " + this.id + " indo para caixa " + caixa.getCaixaId() + "\n");
    }

    private void logSairDoBanco() {
        TextArea log = this.view.controller.log;
        log.setText(log.getText() + "Cliente " + this.id + " foi atendido e saiu do banco\n");
    }

    public StringProperty idPropertyProperty() {
        return idProperty;
    }

    public String getTempoProperty() {
        return tempoProperty.get();
    }

    public StringProperty tempoPropertyProperty() {
        return tempoProperty;
    }

    public StringProperty senhaPropertyProperty() {
        return senhaProperty;
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

    public int getSenha() {
        return this.senha;
    }

    public Semaphore getEsperando() {
        return this.esperando;
    }
}
