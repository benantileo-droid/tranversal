package com.example.msinventario;

import com.example.msinventario.model.Stock;
import com.example.msinventario.repository.StockRepository;
import com.example.msinventario.service.StockService;
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
class StockServiceTest {

    @Mock
    private StockRepository repository;

    @InjectMocks
    private StockService service;

    @Test
    void deberiaRetornarTodosLosStocks() {

        Stock s1 = new Stock(1L, 10L, "Laptop",  20, 5);
        Stock s2 = new Stock(2L, 11L, "Mouse",   50, 10);

        Mockito.when(repository.findAll())
                .thenReturn(List.of(s1, s2));

        List<Stock> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());

        verify(repository).findAll();
    }

    @Test
    void deberiaRetornarStockPorProductoId() {

        Stock stock = new Stock(1L, 10L, "Laptop", 20, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));

        Optional<Stock> resultado = service.obtenerPorProducto(10L);

        assertTrue(resultado.isPresent());
        assertEquals("Laptop", resultado.get().getNombre());
        assertEquals(20, resultado.get().getCantidad());

        verify(repository).findByProductoId(10L);
    }

    @Test
    void deberiaRetornarVacioCuandoProductoNoExisteEnStock() {

        Mockito.when(repository.findByProductoId(99L))
                .thenReturn(Optional.empty());

        Optional<Stock> resultado = service.obtenerPorProducto(99L);

        assertFalse(resultado.isPresent());

        verify(repository).findByProductoId(99L);
    }

    @Test
    void deberiaDescontarStockCorrectamente() {

        Stock stock = new Stock(1L, 10L, "Laptop", 20, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));
        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stock);

        boolean resultado = service.descontar(10L, 8);

        assertTrue(resultado);
        assertEquals(12, stock.getCantidad());

        verify(repository).findByProductoId(10L);
        verify(repository).save(stock);
    }

    @Test
    void deberiaRetornarFalseCuandoStockInsuficienteAlDescontar() {

        Stock stock = new Stock(1L, 10L, "Laptop", 3, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));

        boolean resultado = service.descontar(10L, 10);

        assertFalse(resultado);

        verify(repository).findByProductoId(10L);
    }

    @Test
    void deberiaRetornarFalseCuandoProductoNoExisteAlDescontar() {

        Mockito.when(repository.findByProductoId(99L))
                .thenReturn(Optional.empty());

        boolean resultado = service.descontar(99L, 1);

        assertFalse(resultado);

        verify(repository).findByProductoId(99L);
    }

    @Test
    void deberiaReponerStockCorrectamente() {

        Stock stock = new Stock(1L, 10L, "Laptop", 5, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));
        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stock);

        boolean resultado = service.reponer(10L, 15);

        assertTrue(resultado);
        assertEquals(20, stock.getCantidad());

        verify(repository).findByProductoId(10L);
        verify(repository).save(stock);
    }

    @Test
    void deberiaRetornarFalseCuandoProductoNoExisteAlReponer() {

        Mockito.when(repository.findByProductoId(99L))
                .thenReturn(Optional.empty());

        boolean resultado = service.reponer(99L, 10);

        assertFalse(resultado);

        verify(repository).findByProductoId(99L);
    }

    @Test
    void deberiaRegistrarNuevoStock() {

        Stock stockGuardado = new Stock(1L, 10L, "Laptop", 20, 5);

        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stockGuardado);

        Stock resultado = service.registrar(10L, "Laptop", 20, 5);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getProductoId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(20, resultado.getCantidad());
        assertEquals(5, resultado.getStockMin());

        verify(repository).save(any(Stock.class));
    }

    @Test
    void deberiaRetornarProductosBajoMinimo() {

        Stock bajo1 = new Stock(1L, 10L, "Laptop", 3, 5);  // 3 <= 5 → alerta
        Stock bajo2 = new Stock(2L, 11L, "Mouse",  5, 5);  // 5 <= 5 → alerta
        Stock ok    = new Stock(3L, 12L, "Teclado", 20, 5); // 20 >  5 → ok

        Mockito.when(repository.findAll())
                .thenReturn(List.of(bajo1, bajo2, ok));

        List<Stock> resultado = service.productosBajoMinimo();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(s -> s.getNombre().equals("Laptop")));
        assertTrue(resultado.stream().anyMatch(s -> s.getNombre().equals("Mouse")));

        verify(repository).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoTodosLoProductosTienenStockSuficiente() {

        Stock s1 = new Stock(1L, 10L, "Laptop",  20, 5);
        Stock s2 = new Stock(2L, 11L, "Mouse",   15, 10);

        Mockito.when(repository.findAll())
                .thenReturn(List.of(s1, s2));

        List<Stock> resultado = service.productosBajoMinimo();

        assertTrue(resultado.isEmpty());

        verify(repository).findAll();
    }
}
