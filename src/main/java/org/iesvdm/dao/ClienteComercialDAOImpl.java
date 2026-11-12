package org.iesvdm.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.iesvdm.modelo.ClienteComercial;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteComercialDAOImpl implements ClienteComercialDAO {

	private final JdbcTemplate jdbcTemplate;

	public ClienteComercialDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<ClienteComercial> findByClienteId(int clienteId) {
		return jdbcTemplate.query("""
				SELECT chc.cliente_id, chc.comercial_id, c.nombre, c.apellido1,
				       c.apellido2, chc.fecha, chc.prioridad
				FROM cliente_has_comercial chc
				JOIN comercial c ON c.id = chc.comercial_id
				WHERE chc.cliente_id = ?
				ORDER BY chc.prioridad, c.apellido1, c.nombre
				""",
				(rs, rowNum) -> new ClienteComercial(
					rs.getInt("cliente_id"),
					rs.getInt("comercial_id"),
					rs.getString("nombre"),
					rs.getString("apellido1"),
					rs.getString("apellido2"),
					rs.getDate("fecha").toLocalDate(),
					rs.getInt("prioridad")),
				clienteId);
	}

	@Override
	public void create(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
		jdbcTemplate.update("""
				INSERT INTO cliente_has_comercial (cliente_id, comercial_id, fecha, prioridad)
				VALUES (?, ?, ?, ?)
				""",
				clienteId,
				comercialId,
				Date.valueOf(fecha),
				prioridad);
	}

	@Override
	public void delete(int clienteId, int comercialId) {
		jdbcTemplate.update("""
				DELETE FROM cliente_has_comercial
				WHERE cliente_id = ? AND comercial_id = ?
				""",
				clienteId,
				comercialId);
	}
}