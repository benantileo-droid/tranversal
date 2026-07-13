package com.example.mscomputer;

import com.example.mscomputer.model.Computer;
import com.example.mscomputer.repository.ComputerRepository;
import com.example.mscomputer.service.ComputerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComputerServiceTest {

    @Mock
    private ComputerRepository repo;

    @InjectMocks
    private ComputerService service;

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarComputerCuandoExiste() {

        Computer computer = new Computer();
        computer.setId(1L);
        computer.setNombre("MacBook Pro");
        computer.setMarca("Apple");
        computer.setModelo("M3");
        computer.setProcesador("Apple M3");
        computer.setRam("16GB");
        computer.setAlmacenamiento("512GB SSD");
        computer.setPrecio(new BigDecimal("1999.99"));
        computer.setEstado(Computer.Estado.DISPONIBLE);

        Mockito.when(repo.findById(1L))
                .thenReturn(Optional.of(computer));

        Computer resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("MacBook Pro", resultado.getNombre());
        assertEquals("Apple", resultado.getMarca());
        assertEquals(Computer.Estado.DISPONIBLE, resultado.getEstado());

        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoComputerNoExiste() {

        Mockito.when(repo.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(99L));

        assertTrue(ex.getMessage().contains("99"));

        verify(repo).findById(99L);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarComputer() {

        Computer computer = new Computer();
        computer.setId(1L);
        computer.setNombre("MacBook Pro");
        computer.setMarca("Apple");
        computer.setModelo("M3");
        computer.setProcesador("Apple M3");
        computer.setRam("16GB");
        computer.setAlmacenamiento("512GB SSD");
        computer.setPrecio(new BigDecimal("1999.99"));

        Mockito.when(repo.save(computer))
                .thenReturn(computer);

        Computer resultado = service.save(computer);

        assertNotNull(resultado);
        assertEquals("MacBook Pro", resultado.getNombre());
        assertEquals(new BigDecimal("1999.99"), resultado.getPrecio());

        verify(repo).save(computer);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void deberiaActualizarComputer() {

        Computer existente = new Computer();
        existente.setId(1L);
        existente.setNombre("MacBook Pro");
        existente.setMarca("Apple");
        existente.setModelo("M3");
        existente.setProcesador("Apple M3");
        existente.setRam("16GB");
        existente.setAlmacenamiento("512GB SSD");
        existente.setPrecio(new BigDecimal("1999.99"));
        existente.setEstado(Computer.Estado.DISPONIBLE);

        Computer datos = new Computer();
        datos.setNombre("MacBook Pro Max");
        datos.setMarca("Apple");
        datos.setModelo("M3 Max");
        datos.setProcesador("Apple M3 Max");
        datos.setRam("32GB");
        datos.setAlmacenamiento("1TB SSD");
        datos.setPrecio(new BigDecimal("2999.99"));
        datos.setEstado(Computer.Estado.DISPONIBLE);

        Mockito.when(repo.findById(1L))
                .thenReturn(Optional.of(existente));

        Mockito.when(repo.save(existente))
                .thenReturn(existente);

        Computer resultado = service.update(1L, datos);

        assertEquals("MacBook Pro Max", resultado.getNombre());
        assertEquals("32GB", resultado.getRam());
        assertEquals(new BigDecimal("2999.99"), resultado.getPrecio());

        verify(repo).findById(1L);
        verify(repo).save(existente);
    }

    // ── findByEstado ──────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarComputersPorEstado() {

        Computer c1 = new Computer();
        c1.setId(1L);
        c1.setNombre("MacBook Pro");
        c1.setMarca("Apple");
        c1.setModelo("M3");
        c1.setProcesador("Apple M3");
        c1.setRam("16GB");
        c1.setAlmacenamiento("512GB SSD");
        c1.setPrecio(new BigDecimal("1999.99"));
        c1.setEstado(Computer.Estado.DISPONIBLE);

        Computer c2 = new Computer();
        c2.setId(2L);
        c2.setNombre("Dell XPS 15");
        c2.setMarca("Dell");
        c2.setModelo("XPS 15");
        c2.setProcesador("Intel i9");
        c2.setRam("32GB");
        c2.setAlmacenamiento("1TB SSD");
        c2.setPrecio(new BigDecimal("1799.99"));
        c2.setEstado(Computer.Estado.DISPONIBLE);

        Mockito.when(repo.findByEstado(Computer.Estado.DISPONIBLE))
                .thenReturn(List.of(c1, c2));

        List<Computer> resultado = service.findByEstado(Computer.Estado.DISPONIBLE);

        assertEquals(2, resultado.size());
        assertEquals(Computer.Estado.DISPONIBLE, resultado.get(0).getEstado());

        verify(repo).findByEstado(Computer.Estado.DISPONIBLE);
    }

    // ── findByMarca ───────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarComputersPorMarca() {

        Computer c1 = new Computer();
        c1.setId(1L);
        c1.setNombre("MacBook Pro");
        c1.setMarca("Apple");
        c1.setModelo("M3");
        c1.setProcesador("Apple M3");
        c1.setRam("16GB");
        c1.setAlmacenamiento("512GB SSD");
        c1.setPrecio(new BigDecimal("1999.99"));

        Computer c2 = new Computer();
        c2.setId(2L);
        c2.setNombre("MacBook Air");
        c2.setMarca("Apple");
        c2.setModelo("M2");
        c2.setProcesador("Apple M2");
        c2.setRam("8GB");
        c2.setAlmacenamiento("256GB SSD");
        c2.setPrecio(new BigDecimal("1099.99"));

        Mockito.when(repo.findByMarcaIgnoreCase("Apple"))
                .thenReturn(List.of(c1, c2));

        List<Computer> resultado = service.findByMarca("Apple");

        assertEquals(2, resultado.size());
        assertEquals("Apple", resultado.get(0).getMarca());

        verify(repo).findByMarcaIgnoreCase("Apple");
    }
}
