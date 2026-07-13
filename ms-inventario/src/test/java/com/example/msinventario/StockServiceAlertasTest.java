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
class StockServiceAlertasTest {

    @Mock
    private StockRepository repository;

    @InjectMocks
    private StockService service;

    @Test
    void deberiaDetectarStockExactamenteIgualAlMinimo() {

        // cantidad == stockMin → debe generar alerta (<=)
        Stock stock = new Stock(1L, 10L, "Laptop", 5, 5);

        Mockito.when(repository.findAll())
                .thenReturn(List.of(stock));

        List<Stock> alertas = service.productosBajoMinimo();

        assertEquals(1, alertas.size());
        assertEquals("Laptop", alertas.get(0).getNombre());

        verify(repository).findAll();
    }

    @Test
    void noDeberiaGenerarAlertaCuandoCantidadEsUnoSobreElMinimo() {

        // cantidad = stockMin + 1 → NO debe generar alerta
        Stock stock = new Stock(1L, 10L, "Laptop", 6, 5);

        Mockito.when(repository.findAll())
                .thenReturn(List.of(stock));

        List<Stock> alertas = service.productosBajoMinimo();

        assertTrue(alertas.isEmpty());

        verify(repository).findAll();
    }

    @Test
    void deberiaDescontarStockHastaExactamenteCero() {

        Stock stock = new Stock(1L, 10L, "Laptop", 5, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));
        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stock);

        boolean resultado = service.descontar(10L, 5);

        assertTrue(resultado);
        assertEquals(0, stock.getCantidad());

        verify(repository).save(stock);
    }

    @Test
    void deberiaReponerStockAcumulandoCantidadExistente() {

        Stock stock = new Stock(1L, 10L, "Laptop", 3, 5);

        Mockito.when(repository.findByProductoId(10L))
                .thenReturn(Optional.of(stock));
        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stock);

        service.reponer(10L, 7);

        // 3 existentes + 7 repuestos = 10
        assertEquals(10, stock.getCantidad());

        verify(repository).save(stock);
    }

    @Test
    void deberiaRegistrarStockConStockMinPersonalizado() {

        Stock stockGuardado = new Stock(1L, 20L, "Monitor", 30, 10);

        Mockito.when(repository.save(any(Stock.class)))
                .thenReturn(stockGuardado);

        Stock resultado = service.registrar(20L, "Monitor", 30, 10);

        assertEquals(20L, resultado.getProductoId());
        assertEquals("Monitor", resultado.getNombre());
        assertEquals(30, resultado.getCantidad());
        assertEquals(10, resultado.getStockMin());

        verify(repository).save(any(Stock.class));
    }

    @Test
    void deberiaFiltrarCorrectamenteMezclaDeStocksConYSinAlerta() {

        List<Stock> todos = List.of(
                new Stock(1L, 10L, "Laptop",   2,  5),  // alerta: 2  <= 5
                new Stock(2L, 11L, "Mouse",    5,  5),  // alerta: 5  <= 5
                new Stock(3L, 12L, "Teclado",  6,  5),  // ok:     6  >  5
                new Stock(4L, 13L, "Monitor", 10,  5),  // ok:     10 >  5
                new Stock(5L, 14L, "Webcam",   1, 10)   // alerta: 1  <= 10
        );

        Mockito.when(repository.findAll())
                .thenReturn(todos);

        List<Stock> alertas = service.productosBajoMinimo();

        assertEquals(3, alertas.size());
        assertTrue(alertas.stream().noneMatch(s -> s.getNombre().equals("Teclado")));
        assertTrue(alertas.stream().noneMatch(s -> s.getNombre().equals("Monitor")));

        verify(repository).findAll();
    }
}
