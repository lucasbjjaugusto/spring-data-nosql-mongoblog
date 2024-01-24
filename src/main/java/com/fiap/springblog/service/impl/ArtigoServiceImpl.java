package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements com.fiap.springblog.service.ArtigoService {

    private final MongoTemplate mongoTemplate;

    private final ArtigoRepository artigoRepository;

    private AutorRepository autorRepository;

    @Autowired
    private MongoTransactionManager transactionManager;

    public ArtigoServiceImpl(MongoTemplate mongoTemplate, ArtigoRepository artigoRepository, AutorRepository autorRepository) {
        this.mongoTemplate = mongoTemplate;
        this.artigoRepository = artigoRepository;
        this.autorRepository = autorRepository;
    }

    @Override
    public List<Artigo> obterTodos() {
        return artigoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Artigo obterPorCodigo(String codigo) {
        return artigoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Artigo não existe"));
    }

    @Transactional
    @Override
    public ResponseEntity<?> criar(Artigo artigo) {
        if (artigo.getAutor().getCodigo() != null) {
            Autor autor = autorRepository.findById(artigo.getAutor().getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Autor não existe"));
            artigo.setAutor(autor);
        } else {
            artigo.setAutor(null);
        }
        try {
            artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Artigo ja existe na coleção!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar artigo: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo artigoExistente = artigoRepository.findById(id).orElse(null);
            if (artigoExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artigo nao encontrado na coleção!");
            }
            artigoExistente.setTitulo(artigo.getTitulo());
            artigoExistente.setData(artigoExistente.getData());
            artigoExistente.setTexto(artigo.getTexto());
            artigoRepository.save(artigoExistente);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar artigo: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            try {
                autorRepository.save(autor);
                artigo.setData(LocalDateTime.now());
                artigo.setAutor(autor);
                artigoRepository.save(artigo);
            } catch (Exception e) {
                status.setRollbackOnly(); //rollback todas as transações (Autor e Artigo)
                throw new RuntimeException("Erro ao criar artigo com autor: " + e.getMessage());
            }
            return null;
        });
        return null;
    }

    /*@Transactional
    @Override
    public Artigo criar(Artigo artigo) {
        if (artigo.getAutor().getCodigo() != null) {
            Autor autor = autorRepository.findById(artigo.getAutor()
                            .getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Autor não existe"));
            artigo.setAutor(autor);
        } else {
            artigo.setAutor(null);
        }

        try {
            return artigoRepository.save(artigo);
        } catch (OptimisticLockingFailureException e) {
            Artigo atual = artigoRepository.findById(artigo.getCodigo()).orElse(null);

            if (atual != null) {
                atual.setTitulo(artigo.getTitulo());
                atual.setTexto(artigo.getTexto());
                atual.setStatus(artigo.getStatus());

                atual.setVersion(atual.getVersion() + 1);
                return artigoRepository.save(atual);
            } else {
                throw new RuntimeException("Artigo não encontrado!" + artigo.getCodigo());
            }
        }
    }*/

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime localDateTime) {
        Query query = new Query(Criteria.where("data").gt(localDateTime));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime localDateTime, Integer status) {
        Query query = new Query(Criteria.where("data").is(localDateTime).and("status").is(status));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Transactional
    @Override
    public void atualizarArtigo(Artigo artigo) {
        artigoRepository.save(artigo);
    }

    @Transactional
    @Override
    public void atualizarUrl(String id, String novaUrl) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("url", novaUrl);
        mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        artigoRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteArtigoById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    public void excluirArtigoEAutor(Artigo artigo) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            try {
                artigoRepository.delete(artigo);
                Autor autor = artigo.getAutor();
                autorRepository.delete(autor);
            } catch (Exception e) {
                status.setRollbackOnly(); //rollback todas as transações (Autor e Artigo)
                throw new RuntimeException("Erro ao deletar artigo com autor: " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime localDateTime) {
        return artigoRepository.findByStatusAndDataGreaterThan(status, localDateTime);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo) {
        Criteria criteria = new Criteria();
        criteria.and("data").lte(data);

        if (status != null) {
            criteria.and("status").is(status);
        }
        if (titulo != null && !titulo.isEmpty()) {
            criteria.and("titulo").regex(titulo, "i");
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public Page<Artigo> listaArtigoPaginado(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return artigoRepository.findAll(paginacao);
    }

    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    @Override
    public List<Artigo> findByTexto(String texto) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingPhrase(texto);
        Query query = TextQuery.queryText(textCriteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation = Aggregation.newAggregation(Artigo.class, Aggregation.group("status").count().as("quantidade"), Aggregation.project("quantidade").and("status").previousOperation());
        AggregationResults<ArtigoStatusCount> result = mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);
        return result.getMappedResults();
    }

    @Override
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate dataInicio, LocalDateTime dataFim) {
        TypedAggregation<Artigo> aggregation = Aggregation.newAggregation(
                Artigo.class,
                Aggregation.match(Criteria.where("data")
                        .gte(dataInicio.atStartOfDay())
                        .lte(dataFim.toLocalDate().plusDays(1).atStartOfDay())),
                Aggregation.group("autor").count().as("totalArtigos"),
                Aggregation.project("totalArtigos").and("autor").previousOperation()
        );
        AggregationResults<AutorTotalArtigo> result = mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);
        return result.getMappedResults();
    }

}
