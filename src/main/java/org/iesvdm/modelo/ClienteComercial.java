package org.iesvdm.modelo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteComercial {
	private int clienteId;
	private int comercialId;
	private String comercialNombre;
	private String comercialApellido1;
	private String comercialApellido2;
	private LocalDate fecha;
	private int prioridad;
}