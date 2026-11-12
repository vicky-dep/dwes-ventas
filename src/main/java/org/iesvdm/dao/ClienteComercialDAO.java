package org.iesvdm.dao;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.modelo.ClienteComercial;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteComercialDAO {
	public List<ClienteComercial> findByClienteId(int clienteId);
	public void create(int clienteId, int comercialId, LocalDate fecha, int prioridad);
	public void delete(int clienteId, int comercialId);
}