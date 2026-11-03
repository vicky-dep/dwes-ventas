package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ComercialController {

	@Autowired
	private ComercialService comercialService;

	@GetMapping("/comerciales")
	public String listar(Model model) {
		List<Comercial> listaComerciales = comercialService.listAll();
		model.addAttribute("listaComerciales", listaComerciales);
		return "comerciales";
	}

	@GetMapping("/comerciales/{id}")
	public String detalle(Model model, @PathVariable int id) {
		return comercialService.find(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					return "detalle-comercial";
				})
				.orElse("redirect:/comerciales");
	}

	@GetMapping("/comerciales/crear")
	public String crear(Model model) {
		model.addAttribute("comercial", new Comercial());
		return "crear-comercial";
	}

	@PostMapping("/comerciales/crear")
	public String submitCrear(@ModelAttribute("comercial") Comercial comercial) {
		comercialService.create(comercial);
		return "redirect:/comerciales";
	}

	@GetMapping("/comerciales/editar/{id}")
	public String editar(Model model, @PathVariable int id) {
		return comercialService.find(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					return "editar-comercial";
				})
				.orElse("redirect:/comerciales");
	}

	@PostMapping("/comerciales/editar/{id}")
	public String submitEditar(@PathVariable int id, @ModelAttribute("comercial") Comercial comercial) {
		comercial.setId(id);
		comercialService.update(comercial);
		return "redirect:/comerciales";
	}

	@PostMapping("/comerciales/borrar/{id}")
	public String submitBorrar(@PathVariable int id) {
		comercialService.delete(id);
		return "redirect:/comerciales";
	}
}