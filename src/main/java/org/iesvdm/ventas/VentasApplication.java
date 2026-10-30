package org.iesvdm.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication(scanBasePackages = "org.iesvdm")
@RestController
@Slf4j 
public class VentasApplication implements CommandLineRunner {

	@Autowired 
	private ClienteDAO clienteDAO;

	@Autowired
	private ComercialDAO comercialDAO;

	public static void main(String[] args) {
		SpringApplication.run(VentasApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return "Bienvenido a la aplicación de ventas";
	} 

	@Override 
	public void run (String... args) throws Exception {
		log.info("*********************************");
		log.info("**Prueba de arranque ClienteDAO**");
		log.info("*********************************");

		clienteDAO.getAll().forEach(cliente -> log.info("Cliente: {}", cliente));

		int id = 1;
		Optional<Cliente> cliente = clienteDAO.find(id);

		if (cliente.isPresent()) {
			String nombreOld = cliente.get().getNombre();
			log.info("Nombre original del cliente {}: {}", id, nombreOld);

			cliente.get().setNombre("Jose M");
			clienteDAO.update(cliente.get());

			log.info("Nombre después del cambio: {}", cliente.get().getNombre());

			clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));
			
			cliente.get().setNombre(nombreOld);
			clienteDAO.update(cliente.get());

			log.info("Nombre restaurado: {}", cliente.get().getNombre());
			
			clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));

		} else {
			log.info("No se encontró el cliente con id {}", id);
		}

		log.info("***********************************");
		log.info("**Prueba de arranque ComercialDAO**");
		log.info("***********************************");

		comercialDAO.getAll().forEach(comercial -> log.info("Comercial: {}", comercial));

		Comercial comercialPrueba = new Comercial(0, "Comercial", "Prueba", "DAO", 0.10f);
		comercialDAO.create(comercialPrueba);
		log.info("Comercial creado: {}", comercialPrueba);

		Optional<Comercial> comercial = comercialDAO.find(comercialPrueba.getId());
		if (comercial.isPresent()) {
			comercial.get().setNombre("Comercial actualizado");
			comercialDAO.update(comercial.get());
			log.info("Comercial actualizado: {}", comercial.get());
		}

		comercialDAO.getAll().forEach(c -> log.info("Comercial: {}", c));
		comercialDAO.delete(comercialPrueba.getId());
		log.info("Comercial de prueba eliminado. Encontrado después del borrado: {}",
				comercialDAO.find(comercialPrueba.getId()).isPresent());
	}
}


