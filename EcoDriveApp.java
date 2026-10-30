import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EcoDriveApp extends JFrame {

    private JTextField txtMarca, txtModelo, txtPlaca, txtAno, txtKm;
    private JButton btnSalvar, btnAtualizar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // a conexão vem do config.properties (fora do Git), nunca do código
    private final String url;
    private final String usuario;
    private final String senha;

    private int idSelecionado = -1;

    public EcoDriveApp(Properties config) {
        url = config.getProperty("db.url");
        usuario = config.getProperty("db.user");
        senha = config.getProperty("db.password");

        setTitle("EcoDrive - Gestão de Frota");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlCampos = new JPanel(new GridLayout(3, 4, 5, 5));
        pnlCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlCampos.add(new JLabel(" Marca:"));
        txtMarca = new JTextField(); pnlCampos.add(txtMarca);

        pnlCampos.add(new JLabel(" Modelo:"));
        txtModelo = new JTextField(); pnlCampos.add(txtModelo);

        pnlCampos.add(new JLabel(" Placa:"));
        txtPlaca = new JTextField(); pnlCampos.add(txtPlaca);

        pnlCampos.add(new JLabel(" Ano (AAAA):"));
        txtAno = new JTextField(); pnlCampos.add(txtAno);

        pnlCampos.add(new JLabel(" KM Atual:"));
        txtKm = new JTextField(); pnlCampos.add(txtKm);

        add(pnlCampos, BorderLayout.NORTH);

        JPanel pnlBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnAtualizar = new JButton("Atualizar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        pnlBotoes.add(btnSalvar); pnlBotoes.add(btnAtualizar); pnlBotoes.add(btnExcluir); pnlBotoes.add(btnLimpar);
        add(pnlBotoes, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Marca", "Modelo", "Placa", "Ano", "KM"}, 0);
        tabela = new JTable(modeloTabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Salvar cadastra um veículo novo, ou atualiza o selecionado
        btnSalvar.addActionListener(e -> salvar());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha != -1) {
                idSelecionado = (int) modeloTabela.getValueAt(linha, 0);
                txtMarca.setText((String) modeloTabela.getValueAt(linha, 1));
                txtModelo.setText((String) modeloTabela.getValueAt(linha, 2));
                txtPlaca.setText((String) modeloTabela.getValueAt(linha, 3));
                txtAno.setText(modeloTabela.getValueAt(linha, 4).toString());
                txtKm.setText(modeloTabela.getValueAt(linha, 5).toString());
            }
        });

        btnAtualizar.addActionListener(e -> {
            if (idSelecionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para atualizar.");
                return;
            }
            salvar();
        });

        btnExcluir.addActionListener(e -> {
            if (idSelecionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para excluir.");
                return;
            }
            try (Connection conexao = conectar()) {
                excluir(conexao, idSelecionado);
                JOptionPane.showMessageDialog(this, "Veículo excluído com sucesso!");
                limparCampos();
                atualizarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
            }
        });

        btnLimpar.addActionListener(e -> limparCampos());

        atualizarTabela();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    private void salvar() {
        int ano, km;
        try {
            ano = Integer.parseInt(txtAno.getText().trim());
            km = Integer.parseInt(txtKm.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano e KM precisam ser números inteiros.");
            return;
        }
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String placa = txtPlaca.getText().trim();

        try (Connection conexao = conectar()) {
            if (idSelecionado == -1) {
                inserir(conexao, marca, modelo, placa, ano, km);
                JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!");
            } else {
                atualizar(conexao, idSelecionado, marca, modelo, placa, ano, km);
                JOptionPane.showMessageDialog(this, "Veículo atualizado com sucesso!");
            }
            limparCampos();
            atualizarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    // Os valores vão por parâmetro (?), nunca colados no texto do SQL:
    // assim o que for digitado nos campos não vira comando no banco (SQL injection).
    static void inserir(Connection conexao, String marca, String modelo, String placa, int ano, int km) throws SQLException {
        String sql = "INSERT INTO veiculos (marca, modelo, placa, ano, quilometragem) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, marca);
            ps.setString(2, modelo);
            ps.setString(3, placa);
            ps.setInt(4, ano);
            ps.setInt(5, km);
            ps.executeUpdate();
        }
    }

    static void atualizar(Connection conexao, int id, String marca, String modelo, String placa, int ano, int km) throws SQLException {
        String sql = "UPDATE veiculos SET marca = ?, modelo = ?, placa = ?, ano = ?, quilometragem = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, marca);
            ps.setString(2, modelo);
            ps.setString(3, placa);
            ps.setInt(4, ano);
            ps.setInt(5, km);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    static void excluir(Connection conexao, int id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM veiculos WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, marca, modelo, placa, ano, quilometragem FROM veiculos ORDER BY id";

        try (Connection conexao = conectar();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String placa = rs.getString("placa");
                int ano = rs.getInt("ano");
                int km = rs.getInt("quilometragem");

                modeloTabela.addRow(new Object[]{id, marca, modelo, placa, ano, km});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar os veículos: " + ex.getMessage());
        }
    }

    private void limparCampos() {
        txtMarca.setText(""); txtModelo.setText(""); txtPlaca.setText(""); txtAno.setText(""); txtKm.setText("");
        idSelecionado = -1;
        tabela.clearSelection();
    }

    static Properties carregarConfig() throws IOException {
        Properties config = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        }
        return config;
    }

    public static void main(String[] args) {
        Properties config;
        try {
            config = carregarConfig();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Arquivo config.properties não encontrado na pasta do projeto.\n"
                    + "Copie o config.properties.example e preencha com os dados do seu MySQL.");
            return;
        }
        SwingUtilities.invokeLater(() -> new EcoDriveApp(config).setVisible(true));
    }
}
