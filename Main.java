import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Main extends JFrame {

    private ArrayList<Paciente> pacientes = new ArrayList<>();

    private final String ARQUIVO = "pacientes.dat";

    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    private JTextField campoNome;
    private JTextField campoIdade;
    private JTextField campoTelefone;

    private DefaultTableModel modeloTabela;

    public Main() {
        setTitle("Sistema de Cadastro de Pacientes");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        carregarPacientes();

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        criarTelaCadastro();
        criarTelaListagem();

        add(painelPrincipal);

        cardLayout.show(painelPrincipal, "cadastro");
    }


    private void criarTelaCadastro() {

        JPanel tela = new JPanel(new GridBagLayout());
        tela.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("CADASTRO DE PACIENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        tela.add(titulo, gbc);

        gbc.gridwidth = 1;

        JLabel labelNome = new JLabel("Nome:");
        campoNome = new JTextField(25);

        gbc.gridx = 0;
        gbc.gridy = 1;
        tela.add(labelNome, gbc);

        gbc.gridx = 1;
        tela.add(campoNome, gbc);

        JLabel labelIdade = new JLabel("Idade:");
        campoIdade = new JTextField(25);

        gbc.gridx = 0;
        gbc.gridy = 2;
        tela.add(labelIdade, gbc);

        gbc.gridx = 1;
        tela.add(campoIdade, gbc);

        JLabel labelTelefone = new JLabel("Telefone:");
        campoTelefone = new JTextField(25);

        gbc.gridx = 0;
        gbc.gridy = 3;
        tela.add(labelTelefone, gbc);

        gbc.gridx = 1;
        tela.add(campoTelefone, gbc);

        JButton botaoCadastrar = new JButton("Cadastrar");
        JButton botaoListar = new JButton("Listar pacientes");

        gbc.gridx = 0;
        gbc.gridy = 4;
        tela.add(botaoCadastrar, gbc);

        gbc.gridx = 1;
        tela.add(botaoListar, gbc);

        botaoCadastrar.addActionListener(e -> cadastrarPaciente());

        botaoListar.addActionListener(e -> {
            atualizarTabela();
            cardLayout.show(painelPrincipal, "listagem");
        });

        painelPrincipal.add(tela, "cadastro");
    }


    private void criarTelaListagem() {

        JPanel tela = new JPanel(new BorderLayout(10, 10));
        tela.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("PACIENTES CADASTRADOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        tela.add(titulo, BorderLayout.NORTH);

        String[] colunas = {"Nome", "Idade", "Telefone"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);

        tela.add(scroll, BorderLayout.CENTER);

        JButton botaoVoltar = new JButton("Voltar ao cadastro");

        botaoVoltar.addActionListener(e -> {
            cardLayout.show(painelPrincipal, "cadastro");
        });

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(botaoVoltar);

        tela.add(painelBotoes, BorderLayout.SOUTH);

        painelPrincipal.add(tela, "listagem");
    }


    private void cadastrarPaciente() {

        try {

            String nome = campoNome.getText().trim();
            String idadeTexto = campoIdade.getText().trim();
            String telefone = campoTelefone.getText().trim();

            if (nome.isEmpty() || idadeTexto.isEmpty() || telefone.isEmpty()) {
                throw new DadosInvalidosException(
                    "Todos os campos devem ser preenchidos!"
                );
            }

            if (!nome.matches("[\\p{L} .'-]+")) {
                throw new DadosInvalidosException(
                    "O nome possui caracteres inválidos!"
                );
            }

            if (nome.length() < 3 || nome.length() > 100) {
                throw new DadosInvalidosException(
                    "O nome deve ter entre 3 e 100 caracteres!"
                );
            }

            int idade = Integer.parseInt(idadeTexto);

            if (idade < 0 || idade > 120) {
                throw new DadosInvalidosException(
                    "A idade deve estar entre 0 e 120 anos!"
                );
            }

            if (!telefone.matches("\\d{10,11}")) {
                throw new DadosInvalidosException(
                    "O telefone deve conter 10 ou 11 números!"
                );
            }

            Paciente paciente = new Paciente(nome, idade, telefone);

            pacientes.add(paciente);

            salvarPacientes();

            JOptionPane.showMessageDialog(
                this,
                "Paciente cadastrado com sucesso!"
            );

            campoNome.setText("");
            campoIdade.setText("");
            campoTelefone.setText("");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Digite uma idade válida!",
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (DadosInvalidosException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Dados inválidos",
                JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Ocorreu um erro ao cadastrar o paciente.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void atualizarTabela() {

        try {

            modeloTabela.setRowCount(0);

            for (Paciente paciente : pacientes) {

                modeloTabela.addRow(new Object[]{
                    paciente.getNome(),
                    paciente.getIdade(),
                    paciente.getTelefone()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao carregar a lista de pacientes."
            );
        }
    }


    private void salvarPacientes() {

        try (ObjectOutputStream saida =
                new ObjectOutputStream(
                    new FileOutputStream(ARQUIVO)
                )) {

            saida.writeObject(pacientes);

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao salvar os pacientes no arquivo!"
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarPacientes() {

        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return;
        }

        try (ObjectInputStream entrada =
                new ObjectInputStream(
                    new FileInputStream(arquivo)
                )) {

            Object objeto = entrada.readObject();

            if (!(objeto instanceof ArrayList<?>)) {
                throw new IOException("Formato de arquivo inválido.");
            }

            ArrayList<?> lista = (ArrayList<?>) objeto;

            for (Object item : lista) {
                if (!(item instanceof Paciente)) {
                    throw new IOException("Dados de paciente inválidos.");
                }
            }

            pacientes = (ArrayList<Paciente>) lista;

        } catch (IOException | ClassNotFoundException |
                 ClassCastException e) {

            JOptionPane.showMessageDialog(
                null,
                "Não foi possível carregar os pacientes salvos.\n"
                + "O sistema continuará funcionando.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
            );

            pacientes = new ArrayList<>();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                Main sistema = new Main();
                sistema.setVisible(true);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                    null,
                    "Erro ao iniciar o sistema!"
                );
            }
        });
    }
}

class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private int idade;
    private String telefone;

    public Paciente(String nome, int idade, String telefone) {
        this.nome = nome;
        this.idade = idade;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getTelefone() {
        return telefone;
    }
}

class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
