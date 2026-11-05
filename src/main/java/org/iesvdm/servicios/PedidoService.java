package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	@Autowired
	private PedidoDAO pedidoDAO;

	public List<Pedido> listAll() {
		return pedidoDAO.getAll();
	}

	public Optional<Pedido> find(int id) {
		return pedidoDAO.find(id);
	}

	public List<PedidoDetalle> listAllWithNames() {
		return pedidoDAO.getAllWithNames();
	}

	public Optional<PedidoDetalle> findWithNames(int id) {
		return pedidoDAO.findWithNames(id);
	}

	public void create(Pedido pedido) {
		pedidoDAO.create(pedido);
	}

	public void update(Pedido pedido) {
		pedidoDAO.update(pedido);
	}

	public void delete(int id) {
		pedidoDAO.delete(id);
	}
}