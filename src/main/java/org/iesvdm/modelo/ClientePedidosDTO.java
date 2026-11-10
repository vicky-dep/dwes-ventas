package org.iesvdm.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientePedidosDTO {
	private int clienteId;
	private String clienteNombreCompleto;
	private Double importeTotalPedidos;
}
