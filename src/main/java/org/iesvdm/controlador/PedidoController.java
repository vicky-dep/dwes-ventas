package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PedidoController {

	private final PedidoService pedidoService;
	private final ClienteService clienteService;
	private final ComercialService comercialService;

	public PedidoController(PedidoService pedidoService, ClienteService clienteService,
			ComercialService comercialService) {
		this.pedidoService = pedidoService;
		this.clienteService = clienteService;
		this.comercialService = comercialService;
	}

	@GetMapping("/pedidos")
	public String listar(Model model) {
		model.addAttribute("listaPedidos", pedidoService.listAllWithNames());
		return "pedidos";
	}

	@GetMapping("/pedidos/{id}")
	public String detalle(Model model, @PathVariable int id) {
		return pedidoService.findWithNames(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					return "detalle-pedido";
				})
				.orElse("redirect:/pedidos");
	}

	@GetMapping("/pedidos/crear")
	public String crear(Model model) {
		model.addAttribute("pedido", new Pedido());
		cargarPersonasRelacionadas(model);
		return "crear-pedido";
	}

	@PostMapping("/pedidos/crear")
	public String submitCrear(@ModelAttribute("pedido") Pedido pedido, BindingResult result, Model model) {
		validarRelaciones(pedido, result);
		if (result.hasErrors()) {
			cargarPersonasRelacionadas(model);
			return "crear-pedido";
		}
		pedidoService.create(pedido);
		return "redirect:/pedidos";
	}

	@GetMapping("/pedidos/editar/{id}")
	public String editar(Model model, @PathVariable int id) {
		return pedidoService.find(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					cargarPersonasRelacionadas(model);
					return "editar-pedido";
				})
				.orElse("redirect:/pedidos");
	}

	@PostMapping("/pedidos/editar/{id}")
	public String submitEditar(@PathVariable int id, @ModelAttribute("pedido") Pedido pedido,
			BindingResult result, Model model) {
		pedido.setId(id);
		validarRelaciones(pedido, result);
		if (result.hasErrors()) {
			cargarPersonasRelacionadas(model);
			return "editar-pedido";
		}
		pedidoService.update(pedido);
		return "redirect:/pedidos";
	}

	@PostMapping("/pedidos/borrar/{id}")
	public String submitBorrar(@PathVariable int id) {
		pedidoService.delete(id);
		return "redirect:/pedidos";
	}

	private void cargarPersonasRelacionadas(Model model) {
		List<Cliente> clientes = clienteService.listAll();
		List<Comercial> comerciales = comercialService.listAll();
		model.addAttribute("listaClientes", clientes);
		model.addAttribute("listaComerciales", comerciales);
	}

	private void validarRelaciones(Pedido pedido, BindingResult result) {
		if (clienteService.find(pedido.getClienteId()).isEmpty()) {
			result.rejectValue("clienteId", "pedido.clienteId.invalido", "El cliente seleccionado no existe.");
		}
		if (comercialService.find(pedido.getComercialId()).isEmpty()) {
			result.rejectValue("comercialId", "pedido.comercialId.invalido", "El comercial seleccionado no existe.");
		}
	}
}