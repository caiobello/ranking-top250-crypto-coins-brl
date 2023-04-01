/**@author Caio Bello*/
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Este código em Java é um programa que exibe o ranking das 250 principais
 * criptomoedas em relação ao real brasileiro. O programa utiliza a API pública
 * Coingecko para obter os dados das criptomoedas e os exibe em uma tabela na
 * interface gráfica do usuário.
 */

public class RankingCryptoBrl extends JFrame implements ActionListener {

	private JTable tabela;
	private JButton btnRefresh;

	public RankingCryptoBrl() {
		setTitle("Ranking das Top 250 Criptomoedas");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel pnlRefresh = new JPanel();
		btnRefresh = new JButton("Atualizar");
		btnRefresh.addActionListener(this);
		pnlRefresh.add(btnRefresh);
		add(pnlRefresh, BorderLayout.NORTH);

		tabela = new JTable();
		tabela.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Ranking", "Nome", "Valor (BRL)", "Market Cap (BRL)" }));
		tabela.getColumnModel().getColumn(0).setMaxWidth(50);
		tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
		tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
		tabela.getColumnModel().getColumn(3).setPreferredWidth(150);

		JScrollPane scrollPane = new JScrollPane(tabela);
		add(scrollPane, BorderLayout.CENTER);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRefresh) {
			atualizarRanking();
		}
	}

	public void atualizarRanking() {
		try {
			JSONArray ranking = obterDadosAPI();
			List<Object[]> rows = new ArrayList<>();
			for (int i = 0; i < ranking.length(); i++) {
				JSONObject cripto = ranking.getJSONObject(i);
				String nome = cripto.getString("name") + " (" + cripto.getString("symbol").toUpperCase() + ")";
				String valor = String.format("R$ " + "%.11f", cripto.getDouble("current_price"));
				String marketCap = NumberFormat.getCurrencyInstance().format(cripto.getDouble("market_cap"));
				rows.add(new Object[] { i + 1, nome, valor, marketCap });
			}
			DefaultTableModel model = (DefaultTableModel) tabela.getModel();
			model.setRowCount(0);
			rows.forEach(row -> model.addRow(row));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao atualizar ranking: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JSONArray obterDadosAPI() throws Exception {
		String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=brl&order=market_cap_desc&per_page=250&page=1&sparkline=false";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		return new JSONArray(new String(con.getInputStream().readAllBytes()));
	}

	public static void main(String[] args) {
		RankingCryptoBrl app = new RankingCryptoBrl();
		app.atualizarRanking();
	}
}
