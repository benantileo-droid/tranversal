package com.example.mspedidos.service;

import com.example.mspedidos.dto.ItemPedidoDTO;
import com.example.mspedidos.dto.PedidoRequestDTO;
import com.example.mspedidos.model.DetallePedido;
import com.example.mspedidos.model.Pedido;
import com.example.mspedidos.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    private final RestTemplate restTemplate;

    public PedidoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${productos.service.url}")
    private String productosUrl;

    @Transactional
    public Pedido crearPedido(String username, PedidoRequestDTO request, String jwtToken) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un item");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> entityGet = new HttpEntity<>(headers);

        List<DetallePedido> detalles = new ArrayList<>();
        double total = 0;

        for (ItemPedidoDTO item : request.getItems()) {


            Map productoResp;
            try {
                ResponseEntity<Map> resp = restTemplate.exchange(
                        productosUrl + "/api/v1/productos/" + item.getProductoId(),
                        HttpMethod.GET, entityGet, Map.class);
                productoResp = resp.getBody();
            } catch (HttpClientErrorException e) {
                throw new RuntimeException("Producto no encontrado: id=" + item.getProductoId());
            }

            if (productoResp == null) {
                throw new RuntimeException("Respuesta vacía para productoId: " + item.getProductoId());
            }


            double precioReal = ((Number) productoResp.get("precio")).doubleValue();

            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(item.getProductoId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(precioReal);
            detalle.setSubtotal(precioReal * item.getCantidad());
            detalles.add(detalle);
            total += detalle.getSubtotal();
        }

        Pedido pedido = new Pedido();
        pedido.setUsername(username);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setEstado("CONFIRMADO");
        pedido.setDetalles(detalles);
        detalles.forEach(d -> d.setPedido(pedido));

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPorUsuario(String username) {
        return pedidoRepository.findByUsername(username);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
}
