package org.iesvdm.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.ClienteComercial;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ClienteComercialService;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class ClienteController {

    @Autowired 
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @Autowired
    private ClienteComercialService clienteComercialService;

    @GetMapping("/clientes")
    public String listar(@RequestParam(required = false) String ciudad, Model model) {
        List<Cliente> listaClientes = ciudad == null || ciudad.isBlank()
            ? clienteService.listAll()
            : clienteService.listByCiudad(ciudad);
        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("ciudad", ciudad);
        return "clientes";
    }

    @GetMapping("/clientes/ciudad/{ciudad}")
    public String listarPorCiudad(@PathVariable String ciudad, Model model) {
        return listar(ciudad, model);
    }

    @GetMapping("/clientes/{id}")
    public String detalle(Model model, @PathVariable int id) {
        return clienteService.find(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "detalle-cliente";
                })
                .orElse("redirect:/clientes");
    }

    @GetMapping("/clientes/crear")
    public String crear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "crear-cliente";
    }

    @PostMapping("/clientes/crear")
    public String submitCrear(@ModelAttribute("cliente") Cliente cliente) {
        clienteService.create(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(Model model, @PathVariable int id) {
        return clienteService.find(id)
                .map(cliente -> {
                    List<ClienteComercial> comercialesVinculados = clienteComercialService.findByClienteId(id);
                    Set<Integer> idsVinculados = comercialesVinculados.stream()
                            .map(ClienteComercial::getComercialId)
                            .collect(Collectors.toSet());
                    List<Comercial> comercialesDisponibles = comercialService.listAll().stream()
                            .filter(comercial -> !idsVinculados.contains(comercial.getId()))
                            .toList();

                    model.addAttribute("cliente", cliente);
                    model.addAttribute("comercialesDisponibles", comercialesDisponibles);
                    model.addAttribute("comercialesVinculados", comercialesVinculados);
                    model.addAttribute("fechaActual", LocalDate.now());
                    return "editar-cliente";
                })
                .orElse("redirect:/clientes");
    }

    @PostMapping("/clientes/editar/{id}/comerciales")
    public String submitAñadirComercial(@PathVariable int id,
            @RequestParam int comercialId,
            @RequestParam LocalDate fecha,
            @RequestParam int prioridad) {
        if (clienteService.find(id).isEmpty() || comercialService.find(comercialId).isEmpty()
            || prioridad < 1 || prioridad > 10) {
            return "redirect:/clientes/editar/" + id;
        }

        boolean yaVinculado = clienteComercialService.findByClienteId(id).stream()
                .anyMatch(vinculo -> vinculo.getComercialId() == comercialId);
        if (!yaVinculado) {
            clienteComercialService.create(id, comercialId, fecha, prioridad);
        }
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/clientes/editar/{id}/comerciales/{comercialId}/borrar")
    public String submitBorrarComercial(@PathVariable int id, @PathVariable int comercialId) {
        if (clienteService.find(id).isPresent()) {
            clienteComercialService.delete(id, comercialId);
        }
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/clientes/editar/{id}")
    public String submitEditar(@PathVariable int id, @ModelAttribute("cliente") Cliente cliente) {
        cliente.setId(id);
        clienteService.update(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/borrar/{id}")
    public String submitBorrar(@PathVariable int id) {
        clienteService.delete(id);
        return "redirect:/clientes";
    }
}
