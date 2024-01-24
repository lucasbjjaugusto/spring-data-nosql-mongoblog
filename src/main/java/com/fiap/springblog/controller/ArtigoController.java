package com.fiap.springblog.controller;
/**
 * Autor: Lucas Oliveira
 * Data: 22/01/2024
 * Serviços disponiveis para Consumo da aplicação
 */


import com.fiap.springblog.model.*;
import com.fiap.springblog.service.ArtigoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @GetMapping
    public List<Artigo> obterTodos() {
        return artigoService.obterTodos();
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de concorrência: O Artigo foi atualizado por outro usuário. Por favor, tente novamente!");
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return artigoService.obterPorCodigo(codigo);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Artigo artigo) {
        return artigoService.criar(artigo);
    }

    @PostMapping("/artigo-com-autor")
    public ResponseEntity<?> criarArtigoComAutor(@RequestBody ArtigoComAutorRequest request) {
        Artigo artigo = request.getArtigo();
        Autor autor = request.getAutor();
        return artigoService.criarArtigoComAutor(artigo, autor);
    }

    @PutMapping("/atualiza-artigo/{id}")
    public ResponseEntity<?> atualizarArtigo(@PathVariable("id") String id, @Valid @RequestBody Artigo artigo) {
        return artigoService.atualizarArtigo(id, artigo);
    }

    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime localDateTime) {
        return artigoService.findByDataGreaterThan(localDateTime);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(@RequestParam("data") LocalDateTime localDateTime, @RequestParam("status") Integer status) {
        return artigoService.findByDataAndStatus(localDateTime, status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo artigo) {
        artigoService.atualizarArtigo(artigo);
    }

    @PutMapping("/url/{id}")
    public void atualizarUrl(@PathVariable String id, @RequestBody String novaUrl) {
        artigoService.atualizarUrl(id, novaUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteArtigo(@PathVariable String id) {
        artigoService.deleteById(id);
    }

    @DeleteMapping("/remove")
    public void deleteArtigoById(@RequestParam("id") String id) {
        artigoService.deleteById(id);
    }

    @DeleteMapping("/delete-artigo-autor")
    public void deleteArtigoComAutor(@RequestBody Artigo artigo) {
        artigoService.excluirArtigoEAutor(artigo);
    }

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(@RequestParam("status") Integer status, @RequestParam("data") LocalDateTime localDateTime) {
        return artigoService.findByStatusAndDataGreaterThan(status, localDateTime);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(@RequestParam("de") LocalDateTime de, @RequestParam("ate") LocalDateTime ate) {
        return artigoService.obterArtigoPorDataHora(de, ate);
    }

    @GetMapping("/artigo-complexo")
    public List<Artigo> encontrarArtigosComplexos(@RequestParam("status") Integer status, @RequestParam("data") LocalDateTime data, @RequestParam("titulo") String titulo) {
        return artigoService.encontrarArtigosComplexos(status, data, titulo);
    }

    @GetMapping("/pagina-artigo")
    public ResponseEntity<Page<Artigo>> listaArtigoPaginado(Pageable pageable) {
        Page<Artigo> artigos = artigoService.listaArtigoPaginado(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(@RequestParam("status") Integer status) {
        return artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(@RequestParam("status") Integer status) {
        return artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    @GetMapping("/busca-texto")
    public List<Artigo> findByTexto(@RequestParam("texto") String texto) {
        return artigoService.findByTexto(texto);
    }

    @GetMapping("/contar-artigo")
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        return artigoService.contarArtigosPorStatus();
    }

    @GetMapping("/total-artigo-autor-periodo")
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(@RequestParam("dataInicio") LocalDate dataInicio, @RequestParam("dataFim") LocalDateTime dataFim) {
        return artigoService.calcularTotalArtigosPorAutorNoPeriodo(dataInicio, dataFim);
    }

}
