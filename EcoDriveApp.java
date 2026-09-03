import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EcoDriveApp extends JFrame {

    private JTextField txtMarca, txtModelo, txtPlaca, txtAno, txtKm;
    private JButton btnSalvar, btnAtualizar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private String url = "jdbc:mysql://localhost:3306/ecodrive_db";
    private String usuario = "root"; 
    private String senha = "root"; 

    private int idSelecionado = -1;

    public EcoDriveApp() {
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

        btnSalvar.addActionListener(e -> {
            String sql="";
            if (idSelecionado == -1) {
                sql = "INSERT INTO veiculos (marca, modelo, placa, ano, quilometragem) VALUES ('"
                    + txtMarca.getText() + "', '" + txtModelo.getText() + "', '" + txtPlaca.getText() + "', "
                    + Integer.parseInt(txtAno.getText()) + ", " + Integer.parseInt(txtKm.getText()) + ")";
            }
            else {
                sql = "UPDATE veiculos SET marca='" + txtMarca.getText() + "', modelo='" + txtModelo.getText() 
                       + "', placa='" + txtPlaca.getText() + "', ano=" + Integer.parseInt(txtAno.getText()) 
                       + ", quilometragem=" + Integer.parseInt(txtKm.getText()) + " WHERE id=" + idSelecionado;
            }
            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 Statement statement = conexao.createStatement()) {
                statement.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!");
                limparCampos();
                atualizarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
            }
        });

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
            String sql = "UPDATE veiculos SET marca='" + txtMarca.getText() + "', modelo='" + txtModelo.getText() 
                       + "', placa='" + txtPlaca.getText() + "', ano=" + Integer.parseInt(txtAno.getText()) 
                       + ", quilometragem=" + Integer.parseInt(txtKm.getText()) + " WHERE id=" + idSelecionado;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 Statement statement = conexao.createStatement()) {
                statement.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Veículo atualizado com sucesso!");
                limparCampos();
                atualizarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
            if (idSelecionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para excluir.");
                return;
            }
            String sql = "DELETE FROM veiculos WHERE id = " + idSelecionado;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 Statement statement = conexao.createStatement()) {
                statement.executeUpdate(sql);
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

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT * FROM veiculos";

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
             Statement statement = conexao.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

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
        }
    }

    private void limparCampos() {
        txtMarca.setText(""); txtModelo.setText(""); txtPlaca.setText(""); txtAno.setText(""); txtKm.setText("");
        idSelecionado = -1;
        tabela.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EcoDriveApp().setVisible(true));
    }
}