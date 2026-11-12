package org.iesvdm.servicios;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dao.ClienteComercialDAO;
import org.iesvdm.modelo.ClienteComercial;
import org.springframework.stereotype.Service;

@Service
public class ClienteComercialService {

	private final ClienteComercialDAO clienteComercialDAO;

	public ClienteComercialService(ClienteComercialDAO clienteComercialDAO) {
		this.clienteComercialDAO = clienteComercialDAO;
	}

	public List<ClienteComercial> findByClienteId(int clienteId) {
		return clienteComercialDAO.findByClienteId(clienteId);
	}

	public void create(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
		clienteComercialDAO.create(clienteId, comercialId, fecha, prioridad);
	}

	public void delete(int clienteId, int comercialId) {
		clienteComercialDAO.delete(clienteId, comercialId);
	}
}