package org.iesvdm.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.Pedido;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

	private static final String SELECT_PEDIDOS_WITH_NAMES = """
			SELECT p.id, p.total, p.fecha, p.id_cliente, p.id_comercial,
				CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente_nombre_completo,
				CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial_nombre_completo
			FROM pedido p
			JOIN cliente c ON p.id_cliente = c.id
			JOIN comercial co ON p.id_comercial = co.id
			""";

	private final JdbcTemplate jdbcTemplate;

	PedidoDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public synchronized void create(Pedido pedido) {
		String sqlInsert = """
				INSERT INTO pedido (total, fecha, id_cliente, id_comercial)
				VALUES (?, ?, ?, ?)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[] { "id" });
			ps.setObject(1, pedido.getTotal());
			ps.setDate(2, pedido.getFecha() == null ? null : Date.valueOf(pedido.getFecha()));
			ps.setInt(3, pedido.getClienteId());
			ps.setInt(4, pedido.getComercialId());
			return ps;
		}, keyHolder);

		pedido.setId(keyHolder.getKey().intValue());
		log.info("Insertados {} registros de pedido.", rows);
	}

	@Override
	public List<Pedido> getAll() {
		List<Pedido> pedidos = jdbcTemplate.query("SELECT * FROM pedido", (rs, rowNum) -> mapPedido(rs));
		log.info("Devueltos {} pedidos.", pedidos.size());
		return pedidos;
	}

	@Override
	public Optional<Pedido> find(int id) {
		List<Pedido> pedidos = jdbcTemplate.query(
				"SELECT * FROM pedido WHERE id = ?",
				(rs, rowNum) -> mapPedido(rs),
				id);

		if (pedidos.isEmpty()) {
			log.info("Pedido con id {} no encontrado.", id);
			return Optional.empty();
		}
		return Optional.of(pedidos.get(0));
	}

	@Override
	public List<PedidoDetalle> getAllWithNames() {
		List<PedidoDetalle> pedidos = jdbcTemplate.query(
				SELECT_PEDIDOS_WITH_NAMES,
				(rs, rowNum) -> mapPedidoDetalle(rs));
		log.info("Devueltos {} pedidos con nombres.", pedidos.size());
		return pedidos;
	}

	@Override
	public Optional<PedidoDetalle> findWithNames(int id) {
		List<PedidoDetalle> pedidos = jdbcTemplate.query(
				SELECT_PEDIDOS_WITH_NAMES + " WHERE p.id = ?",
				(rs, rowNum) -> mapPedidoDetalle(rs),
				id);

		if (pedidos.isEmpty()) {
			log.info("Pedido con id {} no encontrado.", id);
			return Optional.empty();
		}
		return Optional.of(pedidos.get(0));
	}

	@Override
	public void update(Pedido pedido) {
		int rows = jdbcTemplate.update("""
				UPDATE pedido SET
					total = ?,
					fecha = ?,
					id_cliente = ?,
					id_comercial = ?
				WHERE id = ?
				""",
				pedido.getTotal(),
				pedido.getFecha() == null ? null : Date.valueOf(pedido.getFecha()),
				pedido.getClienteId(),
				pedido.getComercialId(),
				pedido.getId());

		log.info("Update de Pedido con {} registros actualizados.", rows);
	}

	@Override
	public void delete(int id) {
		int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
		log.info("Delete de Pedido con {} registros eliminados.", rows);
	}

	private Pedido mapPedido(ResultSet rs) throws SQLException {
		Date fecha = rs.getDate("fecha");
		return new Pedido(
				rs.getInt("id"),
				rs.getObject("total", Double.class),
				fecha == null ? null : fecha.toLocalDate(),
				rs.getInt("id_cliente"),
				rs.getInt("id_comercial"));
	}

	private PedidoDetalle mapPedidoDetalle(ResultSet rs) throws SQLException {
		Date fecha = rs.getDate("fecha");
		return new PedidoDetalle(
				rs.getInt("id"),
				rs.getObject("total", Double.class),
				fecha == null ? null : fecha.toLocalDate(),
				rs.getInt("id_cliente"),
				rs.getInt("id_comercial"),
				rs.getString("cliente_nombre_completo"),
				rs.getString("comercial_nombre_completo"));
	}
}