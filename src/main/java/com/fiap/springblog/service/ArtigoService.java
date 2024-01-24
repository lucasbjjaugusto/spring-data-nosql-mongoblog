package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.service.impl.ArtigoServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {
    public List<Artigo> obterTodos();

    public Artigo obterPorCodigo(String codigo);

    //public Artigo criar(Artigo artigo);

    public ResponseEntity<?> criar(Artigo artigo);

    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo);

    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);

    public List<Artigo> findByDataGreaterThan(LocalDateTime localDateTime);

    public List<Artigo> findByDataAndStatus(LocalDateTime localDateTime, Integer status);

    public void atualizarArtigo(Artigo artigo);

    public void atualizarUrl(String id, String novaUrl);

    public void deleteById(String id);

    public void deleteArtigoById(String id);

    public void excluirArtigoEAutor(Artigo artigo);

    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime localDateTime);

    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo);

    Page<Artigo> listaArtigoPaginado(Pageable pageable);

    public List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);

    public List<Artigo> findByTexto(String texto);

    public List<ArtigoStatusCount> contarArtigosPorStatus();

    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate dataInicio, LocalDateTime dataFim);
}
