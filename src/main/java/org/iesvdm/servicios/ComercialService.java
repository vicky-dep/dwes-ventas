package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.ClientePedidosDTO;
import org.iesvdm.modelo.EstadisticasPedidosDTO;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.stereotype.Service;

@Service
public class ComercialService {

	private final ComercialDAO comercialDAO;
	private final PedidoDAO pedidoDAO;

	public ComercialService(ComercialDAO comercialDAO, PedidoDAO pedidoDAO) {
		this.comercialDAO = comercialDAO;
		this.pedidoDAO = pedidoDAO;
	}

	public List<Comercial> listAll() {
		return comercialDAO.getAll();
	}

	public List<PedidoDetalle> listPedidosByComercial(int comercialId) {
		return pedidoDAO.findByComercialId(comercialId);
	}

	public List<ClientePedidosDTO> listClientesByComercialOrderByTotalDesc(int comercialId) {
		return pedidoDAO.findClientesByComercialIdOrderByTotalDesc(comercialId);
	}

	public EstadisticasPedidosDTO calcularEstadisticasPedidos(List<PedidoDetalle> pedidos) {
		double importeTotal = pedidos.stream()
				.mapToDouble(pedido -> pedido.getTotal() == null ? 0.0 : pedido.getTotal())
				.sum();
		double importeMedio = pedidos.isEmpty() ? 0.0 : importeTotal / pedidos.size();
		Double importeMaximo = pedidos.stream()
				.map(PedidoDetalle::getTotal)
				.filter(total -> total != null)
				.max(Double::compareTo)
				.orElse(null);
		Double importeMinimo = pedidos.stream()
				.map(PedidoDetalle::getTotal)
				.filter(total -> total != null)
				.min(Double::compareTo)
				.orElse(null);
		return new EstadisticasPedidosDTO(importeTotal, importeMedio, importeMaximo, importeMinimo);
	}

	public Optional<Comercial> find(int id) {
		return comercialDAO.find(id);
	}

	public void create(Comercial comercial) {
		comercialDAO.create(comercial);
	}

	public void update(Comercial comercial) {
		comercialDAO.update(comercial);
	}

	public void delete(int id) {
		comercialDAO.delete(id);
	}
}