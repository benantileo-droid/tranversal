package com.example.productos;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import com.example.productos.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    @Test
    @DisplayName("Given productos existentes, when se listan, then retorna todos")
    void deberiaRetornarListaDeProductos() {
        // Given
        Producto p1 = new Producto(1L, "Laptop", 999.99, 10);
        Producto p2 = new Producto(2L, "Mouse", 29.99, 50);
        Mockito.when(repository.findAll()).thenReturn(List.of(p1, p2));

        // When
        List<Producto> resultado = service.listar();

        // Then
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Given un id existente, when se obtiene, then retorna el producto")
    void deberiaRetornarProductoCuandoExiste() {
        // Given
        Producto producto = new Producto(1L, "Laptop", 999.99, 10);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Optional<Producto> resultado = service.obtener(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Laptop", resultado.get().getNombre());
        assertEquals(999.99, resultado.get().getPrecio());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Given un id inexistente, when se obtiene, then retorna vacio")
    void deberiaRetornarVacioCuandoProductoNoExiste() {
        // Given
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Producto> resultado = service.obtener(99L);

        // Then
        assertFalse(resultado.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    @DisplayName("Given stock suficiente, when se descuenta, then reduce el stock y retorna true")
    void deberiaDescontarStockCorrectamente() {
        // Given
        Producto producto = new Producto(1L, "Laptop", 999.99, 10);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(producto));
        Mockito.when(repository.save(any(Producto.class))).thenReturn(producto);

        // When
        boolean resultado = service.descontarStock(1L, 3);

        // Then
        assertTrue(resultado);
        assertEquals(7, producto.getStock());
        verify(repository).findById(1L);
        verify(repository).save(producto);
    }

    @Test
    @DisplayName("Given stock insuficiente, when se descuenta, then retorna false y no guarda")
    void deberiaRetornarFalseCuandoStockInsuficiente() {
        // Given
        Producto producto = new Producto(1L, "Laptop", 999.99, 2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        boolean resultado = service.descontarStock(1L, 5);

        // Then
        assertFalse(resultado);
        verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Given un producto inexistente, when se descuenta stock, then retorna false")
    void deberiaRetornarFalseCuandoProductoNoExisteAlDescontar() {
        // Given
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        boolean resultado = service.descontarStock(99L, 1);

        // Then
        assertFalse(resultado);
        verify(repository).findById(99L);
    }
}
