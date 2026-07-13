package com.example.mscheckout.service;

import com.example.mscheckout.dto.CheckoutRequestDTO;
import com.example.mscheckout.dto.CheckoutResultDTO;
import com.example.mscheckout.dto.ItemCheckoutDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CheckoutService {

    private RestTemplate restTemplate;

    public CheckoutService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${productos.service.url}")
    private String productosUrl;

    @Value("${inventario.service.url}")
    private String inventarioUrl;

    @Value("${pedidos.service.url}")
    private String pedidosUrl;

    public CheckoutResultDTO procesarCompra(String username, CheckoutRequestDTO request, String jwtToken) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("El carrito no puede estar vacío");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> entityGet = new HttpEntity<>(headers);

        List<ItemCheckoutDTO> descontados = new ArrayList<>();
        double total = 0;

        try {
            List<Map<String, Object>> itemsParaPedido = new ArrayList<>();

            for (ItemCheckoutDTO item : request.getItems()) {
                if (item.getCantidad() == null || item.getCantidad() <= 0) {
                    throw new RuntimeException("Cantidad inválida para productoId: " + item.getProductoId());
                }


                ResponseEntity<Map> productoResp;
                try {
                    productoResp = restTemplate.exchange(
                            productosUrl + "/api/v1/productos/" + item.getProductoId(),
                            HttpMethod.GET, entityGet, Map.class);
                } catch (HttpClientErrorException e) {
                    throw new RuntimeException("Producto no encontrado: id=" + item.getProductoId());
                }

                Map<String, Object> producto = productoResp.getBody();
                if (producto == null) throw new RuntimeException("Respuesta vacía para productoId: " + item.getProductoId());

                double precio = ((Number) producto.get("precio")).doubleValue();
                String nombre = (String) producto.get("nombre");


                ResponseEntity<Map> stockResp;
                try {
                    stockResp = restTemplate.exchange(
                            inventarioUrl + "/api/v1/inventario/" + item.getProductoId()
                                    + "/descontar?cantidad=" + item.getCantidad(),
                            HttpMethod.PUT, entityGet, Map.class);
                } catch (HttpClientErrorException e) {
                    throw new RuntimeException("Stock insuficiente para: " + nombre);
                }

                if (!stockResp.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("No se pudo descontar stock de: " + nombre);
                }

                descontados.add(item);
                total += precio * item.getCantidad();

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productoId", item.getProductoId());
                itemMap.put("cantidad", item.getCantidad());

                itemMap.put("precioUnit", precio);
                itemsParaPedido.add(itemMap);
            }

            // Crear pedido en ms-pedidos
            Map<String, Object> pedidoBody = new HashMap<>();
            pedidoBody.put("items", itemsParaPedido);

            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.set("Authorization", "Bearer " + jwtToken);
            postHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> pedidoRequest = new HttpEntity<>(pedidoBody, postHeaders);

            ResponseEntity<Map> pedidoResp;
            try {
                pedidoResp = restTemplate.exchange(
                        pedidosUrl + "/api/v1/pedidos",
                        HttpMethod.POST, pedidoRequest, Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al registrar el pedido: " + e.getMessage());
            }

            Map<String, Object> pedido = pedidoResp.getBody();
            Long pedidoId = pedido != null ? Long.valueOf(pedido.get("id").toString()) : null;

            return new CheckoutResultDTO(pedidoId, username, total, "CONFIRMADO",
                    "Compra realizada exitosamente");

        } catch (RuntimeException ex) {
            // Compensación: reponer stock de los items ya descontados
            for (ItemCheckoutDTO d : descontados) {
                try {
                    restTemplate.exchange(
                            inventarioUrl + "/api/v1/inventario/" + d.getProductoId()
                                    + "/reponer?cantidad=" + d.getCantidad(),
                            HttpMethod.PUT, entityGet, Map.class);
                } catch (Exception ignored) {}
            }
            throw ex;
        }
    }
}