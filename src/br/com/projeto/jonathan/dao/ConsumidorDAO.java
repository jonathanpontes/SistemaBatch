package br.com.projeto.jonathan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.projeto.jonathan.model.Consumidor;

public class ConsumidorDAO {

	public void inserir() throws SQLException {

		List<Consumidor> consumidores = new ArrayList<>();
		List<String> lista = gerarCpfCnpj();
		Random numeroAleatorio = new Random();

		ConnectFactory connectFactory = new ConnectFactory();
		Connection conn = null;
		PreparedStatement pstm = null;

		String sql = "INSERT INTO tb_customer_account"
				+ "(id_customer, cpf_cnpj, nm_customer, is_active, vl_total) VALUES" + "(?,?,?,?,?)";
		try {

			conn = connectFactory.obterConexao();
			pstm = conn.prepareStatement(sql);

			conn.setAutoCommit(false);

			for (int i = 0; i < 3000; i++) {

				Consumidor consumidor = new Consumidor();

				int n1 = numeroAleatorio.nextInt(10);

				consumidor.setIdConsumidor(i);
				consumidor.setCpfCnpj(lista.get(i));
				consumidor.setNome("Jose " + i);
				consumidor.setStatus(true);
				consumidor.setValorTotal(n1 * i);

				pstm.setInt(1, consumidor.getIdConsumidor());
				pstm.setString(2, consumidor.getCpfCnpj());
				pstm.setString(3, consumidor.getNome());
				pstm.setBoolean(4, consumidor.isStatus());
				pstm.setDouble(5, consumidor.getValorTotal());
				pstm.addBatch();

				consumidores.add(consumidor);
			}
			pstm.executeBatch();
			conn.commit();

			calcularMedia(consumidores);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			conn.rollback();
		} finally {
			if (pstm != null) {
				pstm.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public void calcularMedia(List<Consumidor> consumidores) {

		List<Consumidor> consumidoresSelecionados = new ArrayList<>();
		List<Consumidor> consumidoresOrdenados = new ArrayList<>();
		Consumidor consumidorView = new Consumidor();
		int media = 0;
		
		for (int i = 0; i < consumidores.size(); i++) {
			if (consumidores.get(i).getValorTotal() > 560 && consumidores.get(i).getIdConsumidor() > 1500
					&& consumidores.get(i).getIdConsumidor() < 2700) {

				consumidoresSelecionados.add(consumidores.get(i));
			}
		}

		for (int i = 0; i < consumidoresSelecionados.size(); i++) {
			
			media += consumidoresSelecionados.get(i).getValorTotal();
			
			consumidorView.setIdConsumidor(consumidoresSelecionados.get(i).getIdConsumidor());
			consumidorView.setNome(consumidoresSelecionados.get(i).getNome());
			consumidorView.setCpfCnpj(consumidoresSelecionados.get(i).getCpfCnpj());
			consumidorView.setValorTotal(consumidoresSelecionados.get(i).getValorTotal());

			consumidoresOrdenados.add(consumidorView);
			
			System.out.println(" | " + consumidorView.getIdConsumidor() + " | " + consumidorView.getNome() + " | " +
					consumidorView.getCpfCnpj()+ "    | " + consumidorView.getValorTotal() + " | ");
		}

		media = media / consumidoresSelecionados.size();
		System.out.println("\nA media do valor Total do clientes acima �: "+ media);
	
	}

	public List<String> gerarCpfCnpj() {
		GeradorCpf geradorCpf = new GeradorCpf();
		GeradorCnpj geradorCnpj = new GeradorCnpj();

		List<String> lista = new ArrayList<>();

		for (int i = 0; i < 3000; i++) {
			String cpf = geradorCpf.geraCPF();
			String cnpj = geradorCnpj.geraCNPJ();

			lista.add(cpf);
			lista.add(cnpj);
		}
		return lista;
	}

}
